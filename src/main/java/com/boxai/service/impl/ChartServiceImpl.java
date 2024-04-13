package com.boxai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxai.model.domain.Chart;
import com.boxai.service.ChartService;
import com.boxai.mapper.ChartMapper;
import com.boxai.utils.ChatAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author Hzh
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2024-03-25 16:22:40
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{

    @Autowired
    private ChatAPI chatAPI;


}




