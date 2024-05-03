package com.boxai.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxai.common.enums.ErrorCode;
import com.boxai.exception.BusinessException;
import com.boxai.mapper.UserMapper;
import com.boxai.model.domain.User;
import com.boxai.model.dto.common.BaseResponse;
import com.boxai.model.dto.common.ResultResponse;
import com.boxai.model.dto.user.*;
import com.boxai.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.Objects;

import static com.boxai.common.enums.ErrorCode.PARAMS_ERROR;

/**
 * @author Hzh
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;

    /**
     * 用户注册
     * @param userRegisterRequest 注册请求参数
     * @return 注册结果
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 校验请求体是否为空
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 提取用户账号、密码和确认密码
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        // 校验账号、密码和确认密码是否为空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        // 调用服务层执行用户注册操作
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        // 构造并返回注册成功的响应
        return ResultResponse.success(result);
    }

    /**
     * 用户登录
     * @param userLoginRequest 登录请求参数
     * @param request 用户的HTTP请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public BaseResponse<UserInfoResponse> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 校验登录请求参数是否为空
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 提取用户名和密码
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        // 校验用户名和密码是否为空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 调用服务层处理登录逻辑，并获取登录结果
        UserInfoResponse userInfoResponse = userService.userLogin(userAccount, userPassword, request);
        // 构造并返回登录成功的响应
        return ResultResponse.success(userInfoResponse);
    }

    /**
     * 用户登出
     * @param request 用户的HTTP请求
     * @return 登出结果
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        // 检查请求对象是否为空，若为空则抛出业务异常
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 执行用户登出操作，并获取操作结果
        boolean result = userService.userLogout(request);
        return ResultResponse.success(result);
    }

    /**
     * 获取登录用户信息
     * @param request 用户的HTTP请求
     * @return 登录用户信息
     */
    @GetMapping("/get/login")
    public BaseResponse<UserInfoResponse> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request); // 获取用户信息
        return ResultResponse.success(userService.getLoginUser(user)); // 返回脱敏后的用户信息
    }

    /**
     * 删除用户
     *
     * @param userDeleteRequest 包含要删除用户ID的请求体
     * @return 返回一个布尔值的成功响应，表示删除操作是否成功
     * @throws BusinessException 如果传入的用户ID不合法，则抛出业务异常
     */
    @PostMapping("/delete")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody UserDeleteRequest userDeleteRequest) {
        // 校验传入的用户删除请求是否合法
        if (userDeleteRequest == null || userDeleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 调用服务层方法，根据ID删除用户
        boolean b = userService.removeById(userDeleteRequest.getId());
        // 构造并返回删除成功与否的响应
        return ResultResponse.success(b);
    }

//    更新密码
    @PostMapping("/update/password")
    public BaseResponse<Integer> upPassword(@RequestBody UserUpPassword upPassword, HttpServletRequest request) {
        User user = userService.getLoginUser(request); // 获取用户信息
        if (user.getId() == null){
            throw new BusinessException(PARAMS_ERROR);
        }
        // 校验请求对象是否为null或id是否合法
        if (!Objects.equals(upPassword.getCheckPassword(), upPassword.getNewPassword())) {
            throw new BusinessException(PARAMS_ERROR);
        }
        if (!Objects.equals(userService.encryptPassword(upPassword.getOldPassword()), user.getUserPassword())){
            throw new BusinessException(PARAMS_ERROR);
        }
        // 对用户密码进行加密处理
        String encryptPassword = userService.encryptPassword(upPassword.getNewPassword());

        int result = userMapper.updateUserPassword(encryptPassword, user.getId());
        // 返回更新结果
        return ResultResponse.success(result);
    }

    // 更新用户信息
    @PostMapping("/update/info")
    public BaseResponse<Integer> upUserInfo(@RequestBody UserUpInfoRequest userUpInfoRequest, HttpServletRequest request) {
        User user = userService.getLoginUser(request); // 获取用户信息
        if (user.getId() == null){
            throw new BusinessException(PARAMS_ERROR);
        }
        // 校验请求对象是否为null或id是否合法
        if (userUpInfoRequest.getUserName() == null || userUpInfoRequest.getUserProfile() == null) {
            throw new BusinessException(PARAMS_ERROR);
        }
        // 调用服务层方法，更新用户信息
        int result = userMapper.updateUserInfo(userUpInfoRequest.getUserName(), userUpInfoRequest.getUserProfile(), user.getId());
        // 返回更新结果
        return ResultResponse.success(result);
    }

    @PostMapping("/update/account")
    public BaseResponse<Integer> upAccount(@RequestBody UserUpInfoRequest userUpInfoRequest, HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        if (user.getId() == null){
            throw new BusinessException(PARAMS_ERROR);
        }
        // 校验请求对象是否为null或id是否合法
        if (userUpInfoRequest.getUserAccount() == null) {
            throw new BusinessException(PARAMS_ERROR);
        }
        int result = userMapper.updateUserAccount(userUpInfoRequest.getUserAccount(), user.getId());
        return ResultResponse.success(result);
    }


    /**
     * 分页查询用户列表信息
     *
     * @param userQueryRequest 用户查询请求对象，包含当前页号和每页大小
     * @param request HttpServletRequest对象，用于获取请求信息（本例中未使用，可根据实际需要添加）
     * @return 返回用户列表的分页响应信息，包含查询到的用户页面对象
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest,
            HttpServletRequest request) {
        // 根据请求获取当前页号和每页大小
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 调用服务层方法，进行用户信息的分页查询
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        // 将查询结果包装成成功响应并返回
        return ResultResponse.success(userPage);
    }
}
