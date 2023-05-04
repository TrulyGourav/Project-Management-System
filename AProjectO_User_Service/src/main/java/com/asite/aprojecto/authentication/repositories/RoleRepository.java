package com.asite.aprojecto.authentication.repositories;

import com.asite.aprojecto.authentication.models.PermissionModel;
import com.asite.aprojecto.authentication.models.RoleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;


public interface RoleRepository extends JpaRepository<RoleModel,Long> {
    @Query("SELECT r FROM RoleModel r WHERE r.roleName = :roleName")
    RoleModel findByRoleName(String roleName);
    List<RoleModel> findAll();
}
