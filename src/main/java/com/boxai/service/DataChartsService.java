package com.boxai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxai.model.dto.datachart.ChartCreatTextDTO;
import com.boxai.model.dto.datachart.ChartCreateDTO;
import com.boxai.model.dto.datachart.ChartQueryDTO;
import com.boxai.model.dto.datachart.ChartUpdateDTO;
import com.boxai.model.entity.DataCharts;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boxai.model.page.PageModel;
import com.boxai.model.vo.datachart.ChartQueryVO;
import com.boxai.model.vo.datachart.UniversalDataChartsVO;
import org.springframework.web.multipart.MultipartFile;

/**
* @author Hzh
* @description 针对表【data_charts(数据信息表)】的数据库操作Service
* @createDate 2024-05-13 19:42:53
*/
public interface DataChartsService extends IService<DataCharts> {

    UniversalDataChartsVO generationFileChart(MultipartFile multipartFile, ChartCreateDTO chartCreateDTO);

    UniversalDataChartsVO generationMultipleChart(MultipartFile[] multipartFiles, ChartCreateDTO chartCreateDTO);

    Boolean deleteChartById(Long id);

    Boolean updateDataCharts(ChartUpdateDTO chartUpdateDTO);

    Page<UniversalDataChartsVO> listChartInfo(ChartQueryDTO chartQueryDTO, PageModel pageModel);

    UniversalDataChartsVO generationFileChartAsync(MultipartFile multipartFile, ChartCreateDTO chartCreateDTO);

    UniversalDataChartsVO generationMultipleChartAsync(MultipartFile[] multipartFiles, ChartCreateDTO chartCreateDTO);

    UniversalDataChartsVO getChartInfo(Long id);

    UniversalDataChartsVO genTextChart(ChartCreatTextDTO chartCreatTextDTO);

    UniversalDataChartsVO genTextChartSync(ChartCreatTextDTO chartCreatTextDTO);
}
