package com.asite.apo.controller;

import com.asite.apo.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import com.asite.apo.api.PermissionConstants;
import com.asite.apo.api.ProjectURLConstant;
import com.asite.apo.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@Validated
@RequestMapping("/project/dashboard")
@CacheConfig(cacheNames = "dashboardCache")
public class DashboardController {

    @Autowired
    DashboardService dashboardService;

    @PreAuthorize(PermissionConstants.CAN_VIEW_REPORTS)
    @GetMapping(value = "/admin/project", produces = "application/json")
    public ResponseEntity<?> getAdminDashboardProject() {
        try {
            return ResponseEntity.ok().body(dashboardService.getAdminDashboardProject());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize(PermissionConstants.CAN_VIEW_REPORTS)
    @GetMapping(value = "/admin/task", produces = "application/json")
    public ResponseEntity<?> getAdminDashboardTask(@RequestParam(value = "projectId", required = false) @Valid @Min(value = 1, message = "project-id-must-be-greater-than-0") Long projectId) {
        try {
            return ResponseEntity.ok().body(dashboardService.getAdminDashboardTask(projectId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = "/user/task", produces = "application/json")
    public ResponseEntity<?> getUserDashboardTask(@RequestParam(value = "userId") @Valid @Min(value = 1, message = "user-id-must-be-greater-than-0") Long userId) {
        try {
            return ResponseEntity.ok().body(dashboardService.getUserDashboardTask(userId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize(PermissionConstants.CAN_VIEW_REPORTS)
    @GetMapping(value = "/admin/constant", produces = "application/json")
    public ResponseEntity<?> getAdminDashboardConstant() {
        try {
            return ResponseEntity.ok().body(dashboardService.getAdminDashboardConstant());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = "/user/constant", produces = "application/json")
    public ResponseEntity<?> getUserDashboardConstant(@RequestParam(value = "userId") @Valid @Min(value = 1, message = "user-id-must-be-greater-than-0") Long userId) {
        try {
            return ResponseEntity.ok().body(dashboardService.getUserDashboardConstant(userId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
