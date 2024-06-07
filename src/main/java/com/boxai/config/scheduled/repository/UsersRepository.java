package com.boxai.config.scheduled.repository;

import com.boxai.model.entity.Users;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface UsersRepository extends JpaRepository<Users, Long> {
    @Query("SELECT u FROM Users u WHERE u.isDelete = 1 AND u.updateTime < :cutoffTime")
    List<Users> deleteUsersByIsDeleteTrueAndUpdateTimeBefore(@Param("cutoffTime") LocalDateTime cutoffTime);
}
