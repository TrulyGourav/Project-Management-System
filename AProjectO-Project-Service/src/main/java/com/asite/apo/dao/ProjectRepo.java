package com.asite.apo.dao;

import com.asite.apo.model.ProjectModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


//@Repository
public interface ProjectRepo extends JpaRepository<ProjectModel, Long> {

//    @Query("from project_tx_pdetails_tbl where project_reporter = ")
    List<ProjectModel> findAllByProjectReporter(Long uid);

    @Query(value = "select COUNT(*) from project_tx_pdetails_tbl", nativeQuery = true)
    Long getProjectCountForAdmin();

    @Query(value = "select COUNT(*) from project_tx_pdetails_tbl where status='Open'", nativeQuery = true)
    Long getInProgressCountForAdmin();

    @Query(value = "select COUNT(project_id) from user_lk_project_role_user_tbl where uid=?1", nativeQuery = true)
    Long getProjectCountForUser(Long userId);

    @Query(value = "select count(*) from project_tx_pdetails_tbl where project_id in (select project_id from user_lk_project_role_user_tbl where uid=?1) and status = 'Open'", nativeQuery = true)
    Long getInProgressCountForUser(Long userId);

    ProjectModel findByProjectId(Long projectId);
    @Query(value = "select * from project_tx_pdetails_tbl where project_id in (?1)",nativeQuery = true)
    List<ProjectModel> findAllWhereProjectIdIn(List projectlist);
}
