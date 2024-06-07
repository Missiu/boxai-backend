package com.boxai.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxai.common.base.R;
import com.boxai.common.base.ReturnCode;
import com.boxai.exception.customize.CustomizeReturnException;
import com.boxai.model.dto.postfavorite.FavoriteAddDTO;
import com.boxai.model.page.PageModel;
import com.boxai.model.vo.postfavorite.PostFavorListQueryVO;
import com.boxai.service.PostFavoritesService;
import com.boxai.utils.threadlocal.UserHolder;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {
    @Resource
    private PostFavoritesService postFavoritesService;
    @PostMapping("/add")
    public R<Boolean> doFavorite(@RequestBody FavoriteAddDTO favoriteAddDTO){
        if (favoriteAddDTO.getPostId() <= 0){
            throw new CustomizeReturnException(ReturnCode.REQUEST_REQUIRED_PARAMETER_IS_EMPTY);
        }
        if (favoriteAddDTO.getUserId() <= 0){
            favoriteAddDTO.setUserId(UserHolder.getUser().getId());
        }
        return R.ok(postFavoritesService.doFavorite(favoriteAddDTO.getUserId(), favoriteAddDTO.getPostId()));
    }
    // 收藏列表
    @GetMapping("/list/page")
    public R<Page<PostFavorListQueryVO>> listFavorite( PageModel pageModel){
        return R.ok(postFavoritesService.listFavorite(pageModel));
    }
}
