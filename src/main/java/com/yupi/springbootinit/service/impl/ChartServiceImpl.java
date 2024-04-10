package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.service.ChartService;
import com.yupi.springbootinit.mapper.ChartMapper;
import com.yupi.springbootinit.utils.ChatAPI;
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

    public String genChat() {
//        Chat()
        return null;
    }
}




