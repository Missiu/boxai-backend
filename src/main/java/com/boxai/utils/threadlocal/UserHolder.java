package com.boxai.utils.threadlocal;

import com.boxai.model.vo.user.UserInfoVO;

public class UserHolder {
    private static final ThreadLocal<UserInfoVO> USER_HOLDER = new ThreadLocal<>();

    public static void saveUser(UserInfoVO userInfoVO) {
        USER_HOLDER.set(userInfoVO);
    }

    public static UserInfoVO getUser() {
        return USER_HOLDER.get();
    }

    public static void removeUser() {
        USER_HOLDER.remove();
    }
}
