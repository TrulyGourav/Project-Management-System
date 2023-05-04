package com.asite.apo.dao;

import com.asite.apo.dto.WorkLogListDTO;
import com.asite.apo.model.ProjectIterationModel;
import com.asite.apo.model.SubTaskModel;
import com.asite.apo.model.TaskModel;
import com.asite.apo.model.WorkLogModel;
import lombok.Lombok;
import lombok.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.text.SimpleDateFormat;
import java.time.LocalDate;

import java.util.Date;
import java.util.List;

public interface WorkLogRepo extends JpaRepository<WorkLogModel, Long> {

    @Query(value = "select SUM(hours_worked) from project_tx_worklog_tbl where task_id In ?1",nativeQuery = true)
    Double getTotalHoursWorkedByTask(List<Long> ids);
    @Query(value = "select * from project_tx_worklog_tbl where task_id = ?1",nativeQuery = true)
    WorkLogRepo findByTaskId(Long id);
    List<WorkLogModel> findByUserId(Long id);
    List<WorkLogModel> findByProjectVersionId_versionId(Long id);
    List<WorkLogModel> findByTaskId_taskId(Long id);
    List<WorkLogModel> findBySubTaskId_subTaskId(Long id);
    @Query(value = "select * from project_tx_worklog_tbl where date_worked = ?1 and version_id = ?2",nativeQuery = true)
    List<WorkLogModel> getByProject(Date dateFormat,Long id);

    @Query(value = "select * from project_tx_worklog_tbl where date_worked = ?1 and task_id = ?2",nativeQuery = true)
    List<WorkLogModel> getByTask(Date dateFormat, Long id);

    @Query(value = "select * from project_tx_worklog_tbl where date_worked = ?1 and sub_task_id = ?2",nativeQuery = true)
    List<WorkLogModel> getBySubtask(Date dateFormat, Long id);

    @Query(value = "select SUM(hours_worked) from project_tx_worklog_tbl where version_id=?1",nativeQuery = true)
    Double getTotalHoursByProject(Long id);

    @Query(value = "select SUM(hours_worked) from project_tx_worklog_tbl where task_id=?1 and version_id=?2",nativeQuery = true)
    Double getTotalHoursByTask(Long tId,Long pId);

    @Query(value = "select SUM(hours_worked) from project_tx_worklog_tbl where sub_task_id=?1 and version_id=?2",nativeQuery = true)
    Double getTotalHoursBySubTask(Long sId,Long pId);

    @Query(value = "select SUM(hours_worked) from project_tx_worklog_tbl where user_id=?1 and date_worked = ?2 and task_id=?3",nativeQuery = true)
    Double getTotalHoursOfUser(Long id,Date date,Long tid);
    @Query(value = "select SUM(hours_worked) from project_tx_worklog_tbl where user_id=?1 and date_worked = ?2 and version_id=?3",nativeQuery = true)
    Double getTotalHoursOfUserOnProject(Long id, Date dateFormat, Long vid);

    @Query(value = "select SUM(hours_worked) from project_tx_worklog_tbl where user_id=?1 and date_worked = ?2 and sub_task_id=?3",nativeQuery = true)
    Double getTotalHoursOfUserOnSubTask(Long id, Date dateFormat, Long sid);
    @Query(value = "select * from project_tx_worklog_tbl where user_id =?1 and date_worked between ?2 and ?3 and version_id=?4",nativeQuery = true)
    List<WorkLogModel> getIntervalOfProjectByUser(Long uid, Date startDate, Date endDate,Long pId);

    @Query(value = "select * from project_tx_worklog_tbl where user_id =?1 and date_worked between ?2 and ?3 and task_id=?4",nativeQuery = true)
    List<WorkLogModel> getIntervalOfTaskByUser(Long uId, Date startDate, Date endDate, Long tId);
    @Query(value = "select * from project_tx_worklog_tbl where user_id =?1 and date_worked between ?2 and ?3 and sub_task_id=?4",nativeQuery = true)
    List<WorkLogModel> getIntervalOfSubTaskByUser(Long uId, Date startDate, Date endDate, Long sId);
    List<WorkLogModel> findAllByUserIdAndTaskId_taskId(Long uId,Long taskId);

    List<WorkLogModel> findAllByUserIdAndSubTaskId_subTaskId(Long uId, Long sId);

    List<WorkLogModel> findAllByUserIdAndProjectVersionId_versionId(Long uId, Long pId);

    List<WorkLogModel> findAllByProjectVersionIdAndDateWorkedBetween(ProjectIterationModel projectIteration, LocalDate startDate, LocalDate endDate);

    List<WorkLogModel> findAllByProjectVersionIdAndUserIdAndDateWorkedBetween(ProjectIterationModel projectIteration, Long userId, LocalDate startDate, LocalDate endDate);

    @Query(value = "select SUM(hours_worked) from project_tx_worklog_tbl where user_id=?1 and task_id=?2",nativeQuery = true)
    Double getTotalHoursOfUserByTask(Long userId, Long taskId);

    @Query(value = "select SUM(hours_worked) from project_tx_worklog_tbl where and sub_task_id IN ?1",nativeQuery = true)
    Double getTotalHoursOfTask(List<Long> tId);
}
