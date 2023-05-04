package com.asite.aprojecto.authentication.controllers;

import com.asite.aprojecto.authentication.constant.PermissionConstant;
import com.asite.aprojecto.authentication.constant.UserServiceURI;
import com.asite.aprojecto.authentication.dto.RoleDTO;
import com.asite.aprojecto.authentication.exceptions.RoleAlreadyExistsException;
import com.asite.aprojecto.authentication.models.PermissionModel;
import com.asite.aprojecto.authentication.models.RoleModel;
import com.asite.aprojecto.authentication.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(UserServiceURI.ROLE_API)
@Validated
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PreAuthorize(PermissionConstant.MANAGE_ROLES)
    @GetMapping(UserServiceURI.ROLE_ALL)
    public ResponseEntity<?> getAllRole(){
        Optional<List<RoleDTO>> roleModels = roleService.getAllRole();
        if (roleModels.isPresent()){
            return ResponseEntity.ok().body(roleModels.get());
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Roles not found!");
        }
    }

    @PreAuthorize(PermissionConstant.MANAGE_ROLES)
    @PostMapping
    public ResponseEntity<?> addRole(@Valid @RequestBody RoleDTO roleDTO){
        try {
            Optional<RoleDTO> role  = Optional.ofNullable(roleService.addRole(roleDTO));
            if (role.isPresent()){
                return ResponseEntity.ok().body(role);
            }else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Role not added!");
            }
        } catch (RoleAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PreAuthorize(PermissionConstant.MANAGE_ROLES)
    @GetMapping("/{role_id}")
    public ResponseEntity<?> getRole(@Valid @NotNull @Min(1) @PathVariable Long role_id){
        try {
            RoleDTO roleDTO = roleService.getRole(role_id);
            return ResponseEntity.ok().body(roleDTO);
        }catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found!");
        }
    }

    @PreAuthorize(PermissionConstant.MANAGE_ROLES)
    @PostMapping(UserServiceURI.GET_ALL_PERMISSIONS_FROM_ROLE)
    public ResponseEntity<?> getPermissions(@Valid @NotNull String roleName){
         Optional<RoleDTO> role = roleService.getPermissions(roleName);
        if (role.isPresent()){
            Set<PermissionModel> permissions = role.get().getPermissions();
            return ResponseEntity.ok().body(permissions);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Permissions not found!");
        }
    }

    @PreAuthorize(PermissionConstant.MANAGE_ROLES)
    @PutMapping(UserServiceURI.UPDATE_ROLE)
    public ResponseEntity<?> updateRole(@RequestBody RoleDTO roleDTO){
        try {
            System.out.println(roleDTO.getRoleId());
            RoleDTO role = roleService.updateRole(roleDTO);
            return ResponseEntity.ok().body(role);
        } catch (RoleNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
