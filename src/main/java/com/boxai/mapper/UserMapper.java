package com.boxai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boxai.model.domain.User;

import java.math.BigDecimal;

/**
* @author Hzh
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2024-04-23 18:41:23
* @Entity com.boxai.model.domain.User
*/
public interface UserMapper extends BaseMapper<User> {

//    更新密码
    int updateUserPassword(String password ,Long id);
//    updateBalance

    int updateBalance(BigDecimal availableBalance, BigDecimal voucherBalance, BigDecimal cashBalance, Long id);

//    更新用户信息包含，用户名，用户简介
    int updateUserName(String username,Long id);
    int updateUserProfile(String userProfile,Long id);

//    更新账号信息
    int updateUserAccount(String userAccount,Long id);
}




