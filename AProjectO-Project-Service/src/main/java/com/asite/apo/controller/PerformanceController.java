package com.asite.apo.controller;
import com.asite.apo.service.PerformanceService;
import com.asite.apo.util.GeneralUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
@RestController
@RequestMapping("project/performance")
public class PerformanceController {

    @Autowired
    private PerformanceService performanceService;

    @GetMapping("/workLogsByDate")
    public ResponseEntity<?> getWorkLogsByDate(
            @RequestParam Long projectId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) throws ParseException {
        LocalDate start,end;
        if (startDate == null) {
            start = LocalDate.now().minusDays(7);
        }else {
            start = GeneralUtil.dateFormat.parse(startDate).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        if (endDate == null) {
            end = LocalDate.now();
        }else {
            end = GeneralUtil.dateFormat.parse(endDate).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return ResponseEntity.ok(performanceService.getWorkLogsByDate(projectId, userId, start, end));
    }
}


