package com.boxai.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxai.common.base.ReturnCode;
import com.boxai.common.constants.RedisKeyConstant;
import com.boxai.exception.customize.CustomizeReturnException;
import com.boxai.mapper.PostsMapper;
import com.boxai.model.entity.DataCharts;
import com.boxai.model.entity.PostFavorites;
import com.boxai.model.entity.PostLikes;
import com.boxai.service.DataChartsService;
import com.boxai.service.PostLikesService;
import com.boxai.mapper.PostLikesMapper;
import com.boxai.utils.threadlocal.UserHolder;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import static com.boxai.common.constants.RedisKeyConstant.POST_FAVORITES_COUNT_TTL;
import static com.boxai.common.constants.RedisKeyConstant.POST_LIKES_COUNT_TTL;

/**
* @author Hzh
* @description 针对表【post_likes(帖子点赞信息表)】的数据库操作Service实现
* @createDate 2024-05-13 19:42:54
*/
@Service
public class PostLikesServiceImpl extends ServiceImpl<PostLikesMapper, PostLikes>
    implements PostLikesService {
    @Resource
    private DataChartsService dataChartsService;
    @Resource
    private PostsMapper postsMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public Boolean doLike(Long userId, Long postId) {
        DataCharts chart = dataChartsService.getById(postId);
        if (chart == null) {
            throw new CustomizeReturnException(ReturnCode.INSTANTIATION_EXCEPTION, "id查询的实体不存在");
        }
        synchronized (String.valueOf(userId).intern()) {
            return doPostLikeInner(userId, postId);
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeReturnException.class)
    public Boolean doPostLikeInner(Long userId, Long postId) {
        // 查询对应收藏数据
        PostLikes oldData = null;
        boolean result = false;
        // 先查缓存
        String key = RedisKeyConstant.POST_LIKES_COUNT + userId + "+" + postId;
        String oldDataByCache = stringRedisTemplate.opsForValue().get(key);
        JSONObject jsonObject = JSONUtil.parseObj(oldDataByCache);
        oldData = jsonObject.toBean(PostLikes.class);
        // 再查数据库
        if (oldData.getId() == null) {
            // 数据库也没有说明没点赞
            oldData = baseMapper.selectByPostIdAndUserId(postId,userId);
        }
        // 如果存在则用户已经点赞,取消点赞
        if (oldData != null) {
            result = this.removeById(oldData.getId());
            if (result) {
                // 点赞数-1
                // 先更新数据库
                result = postsMapper.updateCancelLikes(postId);
                // 再更新缓存
                if (result) {
                    stringRedisTemplate.delete(key);
                    // 表示取消点赞
                    result = false;
                }

            } else {
                throw new CustomizeReturnException(ReturnCode.INSTANTIATION_EXCEPTION, "取消点赞失败");
            }
        } else {
            // 如果不存在则用户未收藏
            PostLikes newData = new PostLikes();
            newData.setPostId(postId);
            newData.setUserId(userId);
            result = this.save(newData);
            if (result) {
                // 插入数据库
                // 点赞数+1
                // 先更新
                result = postsMapper.updateLikes(postId);
                // 再更新缓存
                if (result) {
                    stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(newData),POST_LIKES_COUNT_TTL, TimeUnit.MINUTES);
                }
            } else {
                throw new CustomizeReturnException(ReturnCode.INSTANTIATION_EXCEPTION, "点赞失败");
            }
        }
        UserHolder.removeUser();
        return result;
    }
}




