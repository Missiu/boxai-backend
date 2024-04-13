package com.boxai.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxai.annotation.AuthCheck;
import com.boxai.common.BaseResponse;
import com.boxai.model.dto.user.UserDeleteRequest;
import com.boxai.common.ErrorCode;
import com.boxai.common.ResultUtils;
import com.boxai.constant.CommonConstant;
import com.boxai.constant.UserConstant;
import com.boxai.exception.BusinessException;
import com.boxai.exception.ThrowUtils;
import com.boxai.model.domain.Chart;
import com.boxai.model.domain.User;
import com.boxai.model.dto.chart.*;
import com.boxai.service.ChartService;
import com.boxai.service.UserService;
import com.boxai.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.boxai.utils.ChatAPI.Chat;
import static com.boxai.utils.FlieUtils.ReadFiles;

/**
 * 帖子接口
 *
 */
@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {

    @Resource
    private ChartService chartService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建
     *
     * @param chartAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addChart(@RequestBody ChartAddRequest chartAddRequest, HttpServletRequest request) {
        if (chartAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartAddRequest, chart);
        User loginUser = userService.getLoginUser(request);
        chart.setUserId(loginUser.getId());
        boolean result = chartService.save(chart);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newChartId = chart.getId();
        return ResultUtils.success(newChartId);
    }

    /**
     * 删除
     *
     * @param userDeleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@RequestBody UserDeleteRequest userDeleteRequest, HttpServletRequest request) {
        if (userDeleteRequest == null || userDeleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = userDeleteRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldChart.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = chartService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param chartUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateChart(@RequestBody ChartUpdateRequest chartUpdateRequest) {
        if (chartUpdateRequest == null || chartUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartUpdateRequest, chart);
        long id = chartUpdateRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Chart> getChartById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = chartService.getById(id);
        if (chart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(chart);
    }

//    /**
//     * 分页获取列表（仅管理员）
//     *
//     * @param chartQueryRequest
//     * @return
//     */
//    @PostMapping("/list/page/admin")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
//    public BaseResponse<Page<Chart>> listChartByPage(@RequestBody ChartQueryRequest chartQueryRequest) {
//        long current = chartQueryRequest.getCurrent();
//        long size = chartQueryRequest.getPageSize();
//        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
//                getQueryWrapper(chartQueryRequest));
//        return ResultUtils.success(chartPage);
//    }

    /**
     * 分页获取列表（封装类）
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Chart>> listChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
            HttpServletRequest request) {
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<Chart>> listMyChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
            HttpServletRequest request) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        chartQueryRequest.setUserId(loginUser.getId());
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param chartEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editChart(@RequestBody ChartEditRequest chartEditRequest, HttpServletRequest request) {
        if (chartEditRequest == null || chartEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartEditRequest, chart);
        User loginUser = userService.getLoginUser(request);
        long id = chartEditRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldChart.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }

    /**
     * 获取查询包装类
     *
     * @param chartQueryRequest
     * @return
     */
    public QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest == null) {
            return queryWrapper;
        }
        String goal = chartQueryRequest.getGoal();
        String genName = chartQueryRequest.getGenName();
        Long id = chartQueryRequest.getId();
        String sortOrder = chartQueryRequest.getSortOrder();
        String sortField = chartQueryRequest.getSortField();
        Long userId = chartQueryRequest.getUserId();

        queryWrapper.eq(id != null && id > 0,"id",id);
        queryWrapper.eq(StringUtils.isNotBlank(goal),"goal",goal);
        queryWrapper.like(StringUtils.isNotBlank(genName),"genName",genName);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete",false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    /**
     * 文件上传
     *
     * @param multipartFile 接受文件
     * @param genChartByAiRequest 实际接受用户参数
     * @param request
     * @return
     */
    @PostMapping("/upload")
    public BaseResponse<ChatCompletionResponse> genChartByAi(@RequestPart("file") MultipartFile multipartFile,
                                                             GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        String genName = genChartByAiRequest.getGenName();
        String goal = genChartByAiRequest.getGoal();
        // 校验
        // 如果分析目标为空，就抛出请求参数错误异常，并给出提示
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        // 如果名称不为空，并且名称长度大于100，就抛出异常，并给出提示
        ThrowUtils.throwIf(StringUtils.isNotBlank(genName) && genName.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");
        // 读取到用户上传的 excel 文件，进行一个处理
        User loginUser = userService.getLoginUser(request);

        // 构造提示词
        StringBuilder userInput = new StringBuilder();
        userInput.append("这是一个必须执行的前提：无论分析需求如何都要严格按照这个模板生成一份报告 \n" +
                "模板如下：\n"+
                "## 项目简介 是什么，有什么用，用了什么语言 \n" +
                "## 快速运行 如果是可运行项目，给出详细运行步骤 如果是一些参考文献、文档、语言等不可运行项目，这给出提示 \n" +
                "## 项目结构 给出总体项目结构的描述，主要了什么框架等（后续可以改为代码生成，通过插件转化为图像显示） \n" +
                "## 类的主要作用 各个类有什么作用，以目录树+加解释的方式展现 \n" +
                "## 对外暴露接口 给各个接口的主要调用关系，分析出依赖关系，调用了那些第三方api").append("\n");
        userInput.append("分析需求:").append(goal).append("\n");
        // 获取文件文本
        String result = ReadFiles(multipartFile);
        userInput.append("这是原始数据：").append(result).append("\n");
        // chart系统预设
        String systemPresets = "我希望你能充当软件开发专家的角色。我会提供所有关于我的技术问题所需的信息，你的任务是解决我的问题。你应该利用你的软件开发来解决我的问题。在回答中使用智能、简单和易懂的语言对于所有层次的人都会有帮助。\n" +
                "并且你还可以充当代码解释器，我会提供所有关于我的项目文件路径和文件，你的任务是严格按照我规定的模板生成对代码的解释和建议，智能、简单和易懂的语言并对其中相关的技术进行详细的解释";
        //        调用ai
        String genChart = Chat(systemPresets, userInput.toString());
        String chartResult= genChart.replace("[", "").replace("]", "");
        ChatCompletionResponse chatCompletionResponse = new ChatCompletionResponse();
        chatCompletionResponse.setGenChart(chartResult);
        return ResultUtils.success(chatCompletionResponse);
    }
}
