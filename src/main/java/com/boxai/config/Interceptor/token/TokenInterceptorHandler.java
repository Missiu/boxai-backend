package com.boxai.config.Interceptor.token;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.boxai.common.base.R;
import com.boxai.common.base.ReturnCode;
import com.boxai.common.constants.CommonConstant;
import com.boxai.common.constants.RedisKeyConstant;
import com.boxai.model.vo.user.UserInfoVO;
import com.boxai.utils.threadlocal.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Token拦截器，用于校验请求的token，并将通过校验的用户信息保存到ThreadLocal中。
 */
@Slf4j
public class TokenInterceptorHandler implements HandlerInterceptor {
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 构造函数，注入StringRedisTemplate用于操作Redis。
     * @param stringRedisTemplate Redis模板
     */
    public TokenInterceptorHandler(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 在请求处理之前进行调用。
     * 主要用于校验token的有效性，如果无效则阻止请求继续访问。
     *
     * @param request  HttpServletRequest对象，代表客户端的HTTP请求
     * @param response HttpServletResponse对象，代表服务器对客户端的响应
     * @param handler  将要执行的处理器对象
     * @return boolean 如果校验通过，返回true，请求继续；否则返回false，请求终止
     * @throws Exception 可能抛出的异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        log.info("TokenInterceptorHandler: preHandle method called");
        log.info("Request URI: {}", request.getRequestURI());
        String token = null;

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            // 设置预检请求的响应头
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
        token = request.getHeader("Token");
        if (token == null){
            token = request.getHeader(CommonConstant.AUTHORIZATION);
        }
            // 如果token为空，返回401未授权并设置自定义响应消息
        if (StringUtils.isBlank(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSONUtil.toJsonStr(R.fail(ReturnCode.ACCESS_UNAUTHORIZED)));
            return false;
        }

        // 构造Redis中token对应的key
        String key = RedisKeyConstant.USER_LOGIN_TOKEN + token;
        // 从Redis获取用户信息
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(key);

        // 如果用户信息为空，返回401未授权并设置自定义响应消息
        if (userMap.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSONUtil.toJsonStr(R.fail(ReturnCode.ACCESS_UNAUTHORIZED)));
            return false;
        }

        // 将hash映射填充到UserInfoVO对象
        UserInfoVO userInfoVO = BeanUtil.fillBeanWithMap(userMap, new UserInfoVO(), false);
        // 将用户信息保存到ThreadLocal中，以便在后续的请求处理中访问
        UserHolder.saveUser(userInfoVO);

        // 刷新token在Redis中的有效时间
        stringRedisTemplate.expire(key, RedisKeyConstant.USER_LOGIN_TOKEN_TTL, TimeUnit.MINUTES);

        // 校验通过，允许请求继续
        return true;
    }


}
