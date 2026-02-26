package com.weihua.dydz.controller;

import com.weihua.dydz.domain.Attendance;
import com.weihua.dydz.domain.AttendanceStatus;
import com.weihua.dydz.domain.PayType;
import com.weihua.dydz.domain.Worker;
import com.weihua.dydz.domain.WorkerRole;
import com.weihua.dydz.dto.AttendanceRequest;
import com.weihua.dydz.dto.BatchAttendanceRequest;
import com.weihua.dydz.dto.BatchAttendanceRangeRequest;
import com.weihua.dydz.dto.TempWorkerAttendanceRequest;
import com.weihua.dydz.dto.TempWorkerRequest;
import com.weihua.dydz.repository.AttendanceRepository;
import com.weihua.dydz.repository.WorkerRepository;
import com.weihua.dydz.repository.WorkerRoleRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceRepository attendanceRepository;
    private final WorkerRepository workerRepository;
    private final WorkerRoleRepository workerRoleRepository;

    @GetMapping
    public List<Attendance> list(@RequestParam(required = false) LocalDate date,
                                 @RequestParam(required = false) Long groupId) {
        List<Attendance> list = date == null
                ? attendanceRepository.findAll()
                : attendanceRepository.findByWorkDate(date);
        if (groupId == null) {
            return list;
        }
        return list.stream()
                .filter(a -> a.getWorker().getGroup() != null
                        && groupId.equals(a.getWorker().getGroup().getId()))
                .toList();
    }

    @PostMapping
    public Attendance create(@Valid @RequestBody AttendanceRequest request) {
        Worker worker = workerRepository.findById(request.workerId())
                .orElseThrow(() -> new IllegalArgumentException("Worker not found"));
        Attendance attendance = new Attendance();
        attendance.setWorker(worker);
        attendance.setWorkDate(request.workDate());
        attendance.setStatus(request.status());
        attendance.setRemark(request.remark());
        return attendanceRepository.save(attendance);
    }

    @PostMapping("/batch-present")
    public List<Attendance> batchPresent(@Valid @RequestBody BatchAttendanceRequest request) {
        LocalDate date = request.workDate();
        List<Worker> workers = workerRepository.findByGroupId(request.groupId());
        List<Attendance> existing = attendanceRepository.findByWorkDate(date);

        return workers.stream()
                .map(w -> {
                    Attendance attendance = existing.stream()
                            .filter(a -> a.getWorker().getId().equals(w.getId()))
                            .findFirst()
                            .orElseGet(() -> {
                                Attendance a = new Attendance();
                                a.setWorker(w);
                                a.setWorkDate(date);
                                return a;
                            });
                    attendance.setStatus(request.status());
                    return attendanceRepository.save(attendance);
                })
                .toList();
    }

    @PostMapping("/batch-range")
    public List<Attendance> batchRange(@Valid @RequestBody BatchAttendanceRangeRequest request) {
        if (request.from().isAfter(request.to())) {
            throw new IllegalArgumentException("From date must be before to date");
        }
        List<Worker> workers = workerRepository.findByGroupId(request.groupId());
        LocalDate date = request.from();
        while (!date.isAfter(request.to())) {
            LocalDate current = date;
            List<Attendance> existing = attendanceRepository.findByWorkDate(current);
            workers.forEach(w -> {
                Attendance attendance = existing.stream()
                        .filter(a -> a.getWorker().getId().equals(w.getId()))
                        .findFirst()
                        .orElseGet(() -> {
                            Attendance a = new Attendance();
                            a.setWorker(w);
                            a.setWorkDate(current);
                            return a;
                        });
                attendance.setStatus(request.status());
                attendanceRepository.save(attendance);
            });
            date = date.plusDays(1);
        }
        return attendanceRepository.findByWorkDateBetween(request.from(), request.to());
    }

    @PutMapping("/{id}")
    public Attendance update(@PathVariable Long id, @Valid @RequestBody AttendanceRequest request) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attendance not found"));
        if (request.status() != null) {
            attendance.setStatus(request.status());
        }
        attendance.setRemark(request.remark());
        return attendanceRepository.save(attendance);
    }

    @PostMapping("/temp-worker")
    public Attendance tempWorker(@Valid @RequestBody TempWorkerAttendanceRequest request) {
        TempWorkerRequest w = request.worker();
        if (w.payType() != PayType.DAILY) {
            throw new IllegalArgumentException("Temp worker must be DAILY pay type");
        }
        WorkerRole role = workerRoleRepository.findById(w.roleId())
                .orElseThrow(() -> new IllegalArgumentException("Worker role not found"));
        Worker worker = new Worker();
        worker.setName(w.name());
        worker.setRole(role);
        worker.setPayType(w.payType());
        worker.setDailySalary(w.dailySalary());
        worker.setPhone(w.phone());
        worker.setStatus(com.weihua.dydz.domain.WorkerStatus.ACTIVE);
        worker.setRemark(w.remark());
        Worker saved = workerRepository.save(worker);

        Attendance attendance = new Attendance();
        attendance.setWorker(saved);
        attendance.setWorkDate(request.workDate());
        attendance.setStatus(AttendanceStatus.PRESENT);
        return attendanceRepository.save(attendance);
    }
}
