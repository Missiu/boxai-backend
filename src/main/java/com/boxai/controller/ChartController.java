package com.boxai.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxai.annotation.AuthCheck;
import com.boxai.common.BaseResponse;
import com.boxai.common.ErrorCode;
import com.boxai.common.ResultResponse;
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
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static com.boxai.utils.FlieUtils.readFile;
import static com.boxai.utils.FlieUtils.readFiles;

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
     * add chart
     *
     * @param multipartFile 接受文件
     * @param genChartByAiRequest 实际接受用户参数
     * @param request
     * @return
     */
    @PostMapping("/genChart")
    public BaseResponse<ChatCompletionResponse> genChartByAi(@RequestParam("file") MultipartFile multipartFile,
                                                             GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        String genName = genChartByAiRequest.getGenName();
        String goal = genChartByAiRequest.getGoal();
        // 校验
        // 如果分析目标为空，就抛出请求参数错误异常，并给出提示
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        if (Objects.equals(genName, "")){
            genName = multipartFile.getName();
        }
        User loginUser = userService.getLoginUser(request);
        // 获取文件文本
        String result = readFile(multipartFile);
        String chartResult = chartService.GenChart(goal,result);

        Chart chart = new Chart();
        chart.setGoal(goal);
        chart.setGenName(genName);
//        chart.setGenChart();
        chart.setGenResult(chartResult);
        chart.setChatData(result);
        chart.setUserId(loginUser.getId());
        chartService.save(chart);
        ChatCompletionResponse chatCompletionResponse = new ChatCompletionResponse();
        chatCompletionResponse.setGenChart(chartResult);
        return ResultResponse.success(chatCompletionResponse);
    }

//    @ApiOperation(value = "上传", notes = "上传")
//    @ApiImplicitParams({
//            @ApiImplicitParam(paramType = "form", name = "file", value = "文件对象", required = true, dataType = "__file"),
//            @ApiImplicitParam(paramType = "form", name = "files", value = "文件数组", allowMultiple = true, dataType = "__file")
//    })

//    @PostMapping("/genFilesChart")
@ApiImplicitParam(paramType = "form", name = "files", value = "文件数组", allowMultiple = true, dataType = "__file")
@RequestMapping(value = "/genFilesChart", method = RequestMethod.POST, headers = "content-type=multipart/form-data")
    public BaseResponse<ChatCompletionResponse> genFilesChartByAi(@RequestParam("files") MultipartFile[] multipartFiles, GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        String genName = genChartByAiRequest.getGenName();
        String goal = genChartByAiRequest.getGoal();
        User loginUser = userService.getLoginUser(request);

//        List<MultipartFile> files = genChartByAiRequest.getMultipartFiles();


        // 读取文件内容
        String data = readFiles(multipartFiles);

        // 生成图表
        String chartResult = chartService.GenChart(goal, data);

        // 构建 Chart 对象
        Chart chart = new Chart();
        chart.setGoal(goal);
        chart.setGenName(genName);
        chart.setGenResult(chartResult);
        chart.setChatData(data);
        chart.setUserId(loginUser.getId());
        chartService.save(chart);

        // 构建响应对象
        ChatCompletionResponse chatCompletionResponse = new ChatCompletionResponse();
        chatCompletionResponse.setGenChart(chartResult);

        return ResultResponse.success(chatCompletionResponse);
    }

//    @PostMapping("/postFiles")
//    public BaseResponse<String> postFiles(@RequestParam("files") List<MultipartFile> multipartFiles) {
//        // 读取文件内容
//        String data = readFiles(multipartFiles);
//        return ResultResponse.success(data);
//    }


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
        Long id = chartQueryRequest.getId();
        String genName = chartQueryRequest.getGenName();
        String goal = chartQueryRequest.getGoal();
        Long userId = chartQueryRequest.getUserId();
        int current = chartQueryRequest.getCurrent();
        int pageSize = chartQueryRequest.getPageSize();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();

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
     * 删除
     *
     * @param chartDeleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@RequestBody ChartDeleteRequest chartDeleteRequest, HttpServletRequest request) {
        if (chartDeleteRequest == null || chartDeleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = chartDeleteRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldChart.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = chartService.removeById(id);
        return ResultResponse.success(b);
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
        return ResultResponse.success(result);
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
        return ResultResponse.success(chartPage);
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
        if (chartEditRequest == null || chartEditRequest.getId() <= 0 || chartEditRequest.getUserId() <=0) {
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
        return ResultResponse.success(result);
    }





}
