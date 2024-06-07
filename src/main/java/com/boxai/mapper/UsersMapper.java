package com.boxai.mapper;

import com.boxai.model.entity.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.math.BigDecimal;

/**
* @author Hzh
* {@code @description} 针对表【users(用户信息表)】的数据库操作Mapper
* {@code @createDate} 2024-05-13 19:42:54
* {@code @Entity} generator.entity.Users
 */
public interface UsersMapper extends BaseMapper<Users> {
    /**
     * 根据账户名选择用户信息。
     *
     * @param account 用户的账户名
     * @return Users
     */
    Users selectByAccount(String account);
    /**
     * 根据账户存在性选择。
     *
     * @param account 用户账户
     * @return 如果账户存在，为1，否则为null。
     */
    Integer selectByAccountExist(String account);
    /**
     * 根据id更新用户密码。
     *
     * @param passwordHash 用户密码
     * @param id 用户id
     * @return 如果更新成功，为true，否则为false。
     */
    Boolean updateUserPassword(String passwordHash ,Long id);

    /**
     * 更新用户昵称。
     * @param nickname 新的昵称。
     * @param id 用户的ID。
     * @return 如果更新成功返回true，否则返回false。
     */
    Boolean updateUserNickname(String nickname ,Long id);

    /**
     * 更新用户个人资料。
     * @param profile 新的个人资料信息。
     * @param id 用户的ID。
     * @return 如果更新成功返回true，否则返回false。
     */
    Boolean updateUserProfile(String profile ,Long id);

    /**
     * 更新用户账号信息。
     * @param account 新的账号信息。
     * @param id 用户的ID。
     * @return 如果更新成功返回true，否则返回false。
     */
    Boolean updateUserAccount(String account ,Long id);

    /**
     * 更新用户代金券状态,数量-1
     * @param id 用户的ID。
     * @return 更新后的代金券状态，作为整数返回。
     */
    Boolean updateUserVoucher(Long id);

    /**
     * 根据ID删除用户。
     * @param id 要删除的用户的ID。
     * @return 如果删除成功返回true，否则返回false。
     */
    Boolean deleteUserById(Long id);

    /**
     * 根据id更新当前时间
     * @param id 用户id
     * @return 如果更新成功返回true，否则返回false。
     */
    Boolean updateCurrentTime(Long id);

//    更新role
    Boolean updateUserRole(String role ,Long id);
}




