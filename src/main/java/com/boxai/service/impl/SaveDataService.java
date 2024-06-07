package com.boxai.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.boxai.common.base.ReturnCode;
import com.boxai.common.constants.RedisKeyConstant;
import com.boxai.common.enumerate.AIGCEnum;
import com.boxai.common.enumerate.FileTypeEnum;
import com.boxai.common.enumerate.UserEnum;
import com.boxai.exception.customize.CustomizeReturnException;
import com.boxai.exception.customize.CustomizeTransactionException;
import com.boxai.mapper.DataChartsMapper;
import com.boxai.mapper.UsersMapper;
import com.boxai.model.entity.DataCharts;
import com.boxai.model.vo.datachart.UniversalDataChartsVO;
import com.boxai.model.vo.user.UserInfoVO;
import com.boxai.utils.chat.Message;
import com.boxai.utils.chat.MoonshotAiPrompt;
import com.boxai.utils.chat.MoonshotAiUtils;
import com.boxai.utils.rateLimit.RateLimitUtils;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.boxai.common.enumerate.UserEnum.UserTypeEnum.NORMAL;

@Service
public class SaveDataService {
    /**
     * 用于设置redis缓存
     */
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 用于设置限流器
     */
    @Resource
    private RateLimitUtils rateLimitUtils;
    /**
     * 用于引入提示词
     */
    @Resource
    private MoonshotAiPrompt moonshotAiPrompt;
    /**
     * 用于更新用户代金券状态
     */
    @Resource
    private UsersMapper usersMapper;

    @Resource
    private DataChartsMapper dataChartsMapper;
    /**
     * 更新缓存里status数据
     *
     * @param universalDataChartsVO 数据状态设置
     * @return UniversalDataChartsVO 更新了status数据
     */
    @Transactional
    public UniversalDataChartsVO updateStatusRedisCache(UniversalDataChartsVO universalDataChartsVO, String status) {
        if (universalDataChartsVO.getId() == null) {
            throw new CustomizeTransactionException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }

        // key为AI生成内容前缀
        String key = RedisKeyConstant.AI_GENERATION_CONTENT + universalDataChartsVO.getId();

        // 使用opsForHash存储
        // 直接将对象转换为Map
        Map<String, Object> chartDataMap = new HashMap<>(JSONUtil.parseObj(JSONUtil.toJsonStr(universalDataChartsVO)));
        chartDataMap.replaceAll((k, v) -> v == null ? "" : v.toString());

        // 为空说明非异步调用，不必修改状态值，直接保存universalDataCharts到缓存
        if (status == null) {
            stringRedisTemplate.opsForHash().putAll(key, chartDataMap);
            stringRedisTemplate.expire(key, RedisKeyConstant.AI_GENERATION_CONTENT_TTL, TimeUnit.MINUTES);
            return universalDataChartsVO;
        }
        // 修改状态值，保存到缓存
        universalDataChartsVO.setStatus(status);
        chartDataMap.put("status", status);
        stringRedisTemplate.opsForHash().putAll(key, chartDataMap);
        stringRedisTemplate.expire(key, RedisKeyConstant.AI_GENERATION_CONTENT_TTL, TimeUnit.MINUTES);

        return universalDataChartsVO;
    }

    /**
     * 调用ai生生成原始数据,为了让事务生效，使用getBean获取上下文的方法调用
     *
     * @param universalDataChartsVO 预处理后的数据
     * @return 原始数据
     */
    @Transactional
    public String genChart(UniversalDataChartsVO universalDataChartsVO, UserInfoVO userInfoVO) {
        // 判断余额
        if (userInfoVO.getAvailableBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomizeReturnException(ReturnCode.USER_BALANCE_IS_INSUFFICIENT);
        }

        // 限流key
        String rateLimitKey = RedisKeyConstant.RATE_LIMIT_AI_GENERATION + userInfoVO.getId();
        // 用户信息缓存tokenKey
        String userTokenKey = RedisKeyConstant.USER_LOGIN_TOKEN + userInfoVO.getToken();

        List<Message> messages;
        if (StringUtils.isEmpty(universalDataChartsVO.getGoalDescription())){
            messages = CollUtil.newArrayList(
                    new Message(AIGCEnum.SYSTEM.getDescription(), moonshotAiPrompt.getSystemPresets()),
                    new Message(AIGCEnum.USER.getDescription(), ( moonshotAiPrompt.getCodeTemplate()+
                            "如下是你需要分析的内容: \n" + universalDataChartsVO.getRawData()
                    )));
        }else {
            messages = CollUtil.newArrayList(
                    new Message(AIGCEnum.SYSTEM.getDescription(), moonshotAiPrompt.getSystemPresets()),
                    new Message(AIGCEnum.USER.getDescription(), ( moonshotAiPrompt.getCodeTemplate()+
                            "如下是你需要分析的内容: \n" + universalDataChartsVO.getRawData()+
                            "你的分析的主要目标是: \n" + universalDataChartsVO.getGoalDescription()
                    )));
        }

        String userChatUResult;
        // 调用ai，有自定义key 或者 user 或者 vip三种类型
        // 普通用户，单文件8k，多文件32k 模型选择
        if (Objects.equals(userInfoVO.getRole(), NORMAL.getDescription())) {
            // 限流操作，用户
            rateLimitUtils.doRateLimit(rateLimitKey, false);
            userChatUResult = universalDataChartsVO.getFileType().equals(FileTypeEnum.SINGLE_FILE.getDescription()) ? MoonshotAiUtils.FileUserChatUtil(messages, null) : MoonshotAiUtils.MultipleFilesUserChatUtil(messages, null);
        } else if (Objects.equals(userInfoVO.getRole(), UserEnum.UserTypeEnum.VIP.getDescription())) {
            // 限流操作，vip
            rateLimitUtils.doRateLimit(rateLimitKey, true);
            // vip 128k
            userChatUResult = MoonshotAiUtils.VIPUserChatUtil(messages, null);
        } else {
            // 自定义key
            return MoonshotAiUtils.CustomizeUserChatUtil(messages, userInfoVO.getRole());
        }

        if (userChatUResult.isEmpty()) {
            throw new CustomizeReturnException(ReturnCode.AI_CONTENT_GENERATION_EXCEPTION);
        }

        // 更新用户代金券状态,数量-1
        if (Boolean.FALSE.equals(usersMapper.updateUserVoucher(userInfoVO.getId()))) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }

        // 更新用户代金券缓存
        stringRedisTemplate.opsForHash().put(userTokenKey, "voucherBalance", userInfoVO.getVoucherBalance().subtract(BigDecimal.ONE).toString());
        stringRedisTemplate.opsForHash().put(userTokenKey, "availableBalance", userInfoVO.getAvailableBalance().subtract(BigDecimal.ONE).toString());
        return userChatUResult;
    }
    /**
     * 持久化数据，保存时获取了id
     *
     * @return UniversalDataChartsVO 增加了数据库id等信息
     */
    @Transactional
    public UniversalDataChartsVO saveData(UniversalDataChartsVO universalDataChartsVO) {
        DataCharts dataCharts = new DataCharts();
        // 使用BeanUtils复制属性，避免手动复制
       dataCharts.setCodeSuggestions( universalDataChartsVO.getCodeSuggestions());
       dataCharts.setCodeNormRadar( universalDataChartsVO.getCodeNormRadar());
       dataCharts.setCodeNormRadarDescription( universalDataChartsVO.getCodeNormRadarDescription());
       dataCharts.setCodeTechnologyPie( universalDataChartsVO.getCodeTechnologyPie());
       dataCharts.setCodeEntities( universalDataChartsVO.getCodeEntities());
       dataCharts.setCodeApis( universalDataChartsVO.getCodeApis());
       dataCharts.setCodeExecution( universalDataChartsVO.getCodeExecution());
       dataCharts.setCodeProfileDescription( universalDataChartsVO.getCodeProfileDescription());
       dataCharts.setCodeComments( universalDataChartsVO.getCodeComments());
       dataCharts.setGenerationName( universalDataChartsVO.getGenerationName());
       dataCharts.setGoalDescription( universalDataChartsVO.getGoalDescription());
       dataCharts.setRawData( universalDataChartsVO.getRawData());
       dataCharts.setUserId( universalDataChartsVO.getUserId());
       dataCharts.setAiTokenUsage( universalDataChartsVO.getAiTokenUsage());
       dataCharts.setIsDelete( universalDataChartsVO.getIsDelete());
       dataCharts.setCodeCatalogPath( universalDataChartsVO.getCodeCatalogPath());
       dataCharts.setUpdateTime( universalDataChartsVO.getUpdateTime());
       dataCharts.setCreateTime( universalDataChartsVO.getCreateTime());
       dataCharts.setId( universalDataChartsVO.getId());

        // 检查是否是更新操作
        if (universalDataChartsVO.getId() == null) {
            // 插入新记录，如果插入失败则抛出异常
            if (dataChartsMapper.insert(dataCharts) != 1) {
                throw new CustomizeTransactionException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
            }
        } else {
            if (dataChartsMapper.updateById(dataCharts) != 1) {
                throw new CustomizeTransactionException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
            }
        }
        universalDataChartsVO.setId(dataCharts.getId());
        universalDataChartsVO.setCreateTime(dataCharts.getCreateTime());
        universalDataChartsVO.setUpdateTime(dataCharts.getUpdateTime());
        return universalDataChartsVO;
    }

}
