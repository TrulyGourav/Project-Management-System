package com.asite.aprojecto.authentication.controllers;

import com.asite.aprojecto.authentication.constant.PermissionConstant;
import com.asite.aprojecto.authentication.constant.UserServiceURI;
import com.asite.aprojecto.authentication.dto.ProjectUserDTO;
import com.asite.aprojecto.authentication.services.ProjectService;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(UserServiceURI.PROJECT_API)
public class ProjectController {
    @Autowired
    ProjectService projectService;

    @PreAuthorize(PermissionConstant.MANAGE_PROJECT)
    @PostMapping(UserServiceURI.ADD_USERS_TO_PROJECT_API)
    public ResponseEntity<String> addUserToProject(@PathVariable("project_id") Long projectId, @RequestBody ProjectUserDTO projectUserDTO){
        boolean isAdded= false;
        try {
            isAdded = projectService.addUsersToProject(projectId,projectUserDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (isAdded){
            return ResponseEntity.ok().body("Added Successfully");
        }else {
            return ResponseEntity.ok().body("Users Not Added!");
        }
    }

    @PreAuthorize(PermissionConstant.MANAGE_PROJECT)
    @GetMapping(UserServiceURI.GET_USERS_TO_PROJECT_API)
    public ResponseEntity<JSONArray> getUserToProject(@PathVariable("project_id") Long projectId){
        try {
            JSONArray usersToProject = projectService.getUsersToProject(projectId);
            return ResponseEntity.ok().body(usersToProject);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize(PermissionConstant.MANAGE_PROJECT)
    @DeleteMapping(UserServiceURI.DELETE_USER_TO_PROJECT_API)
    public ResponseEntity<String> deleteUserToProject(@PathVariable("project_id") Long projectId,@PathVariable("uid") Long uid){
        try {
            String message = projectService.softDeleteUserRoleProject(projectId,uid);
            return ResponseEntity.ok().body(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
