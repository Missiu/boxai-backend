package com.boxai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boxai.model.entity.PostLikes;

/**
 * @author Hzh
 * @description 针对表【post_likes(帖子点赞信息表)】的数据库操作Mapper
 * @createDate 2024-05-13 19:42:54
 * @Entity generator.entity.PostLikes
 */
public interface PostLikesMapper extends BaseMapper<PostLikes> {
    PostLikes selectByPostIdAndUserId(Long postId, Long userId);
}




