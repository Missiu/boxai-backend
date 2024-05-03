package com.boxai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxai.model.domain.PostComment;
import com.boxai.service.PostCommentService;
import com.boxai.mapper.PostCommentMapper;
import org.springframework.stereotype.Service;

/**
* @author Hzh
* @description 针对表【post_comment(帖子评论)】的数据库操作Service实现
* @createDate 2024-05-03 18:04:13
*/
@Service
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment>
    implements PostCommentService{

}




