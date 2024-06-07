package com.boxai.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxai.common.base.ReturnCode;
import com.boxai.exception.customize.CustomizeReturnException;
import com.boxai.exception.customize.CustomizeTransactionException;
import com.boxai.mapper.PostsMapper;
import com.boxai.model.dto.post.PostAddDTO;
import com.boxai.model.dto.post.PostQueryDTO;
import com.boxai.model.entity.DataCharts;
import com.boxai.model.entity.Posts;
import com.boxai.model.entity.Users;
import com.boxai.model.page.PageModel;
import com.boxai.model.vo.post.PostListQueryVO;
import com.boxai.model.vo.user.UserInfoVO;
import com.boxai.service.DataChartsService;
import com.boxai.service.PostsService;
import com.boxai.service.UsersService;
import com.boxai.utils.threadlocal.UserHolder;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.boxai.common.constants.RedisKeyConstant.POST_SHARE_COUNT;
import static com.boxai.common.constants.RedisKeyConstant.POST_SHARE_COUNT_TTL;

/**
 * @author Hzh
 * @description 针对表【posts(帖子信息表)】的数据库操作Service实现
 * @createDate 2024-05-13 19:42:54
 */
@Service
public class PostsServiceImpl extends ServiceImpl<PostsMapper, Posts>
        implements PostsService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UsersService usersService;
    @Resource
    private DataChartsService dataChartsService;

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public Boolean sharePosts(PostAddDTO postAddDTO, String token) {
        if (postAddDTO.getContent().isEmpty()) {
            postAddDTO.setContent("分享一个链接");
        }
        // 获取用户信息
        UserInfoVO user = UserHolder.getUser();
        // 插入数据到数据库
        Posts posts = new Posts();
        posts.setContent(postAddDTO.getContent());
        posts.setUserId(user.getId());
        posts.setPostId(postAddDTO.getChartId());
        if (baseMapper.insert(posts) <= 0) {
            // 数据库插入失败
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        // 插入数据到redis(JSON)
        stringRedisTemplate.opsForValue().set(POST_SHARE_COUNT + posts.getId(), JSONUtil.toJsonStr(posts), POST_SHARE_COUNT_TTL, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public Boolean deletePosts(Long id, String token) {
        if (baseMapper.deletePostById(id)) {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(POST_SHARE_COUNT + id))) {
                stringRedisTemplate.delete(POST_SHARE_COUNT + id);
            }
            baseMapper.updateCurrentTime(id);
            return true;
        } else {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = CustomizeTransactionException.class)
    public Page<PostListQueryVO> listPosts(PostQueryDTO postQueryDTO, PageModel pageModel) {
        // 根据post信息查询
        Page<Posts> postsPage = pageModel.build();
        QueryWrapper<Posts> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(postQueryDTO.getContent()), "content", postQueryDTO.getContent());
        queryWrapper.eq(postQueryDTO.getId() != null, "id", postQueryDTO.getId());
        queryWrapper.eq(postQueryDTO.getUserId() != null, "user_id", postQueryDTO.getUserId());
        queryWrapper.eq(postQueryDTO.getPostId() != null, "post_id", postQueryDTO.getPostId());
        baseMapper.selectPage(postsPage, queryWrapper);
        List<Posts> postsList = postsPage.getRecords();
        // 提取出每个userID
        Set<Long> userIdSet = postsList.stream().map(Posts::getUserId).collect(Collectors.toSet());
        // 提取出每个chartId
        Set<Long> chartIdSet = postsList.stream().map(Posts::getPostId).collect(Collectors.toSet());
        // 根据用户ID查询用户信息，并过滤
        Map<Long, List<Users>> userInfoListMap = usersService.listByIds(userIdSet).stream()
                .filter(user -> StringUtils.isBlank(postQueryDTO.getNickname()) || user.getNickname().contains(postQueryDTO.getNickname()))
                .collect(Collectors.groupingBy(Users::getId));
        // 根据chart信息查询
        Map<Long, List<DataCharts>> chartInfoList = dataChartsService.listByIds(chartIdSet).stream()
                .filter(dataCharts -> StringUtils.isBlank(postQueryDTO.getGenerationName()) || dataCharts.getGenerationName().contains(postQueryDTO.getGenerationName())
                        && (StringUtils.isBlank(postQueryDTO.getCodeProfileDescription()) || dataCharts.getCodeProfileDescription().contains(postQueryDTO.getCodeProfileDescription())))
                .collect(Collectors.groupingBy(DataCharts::getId));
        List<PostListQueryVO> postListQueryVOS = postsList.stream().map(post -> {
                    PostListQueryVO postVO = new PostListQueryVO();
                    // 复制数据到VO
                    BeanUtils.copyProperties(post, postVO);
                    List<Users> users = userInfoListMap.get(post.getUserId());
                    if (users != null && !users.isEmpty()) {
                        Users user = users.get(0); // 假设一个userId对应唯一用户，直接取第一个
                        postVO.setAvatarUrl(user.getAvatarUrl());
                        postVO.setNickname(user.getNickname());
                    }
                    List<DataCharts> dataCharts = chartInfoList.get(post.getPostId());
                    if (dataCharts != null && !dataCharts.isEmpty()) {
                        DataCharts dataChart = dataCharts.get(0); // 假设一个chartId对应唯一图表，直接取第一个
                        postVO.setGenerationName(dataChart.getGenerationName());
                        postVO.setCodeProfileDescription(dataChart.getCodeProfileDescription());
                    }
                    return postVO;
                })
                .collect(Collectors.toList());
        Page<PostListQueryVO> postListQueryVOPage = pageModel.build();
        postListQueryVOPage.setRecords(postListQueryVOS);
        postListQueryVOPage.setTotal(postsPage.getTotal());
        postListQueryVOPage.setSize(postsPage.getSize());
        postListQueryVOPage.setCurrent(postsPage.getCurrent());
        return postListQueryVOPage;
    }

}




