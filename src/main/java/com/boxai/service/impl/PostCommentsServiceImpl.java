package com.boxai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxai.model.entity.PostComments;
import com.boxai.service.PostCommentsService;
import com.boxai.mapper.PostCommentsMapper;
import org.springframework.stereotype.Service;

/**
* @author Hzh
* @description 针对表【post_comments(帖子评论信息表)】的数据库操作Service实现
* @createDate 2024-05-13 19:42:53
*/
@Service
public class PostCommentsServiceImpl extends ServiceImpl<PostCommentsMapper, PostComments>
    implements PostCommentsService{

}




