package com.asite.apo.dao;

import com.asite.apo.model.ProjectIterationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectIterationRepo extends JpaRepository<ProjectIterationModel, Long> {

    List<ProjectIterationModel> findAllByproject_projectId(Long projectId);
}
