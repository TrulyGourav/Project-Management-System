package com.asite.aprojecto.authentication.repositories;

import com.asite.aprojecto.authentication.models.PermissionModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<PermissionModel,Integer> {
}
