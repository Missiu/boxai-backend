package com.boxai.config.scheduled.repository;

import com.boxai.model.entity.DataCharts;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 提供对DataCharts实体的数据库操作。
 */
public interface DataChartsRepository extends JpaRepository<DataCharts, Long> {
    // 删除30天的isDelete为1数据
    @Query("SELECT dc FROM DataCharts dc WHERE dc.isDelete = 1 AND dc.updateTime < :cutoffTime")
    List<DataCharts> deleteDataChartsByIsDeleteTrueAndUpdateTimeBefore(@Param("cutoffTime") LocalDateTime cutoffTime);
}
