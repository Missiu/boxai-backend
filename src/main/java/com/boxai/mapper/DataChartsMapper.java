package com.boxai.mapper;

import com.boxai.model.entity.DataCharts;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author Hzh
* @description 针对表【data_charts(数据信息表)】的数据库操作Mapper
* @createDate 2024-05-13 19:42:53
* @Entity generator.entity.DataCharts
*/
public interface DataChartsMapper extends BaseMapper<DataCharts> {
    // 更新goalDescription
    Boolean updateGoalDescription(String goalDescription, Long id);
    // 更新generationName
    Boolean updateGenerationName(String generationName, Long id);
    // 更新codeComments
    Boolean updateCodeComments(String codeComments, Long id);
    // 更新codeProfileDescription
    Boolean updateCodeProfileDescription(String codeProfileDescription, Long id);
    // 更新codeEntities
    Boolean updateCodeEntities(String codeEntities, Long id);
    // 更新codeApis
    Boolean updateCodeApis(String codeApis, Long id);
    // 更新codeExecution
    Boolean updateCodeExecution(String codeExecution, Long id);
    // 更新codeSuggestions
    Boolean updateCodeSuggestions(String codeSuggestions, Long id);
    // 更新codeNormRadar
    Boolean updateCodeNormRadar(String codeNormRadar, Long id);
    // 更新codeNormRadarDescription
    Boolean updateCodeNormRadarDescription(String codeNormRadarDescription, Long id);
    // 更新codeTechnologyPie
    Boolean updateCodeTechnologyPie(String codeTechnologyPie, Long id);
    // 更新codeCatalogPath
    Boolean updateCodeCatalogPath(String codeCatalogPath, Long id);

    /**
     * 根据图表ID删除图表
     * @param id 图表的唯一标识符
     * @return 返回操作结果，成功为true，失败为false
     */
    Boolean deleteChartById (Long id);

    /**
     * 根据图表ID更新 更新图表时间为调用时间
     * @param id 图表的唯一标识符
     * @return 返回操作结果，成功为true，失败为false
     */
    Boolean updateCurrentTime (Long id);

}




