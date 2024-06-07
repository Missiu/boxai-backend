package com.boxai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxai.model.dto.post.PostAddDTO;
import com.boxai.model.dto.post.PostQueryDTO;
import com.boxai.model.entity.Posts;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boxai.model.page.PageModel;
import com.boxai.model.vo.post.PostListQueryVO;

/**
* @author Hzh
* @description 针对表【posts(帖子信息表)】的数据库操作Service
* @createDate 2024-05-13 19:42:54
*/
public interface PostsService extends IService<Posts> {

    Boolean sharePosts(PostAddDTO postAddDTO, String token);

    Boolean deletePosts(Long id, String token);

    Page<PostListQueryVO> listPosts(PostQueryDTO postQueryDTO, PageModel pageModel);
}
