package com.asite.apo.controller;

import com.asite.apo.api.IterationURLConstant;
import com.asite.apo.api.PermissionConstants;
import com.asite.apo.dto.CreateProjectItrDTO;
import com.asite.apo.dto.UpdateProjectItrDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.asite.apo.service.IterationService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@Validated
@RequestMapping("/project/projectversion")
public class IterationController {

    @Autowired
    private IterationService iterationService;

    //todo Check ProjectId
    @PreAuthorize(PermissionConstants.MANAGE_PROJECT)
    @PostMapping(value = IterationURLConstant.CREATE_VERSION)
    public ResponseEntity<CreateProjectItrDTO> createProjectIteration(@Valid CreateProjectItrDTO createProjectItrDTO) {
        try {
            return ResponseEntity.ok().body(iterationService.createIteration(createProjectItrDTO,false));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //todo Check ProjectId
    @PreAuthorize(PermissionConstants.MANAGE_PROJECT)
    @PutMapping(value = IterationURLConstant.UPDATE_VERSION)
    public ResponseEntity<UpdateProjectItrDTO> updateProjectIteration(@Valid UpdateProjectItrDTO updateProjectItrDTO) {

        try {
            return ResponseEntity.ok().body(iterationService.updateIteration(updateProjectItrDTO));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize(PermissionConstants.MANAGE_PROJECT)
    @DeleteMapping(value = IterationURLConstant.DELETE_VERSION+"/{versionId}")
    public ResponseEntity<?> deleteProjectIteration(@Valid @Min(value = 1, message = "version-id-must-be-greater-than-0") @PathVariable(value = "versionId") Long versionId) {
        try {
            return ResponseEntity.ok().body(iterationService.deleteIteration(versionId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping(value = IterationURLConstant.GET_VERSION_BY_VERSION_ID+"/{versionId}")
    public ResponseEntity<?> getProjectIteration(@Valid @Min(value = 1, message = "version-id-must-be-greater-than-1")@PathVariable(value = "versionId") Long versionId) {
        try {
            return ResponseEntity.ok().body(iterationService.getProjectIteration(versionId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = IterationURLConstant.GET_VERSION_BY_PROJECT_ID+"/{projectId}")
    public ResponseEntity<?> getProjectIterationByProject(@Valid @Min(value = 1, message = "project-id-must-be-greater-than-1")@PathVariable(value = "projectId") Long projectId) {
        try {
            return ResponseEntity.ok().body(iterationService.getProjectIterationByProject(projectId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
