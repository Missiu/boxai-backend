package com.boxai.service;

import com.boxai.model.domain.PostThumb;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boxai.model.domain.User;

/**
* @author Hzh
* @description 针对表【post_thumb(帖子点赞)】的数据库操作Service
* @createDate 2024-05-03 18:04:14
*/
public interface PostThumbService extends IService<PostThumb> {
    int doPostThumb(long postId, User loginUser);

    int doPostThumbInner(long userId, long postId);
}
