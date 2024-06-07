package com.boxai.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户删除
 */
@Data
public class UserDeleteDTO implements Serializable {
    private Long id;
    @Serial
    private static final long serialVersionUID = 1L;
}
