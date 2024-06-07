package com.boxai.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxai.common.base.R;
import com.boxai.model.dto.post.PostAddDTO;
import com.boxai.model.dto.post.PostQueryDTO;
import com.boxai.model.page.PageModel;
import com.boxai.model.vo.post.PostListQueryVO;
import com.boxai.service.PostsService;
import com.boxai.service.impl.PostsServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/post")
public class PostController {
    @Resource
    private PostsService postsService;
    // 分享帖子
    @PostMapping("/share")
    public R<Boolean> sharePosts(@RequestBody PostAddDTO postAddDTO, HttpServletRequest request) {
        // 获取传入token
        String token = request.getHeader("Authorization");
        return R.ok(postsService.sharePosts(postAddDTO, token));
    }
    // 删除帖子
    @PostMapping("/delete")
    public R<Boolean> deletePosts(Long id, HttpServletRequest request) {
        // 获取传入token
        String token = request.getHeader("Authorization");
        return R.ok(postsService.deletePosts(id, token));
    }
    // 分页查询帖子
    @PostMapping("/list/page")
    public R<Page<PostListQueryVO>> listPosts(@RequestBody PostQueryDTO postQueryDTO, PageModel pageModel) {
        return R.ok(postsService.listPosts(postQueryDTO, pageModel));
    }
}
