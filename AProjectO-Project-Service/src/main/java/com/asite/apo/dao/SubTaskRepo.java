package com.asite.apo.dao;

import com.asite.apo.model.SubTaskModel;
import com.asite.apo.model.TaskModel;
//import jdk.internal.loader.AbstractClassLoaderValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubTaskRepo extends JpaRepository<SubTaskModel, Long> {

    List<SubTaskModel> findAllBySubTaskAssignedTo(Long id);
    List<SubTaskModel> findAllByTaskId_taskId(Long id);
    List<SubTaskModel> findAllBySubTaskAssignedToAndTaskId_ProjectId_projectId(Long taskId,Long projectId);
    List<SubTaskModel> findAllBySubTaskAssignedToAndTaskId_taskId(Long userId,Long taskId);

    Optional<SubTaskModel> findBySubTaskId(long subTaskId);
}
