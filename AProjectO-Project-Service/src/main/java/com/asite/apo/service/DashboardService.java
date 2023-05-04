package com.asite.apo.service;

import com.asite.apo.dao.IterationRepo;
import com.asite.apo.dao.ProjectRepo;
import com.asite.apo.dao.TaskRepo;
import com.asite.apo.dto.DashboardProjectDTO;
import com.asite.apo.model.ProjectModel;
import com.asite.apo.util.MappingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardService.class);
    @Autowired
    TaskRepo taskRepo;
    @Autowired
    ProjectRepo projectRepo;

    @Autowired
    IterationRepo iterationRepo;


    public List<DashboardProjectDTO> getAdminDashboardProject() {
        LOGGER.debug("Getting Admin Dashboard Project");
        List<ProjectModel> projectModels = projectRepo.findAll();
        return MappingHelper.getDashboardProjectDTOsFromProjectModel(projectModels);
    }

    public Map<String, Long> getAdminDashboardTask(Long projectId) {
        LOGGER.debug("Getting Admin Dashboard Task",projectId);
        Map<String, Long> counts = new HashMap<>();

        if (projectId == null) {
            counts.put("Done", taskRepo.getTasksCountByTaskDone());
            counts.put("In Progress", taskRepo.getTasksCountByTaskInProgress());
            counts.put("Backlog", taskRepo.getTasksCountByTaskBacklog());
        } else {
            counts.put("Done", taskRepo.getTasksCountByTaskDoneForProject(projectId));
            counts.put("In Progress", taskRepo.getTasksCountByTaskInProgressForProject(projectId));
            counts.put("Backlog", taskRepo.getTasksCountByTaskBacklogForProject(projectId));
        }
        LOGGER.debug("Found Admin Dashboard Task",projectId);
        return counts;
    }

    public Map<String, Long> getUserDashboardTask(Long userId) {
        LOGGER.debug("Getting User Dashboard Task",userId);
        Map<String, Long> counts = new HashMap<>();
        counts.put("Done", taskRepo.getTasksCountByTaskDoneForUser(userId));
        counts.put("In Progress", taskRepo.getTasksCountByTaskInProgressForUser(userId));
        counts.put("Backlog", taskRepo.getTasksCountByTaskBacklogForUser(userId));
        LOGGER.debug("Found User Dashboard Task",userId);
        return counts;
    }

    public Map<String, Long> getAdminDashboardConstant() {
        LOGGER.debug("Getting Admin Dashboard Constant");
        Map<String, Long> counts = new HashMap<>();
        counts.put("All Projects", projectRepo.getProjectCountForAdmin());
        counts.put("Task In Progress", projectRepo.getInProgressCountForAdmin());
        counts.put("Backlog Tasks", taskRepo.getBacklogTaskCount());
        LOGGER.debug("Found Admin Dashboard Constant");
        return counts;
    }

    public Map<String, Long> getUserDashboardConstant(Long userId) {
        LOGGER.debug("Getting User Dashboard Constant",userId);
        Map<String, Long> counts = new HashMap<>();
        counts.put("All Projects", projectRepo.getProjectCountForUser(userId));
        counts.put("Task In Progress", projectRepo.getInProgressCountForUser(userId));
        counts.put("Tasks Done", taskRepo.getTasksCountByTaskDoneForUser(userId));
        LOGGER.debug("Found User Dashboard Constant",userId);
        return counts;
    }
}
