package com.boxai.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxai.common.enums.ErrorCode;
import com.boxai.exception.BusinessException;
import com.boxai.exception.ThrowUtils;
import com.boxai.model.domain.Chart;
import com.boxai.model.domain.User;
import com.boxai.model.dto.aigc.*;
import com.boxai.model.dto.common.BaseResponse;
import com.boxai.model.dto.common.ResultResponse;
import com.boxai.service.ChartService;
import com.boxai.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.boxai.common.enums.ErrorCode.*;
import static com.boxai.utils.FileUtils.readFile;
import static com.boxai.utils.FileUtils.readFiles;


/**
 * @author Hzh
 */
@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {
    @Resource
    private ChartService chartService;
    @Resource
    private UserService userService;
    /**
     * 通过AI生成图表
     *
     * @param multipartFile 用户上传的文件
     * @param chartGenRequest 包含生成图表请求信息的对象，如生成名称和目标
     * @param request 用户的请求对象，用于获取登录用户信息
     * @return 返回一个包含生成图表结果的响应对象
     */
    @PostMapping("/FileAIGC")
    public BaseResponse<ChartContentResponse> FileAIGC(@RequestParam("file") MultipartFile multipartFile,
                                                       ChartGenRequest chartGenRequest, HttpServletRequest request) {
        // 从请求中获取生成名称和目标
        String genName = chartGenRequest.getGenName();
        String goal = chartGenRequest.getGoal();

        // 检查目标参数是否为空，如果为空则抛出错误
        ThrowUtils.throwIf(StringUtils.isBlank(goal), PARAMS_ERROR, "目标为空");

        // 如果生成名称为空，则使用上传文件的名称作为生成名称
        if (genName == null || genName.isEmpty()) {
            genName = multipartFile.getOriginalFilename();
        }
        // 获取登录用户信息
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(NOT_LOGIN_ERROR,"用户未登录");
        }

        // 读取上传的文件内容
        String readFile = null;
        try {
            readFile = readFile(multipartFile);
        } catch (IOException e) {
            throw new BusinessException(IO_ERROR,"文件读取失败");
        }
        // 使用上传文件的内容生成图表
        String chartResult = chartService.genChart(goal,readFile);
        if (chartResult == null){
            throw new BusinessException(GEN_ERROR,"图表生成失败");
        }

        // 创建并设置图表信息
        Chart chart = new Chart();
        chart.setGoal(goal);
        chart.setGenName(genName);
        chart.setGenResult(chartResult);
        chart.setRawData(readFile);
        chart.setUserId(loginUser.getId());
        // 保存图表信息
        chartService.save(chart);
        // 构建并返回图表生成的响应结果
        ChartContentResponse chartContentResponse = new ChartContentResponse();
        chartContentResponse.setGenResult(chartResult);
        return ResultResponse.success(chartContentResponse);
    }

/**
 * 使用AI生成图表的接口。
 *
 * @param multipartFiles 上传的文件数组，使用multipart/form-data格式。
 * @param chartGenRequest 包含生成图表所需参数的请求对象，如生成名称和目标。
 * @param request HTTP请求对象，用于获取登录用户信息。
 * @return BaseResponse<ChartContentResponse> 包含生成的图表结果或错误信息的响应对象。
 */
@ApiImplicitParam(paramType = "form", name = "files", value = "文件数组", allowMultiple = true, dataType = "__file")
@RequestMapping(value = "/FilesAIGC", method = RequestMethod.POST, headers = "content-type=multipart/form-data")
public BaseResponse<ChartContentResponse> FilesAIGC(
        @RequestParam("files") MultipartFile[] multipartFiles,
        ChartGenRequest chartGenRequest,
        HttpServletRequest request) {

    // 检查上传的文件和请求参数是否完整
    if (multipartFiles == null || multipartFiles.length == 0) {
        throw new BusinessException(PARAMS_ERROR,"上传文件为空");
    }
    if (chartGenRequest == null || chartGenRequest.getGenName() == null || chartGenRequest.getGoal() == null) {
        throw new BusinessException(PARAMS_ERROR,"参数为空");
    }

    // 验证用户是否已登录
    User loginUser = userService.getLoginUser(request);
    if (loginUser == null) {
        throw new BusinessException(NOT_LOGIN_ERROR,"用户未登录");
    }

    try {
        // 综合处理上传的文件，生成图表
        String data = readFiles(multipartFiles); // 读取文件内容
        String chartResult = chartService.genChart(chartGenRequest.getGoal(), data); // 生成图表

        // 保存生成的图表信息到数据库
        Chart chart = new Chart();
        chart.setGoal(chartGenRequest.getGoal());
        chart.setGenName(chartGenRequest.getGenName());
        chart.setGenResult(chartResult);
        chart.setRawData(data);
        chart.setUserId(loginUser.getId());
        chartService.save(chart);

        // 构建并返回处理结果
        ChartContentResponse chartContentResponse = new ChartContentResponse();
        chartContentResponse.setGenResult(chartResult);

        return ResultResponse.success(chartContentResponse);
    } catch (Exception e) {
        // 异常处理，返回错误信息
        throw new BusinessException(SYSTEM_ERROR,"系统错误");
    }
}

    /**
     * 删除图表信息
     *
     * @param chartDeleteRequest 包含要删除的图表id的请求对象
     * @param request 用户的请求对象，用于获取登录用户信息
     * @return 返回一个基础响应对象，包含操作是否成功的布尔值
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@RequestBody ChartDeleteRequest chartDeleteRequest, HttpServletRequest request) {
        // 验证请求参数的合法性
        if (chartDeleteRequest == null || chartDeleteRequest.getId() <= 0) {
            throw new BusinessException(PARAMS_ERROR);
        }
        // 获取登录用户信息
        User user = userService.getLoginUser(request);
        long id = chartDeleteRequest.getId();

        // 校验图表是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);

        // 判断用户是否有权限删除图表
        if (!oldChart.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 执行删除操作
        boolean b = chartService.removeById(id);
        return ResultResponse.success(b);
    }

    /**
     * 更新图表信息
     *
     * @param chartUpdateRequest 包含图表更新信息的请求对象，不能为null且id必须大于0。
     * @return 返回操作结果，成功返回true，失败返回false。
     * @throws BusinessException 如果请求对象为null或id不合法，抛出业务异常。
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateChart(@RequestBody ChartUpdateRequest chartUpdateRequest) {
        // 校验请求对象是否为null或id是否合法
        if (chartUpdateRequest == null || chartUpdateRequest.getId() <= 0) {
            throw new BusinessException(PARAMS_ERROR);
        }
        Chart chart = new Chart();
        // 使用BeanUtils复制属性，从请求对象到图表实体
        BeanUtils.copyProperties(chartUpdateRequest, chart);
        long id = chartUpdateRequest.getId();
        // 判断图表是否存在
        Chart oldChart = chartService.getById(id);
        // 如果不存在，抛出未找到异常
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 更新图表信息
        boolean result = chartService.updateById(chart);
        // 返回更新结果
        return ResultResponse.success(result);
    }

    /**
     * 分页查询用户图表列表
     *
     * @param chartQueryRequest 包含图表查询条件的请求体，不可为null
     * @param request 用户的HTTP请求，用于获取登录用户信息
     * @return 返回图表分页列表的响应体，包含查询结果和页码信息
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Chart>> listMyChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
                                                       HttpServletRequest request) {
        // 校验查询请求体是否为null
        if (chartQueryRequest == null) {
            throw new BusinessException(PARAMS_ERROR);
        }
        // 获取登录用户信息，并设置到查询请求中
        User loginUser = userService.getLoginUser(request);
        chartQueryRequest.setUserId(loginUser.getId());
        // 获取当前页码和每页大小
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制每页大小不超过20条，超过则抛出业务异常
        ThrowUtils.throwIf(size > 20, PARAMS_ERROR);
        // 执行图表分页查询
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                chartService.getQueryWrapper(chartQueryRequest));
        // 返回查询结果的成功响应
        return ResultResponse.success(chartPage);
    }

    /**
     * 编辑图表信息
     *
     * @param chartEditRequest 包含图表编辑信息的请求体，不能为空，且id和userId必须大于0
     * @param request 用户的请求对象，用于获取登录用户信息
     * @return 返回操作结果，成功为true，失败为false
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editChart(@RequestBody ChartEditRequest chartEditRequest, HttpServletRequest request) {
        // 参数校验
        if (chartEditRequest == null || chartEditRequest.getId() <= 0 || chartEditRequest.getUserId() <=0) {
            throw new BusinessException(PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartEditRequest, chart);
        User loginUser = userService.getLoginUser(request);
        long id = chartEditRequest.getId();

        // 判断图表是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);

        // 判断是否有权限编辑，仅本人或管理员可编辑
        if (!oldChart.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 更新图表信息
        boolean result = chartService.updateById(chart);
        return ResultResponse.success(result);
    }
}
