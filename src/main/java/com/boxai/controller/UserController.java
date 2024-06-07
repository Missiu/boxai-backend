package com.boxai.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxai.common.base.R;
import com.boxai.common.constants.CommonConstant;
import com.boxai.model.dto.user.*;
import com.boxai.model.page.PageModel;
import com.boxai.model.vo.user.UserInfoVO;
import com.boxai.service.UsersService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UsersService usersService;

    /**
     * 用户登录接口
     *
     * @param userLoginDTO 用户登录参数，包含账号密码
     * @return 登录结果，包含token等信息
     */
    @PostMapping("/login")
    public R<String> userLogin(@RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request) {
        String oldToken = request.getHeader("Token");
        String toekn = usersService.userLogin(userLoginDTO, oldToken);
        request.getSession().setAttribute(CommonConstant.TOKEN, toekn);
        return R.ok(toekn);
    }

    /**
     * 用户注册接口
     *
     * @param userRegisterDTO 用户注册参数，包含账号、密码、确认密码
     * @return 注册结果
     */
    @PostMapping("/register")
    public R<String> userRegister(@RequestBody UserRegisterDTO userRegisterDTO,HttpServletRequest request) {
        String token = usersService.userRegister(userRegisterDTO);
        request.getSession().setAttribute(CommonConstant.TOKEN, token);
        return R.ok(token);
    }

    /**
     * 用户登出接口
     *
     * @return 登出结果，通常为成功或失败标识
     */
    @GetMapping("/logout")
    public R<Boolean> userLogout() {
        // 执行用户登出操作，并获取操作结果
        return R.ok(usersService.userLogout());
    }

    /**
     * 获取登录用户信息接口
     *
     * @return 登录用户的信息，包含脱敏后的数据
     */
    @GetMapping("/info")
    public R<UserInfoVO> userLoginInfo() {
        // 返回脱敏后的用户信息
        return R.ok(usersService.getLoginUser());
    }

    /**
     * 删除用户接口
     *
     * @return 删除结果，成功或失败标识
     */
    @GetMapping("/delete")
    public R<Boolean> userDelete() {
        // 调用服务层方法，根据ID删除用户
        return R.ok(usersService.deleteById());
    }

    /**
     * 更新用户密码接口
     *
     * @param updateUserPassword 用户密码更新参数
     * @return 更新结果，成功或失败标识
     */
    @PutMapping("/password")
    public R<Boolean> updateUserPassword(@RequestBody UserUpdatePasswordDTO updateUserPassword) {
        return R.ok(usersService.updateUserPassword(updateUserPassword));
    }

    /**
     * 更新用户信息接口
     *
     * @param userUpdateDTO 用户信息更新参数
     * @return 更新结果，成功或失败标识
     */
    @PutMapping("/info")
    public R<Boolean> updateUserInfo(@RequestBody UserUpdateDTO userUpdateDTO) {
        return R.ok(usersService.updateUserInfo(userUpdateDTO));
    }

    /**
     * 分页查询用户信息接口,可添加管理员鉴权
     *
     * @param userQueryDTO 用户信息查询参数
     * @param pageModel    分页参数模型
     * @return 用户信息的分页查询结果
     */
    @PostMapping("/list/info")
    public R<Page<UserInfoVO>> listUserInfo(@RequestBody UserQueryDTO userQueryDTO,
                                            PageModel pageModel) {
        return R.ok(usersService.listUserInfo(userQueryDTO, pageModel));
    }


    @PutMapping("/key")
    public R<Boolean> updateUserKey(@RequestBody UserUpdateKeyDTO userUpdatekeyDTO) {
        return R.ok(usersService.UserUpdateAIKey(userUpdatekeyDTO));
    }
}
