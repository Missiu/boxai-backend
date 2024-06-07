package com.boxai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxai.model.dto.postfavorite.FavoriteQueryDTO;
import com.boxai.model.entity.PostFavorites;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boxai.model.page.PageModel;
import com.boxai.model.vo.postfavorite.PostFavorListQueryVO;

/**
* @author Hzh
* @description 针对表【post_favorites(帖子收藏信息表)】的数据库操作Service
* @createDate 2024-05-13 19:42:54
*/
public interface PostFavoritesService extends IService<PostFavorites> {

    Boolean doFavorite(Long userId, Long postId);
    Boolean doPostFavoriteInner(Long userId, Long postId);

    Page<PostFavorListQueryVO> listFavorite(PageModel pageModel);
}
