package com.boxai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boxai.model.entity.Posts;

/**
 * @author Hzh
 * @description 针对表【posts(帖子信息表)】的数据库操作Mapper
 * @createDate 2024-05-13 19:42:54
 * @Entity generator.entity.Posts
 */
public interface PostsMapper extends BaseMapper<Posts> {
    Boolean deletePostById(Long id);
    Boolean updateCurrentTime(Long id);
    Boolean updateCancelFavorites(Long id);
    Boolean updateCancelLikes(Long id);
    Boolean updateLikes(Long id);
    Boolean updateFavorites(Long id);
    Posts selectPostById (Long id);

}




