package com.boxai.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxai.common.base.R;
import com.boxai.common.base.ReturnCode;
import com.boxai.exception.customize.CustomizeReturnException;
import com.boxai.model.dto.postfavorite.FavoriteAddDTO;
import com.boxai.model.dto.postfavorite.FavoriteQueryDTO;
import com.boxai.model.page.PageModel;
import com.boxai.model.vo.postfavorite.PostFavorListQueryVO;
import com.boxai.service.PostLikesService;
import com.boxai.service.UsersService;
import com.boxai.utils.threadlocal.UserHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/like")
public class LikeController {
    @Resource
    private PostLikesService postLikesService;

    @PostMapping("/add")
    public R<Boolean> doLike(@RequestBody FavoriteAddDTO favoriteAddDTO) {
        if (favoriteAddDTO.getPostId() <= 0) {
            throw new CustomizeReturnException(ReturnCode.REQUEST_REQUIRED_PARAMETER_IS_EMPTY);
        }
        if (favoriteAddDTO.getUserId() <= 0) {
            favoriteAddDTO.setUserId(UserHolder.getUser().getId());
        }
        return R.ok(postLikesService.doLike(favoriteAddDTO.getUserId(), favoriteAddDTO.getPostId()));
    }
}
