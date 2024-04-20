package com.boxai.model.dto.user;

import com.boxai.model.dto.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户查询请求类，继承自PageRequest，用于定义用户信息的查询请求参数
 * @author Hzh
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    /**
     * 用户id，用于查询特定用户的详细信息
     */
    private Long id;

    /**
     * 用户昵称，用于按照用户昵称进行查询
     */
    private String userName;

    /**
     * 用户简介，用于查询具有特定简介的用户
     */
    private String userProfile;

    /**
     * 用户角色
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}
