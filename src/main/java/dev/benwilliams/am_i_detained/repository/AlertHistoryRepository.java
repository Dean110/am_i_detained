package dev.benwilliams.am_i_detained.repository;

import dev.benwilliams.am_i_detained.entity.AlertHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface AlertHistoryRepository extends JpaRepository<AlertHistory, Long> {
    List<AlertHistory> findByUserIdAndAlertTimestampAfterOrderByAlertTimestampDesc(Long userId, LocalDateTime after);
}
