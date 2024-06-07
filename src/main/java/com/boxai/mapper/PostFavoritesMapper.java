package com.boxai.mapper;

import com.boxai.model.entity.PostFavorites;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author Hzh
* @description 针对表【post_favorites(帖子收藏信息表)】的数据库操作Mapper
* @createDate 2024-05-13 19:42:54
* @Entity generator.entity.PostFavorites
*/
public interface PostFavoritesMapper extends BaseMapper<PostFavorites> {
    PostFavorites selectByPostIdAndUserId (Long postId, Long userId);
}




