package com.boxai;

import cn.hutool.core.util.RandomUtil;
import com.boxai.mapper.DataChartsMapper;
import com.boxai.model.entity.DataCharts;
import com.boxai.utils.rateLimit.RateLimitUtils;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.boxai.utils.dataclean.DataClean.extractFlagsContent;

@SpringBootTest
class BoxaiApplicationTests {
    @Autowired
    private DataChartsMapper dataChartsMapper;

    @Test
    void contextLoads() {
        DataCharts dataCharts = new DataCharts();
        dataCharts.setId(7L);
        dataCharts.setUserId(2L);
        dataCharts.setCodeApis("{\"code\":\"\",\"message\":\"\",\"type\":\"\",\"flag\":\"\",\"content\":\"\",\"flagContent\":\"\",\"flagContentList\":[]}");
        Boolean b = dataChartsMapper.dataUpdateById(dataCharts);
        System.out.println(b);
    }
}
