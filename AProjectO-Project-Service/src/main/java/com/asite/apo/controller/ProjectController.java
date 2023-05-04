package com.asite.apo.controller;

import com.asite.apo.api.PermissionConstants;
import com.asite.apo.api.ProjectURLConstant;
import com.asite.apo.dto.ProjectListDTO;
import com.asite.apo.dto.ProjectModelDTO;
import com.asite.apo.exception.ResourceNotFoundException;
import com.asite.apo.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(ProjectURLConstant.PROJECT_API)
@CacheConfig(cacheNames = "projectCache")
public class ProjectController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    CacheManager cacheManager;

    @GetMapping("/{projectId}")
    @Cacheable(key = "#projectId")
    public ResponseEntity<?> getProjectDetails(@PathVariable("projectId")Long projectId) throws ResourceNotFoundException, ParseException {
        HashMap hashMap =  projectService.getProjectDetails(projectId);
        return new ResponseEntity<>(hashMap, HttpStatus.OK);
    }

    @GetMapping("/all")
    @PreAuthorize(PermissionConstants.CAN_VIEW_ALL_PROJECT)
    @Cacheable()
    public ResponseEntity<?> getAllProjects() throws ParseException {
        List<ProjectListDTO> projectListDTOList =  projectService.getAllProjects();
        return new ResponseEntity<>(projectListDTOList, HttpStatus.OK);
    }


    @GetMapping(value = ProjectURLConstant.GET_PROJECT_BY_USER_ID_URL+"/{uid}")
    public ResponseEntity<?> getProjectList(@PathVariable("uid") Long uid) throws ParseException {
        return projectService.getProjectList(uid);
    }

    @PreAuthorize(PermissionConstants.CAN_CREATE_PROJECT)
    @PostMapping
    @CachePut()
    public ResponseEntity<?> createProject(@Valid ProjectModelDTO projectModelDTO) throws Exception {
        Cache cache = cacheManager.getCache("projectCache");
        cache.clear();
        projectModelDTO = projectService.createProject(projectModelDTO);
        return new ResponseEntity<>(projectModelDTO, HttpStatus.OK);
    }

    //todo Check ProjectId
    @PreAuthorize(PermissionConstants.MANAGE_PROJECT)
    @PutMapping("/{projectId}")
    @CachePut(key = "#projectId")
    public ResponseEntity<?> updateProject(@PathVariable(name = "projectId")Long projectId, @Valid ProjectModelDTO projectModelDTO) throws Exception {
        projectModelDTO = projectService.updateProject(projectId, projectModelDTO);
        return new ResponseEntity<>(projectModelDTO, HttpStatus.ACCEPTED);
    }

//    @DeleteMapping("/{projectId}")
//    @CacheEvict(key = "#projectId")
//    public ResponseEntity<?> deleteProject(@PathVariable("projectId") Long projectId)
//    {
//        Cache cache = cacheManager.getCache("projectCache");
//        cache.clear();
//        return projectService.deleteProject(projectId);
//    }

}
