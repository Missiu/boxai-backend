package com.boxai.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boxai.model.domain.Post;
import com.boxai.model.dto.post.PostQueryRequest;
import com.boxai.model.vo.PostVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author Hzh
* @description 针对表【post(帖子)】的数据库操作Service
* @createDate 2024-05-03 18:24:01
*/
public interface PostService extends IService<Post> {
//    查询Post表信息
    QueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest);

    Page<PostVO> getPostVOPage(Page<Post> postPage, HttpServletRequest request);
}
