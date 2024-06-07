package com.boxai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxai.common.base.ReturnCode;
import com.boxai.common.constants.RedisKeyConstant;
import com.boxai.exception.customize.CustomizeReturnException;
import com.boxai.mapper.PostFavoritesMapper;
import com.boxai.mapper.PostsMapper;
import com.boxai.model.dto.post.PostQueryDTO;
import com.boxai.model.dto.postfavorite.FavoriteQueryDTO;
import com.boxai.model.entity.DataCharts;
import com.boxai.model.entity.PostFavorites;
import com.boxai.model.entity.Posts;
import com.boxai.model.entity.Users;
import com.boxai.model.page.PageModel;
import com.boxai.model.vo.post.PostListQueryVO;
import com.boxai.model.vo.postfavorite.PostFavorListQueryVO;
import com.boxai.service.DataChartsService;
import com.boxai.service.PostFavoritesService;
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

import static com.boxai.common.constants.RedisKeyConstant.POST_FAVORITES_COUNT_TTL;

/**
 * @author Hzh
 * @description 针对表【post_favorites(帖子收藏信息表)】的数据库操作Service实现
 * @createDate 2024-05-13 19:42:54
 */
@Service
public class PostFavoritesServiceImpl extends ServiceImpl<PostFavoritesMapper, PostFavorites>
        implements PostFavoritesService {

    @Resource
    private DataChartsService dataChartsService;
    @Resource
    private PostsMapper postsMapper;
    @Resource
    private UsersService usersService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Boolean doFavorite(Long userId, Long postId) {
        DataCharts chart = dataChartsService.getById(postId);
        if (chart == null) {
            throw new CustomizeReturnException(ReturnCode.INSTANTIATION_EXCEPTION, "id查询的实体不存在");
        }
        synchronized (String.valueOf(userId).intern()) {
            return doPostFavoriteInner(userId, postId);
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeReturnException.class)
    public Boolean doPostFavoriteInner(Long userId, Long postId) {
        // 查询对应收藏数据
        PostFavorites oldData = null;
        boolean result = false;
        // 先查缓存
        String key = RedisKeyConstant.POST_FAVORITES_COUNT + userId + "+" + postId;
        String oldDataByCache = stringRedisTemplate.opsForValue().get(key);
        JSONObject jsonObject = JSONUtil.parseObj(oldDataByCache);
        oldData = jsonObject.toBean(PostFavorites.class);
        // 再查数据库
        if (oldData.getId() == null) {
            // 数据库也没有说明没点赞
            oldData = baseMapper.selectByPostIdAndUserId( postId,userId);
        }
        // 如果存在则用户已经收藏,取消收藏
        if (oldData != null) {
            result = this.removeById(oldData.getId());
            if (result) {
                // 点赞数-1
                // 先更新数据库
                result = postsMapper.updateCancelFavorites(postId);
                // 再更新缓存
                if (result) {
                    stringRedisTemplate.delete(key);
                    result = false;
                }
            } else {
                throw new CustomizeReturnException(ReturnCode.INSTANTIATION_EXCEPTION, "取消收藏失败");
            }
        } else {
            // 如果不存在则用户未收藏
            PostFavorites newData = new PostFavorites();
            newData.setPostId(postId);
            newData.setUserId(userId);
            result = this.save(newData);
            if (result) {
                // 点赞数+1
                // 先更新数据库
                result = postsMapper.updateFavorites(postId);
                // 再更新缓存
                if (result) {
                    stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(newData), POST_FAVORITES_COUNT_TTL, TimeUnit.MINUTES);
                }
            } else {
                throw new CustomizeReturnException(ReturnCode.INSTANTIATION_EXCEPTION, "收藏失败");
            }
        }
        UserHolder.removeUser();
        return result;
    }

    @Override
    public Page<PostFavorListQueryVO> listFavorite(PageModel pageModel) {
        Long userId = UserHolder.getUser().getId();
        QueryWrapper<PostFavorites> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(userId != null, "user_id", userId);
        List<PostFavorites> postFavorites = baseMapper.selectList(queryWrapper);
        // 提取出每个chartId
        Set<Long> chartIdSet = postFavorites.stream().map(PostFavorites::getPostId).collect(Collectors.toSet());
        // 根据chart信息查询
        Map<Long, List<DataCharts>> chartInfoList = dataChartsService.listByIds(chartIdSet).stream()
                .collect(Collectors.groupingBy(DataCharts::getId));
        List<PostFavorListQueryVO> postListQueryVOS = postFavorites.stream().map(favorites -> {
                    PostFavorListQueryVO postVO = new PostFavorListQueryVO();
                    // 提取出作品简介
                    Posts posts = postsMapper.selectPostById(favorites.getPostId());
                    // 赋值
                    BeanUtil.copyProperties(posts, postVO);
                    // 提取出图表信息
                    List<DataCharts> dataCharts = chartInfoList.get(favorites.getPostId());
                    if (dataCharts != null && !dataCharts.isEmpty()) {
                        DataCharts dataChart = dataCharts.get(0); // 假设一个chartId对应唯一图表，直接取第一个
                        // 提取出每个作者信息
                        Users user = usersService.getById(dataChart.getUserId());
                        postVO.setGenerationName(dataChart.getGenerationName());
                        postVO.setCodeProfileDescription(dataChart.getCodeProfileDescription());
                        postVO.setAvatarUrl(user.getAvatarUrl());
                        postVO.setNickname(user.getNickname());
                    }
                    return postVO;
                })
                .collect(Collectors.toList());
        Page<PostFavorListQueryVO> postFavoritesPage = pageModel.build();
        postFavoritesPage.setRecords(postListQueryVOS);
        UserHolder.removeUser();
        return postFavoritesPage;
    }


}




