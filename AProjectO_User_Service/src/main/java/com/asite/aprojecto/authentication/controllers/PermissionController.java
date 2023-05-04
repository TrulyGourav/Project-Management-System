package com.asite.aprojecto.authentication.controllers;

import com.asite.aprojecto.authentication.constant.PermissionConstant;
import com.asite.aprojecto.authentication.constant.UserServiceURI;
import com.asite.aprojecto.authentication.models.PermissionModel;
import com.asite.aprojecto.authentication.services.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(UserServiceURI.PERMISSION_API)
@Validated
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @PreAuthorize(PermissionConstant.MANAGE_ROLES)
    @GetMapping("/{role_id}")
    public ResponseEntity<?> getPermissions(@Valid @NotNull @Min(1) @PathVariable Integer role_id){
//      List<PermissionModel> permissions=permissionService.getPermissions(role_id);
        return ResponseEntity.ok().body("Fetch Permissions of this role");
    }

    @PreAuthorize(PermissionConstant.MANAGE_ROLES)
    @GetMapping(UserServiceURI.GET_ALL_PERMISSIONS)
    public ResponseEntity<?> getAllPermissions(){
        Optional<List<PermissionModel>> permissions= Optional.ofNullable(permissionService.getAllPermissions());
        if (permissions.isPresent()){
            return ResponseEntity.ok().body(permissions.get());
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("permissions not found!");
        }
    }
}
