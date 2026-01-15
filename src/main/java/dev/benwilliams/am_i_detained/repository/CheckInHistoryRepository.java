package dev.benwilliams.am_i_detained.repository;

import dev.benwilliams.am_i_detained.entity.CheckInHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface CheckInHistoryRepository extends JpaRepository<CheckInHistory, Long> {
    List<CheckInHistory> findByUserIdAndCheckInTimestampAfterOrderByCheckInTimestampDesc(Long userId, LocalDateTime after);
}
