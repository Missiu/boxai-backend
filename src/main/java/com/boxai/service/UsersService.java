package com.boxai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boxai.model.dto.user.*;
import com.boxai.model.entity.Users;
import com.boxai.model.page.PageModel;
import com.boxai.model.vo.user.UserInfoVO;

/**
 * @author Hzh
 * @description 针对表【users(用户信息表)】的数据库操作Service
 * @createDate 2024-05-13 19:42:54
 */
public interface UsersService extends IService<Users> {
    /**
     * 处理用户登录请求。
     *
     * @param userLoginDTO 用户登录数据传输对象
     * @return 返回token
     */
    String userLogin(UserLoginDTO userLoginDTO, String token);

    /**
     * 注册用户。
     *
     * @param userRegisterDTO 用户注册数据传输对象
     * @return 注册结果
     */
    String userRegister(UserRegisterDTO userRegisterDTO);

    /**
     * 处理用户登出的逻辑。
     *
     * @return 登出是否成功。
     */
    Boolean userLogout();

    /**
     * 获取登录用户信息。
     * @return UserInfoVO 用户的详细信息。
     */
    UserInfoVO getLoginUser();

    /**
     * 根据ID删除用户。
     * @return Boolean 操作成功返回true，失败返回false。
     */
    Boolean deleteById();

    /**
     * 更新用户密码。
     * @param updateUserPassword 包含新密码信息的DTO对象。
     * @return Boolean 操作成功返回true，失败返回false。
     */
    Boolean updateUserPassword(UserUpdatePasswordDTO updateUserPassword);

    /**
     * 更新用户信息。
     * @param userUpdateDTO 包含更新信息的DTO对象。
     * @return Boolean 操作成功返回true，失败返回false。
     */
    Boolean updateUserInfo(UserUpdateDTO userUpdateDTO);

    /**
     * 分页查询用户信息。
     * @param userQueryDTO 包含查询条件的DTO对象。
     * @param pageModel 分页参数模型，包含页码和每页数量。
     * @return Page<UserInfoVO> 用户信息的分页结果，包含当前页的用户信息列表。
     */
    Page<UserInfoVO> listUserInfo(UserQueryDTO userQueryDTO, PageModel pageModel);

    Boolean UserUpdateAIKey(UserUpdateKeyDTO userUpdatekeyDTO);
}
