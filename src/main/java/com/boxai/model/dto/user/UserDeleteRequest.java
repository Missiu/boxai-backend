package com.boxai.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户删除请求类，用于封装删除用户请求的数据。
 * @author Hzh
 */
@Data
public class UserDeleteRequest implements Serializable {

    /**
     * 序列化版本号，用于在序列化对象时保持兼容性。
     */
    @Serial
    private static final long serialVersionUID = -5564932158817453622L;
    /**
     * 用户的ID，用于指定要删除的用户。
     */
    private Long id;
}
