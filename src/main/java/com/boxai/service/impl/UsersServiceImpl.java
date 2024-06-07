package com.boxai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxai.common.base.ReturnCode;
import com.boxai.exception.customize.CustomizeReturnException;
import com.boxai.exception.customize.CustomizeTransactionException;
import com.boxai.mapper.UsersMapper;
import com.boxai.model.dto.user.*;
import com.boxai.model.entity.Users;
import com.boxai.model.page.PageModel;
import com.boxai.model.vo.user.UserInfoVO;
import com.boxai.service.UsersService;
import com.boxai.utils.chat.MoonshotAiUtils;
import com.boxai.utils.threadlocal.UserHolder;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import static com.boxai.common.constants.RedisKeyConstant.USER_LOGIN_TOKEN;
import static com.boxai.common.constants.RedisKeyConstant.USER_LOGIN_TOKEN_TTL;
import static com.boxai.utils.encrypt.EncryptPasswordUtil.encryptPassword;
import static com.boxai.utils.encrypt.EncryptPasswordUtil.matchesPassword;

/**
 * @author Hzh
 * {@code @description} 针对表【users(用户信息表)】的数据库操作Service实现
 * {@code @createDate} 2024-05-13 19:42:54
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
        implements UsersService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 登录操作，把token放入redis
     *
     * @param userLoginDTO 用户登录数据传输对象，包含登录所需的用户凭证信息。
     * @return token，也就是redis key
     */
    @Override
    public String userLogin(UserLoginDTO userLoginDTO, String oldToken) {
        // 生成token
        String token = UUID.randomUUID().toString(true);
        // 前缀 + token 为key
        String key = USER_LOGIN_TOKEN + token;
        // 刷新用户登录信息，token保持不变
        if (oldToken != null && Boolean.TRUE.equals(stringRedisTemplate.hasKey(USER_LOGIN_TOKEN+oldToken))) {
            stringRedisTemplate.delete(USER_LOGIN_TOKEN + oldToken);
            key = USER_LOGIN_TOKEN + oldToken;
            token = oldToken;
        }
        // 获取用户信息
        Users user = baseMapper.selectByAccount(userLoginDTO.getUserAccount());
        if (user == null) {
            throw new CustomizeReturnException(ReturnCode.USER_ACCOUNT_DOES_NOT_EXIST);
        }
        // 密码校验成功
        if (matchesPassword(userLoginDTO.getUserPassword(), user.getPasswordHash())) {
            // 将user的属性值复制到UserInfoVO对象中
            UserInfoVO userInfoVO = BeanUtil.copyProperties(user, UserInfoVO.class);
            // 如果用户自己传入了apikey，那么查询自己的余额，没有key则使用代金券
            if (!user.getRole().equals("user") && !user.getRole().equals("vip")) {
                Map<String, Double> balanceMap = MoonshotAiUtils.fetchBalance(user.getRole());
                userInfoVO.setAvailableBalance(BigDecimal.valueOf(balanceMap.get("available_balance")));
                userInfoVO.setVoucherBalance(BigDecimal.valueOf(balanceMap.get("voucher_balance")));
                userInfoVO.setCashBalance(BigDecimal.valueOf(balanceMap.get("cash_balance")));
            }
            userInfoVO.setToken(token);
            // 将所有值转换为字符串
            Map<String, Object> userInfoVOMap = BeanUtil.beanToMap(userInfoVO);
            userInfoVOMap.replaceAll((k, v) -> v == null ? "" : v.toString());
            // 保存信息到redis中
            stringRedisTemplate.opsForHash().putAll(key, userInfoVOMap);
            // 设置有效期
            stringRedisTemplate.expire(key, USER_LOGIN_TOKEN_TTL, TimeUnit.MINUTES);
            // 设置session 的token值

            // 返回token
            return token;
        } else {
            throw new CustomizeReturnException(ReturnCode.WRONG_USER_ACCOUNT_OR_PASSWORD);
        }
    }

    /**
     * 注册操作，插入账号密码到用户，成功后登录
     *
     * @param userRegisterDTO 用户注册数据传输对象
     * @return token
     */
    @Override
    public String userRegister(UserRegisterDTO userRegisterDTO) {
        if (!StringUtils.equals(userRegisterDTO.getUserPassword(), userRegisterDTO.getCheckPassword())) {
            throw new CustomizeReturnException(ReturnCode.PASSWORD_AND_SECONDARY_PASSWORD_NOT_SAME);
        }
        // 使用同步代码块确保线程安全地检查账号唯一性
        synchronized (this) {
            Integer exist = baseMapper.selectByAccountExist(userRegisterDTO.getUserAccount());
            if (exist == 1) {
                throw new CustomizeReturnException(ReturnCode.USERNAME_ALREADY_EXISTS);
            }
        }
        // 密码加密
        String encryptPassword = encryptPassword(userRegisterDTO.getUserPassword());

        // 构建持久化数据库的User数据
        Users users = new Users();
        users.setAccount(userRegisterDTO.getUserAccount());
        users.setPasswordHash(encryptPassword);
        // 默认预算和默认可用为5
        users.setVoucherBalance(new BigDecimal(5));
        users.setAvailableBalance(new BigDecimal(5));
        users.setCashBalance(new BigDecimal(0));
        users.setIsDelete(0);

        int insert = baseMapper.insert(users);
        if (insert <= 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUserPassword(userRegisterDTO.getUserPassword());
        userLoginDTO.setUserAccount(userRegisterDTO.getUserAccount());
        return userLogin(userLoginDTO, null);
    }

    /**
     * 用户登出功能。
     * 通过传入的token从Redis中删除对应的用户登录token，以实现用户登出。
     *
     * @return 返回一个Boolean值，表示删除操作是否成功。
     */
    @Override
    public Boolean userLogout() {
        // 本地缓存
        String token = UserHolder.getUser().getToken();
        if (StringUtils.isBlank(token)) {
            throw new CustomizeReturnException(ReturnCode.USER_LOGIN_HAS_EXPIRED);
        }
        UserHolder.removeUser();
        // 从Redis中删除指定格式的token
        return stringRedisTemplate.delete(USER_LOGIN_TOKEN + token);
    }

    /**
     * 获取登录用户信息
     *
     * @return 用户信息
     */
    @Override
    public UserInfoVO getLoginUser() {
        // 本地缓存
        UserInfoVO userInfoVO = UserHolder.getUser();
        if (StringUtils.isBlank(userInfoVO.getAccount())) {
            throw new CustomizeReturnException(ReturnCode.USER_LOGIN_HAS_EXPIRED);
        }
        UserHolder.removeUser();
        return userInfoVO;
    }

    /**
     * 根据ID和token删除用户信息。
     * 该方法首先进行逻辑删除，即在数据库中标记该用户为删除状态。
     * 如果逻辑删除成功，则尝试从缓存中删除对应的用户登录token。
     *
     * @return 返回一个布尔值，如果删除成功，则返回true；否则返回false。
     */
    @Override
    @Transactional
    public Boolean deleteById() {
        UserInfoVO userInfoVO = UserHolder.getUser();
        if (StringUtils.isBlank(userInfoVO.getAccount())) {
            throw new CustomizeReturnException(ReturnCode.USER_LOGIN_HAS_EXPIRED);
        }
        baseMapper.updateCurrentTime(userInfoVO.getId());
        // 先逻辑删除用户
        if (Boolean.FALSE.equals(baseMapper.deleteUserById(userInfoVO.getId()))) {
            throw new CustomizeTransactionException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        // 从缓存中删除用户登录token，返回删除结果
        if (!Boolean.TRUE.equals(stringRedisTemplate.delete(USER_LOGIN_TOKEN + userInfoVO.getToken()))) {
            throw new CustomizeTransactionException(ReturnCode.MAIN_MEMORY_DATABASE_SERVICE_ERROR);
        }
        UserHolder.removeUser();
        return true;
    }

    /**
     * 更新用户信息
     *
     * @param updateUserPassword 用户更新数据传输对象
     * @return 更新结果
     */
    @Override
    @Transactional
    public Boolean updateUserPassword(UserUpdatePasswordDTO updateUserPassword) {
        UserInfoVO user = getLoginUser();
        if (Objects.isNull(user)) {
            throw new CustomizeTransactionException(ReturnCode.MAIN_MEMORY_DATABASE_SERVICE_ERROR);
        }
        // 新密码与二次密码不一致
        if (!updateUserPassword.getCheckPassword().equals(updateUserPassword.getNewPassword())) {
            throw new CustomizeReturnException(ReturnCode.PASSWORD_AND_SECONDARY_PASSWORD_NOT_SAME);
        }
        // 原密码不能等于新密码
        if (updateUserPassword.getNewPassword().equals(updateUserPassword.getOldPassword())) {
            throw new CustomizeReturnException(ReturnCode.NEW_PASSWORD_AND_OLD_PASSWORD_ARE_SAME);
        }
        // 原密码和数据库密码不一致
        if (!matchesPassword(updateUserPassword.getOldPassword(), baseMapper.selectById(user.getId()).getPasswordHash())) {
            throw new CustomizeReturnException(ReturnCode.WRONG_USER_ACCOUNT_OR_PASSWORD);
        }
        String passwordHash = encryptPassword(updateUserPassword.getNewPassword());
        Boolean aBoolean = baseMapper.updateUserPassword(passwordHash, user.getId());
        // 更新数据库密码
        if (Boolean.FALSE.equals(aBoolean)) {
            throw new CustomizeTransactionException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        baseMapper.updateCurrentTime(user.getId());
        // 删除缓存， 退出重新登录
        stringRedisTemplate.delete(USER_LOGIN_TOKEN + user.getToken());
        UserHolder.removeUser();
        return aBoolean;
    }

    @Override
    public Boolean updateUserInfo(UserUpdateDTO userUpdateDTO) {
        UserInfoVO user = getLoginUser();
        if (Objects.isNull(user)) {
            throw new CustomizeTransactionException(ReturnCode.MAIN_MEMORY_DATABASE_SERVICE_ERROR);
        }
        String key = USER_LOGIN_TOKEN + user.getToken();
        // 账号不为空，更新账号
        if (StringUtils.isNotBlank(userUpdateDTO.getUserAccount())) {
            baseMapper.updateUserAccount(userUpdateDTO.getUserAccount(), user.getId());
            stringRedisTemplate.opsForHash().put(key, "account", userUpdateDTO.getUserAccount());
        }
        // 简介不为空，更新简介
        if (StringUtils.isNotBlank(userUpdateDTO.getProfile())) {
            baseMapper.updateUserProfile(userUpdateDTO.getProfile(), user.getId());
            stringRedisTemplate.opsForHash().put(key, "profile", userUpdateDTO.getProfile());
        }
        // 昵称不为空，更新昵称
        if (StringUtils.isNotBlank(userUpdateDTO.getNickname())) {
            baseMapper.updateUserNickname(userUpdateDTO.getNickname(), user.getId());
            stringRedisTemplate.opsForHash().put(key, "nickname", userUpdateDTO.getNickname());
        }
        baseMapper.updateCurrentTime(user.getId());
        UserHolder.removeUser();
        return true;
    }
    /**
     * 查询用户信息列表。
     *
     * @param userQueryDTO 包含用户查询条件的数据传输对象，如用户ID、昵称、账号等。
     * @param pageModel    分页模型对象，用于构建和返回分页信息。
     * @return 返回用户信息的分页模型，包含查询到的用户信息列表。
     * @throws CustomizeTransactionException 如果查询过程中发生错误，抛出自定义事务异常。
     */
    @Override
    @Transactional(readOnly = true, rollbackFor = CustomizeTransactionException.class)
    public Page<UserInfoVO> listUserInfo(UserQueryDTO userQueryDTO, PageModel pageModel) {
        // 构建用户的分页模型
        Page<Users> usersPageModel = pageModel.build();
        // 根据查询条件构建查询Wrapper（如果有）
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(userQueryDTO.getId() > 0, "id", userQueryDTO.getId());
        queryWrapper.eq("is_delete", 0);
        queryWrapper.like(StringUtils.isNotBlank(userQueryDTO.getNickname()), "nickname", userQueryDTO.getNickname());
        queryWrapper.like(StringUtils.isNotBlank(userQueryDTO.getUserAccount()), "account", userQueryDTO.getUserAccount());
        // 按创建时间升序排序
        queryWrapper.orderByAsc("create_time");
        // 执行分页查询
        baseMapper.selectPage(usersPageModel, queryWrapper);
        if (usersPageModel.getRecords().isEmpty()) {
            // 如果查询结果为空，抛出自定义事务异常，
            throw new CustomizeTransactionException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        // 构建用户信息的分页模型
        Page<UserInfoVO> userInfoVOPageModel = pageModel.build();
        // 将用户实体列表转换为用户信息VO列表（清除敏感信息）
        List<UserInfoVO> userInfoVOList = BeanUtil.copyToList(usersPageModel.getRecords(), UserInfoVO.class);
        // 设置到用户信息的分页模型中
        userInfoVOPageModel.setRecords(userInfoVOList);
        UserHolder.removeUser();
        return userInfoVOPageModel;
    }

    @Override
    public Boolean UserUpdateAIKey(UserUpdateKeyDTO userUpdatekeyDTO) {
        UserInfoVO user = UserHolder.getUser();
        if (user.getRole().equals("user") && Objects.isNull(userUpdatekeyDTO)){
            baseMapper.updateUserRole("user",UserHolder.getUser().getId());
        }
        if (user.getRole().equals("vip") && Objects.nonNull(userUpdatekeyDTO)){
            baseMapper.updateUserRole("vip",UserHolder.getUser().getId());
        }
        if (Objects.equals(userUpdatekeyDTO.getRole(), "user") ||               Objects.equals(userUpdatekeyDTO.getRole(), "vip")){
            return true;
        }
        Boolean aBoolean = baseMapper.updateUserRole(userUpdatekeyDTO.getRole(), user.getId());
        if (Boolean.FALSE.equals(aBoolean)){
            throw new CustomizeTransactionException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        stringRedisTemplate.opsForHash().put(USER_LOGIN_TOKEN + user.getToken(), "role", userUpdatekeyDTO.getRole());
        UserHolder.removeUser();
        return true;
    }
}




