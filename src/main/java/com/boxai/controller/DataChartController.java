package com.boxai.controller;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxai.common.base.R;
import com.boxai.common.base.ReturnCode;
import com.boxai.exception.customize.CustomizeReturnException;
import com.boxai.model.dto.datachart.*;
import com.boxai.model.page.PageModel;
import com.boxai.model.vo.datachart.ChartQueryVO;
import com.boxai.model.vo.datachart.UniversalDataChartsVO;
import com.boxai.service.DataChartsService;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/chart")
public class DataChartController {
    @Resource
    private DataChartsService dataChartsService;

    /**
     * 通过文件生成图表
     *
     * @return 返回生成的图表信息
     */
    @PostMapping(value = "/gen/file")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {@Content(
            mediaType = "multipart/form-data",
            schema = @Schema(type = "object"),
            schemaProperties = {
                    @SchemaProperty(
                            name = "file",
                            schema = @Schema(type = "string", format = "binary")
                    ),
                    @SchemaProperty(
                            name = "goalDescription",
                            schema = @Schema(type = "text")
                    ),
                    @SchemaProperty(
                            name = "generationName",
                            schema = @Schema(type = "text")
                    )
            }
    )})
    public R<UniversalDataChartsVO> genFileChart(MultipartFile file,
                                                 String goalDescription, String generationName) {
        ChartCreateDTO chartCreateDTO = new ChartCreateDTO(goalDescription, generationName);
        return R.ok(dataChartsService.generationFileChart(file, chartCreateDTO));
    }

    /**
     * 通过多个文件生成图表
     *
     * @return 返回生成的多个图表信息
     */
    @PostMapping(value = "/gen/multiple")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {@Content(
            mediaType = "multipart/form-data",
            schema = @Schema(type = "object"),
            schemaProperties = {
                    @SchemaProperty(
                            name = "files",
                            schema = @Schema(type = "string", format = "binary")
                    ),
                    @SchemaProperty(
                            name = "goalDescription",
                            schema = @Schema(type = "text")
                    ),
                    @SchemaProperty(
                            name = "generationName",
                            schema = @Schema(type = "text")
                    )
            }
    )})
    public R<UniversalDataChartsVO> genMultipleChart(MultipartFile[] files,
                                                     String goalDescription, String generationName) {
        ChartCreateDTO chartCreateDTO = new ChartCreateDTO(goalDescription, generationName);
        return R.ok(dataChartsService.generationMultipleChart(files, chartCreateDTO));
    }

    /**
     * 通过文件生成单个图表 异步
     *
     * @return 返回生成的图表信息
     */
    @PostMapping(value = "/gen/file/async")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {@Content(
            mediaType = "multipart/form-data",
            schema = @Schema(type = "object"),
            schemaProperties = {
                    @SchemaProperty(
                            name = "file",
                            schema = @Schema(type = "string", format = "binary")
                    ),
                    @SchemaProperty(
                            name = "goalDescription",
                            schema = @Schema(type = "text")
                    ),
                    @SchemaProperty(
                            name = "generationName",
                            schema = @Schema(type = "text")
                    )
            }
    )})
    public R<UniversalDataChartsVO> genFileChartAsync(MultipartFile file,
                                                      String goalDescription, String generationName) {
        ChartCreateDTO chartCreateDTO = new ChartCreateDTO(goalDescription, generationName);
        return R.ok(dataChartsService.generationFileChartAsync(file, chartCreateDTO));
    }

    /**
     * 通过多个文件生成多个图表 异步
     *
     * @return 返回生成的多个图表信息
     */
    @PostMapping(value = "/gen/multiple/sync")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {@Content(
            mediaType = "multipart/form-data",
            schema = @Schema(type = "object"),
            schemaProperties = {
                    @SchemaProperty(
                            name = "files",
                            schema = @Schema(type = "string", format = "binary")
                    ),
                    @SchemaProperty(
                            name = "goalDescription",
                            schema = @Schema(type = "text")
                    ),
                    @SchemaProperty(
                            name = "generationName",
                            schema = @Schema(type = "text")
                    )
            }
    )})
    public R<UniversalDataChartsVO> genMultipleChartAsync(MultipartFile[] files,
                                                          String goalDescription, String generationName) {
        ChartCreateDTO chartCreateDTO = new ChartCreateDTO(goalDescription, generationName);
        return R.ok(dataChartsService.generationMultipleChartAsync(files, chartCreateDTO));
    }

    @PostMapping("/gen/text")
    public R<UniversalDataChartsVO> genTextChart(@RequestBody ChartCreatTextDTO chartCreatTextDTO) {
        return R.ok(dataChartsService.genTextChart(chartCreatTextDTO));
    }

    @PostMapping("/gen/text/sync")
    public R<UniversalDataChartsVO> genTextChartSync(@RequestBody ChartCreatTextDTO chartCreatTextDTO) {
        return R.ok(dataChartsService.genTextChartSync(chartCreatTextDTO));
    }

    /**
     * 删除图表
     *
     * @param chartDeleteDTO 包含图表ID的删除信息
     * @return 返回删除操作的结果
     */
    @PostMapping("/delete")
    public R<Boolean> deleteChart(@RequestBody ChartDeleteDTO chartDeleteDTO) {
        if (chartDeleteDTO.getId() == null) {
            throw new CustomizeReturnException(ReturnCode.REQUEST_REQUIRED_PARAMETER_IS_EMPTY);
        }
        return R.ok(dataChartsService.deleteChartById(chartDeleteDTO.getId()));
    }

    /**
     * 更新图表名称
     *
     * @param chartUpdateDTO 包含图表ID和新名称的更新信息
     * @return 返回更新操作的结果
     */
    @PutMapping("/info")
    public R<Boolean> updateChartInfo(@RequestBody ChartUpdateDTO chartUpdateDTO) {
        return R.ok(dataChartsService.updateDataCharts(chartUpdateDTO));
    }

    /**
     * 查询图表信息列表
     *
     * @param chartQueryDTO 查询条件
     * @param pageModel     分页信息
     * @return 返回图表信息的分页列表
     */
    @PostMapping("/list/info")
    public R<Page<UniversalDataChartsVO>> listChartInfo(@RequestBody ChartQueryDTO chartQueryDTO,
                                                        PageModel pageModel) {
        return R.ok(dataChartsService.listChartInfo(chartQueryDTO, pageModel));
    }

    @GetMapping("/info")

    public R<UniversalDataChartsVO> getChartInfo(String id) {
        if (id == null) {
            throw new CustomizeReturnException(ReturnCode.REQUEST_REQUIRED_PARAMETER_IS_EMPTY);
        }

        return R.ok(dataChartsService.getChartInfo(Long.valueOf(id)));
    }
}
