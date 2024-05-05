package com.boxai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxai.common.constant.CommonConstant;
import com.boxai.mapper.PostMapper;
import com.boxai.model.domain.Post;
import com.boxai.model.domain.Result;
import com.boxai.model.domain.User;
import com.boxai.model.dto.post.PostQueryRequest;
import com.boxai.model.vo.PostVO;
import com.boxai.service.PostService;
import com.boxai.service.ResultService;
import com.boxai.service.UserService;
import com.boxai.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Hzh
 * @description 针对表【post(帖子)】的数据库操作Service实现
 * @createDate 2024-05-03 18:24:01
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
        implements PostService {
    @Resource
    private UserService userService;
    @Resource
    private ResultService resultService;

    @Override
    public QueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest) {

        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        if (postQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = postQueryRequest.getSearchText();
        String title = postQueryRequest.getTitle();
        String content = postQueryRequest.getContent();
        Long userId = postQueryRequest.getUserId();
        String sortField = postQueryRequest.getSortField();
        String sortOrder = postQueryRequest.getSortOrder();
        Long resultId = postQueryRequest.getResultId();
        // 1. 关联查询用户信息

        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("title", searchText).or().like("content", searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.eq("isDelete", false);
        if (userId != null && userId > 0){
            queryWrapper.eq("userId", userId);
        }
        if (resultId != null && resultId > 0){
            queryWrapper.eq("resultId", resultId);
        }
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;

    }

    @Override
    public Page<PostVO> getPostVOPage(Page<Post> postPage, HttpServletRequest request) {
//        获取页面中的记录列表
        List<Post> postList = postPage.getRecords();
        // 获取页面信息
        Page<PostVO> postVOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        if (CollectionUtils.isEmpty(postList)) {
            return postVOPage;
        }
        // 将postList列表中每个Post对象的userId字段提取出来
        Set<Long> userIdSet = postList.stream().map(Post::getUserId).collect(Collectors.toSet());
        // 根据用户ID将用户列表分组
        // 根据resultIdSet查询所有的Result实体
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 将postList列表中每个Post对象的resultId字段提取出来
        Set<Long> resultIdSet = postList.stream().map(Post::getResultId).collect(Collectors.toSet());
        // 根据resultId,查询result数据，把数据和resultIdSet里的resultId进行匹配对应，形成一个map集合
        Map<Long, List<Result>> resultIdResultListMap = resultService.listByIds(resultIdSet).stream()
                .collect(Collectors.groupingBy(Result::getId));

        List<PostVO> postVOList = postList.stream().map(post -> {
            PostVO postVO = new PostVO();
            BeanUtils.copyProperties(post, postVO);
            // 假设从userIdUserListMap中获取用户信息设置到PostVO
            List<User> users = userIdUserListMap.get(post.getUserId());
            if (users != null && !users.isEmpty()) {
                User user = users.get(0); // 假设一个userId对应唯一用户，直接取第一个
                postVO.setUserAvatar(user.getUserAvatar());
                postVO.setUserName(user.getUserName());
            }

            // 修改前：List<Result> results = resultIdResultListMap.get(post.getResultId());
            // 修改后：添加空值检查
            List<Result> results = resultIdResultListMap.get(post.getResultId());
            if (results != null && !results.isEmpty()) {
                Result result = results.get(0); // 假设一个resultId对应唯一结果，直接取第一个
                postVO.setGenName(result.getGenName());
                postVO.setCodeProfile(result.getCodeProfile());
            }

            return postVO;
        }).collect(Collectors.toList());
        postVOPage.setRecords(postVOList);
        return postVOPage;
    }




}




