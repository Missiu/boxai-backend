package com.boxai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxai.common.base.ReturnCode;
import com.boxai.common.enumerate.AIGCEnum;
import com.boxai.common.enumerate.FileTypeEnum;
import com.boxai.config.bean.SpringContextHolder;
import com.boxai.exception.customize.CustomizeReturnException;
import com.boxai.exception.customize.CustomizeTransactionException;
import com.boxai.mapper.DataChartsMapper;
import com.boxai.mapper.UsersMapper;
import com.boxai.model.dto.datachart.ChartCreatTextDTO;
import com.boxai.model.dto.datachart.ChartCreateDTO;
import com.boxai.model.dto.datachart.ChartQueryDTO;
import com.boxai.model.dto.datachart.ChartUpdateDTO;
import com.boxai.model.entity.DataCharts;
import com.boxai.model.page.PageModel;
import com.boxai.model.vo.datachart.ChartQueryVO;
import com.boxai.model.vo.datachart.UniversalDataChartsVO;
import com.boxai.model.vo.user.UserInfoVO;
import com.boxai.service.DataChartsService;
import com.boxai.utils.chat.MoonshotAiPrompt;
import com.boxai.utils.dataclean.DataClean;
import com.boxai.utils.rateLimit.RateLimitUtils;
import com.boxai.utils.threadlocal.UserHolder;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiFunction;

import static com.boxai.common.constants.RedisKeyConstant.AI_GENERATION_CONTENT;
import static com.boxai.utils.dataclean.DataClean.extractFlagsContent;
import static com.boxai.utils.file.FileUtils.readFile;
import static com.boxai.utils.file.FileUtils.readMultipleFiles;

/**
 * @author Hzh
 * {@code @description} 针对表【data_charts(数据信息表)】的数据库操作Service实现
 * {@code @createDate} 2024-05-13 19:42:53
 */
@Service
public class DataChartsServiceImpl extends ServiceImpl<DataChartsMapper, DataCharts> implements DataChartsService {
    /**
     * 用于引入提示词
     */
    @Resource
    private MoonshotAiPrompt moonshotAiPrompt;
    /**
     * 用于设置redis缓存
     */
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 用于更新用户代金券状态
     */
    @Resource
    private UsersMapper usersMapper;
    /**
     * 用于设置限流器
     */
    @Resource
    private RateLimitUtils rateLimitUtils;
    /**
     * 用于配置线程，让ai生成异步化
     */
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    /**
     * 用于获取当前上下文
     */
    @Resource
    private SpringContextHolder springContextHolder;

    @Autowired
    private SaveDataService saveDataService;

    /**
     * 单文件同步生成
     *
     * @param multipartFile  原始文件数据
     * @param chartCreateDTO 用户目标、生成名称的请求
     * @return UniversalDataChartsVO
     */
    @Override
    public UniversalDataChartsVO generationFileChart(MultipartFile multipartFile, ChartCreateDTO chartCreateDTO) {
        return processChart(new MultipartFile[]{multipartFile}, chartCreateDTO, false);
    }

    /**
     * 多文件同步生成
     *
     * @param multipartFiles 原始文件数据
     * @param chartCreateDTO 用户目标、生成名称的请求
     * @return UniversalDataChartsVO
     */
    @Override
    public UniversalDataChartsVO generationMultipleChart(MultipartFile[] multipartFiles, ChartCreateDTO chartCreateDTO) {
        return processChart(multipartFiles, chartCreateDTO, false);
    }

    /**
     * 单文件异步生成
     *
     * @param multipartFile  原始文件数据
     * @param chartCreateDTO 用户目标、生成名称的请求
     * @return UniversalDataChartsVO
     */
    @Override
    public UniversalDataChartsVO generationFileChartAsync(MultipartFile multipartFile, ChartCreateDTO chartCreateDTO) {
        return processChart(new MultipartFile[]{multipartFile}, chartCreateDTO, true);
    }

    @Override
    public UniversalDataChartsVO generationMultipleChartAsync(MultipartFile[] multipartFiles, ChartCreateDTO chartCreateDTO) {
        return processChart(multipartFiles, chartCreateDTO, true);
    }

    @Override
    public UniversalDataChartsVO genTextChart(ChartCreatTextDTO chartCreatTextDTO) {
        // 预处理
        UniversalDataChartsVO universalDataChartsVO = new UniversalDataChartsVO();
        universalDataChartsVO.setRawData(chartCreatTextDTO.getText());
        universalDataChartsVO.setUserId(UserHolder.getUser().getId());

        universalDataChartsVO.setFileType(FileTypeEnum.SINGLE_FILE.getDescription());
        String getGenerationName = chartCreatTextDTO.getText();
        if (StringUtils.isEmpty(getGenerationName)) {
            universalDataChartsVO.setGenerationName("暂无分析名称"+System.currentTimeMillis());
        }else {
            universalDataChartsVO.setGenerationName(chartCreatTextDTO.getGenerationName());
        }
        // 调用ai
        String s = saveDataService.genChart(universalDataChartsVO, UserHolder.getUser());
        UniversalDataChartsVO dataChartsVO = splitData(s, universalDataChartsVO);
        UniversalDataChartsVO dataChartsVO1 = saveDataService.saveData(dataChartsVO);
        saveDataService.updateStatusRedisCache(dataChartsVO1, null);
        UserHolder.removeUser();
        return universalDataChartsVO;
    }

    @Override
    public UniversalDataChartsVO genTextChartSync(ChartCreatTextDTO chartCreatTextDTO) {
        // 预处理
        UserInfoVO userInfoVO = UserHolder.getUser();
        UniversalDataChartsVO universalDataChartsVO = new UniversalDataChartsVO();
        universalDataChartsVO.setRawData(chartCreatTextDTO.getText());
        universalDataChartsVO.setUserId(userInfoVO.getId());
        String getGenerationName = chartCreatTextDTO.getGenerationName();
        if (StringUtils.isEmpty(getGenerationName)) {
            universalDataChartsVO.setGenerationName("暂无名称");
        }else {
            universalDataChartsVO.setGenerationName(chartCreatTextDTO.getGenerationName());
        }
        // 先保存到数据库获取id等信息、更新缓存，设置状态为排队中
        UniversalDataChartsVO updateStatusRedisCache = saveDataService.updateStatusRedisCache(saveDataService.saveData(universalDataChartsVO), String.valueOf(AIGCEnum.QUEUEING));
        try{
            CompletableFuture.supplyAsync(() -> {
                // 进入异步，设置状态为执行中
                UniversalDataChartsVO updateStatusRedisCacheTemp = saveDataService.updateStatusRedisCache(updateStatusRedisCache, String.valueOf(AIGCEnum.EXECUTING));
                // 调用ai生成原始数据、分割数据、保存到数据库
                String s =  saveDataService.genChart(updateStatusRedisCacheTemp, userInfoVO);
                UniversalDataChartsVO dataChartsVO = splitData(s, updateStatusRedisCacheTemp);
                UniversalDataChartsVO dataChartsVO1 = saveDataService.saveData(dataChartsVO);
                // 传入数据 更新缓存，设置状态为完成
                return saveDataService.updateStatusRedisCache(dataChartsVO1, String.valueOf(AIGCEnum.COMPLETED));
            });
        }catch (Exception e){
            saveDataService.updateStatusRedisCache(updateStatusRedisCache, String.valueOf(AIGCEnum.FAILED));
        }
        UserHolder.removeUser();
        return universalDataChartsVO;
    }

    @Override
    public UniversalDataChartsVO getChartInfo(Long id) {
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(AI_GENERATION_CONTENT + id);
        UniversalDataChartsVO dataChartsVO = BeanUtil.mapToBean(entries, UniversalDataChartsVO.class, true);
        if (dataChartsVO == null || dataChartsVO.getId() == null){
            DataCharts dataCharts = baseMapper.selectById(id);
            BeanUtil.copyProperties(dataCharts, dataChartsVO);
//            dataChartsVO.setStatus(String.valueOf(AIGCEnum.COMPLETED));
        }
        return dataChartsVO;
    }


    @Override
    public Boolean deleteChartById(Long id) {
        String cacheKey = AI_GENERATION_CONTENT + id;
        // 先逻辑删除
        if (Boolean.TRUE.equals(baseMapper.deleteChartById(id))) {
            stringRedisTemplate.delete(cacheKey);
            // 纪录当前时间(有自动执行任务来删除数据)
            baseMapper.updateCurrentTime(id);
            return true;
        } else {
            throw new CustomizeTransactionException(ReturnCode.MAIN_MEMORY_DATABASE_SERVICE_ERROR);
        }
    }

    /**
     * 更新数据图表信息。
     * 该方法根据传入的ChartUpdateDTO对象，更新对应的图表数据。通过将不同的更新操作映射到一个方法映射表中，
     * 然后根据DTO中的字段动态选择相应的更新操作进行执行。
     *
     * @param chartUpdateDTO 包含需要更新的图表信息的数据传输对象。该对象中定义了各种可能的更新操作及其对应的值。
     * @return 返回一个布尔值，表示更新操作是否成功执行。若至少有一个更新操作被执行，则返回true；否则返回false。
     */
    @Override
    public Boolean updateDataCharts(ChartUpdateDTO chartUpdateDTO) {
        // 创建一个方法映射表，用于存储各种更新操作及其对应的执行函数。
        Map<String, BiFunction<String, Long, Boolean>> updateMethods = new HashMap<>();

        // 动态向方法映射表中添加更新操作及其对应的执行函数。
        updateMethods.put(chartUpdateDTO.getGoalDescription(), baseMapper::updateGoalDescription);
        updateMethods.put(chartUpdateDTO.getGenerationName(), baseMapper::updateGenerationName);
        updateMethods.put(chartUpdateDTO.getCodeComments(), baseMapper::updateCodeComments);
        updateMethods.put(chartUpdateDTO.getCodeProfileDescription(), baseMapper::updateCodeProfileDescription);
        updateMethods.put(chartUpdateDTO.getCodeEntities(), baseMapper::updateCodeEntities);
        updateMethods.put(chartUpdateDTO.getCodeApis(), baseMapper::updateCodeApis);
        updateMethods.put(chartUpdateDTO.getCodeExecution(), baseMapper::updateCodeExecution);
        updateMethods.put(chartUpdateDTO.getCodeSuggestions(), baseMapper::updateCodeSuggestions);
        updateMethods.put(chartUpdateDTO.getCodeNormRadar(), baseMapper::updateCodeNormRadar);
        updateMethods.put(chartUpdateDTO.getCodeNormRadarDescription(), baseMapper::updateCodeNormRadarDescription);
        updateMethods.put(chartUpdateDTO.getCodeTechnologyPie(), baseMapper::updateCodeTechnologyPie);
        updateMethods.put(chartUpdateDTO.getCodeCatalogPath(), baseMapper::updateCodeCatalogPath);

        // 遍历方法映射表，执行相应的更新操作。
        for (Map.Entry<String, BiFunction<String, Long, Boolean>> entry : updateMethods.entrySet()) {
            String value = entry.getKey();
            BiFunction<String, Long, Boolean> updateMethod = entry.getValue();
            // 当前值非空时，执行更新操作，并清除对应的Redis缓存。
            if (StringUtils.isNotBlank(value)) {
                if (stringRedisTemplate.opsForValue().get(AI_GENERATION_CONTENT + chartUpdateDTO.getId()) != null) {
                    stringRedisTemplate.delete(AI_GENERATION_CONTENT + chartUpdateDTO.getId());
                }
                // 执行更新操作并返回结果。
                return updateMethod.apply(value, chartUpdateDTO.getId());
            }
        }
        // 若没有执行任何更新操作，则返回false。
        return false;
    }

    /**
     * 查询图表信息列表。
     *
     * @param chartQueryDTO 包含图表查询条件的数据传输对象
     * @param pageModel     分页模型，用于构建和返回分页信息
     * @return Page<ChartQueryVO> 返回图表查询结果的分页对象
     * @throws CustomizeTransactionException 当查询结果为空时抛出自定义事务异常
     */
    @Override
    public Page<UniversalDataChartsVO> listChartInfo(ChartQueryDTO chartQueryDTO, PageModel pageModel) {
        Page<DataCharts> dataChartsPage = pageModel.build();

        // 根据查询条件构建查询Wrapper
        QueryWrapper<DataCharts> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(chartQueryDTO.getId() != null && chartQueryDTO.getId() > 0, "id", chartQueryDTO.getId());
        queryWrapper.eq(chartQueryDTO.getUserId() != null && chartQueryDTO.getUserId() > 0, "user_id", chartQueryDTO.getUserId());
        // 开始一个分组，用于添加 or 条件
        if (StringUtils.isNotBlank(chartQueryDTO.getGoalDescription()) || StringUtils.isNotBlank(chartQueryDTO.getGenerationName())) {
            queryWrapper.and(wrapper -> {
                // goalDescription 作为可选条件
                if (StringUtils.isNotBlank(chartQueryDTO.getGoalDescription())) {
                    wrapper.or().like("goal_description", chartQueryDTO.getGoalDescription());
                }
                // generationName 作为可选条件
                if (StringUtils.isNotBlank(chartQueryDTO.getGenerationName())) {
                    wrapper.or().like("generation_name", chartQueryDTO.getGenerationName());
                }
            });
        }
        queryWrapper.orderByAsc("create_time");

        // 执行数据库查询
        baseMapper.selectPage(dataChartsPage, queryWrapper);

        // 构建返回的分页对象
        Page<UniversalDataChartsVO> chartQueryVOPage = new Page<>(dataChartsPage.getCurrent(), dataChartsPage.getSize(), dataChartsPage.getTotal());

        // 将查询结果转换为VO列表
        if (!dataChartsPage.getRecords().isEmpty()) {
            List<UniversalDataChartsVO> chartQueryVOList = BeanUtil.copyToList(dataChartsPage.getRecords(), UniversalDataChartsVO.class);
            chartQueryVOPage.setRecords(chartQueryVOList);
        }

        // 返回分页对象
        return chartQueryVOPage;
    }


    /**
     * 预处理数据
     *
     * @param multipartFiles 文件内容
     * @param chartCreateDTO 请求参数
     * @return dataCharts封装数据
     */
    private UniversalDataChartsVO pretreatment(MultipartFile[] multipartFiles, ChartCreateDTO chartCreateDTO, UserInfoVO userInfoVO) {
        UniversalDataChartsVO universalDataChartsVOTemp = new UniversalDataChartsVO();
        String readFileStr;
        // 判断传入文件数量，读取文件
        if (multipartFiles.length == 1) {
            // 单文件则调用readFile
            readFileStr = readFile(multipartFiles[0]);
            // 标记单文件
            universalDataChartsVOTemp.setFileType(FileTypeEnum.SINGLE_FILE.getDescription());
        } else {
            // 多文件则调用readFile
            readFileStr = readMultipleFiles(multipartFiles);
            // 标记多文件
            universalDataChartsVOTemp.setFileType(FileTypeEnum.MULTIPLE_FILE.getDescription());
        }
        if (StringUtils.isBlank(readFileStr)) {
            throw new CustomizeReturnException(ReturnCode.USER_DO_NOT_UPLOAD_FILE, "用户文件内容为空");
        }
        // 保存原始数据
        universalDataChartsVOTemp.setRawData(readFileStr);

        // 文件校验、格式、大小等

        // 如果名称为空则以首文件命名
        if (StringUtils.isBlank(chartCreateDTO.getGenerationName())) {
            chartCreateDTO.setGenerationName(multipartFiles[0].getOriginalFilename());
        }
        // 保存名称、目标
        universalDataChartsVOTemp.setGenerationName(chartCreateDTO.getGenerationName());
        universalDataChartsVOTemp.setGoalDescription(chartCreateDTO.getGoalDescription());
        // 设置为未删除属性
        universalDataChartsVOTemp.setIsDelete(0);
        universalDataChartsVOTemp.setUserId(userInfoVO.getId());
        return universalDataChartsVOTemp;
    }


    /**
     * 拆分数据
     *
     * @param genChartRawData       ai生成的原始数据，如果为null则设置universalDataCharts为等待状态
     * @param universalDataChartsVO 通用参数方便传参
     * @return 把拆分后的数据储存到universalDataCharts
     */
    private UniversalDataChartsVO splitData(String genChartRawData, UniversalDataChartsVO universalDataChartsVO) {
        // 根据数据格式清洗、分割数据。
        List<String> generateData = extractFlagsContent(DataClean.cleanFileContent(genChartRawData));

        // 确保 generateData 的大小和 AIGCEnum枚举的 的长度一致
        if (generateData.size() != AIGCEnum.CodeKey.values().length) {
            throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH);
        }
        UniversalDataChartsVO universalDataChartsVOTemp = universalDataChartsVO;
        // 遍历枚举并使用setter方法更新UniversalDataCharts对象
        for (AIGCEnum.CodeKey key : AIGCEnum.CodeKey.values()) {
            //  根据key的顺序，从generateData映射中获取对应的字符串数据
            String data = generateData.get(key.ordinal());
            switch (key) {
                case CODE_COMMENTS -> universalDataChartsVOTemp.setCodeComments(data);
                case CODE_PROFILE_DESCRIPTION -> universalDataChartsVOTemp.setCodeProfileDescription(data);
                case CODE_ENTITIES -> universalDataChartsVOTemp.setCodeEntities(DataClean.extractCodeContent(data));
                case CODE_APIS -> universalDataChartsVOTemp.setCodeApis(data);
                case CODE_EXECUTION -> universalDataChartsVOTemp.setCodeExecution(data);
                case CODE_NORM_RADAR_DESCRIPTION -> universalDataChartsVOTemp.setCodeNormRadarDescription(data);
                case CODE_NORM_RADAR -> universalDataChartsVOTemp.setCodeNormRadar(DataClean.extractCodeContent(data));
                case CODE_TECHNOLOGY_PIE ->
                        universalDataChartsVOTemp.setCodeTechnologyPie(DataClean.extractCodeContent(data));
                case CODE_CATALOG_PATH -> universalDataChartsVOTemp.setCodeCatalogPath(data);
                case CODE_SUGGESTIONS -> universalDataChartsVOTemp.setCodeSuggestions(data);
                case AI_TOKEN_USAGE ->
                        universalDataChartsVOTemp.setAiTokenUsage(Integer.parseInt(DataClean.extractNumbersContent(data)));
                default -> throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH);
            }
        }
        return universalDataChartsVOTemp;
    }

    /**
     * 通用方法，用于处理文件和生成图表
     *
     * @param multipartFiles 文件数据
     * @param chartCreateDTO 请求数据
     * @param isAsync        是否同步
     * @return UniversalDataChartsVO 包含完整信息的对象
     */
    private UniversalDataChartsVO processChart(MultipartFile[] multipartFiles, ChartCreateDTO chartCreateDTO, boolean isAsync) {
        // 获取用户信息
        UserInfoVO userInfoVO = UserHolder.getUser();

        // 预处理
        UniversalDataChartsVO universalDataChartsVO = pretreatment(multipartFiles, chartCreateDTO, userInfoVO);

        if (isAsync) {
            // 异步处理
            // 先保存到数据库获取id等信息、更新缓存，设置状态为排队中
            UniversalDataChartsVO updateStatusRedisCache = saveDataService.updateStatusRedisCache(saveDataService.saveData(universalDataChartsVO), String.valueOf(AIGCEnum.QUEUEING));

            try{
                CompletableFuture.supplyAsync(() -> {
                    // 进入异步，设置状态为执行中
                    UniversalDataChartsVO updateStatusRedisCacheTemp = saveDataService.updateStatusRedisCache(updateStatusRedisCache, String.valueOf(AIGCEnum.EXECUTING));
                    // 调用ai生成原始数据、分割数据、保存到数据库
                    String s =  saveDataService.genChart(updateStatusRedisCacheTemp, userInfoVO);
                    UniversalDataChartsVO dataChartsVO = splitData(s, updateStatusRedisCacheTemp);
                    UniversalDataChartsVO dataChartsVO1 = saveDataService.saveData(dataChartsVO);
                    // 传入数据 更新缓存，设置状态为完成
                    return saveDataService.updateStatusRedisCache(dataChartsVO1, String.valueOf(AIGCEnum.COMPLETED));
                });
            }catch (Exception e){
                saveDataService.updateStatusRedisCache(updateStatusRedisCache, String.valueOf(AIGCEnum.FAILED));
            }
        } else {
            String s = saveDataService.genChart(universalDataChartsVO, userInfoVO);
            UniversalDataChartsVO dataChartsVO = splitData(s, universalDataChartsVO);
            // 同步处理
            UniversalDataChartsVO dataChartsVO1 = saveDataService.saveData(dataChartsVO);
            saveDataService.updateStatusRedisCache(dataChartsVO1, null);
        }
        UserHolder.removeUser();
        return universalDataChartsVO;
    }

}




