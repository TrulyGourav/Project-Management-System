package com.asite.apo.dao;

import com.asite.apo.model.ProjectIterationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IterationRepo extends JpaRepository<ProjectIterationModel, Long> {

    @Query(value = "select COUNT(*) from project_tx_iteration_tbl", nativeQuery = true)
    Long getAllIteration();

    ProjectIterationModel findByVersionId(Long lastIterId);
}
	


