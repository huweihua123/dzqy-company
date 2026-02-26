package com.weihua.dydz.repository;

import com.weihua.dydz.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByWorkDate(LocalDate workDate);
    List<Attendance> findByWorkDateBetween(LocalDate start, LocalDate end);
}
