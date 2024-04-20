package com.boxai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxai.common.constant.CommonConstant;
import com.boxai.common.enums.ErrorCode;
import com.boxai.common.enums.UserRoleEnum;
import com.boxai.exception.BusinessException;
import com.boxai.mapper.UserMapper;
import com.boxai.model.domain.User;
import com.boxai.model.dto.user.UserLoginResponse;
import com.boxai.model.dto.user.UserQueryRequest;
import com.boxai.model.dto.user.UserUpdateRequest;
import com.boxai.service.UserService;
import com.boxai.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static com.boxai.common.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    public static final String SALT = "HZHBoxAI"; // 密码加密的盐值


    private final Object lock = new Object();

    /**
     * 用户注册接口。
     *
     * @param userAccount   用户账号，不能为空，长度不能少于4个字符。
     * @param userPassword  用户密码，不能为空，长度不能少于8个字符。
     * @param checkPassword 确认密码，必须和用户密码一致，长度不能少于8个字符。
     * @return 返回新用户ID，若注册成功；否则抛出异常。
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 参数校验及密码加密前的预处理
        // 校验参数完整性及一致性
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        // 密码强度检查
//        if (!isPasswordStrong(userPassword)) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码强度不足");
//        }
        // 使用同步代码块确保线程安全地检查账号唯一性
        synchronized (lock) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "该账号已被注册");
            }
        }
        // 对用户密码进行加密处理
        String encryptPassword = encryptPassword(userPassword);
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user); // 假设save方法负责将用户对象保存到数据库
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        }
        // 将加密后的用户信息保存到数据库
        return user.getId();
    }

    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request      请求对象，用于记录登录状态
     * @return 登录用户响应信息
     */
    @Override
    public UserLoginResponse userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 校验参数
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 加密密码
        String encryptPassword = encryptPassword(userPassword);
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper); // 根据查询条件尝试获取用户信息
        if (user == null && user.getId() == null) {
            // 如果查询不到用户，记录日志并抛出用户不存在或密码错误的异常
            log.info("User login attempt failed for userAccount: " + userAccount);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getLoginUser(user);
    }

    /**
     * 检查密码强度。
     *
     * @param password 待检查密码
     * @return 返回是否满足强度要求
     */
    private boolean isPasswordStrong(String password) {
        // 示例：检查密码是否包含数字、字母和特殊字符
        boolean hasDigit = false, hasLetter = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) hasDigit = true;
            else if (Character.isLetter(c)) hasLetter = true;
            else if (!Character.isWhitespace(c)) hasSpecial = true;
        }
        return hasDigit && hasLetter && hasSpecial;
    }

    /**
     * 对用户密码进行加密处理。
     *
     * @param plainPassword 明文密码。
     * @return 返回加密后的密码字符串。
     */
    private String encryptPassword(String plainPassword) {
        // 使用更安全的bcrypt算法或其他加盐哈希算法
        return DigestUtils.sha256Hex((SALT + plainPassword).getBytes());
    }

    /**
     * 从请求中获取当前登录的用户(全部信息)。
     *
     * @param request HttpServletRequest对象，用于获取会话信息。
     * @return 当前登录的User对象。
     * @throws BusinessException 如果用户未登录或用户ID无效，则抛出业务异常。
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 从会话中获取登录用户对象
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        // 判断用户对象是否存在且为User类型
        if (!(userObj instanceof User)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        User currentUser = (User) userObj;
        // 根据用户ID从数据库或缓存中重新获取用户信息
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        // 检查获取的用户信息是否为空
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 获取登录用户响应信息(脱敏信息)
     *
     * @param user 用户信息对象，不可为null
     * @return 登录用户响应对象，如果输入的用户对象为null，则返回null
     */
    public UserLoginResponse getLoginUser(User user) {
        // 检查传入的用户对象是否为null
        if (user == null) {
            return null;
        }
        // 创建登录用户响应对象
        UserLoginResponse userLoginResponse = new UserLoginResponse();
        // 将user对象的属性值复制到userLoginResponse对象中
        BeanUtils.copyProperties(user, userLoginResponse);
        return userLoginResponse;
    }

    /**
     * 更新用户信息。
     *
     * @param userUpdateRequest    用户信息对象，包含需要更新的用户信息。
     * @param request HttpServletRequest对象，用于获取当前登录用户信息。
     * @return 更新操作的布尔结果。成功返回true，失败返回false。
     * @throws BusinessException 如果用户未登录或更新操作失败，则抛出业务异常。
     */
    @Override
    public Boolean updateUser(UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        // 从请求会话中获取当前登录的用户对象
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        // 检查获取的用户对象是否存在且是User类型
        if (!(userObj instanceof User) ) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        User currentUser = (User) userObj;
        if(currentUser.getId() == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 如果传入的用户密码非空，则加密后更新当前用户密码
        if (userUpdateRequest.getUserPassword() != null) {
            String encryptPassword = encryptPassword(userUpdateRequest.getUserPassword());
            currentUser.setUserPassword(encryptPassword);
        }
        // 更新用户头像、个人简介和用户名，如果这些信息非空
        if (userUpdateRequest.getUserAvatar() != null) {
            currentUser.setUserAvatar(userUpdateRequest.getUserAvatar());
        }
        if (userUpdateRequest.getUserProfile() != null) {
            currentUser.setUserProfile(userUpdateRequest.getUserProfile());
        }
        if (userUpdateRequest.getUserName() != null) {
            currentUser.setUserName(userUpdateRequest.getUserName());
        }
        // 更新用户角色，如果用户账号非空
        if (userUpdateRequest.getUserAccount() != null) {
            synchronized (lock) {
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("userAccount", userUpdateRequest.getUserAccount());
                long count = this.baseMapper.selectCount(queryWrapper);
                if (count > 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "该账号已被注册");
                }
            }
            currentUser.setUserAccount(userUpdateRequest.getUserAccount());
        }
        // 设置更新时间为当前时间
        currentUser.setUpdateTime(new Date());
        // 尝试根据当前用户ID更新用户信息，如果失败则抛出业务异常
        if (!updateById(currentUser)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        updateById(currentUser);
        // 尝试更新传入的用户信息对象，返回更新结果
        return updateById(currentUser);
    }

    /**
     * 判断用户是否为管理员
     *
     * @param request 请求对象，用于获取登录用户信息
     * @return 如果用户是管理员，返回true，否则返回false
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    /**
     * 判断用户是否为管理员
     *
     * @param user 待判断的用户对象
     * @return 如果用户是管理员，返回true；否则返回false。
     */
    @Override
    public boolean isAdmin(User user) {
        // 判断用户不为null且用户角色为管理员
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 用户登出处理
     *
     * @param request 用户发起的HTTP请求
     * @return 登出成功返回true
     * @throws BusinessException 如果用户未登录，抛出业务异常
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 检查用户是否已登录，未登录抛出异常
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录状态，实现登出逻辑
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }


    /**
     * 根据用户查询请求构建查询包装器。
     *
     * @param userQueryRequest 用户查询请求对象，包含各种查询条件。
     * @return 返回构造的查询包装器对象，用于数据库查询条件的设定。
     * @throws BusinessException 如果用户查询请求对象为空，抛出此异常。
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 从用户查询请求中获取各项参数
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        // 创建查询包装器，并根据请求参数设定查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 为不为空的id、userRole添加等于条件
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        // 为不为空的userProfile、userName添加模糊查询条件
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        // 根据请求设定排序条件
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
}
