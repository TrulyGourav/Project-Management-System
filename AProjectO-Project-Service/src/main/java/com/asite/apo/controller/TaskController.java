package com.asite.apo.controller;


import com.asite.apo.api.PermissionConstants;
import com.asite.apo.api.ProjectServiceURI;
import com.asite.apo.dto.TaskDTO;
import com.asite.apo.exception.ResourceNotFoundException;
import com.asite.apo.service.TaskService;
import com.asite.apo.util.MappingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;

@RestController
@RequestMapping(ProjectServiceURI.TASK_API)
@CacheConfig(cacheNames = "taskCache")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    CacheManager cacheManager;

    @GetMapping(value = ProjectServiceURI.GET_TASK_BY_USER_URI)
//    @Cacheable(key= "#uId")
    public ResponseEntity<?> getTaskListByUserId(@RequestParam("uId")Long uId){
        try{
            return ResponseEntity.ok().body(taskService.getTaskListByUserId(uId));
        } catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while fetching. " + e.getMessage());
        }
    }

    @PreAuthorize(PermissionConstants.CAN_VIEW_ALL_PROJECT)
    @GetMapping(value = ProjectServiceURI.GET_ALL_TASKS_URI)
//    @Cacheable(key= "#uId")
    public ResponseEntity<?> getTaskList(){
        try{
            return ResponseEntity.ok().body(taskService.getTaskList());
        } catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while fetching. " + e.getMessage());
        }
    }

//  @PreAuthorize(PermissionConstants.MANAGE_TASKS)
    @PutMapping(value = ProjectServiceURI.UPDATE_TASK_STATUS_URI)
    public ResponseEntity<?> updateStatus(@RequestParam("pId")Long pId,@RequestParam("tId")Long tId,@RequestParam("status") @Pattern(regexp = "Backlog|Done|In Progress") String status)
    {
        try{
            return ResponseEntity.ok().body(taskService.updateStatus(pId,tId,status));
        } catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while fetching. " + e.getMessage());
        }
    }

    @GetMapping(value = ProjectServiceURI.GET_TASK_BY_PROJECT_URI)
    @Cacheable(key = "#pId")
    public ResponseEntity<?> getTaskListByProjectId(@RequestParam("pId") Long pId){
        try{
            return ResponseEntity.ok().body(taskService.getTaskListByProjectId(pId));
        } catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while fetching. " + e.getMessage());
        }
    }

    @GetMapping
    @Cacheable(key = "#taskId")
    public ResponseEntity<?> getTaskDetails(@RequestParam("tId") Long taskId){
        try{
            return ResponseEntity.ok().body(taskService.getTaskDetails(taskId));
        } catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while fetching. " + e.getMessage());
        }
    }

    //todo Check ProjectId
    @PreAuthorize(PermissionConstants.MANAGE_TASKS)
    @PostMapping(value = ProjectServiceURI.CREATE_TASK_URI)
    @CachePut
    public ResponseEntity<?> createTask(@RequestParam("taskDTO") String task, @RequestParam(value = "files",required = false) List<MultipartFile> files) throws Exception {
            TaskDTO taskDTO = MappingHelper.getTaskDTOFromParam(task);
            Cache cache = cacheManager.getCache("taskCache");
            cache.clear();
            return new ResponseEntity<>(taskService.createTask( taskDTO, files), HttpStatus.OK);
    }

    //todo Check ProjectId
    @PutMapping(value = ProjectServiceURI.UPDATE_TASK_URI)
    @PreAuthorize(PermissionConstants.MANAGE_TASKS)
    @CachePut()
    public ResponseEntity<?> updateTask(@Valid @RequestParam("taskDTO") String task,@RequestParam(value = "files",required = false) List<MultipartFile> files){
        try{
            TaskDTO taskDTO = MappingHelper.getTaskDTOFromParam(task);
            Cache cache = cacheManager.getCache("taskCache");
            cache.clear();
            return new ResponseEntity<>(taskService.updateTask(taskDTO,files), HttpStatus.OK);
        } catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while updating. " + e.getMessage());
        }
    }



    //todo Check ProjectId
    @DeleteMapping(value = ProjectServiceURI.DELETE_TASK_URI)
    @PreAuthorize(PermissionConstants.MANAGE_TASKS)
    @CacheEvict(key = "#tId")
    public ResponseEntity<?> deleteTask(@RequestParam("tId") Long tId){
        try {
            Cache cache = cacheManager.getCache("taskCache");
            cache.clear();
            return new ResponseEntity<>(taskService.deleteTask(tId),HttpStatus.OK);
        } catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while deleting. " + e.getMessage());
        }
    }

    @GetMapping(ProjectServiceURI.GET_TASK_ATTACHMENT_BY_ID)
    public ResponseEntity<?> viewFile(@PathVariable("tId")Long taskId, @RequestParam("aId") Long attachmentId){
        try {
            return taskService.viewFile(taskId,attachmentId);
        }catch (Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in getting File." + e.getMessage());
        }
    }
    @GetMapping(ProjectServiceURI.GET_TASK_BY_USER_PROJECT_URI)
    public ResponseEntity<?> getTaskListByUserAndProjectId(@RequestParam("tId")Long taskId, @RequestParam("pId") Long projectId){
        try {
            return ResponseEntity.ok().body(taskService.getTaskListByUserAndProjectId(taskId,projectId));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in getting File." + e.getMessage());
        }
    }

    //todo Check ProjectId
    @DeleteMapping(ProjectServiceURI.DELETE_TASK_ATTACHMENT_BY_ID)
    @PreAuthorize(PermissionConstants.MANAGE_TASKS)
    @CacheEvict()
    public ResponseEntity<?> deleteFile(@PathVariable("tId")Long taskID,@RequestParam("aId") Long attachmentId){
        try {
            Cache cache = cacheManager.getCache("taskCache");
            cache.clear();
            return ResponseEntity.ok().body(taskService.deleteFile(taskID,attachmentId));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in getting File." + e.getMessage());
        }
    }

}
