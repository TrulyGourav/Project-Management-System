package com.asite.aprojecto.authentication.services;

import com.asite.aprojecto.authentication.models.PermissionModel;
import com.asite.aprojecto.authentication.repositories.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    public List<PermissionModel> getAllPermissions() {
        return permissionRepository.findAll();
    }
}
