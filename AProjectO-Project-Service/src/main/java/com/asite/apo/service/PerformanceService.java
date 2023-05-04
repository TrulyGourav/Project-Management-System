package com.asite.apo.service;

import com.asite.apo.dao.ProjectIterationRepo;
import com.asite.apo.dao.WorkLogRepo;
import com.asite.apo.model.ProjectIterationModel;
import com.asite.apo.model.WorkLogModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PerformanceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceService.class);

    @Autowired
    private WorkLogRepo workLogRepository;

    @Autowired
    private ProjectIterationRepo projectIterationRepository;

    public Map<LocalDate, Double> getWorkLogsByDate(Long projectId, Long userId, LocalDate startDate, LocalDate endDate) {
        LOGGER.debug("Getting Worklogs by Date",projectId,userId,startDate,endDate);
        Map<LocalDate, Double> workLogsByDate = new HashMap<>();
        List<ProjectIterationModel> projectIterations = projectIterationRepository.findAllByproject_projectId(projectId);
        for (ProjectIterationModel projectIteration : projectIterations) {
            List<WorkLogModel> workLogs;
            if (userId == null) {
                workLogs = workLogRepository.findAllByProjectVersionIdAndDateWorkedBetween(projectIteration, startDate, endDate);
            } else {
                workLogs = workLogRepository.findAllByProjectVersionIdAndUserIdAndDateWorkedBetween(projectIteration, userId, startDate, endDate);
            }
            for (WorkLogModel workLog : workLogs) {
                LocalDate date = workLog.getDateWorked();
                Double hoursWorked = workLog.getHoursWorked();
                Double totalHours = workLogsByDate.getOrDefault(date, 0.0);
                workLogsByDate.put(date, totalHours + hoursWorked);
            }
        }
        LOGGER.debug("Found Worklogs by Date",projectId,userId,startDate,endDate);
        return workLogsByDate;
    }
}