package com.boxai.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求
 *
 */
@Data
public class UserDeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}