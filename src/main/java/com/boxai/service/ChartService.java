package com.boxai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.boxai.model.domain.Chart;

/**
* @author Hzh
* @description 针对表【chart(图表信息表)】的数据库操作Service
* @createDate 2024-03-25 16:22:40
*/
public interface ChartService extends IService<Chart> {
    String GenChart(String goal, String result);
}
