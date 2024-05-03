package com.boxai.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxai.model.domain.Post;
import com.boxai.model.domain.PostFavour;
import org.apache.ibatis.annotations.Param;

/**
* @author Hzh
* @description 针对表【post_favour(帖子收藏)】的数据库操作Mapper
* @createDate 2024-05-03 18:04:14
* @Entity com.boxai.model.domain.PostFavour
*/
public interface PostFavourMapper extends BaseMapper<PostFavour> {
    Page<Post> listFavourPostByPage(IPage<Post> page, @Param(Constants.WRAPPER) Wrapper<Post> queryWrapper,
                                    long favourUserId);
}




