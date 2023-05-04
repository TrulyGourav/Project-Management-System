package com.asite.aprojecto.authentication.repositories;

import com.asite.aprojecto.authentication.models.UserRoleProjectModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectUserRoleRepository extends JpaRepository<UserRoleProjectModel, Long> {
    List<UserRoleProjectModel> findByUid(Long uid);
    List<UserRoleProjectModel> findByUidAndProjectId(Long uid, Long projectId);
    List<UserRoleProjectModel> findByProjectId(Long projectId);

}