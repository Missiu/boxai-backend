package com.boxai.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boxai.model.domain.User;
import com.boxai.model.dto.user.UserLoginResponse;
import com.boxai.model.dto.user.UserQueryRequest;
import com.boxai.model.dto.user.UserUpdateRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务接口，定义了用户相关的业务操作
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 验证密码，用于确认密码输入正确
     * @return 注册成功的用户ID，出错返回-1
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param request HTTP请求对象，用于获取登录IP等信息
     * @return 登录成功返回用户信息及token等组成的响应对象，失败返回null
     */
    UserLoginResponse userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录的用户信息
     *
     * @param request HTTP请求对象，用于从session中获取用户信息
     * @return 登录的用户对象，未登录返回null
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 根据用户对象获取登录用户响应信息
     *
     * @param user 用户对象
     * @return 登录用户响应信息
     */
    UserLoginResponse getLoginUser(User user);

    /**
     * 更新用户信息。
     *
     * @param userUpdateRequest 包含更新后用户信息的对象。
     * @param request HttpServletRequest对象，用于获取请求相关信息（例如用户身份验证信息）。
     * @return 返回一个布尔值，表示用户信息是否更新成功。成功返回true，失败返回false。
     */
    Boolean updateUser(UserUpdateRequest userUpdateRequest, HttpServletRequest request);


    /**
     * 判断当前请求是否来自管理员
     *
     * @param request HTTP请求对象，用于判断管理员身份
     * @return 是管理员返回true，否则返回false
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 判断用户是否为管理员
     *
     * @param user 用户对象
     * @return 是管理员返回true，否则返回false
     */
    boolean isAdmin(User user);

    /**
     * 用户登出
     *
     * @param request HTTP请求对象，用于登出操作
     * @return 登出成功返回true，失败返回false
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 根据用户查询请求构建查询包装器
     *
     * @param userQueryRequest 用户查询请求对象
     * @return 查询包装器对象
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

}
