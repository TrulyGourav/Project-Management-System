package com.asite.apo.dao;

import com.asite.apo.model.TaskModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepo extends JpaRepository<TaskModel, Long> {
    List<TaskModel> findAllBytaskAssignee(Long uid);

    List<TaskModel> findAllByProjectId_projectId(Long projectModel);

    List<TaskModel> findAllBytaskAssigneeAndProjectId_projectId(Long projectId, Long taskId);

    TaskModel findByTaskId(Long id);

    boolean existsByTaskId(Long taskId);

    @Query(value = "select COUNT(*) from project_tx_task_tbl where status='Done'", nativeQuery = true)
    Long getTasksCountByTaskDone();

    @Query(value = "select COUNT(*) from project_tx_task_tbl where status='In Progress'", nativeQuery = true)
    Long getTasksCountByTaskInProgress();

    @Query(value = "select COUNT(*) from project_tx_task_tbl where status='Backlog'", nativeQuery = true)
    Long getTasksCountByTaskBacklog();

    @Query(value = "select COUNT(*) from project_tx_task_tbl where status='Done' and project_id = ?1", nativeQuery = true)
    Long getTasksCountByTaskDoneForProject(Long projectId);

    @Query(value = "select COUNT(*) from project_tx_task_tbl where status='In Progress' and project_id = ?1", nativeQuery = true)
    Long getTasksCountByTaskInProgressForProject(Long projectId);

    @Query(value = "select COUNT(*) from project_tx_task_tbl where status='Backlog' and project_id = ?1", nativeQuery = true)
    Long getTasksCountByTaskBacklogForProject(Long projectId);


    @Query(value = "SELECT COUNT(*) FROM project_tx_task_tbl WHERE task_assigned_to = ?1 AND status = 'Done'", nativeQuery = true)
    Long getTasksCountByTaskDoneForUser(Long userId);

    @Query(value = "SELECT COUNT(*) FROM project_tx_task_tbl WHERE task_assigned_to = ?1 AND status = 'In Progress'", nativeQuery = true)
    Long getTasksCountByTaskInProgressForUser(Long userId);

    @Query(value = "SELECT COUNT(*) FROM project_tx_task_tbl WHERE task_assigned_to = ?1 AND status = 'Backlog'", nativeQuery = true)
    Long getTasksCountByTaskBacklogForUser(Long userId);

    List<TaskModel> findAll();

    @Query(value = "SELECT COUNT(*) FROM project_tx_task_tbl WHERE status = 'Backlog'", nativeQuery = true)
    Long getBacklogTaskCount();
}

