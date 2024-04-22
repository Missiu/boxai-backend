package com.boxai.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChartServiceImplTest {

    @Autowired
    private ChartServiceImpl chartService;
    @Test
    void genChart() {
        chartService.genChart("生成词云图", "生成词云图");
    }
}