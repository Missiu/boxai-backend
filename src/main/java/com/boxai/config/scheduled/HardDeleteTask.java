package com.boxai.config.scheduled;

import com.boxai.config.scheduled.repository.DataChartsRepository;
import com.boxai.config.scheduled.repository.UsersRepository;
import com.boxai.model.entity.DataCharts;
import com.boxai.model.entity.Users;
import com.boxai.service.UsersService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * 配置类，用于定义定时任务。
 */
@Configuration
@Slf4j
public class HardDeleteTask {

    @Resource
    private DataChartsRepository dataChartsRepository; // 数据库操作Repository

    @Resource
    private UsersRepository usersRepository;

    /**
     * 定时任务，每天执行一次，用于硬删除（永久删除）30天前标记为删除的数据。
     * 删除条件：is_delete=true且update_time早于30天前
     * 删除内容：DataCharts实体类
     */
    @Scheduled(fixedRate = 86400000) // 每天执行一次
    public void executeHardDelete() {
        // 获取当前日期时间
        LocalDateTime now = LocalDateTime.now();
        // 计算30天前的日期时间
        LocalDateTime thirtyDaysAgo = now.minusDays(30);
        // 查询并删除30天前标记为删除的数据
        List<DataCharts> byIsDeleteTrueAndDeletedAtBefore = dataChartsRepository.deleteDataChartsByIsDeleteTrueAndUpdateTimeBefore(thirtyDaysAgo);
        List<Users> byIsDeleteTrueAndUpdateTimeBefore = usersRepository.deleteUsersByIsDeleteTrueAndUpdateTimeBefore(thirtyDaysAgo);
        dataChartsRepository.deleteAll(byIsDeleteTrueAndDeletedAtBefore);
        usersRepository.deleteAll(byIsDeleteTrueAndUpdateTimeBefore);
    }

    /**
     * 初始化依赖注入信息。
     */
    @PostConstruct
    private void initDi() {
        log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }
}
