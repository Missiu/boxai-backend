package com.boxai.service;

import com.boxai.model.entity.PostLikes;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Hzh
* @description 针对表【post_likes(帖子点赞信息表)】的数据库操作Service
* @createDate 2024-05-13 19:42:54
*/
public interface PostLikesService extends IService<PostLikes> {
    Boolean doLike(Long userId, Long postId);
    Boolean doPostLikeInner(Long userId, Long postId);
}
