package dev.benwilliams.am_i_detained.repository;

import dev.benwilliams.am_i_detained.entity.CheckInSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface CheckInScheduleRepository extends JpaRepository<CheckInSchedule, Long> {
    Optional<CheckInSchedule> findByUserId(Long userId);
    List<CheckInSchedule> findByEnabledTrue();
}
