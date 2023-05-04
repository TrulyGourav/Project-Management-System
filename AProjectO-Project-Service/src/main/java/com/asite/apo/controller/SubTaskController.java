package com.asite.apo.controller;


import com.asite.apo.api.PermissionConstants;
import com.asite.apo.api.ProjectURLConstant;
import com.asite.apo.dao.SubTaskRepo;
//import com.asite.apo.model.TaskListDTO;
import com.asite.apo.dto.SubTaskDTO;
import com.asite.apo.exception.SubTaskCreationException;
import com.asite.apo.exception.SubTaskDeleteException;
import com.asite.apo.exception.SubTaskGetException;
import com.asite.apo.exception.SubTaskUpdateException;
import com.asite.apo.service.SubTaskService;
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
import java.util.List;

@RestController
@RequestMapping(ProjectURLConstant.SUB_TASK_API)
@CacheConfig(cacheNames = "SubTaskCache")
public class SubTaskController {
    @Autowired
    SubTaskRepo t;
    @Autowired
    CacheManager cacheManager;
    @Autowired
    private SubTaskService subTaskService;

    //todo Check ProjectId
    @PostMapping(ProjectURLConstant.CREATE_SUB_TASK_URI)
    @PreAuthorize(PermissionConstants.MANAGE_SUB_TASKS)
    @ResponseBody
    @CachePut()
    public ResponseEntity<?> createTask(@RequestParam("subTaskDTO") String subTask, @RequestParam(value = "files",required = false) List<MultipartFile> files)
    {
        try {
            Cache subTaskCache = cacheManager.getCache("SubTaskCache");
            subTaskCache.clear();
            Cache taskCache = cacheManager.getCache("taskCache");
            taskCache.clear();

            SubTaskDTO subTaskDTO = MappingHelper.getSubTaskDTOFromParam(subTask);
            return new ResponseEntity<>(subTaskService.createSubTask(subTaskDTO,files), HttpStatus.OK);
        }catch (SubTaskCreationException subTaskCreationException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(subTaskCreationException.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body((e.getMessage()));
        }
    }

    @GetMapping()
    @Cacheable(key = "#id")
    public ResponseEntity<?> getSubTask(@RequestParam("id") Long id){
        try{
            return new ResponseEntity<>(subTaskService.getSubTask(id),HttpStatus.OK);
        }catch (SubTaskGetException subTaskGetException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(subTaskGetException.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping(ProjectURLConstant.GET_SUB_TASK_LIST_BY_USER_AND_PROJECT_URI)
    public ResponseEntity<?> getSubTaskByProject(@RequestParam("uId") Long uId,@RequestParam("pId") Long pId){
        try{
            return new ResponseEntity<>(subTaskService.getSubTaskByProject(uId,pId),HttpStatus.OK);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping(ProjectURLConstant.GET_SUB_TASK_LIST_BY_USER_AND_TASK_URI+"/{uId}/{tId}")
    @Cacheable(key = "#userId")
    public ResponseEntity<?> getSubTaskByTask(@PathVariable("uId") Long userId, @PathVariable("tId") Long taskId){
        try{
            return new ResponseEntity<>(subTaskService.getSubTaskByTask(userId,taskId),HttpStatus.OK);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping(value = ProjectURLConstant.GET_SUB_TASK_LIST_BY_USER_URI)
    public ResponseEntity<?> getSubTaskByUserid(@RequestParam("uid") Long id){
        try {
            return new ResponseEntity<>(subTaskService.getSubTaskByUserId(id),HttpStatus.OK);
        }catch (SubTaskGetException subTaskGetException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(subTaskGetException.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping(value = ProjectURLConstant.GET_SUB_TASK_LIST_BY_TASK_URI)
    @Cacheable(key = "#id")
    public ResponseEntity<?> getSubTaskByTaskId(@RequestParam("tid") Long id){
        try{
            return new ResponseEntity<>(subTaskService.getSubTaskByTaskId(id),HttpStatus.OK);
        }catch (SubTaskGetException subTaskGetException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(subTaskGetException.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        }
    }

    //todo Check ProjectId
    @PreAuthorize(PermissionConstants.MANAGE_SUB_TASKS)
    @PutMapping(value = ProjectURLConstant.UPDATE_SUB_TASK_URI)
    @CachePut()
    public ResponseEntity<?> updateSubTask(@RequestParam("subTaskDTO") String subTask,@RequestParam(value = "files",required = false) List<MultipartFile> files){
        try {
            Cache cache = cacheManager.getCache("subTaskCache");
            cache.clear();
            SubTaskDTO subTaskDTO = MappingHelper.getSubTaskDTOFromParam(subTask);
            return new ResponseEntity<>(subTaskService.updateSubTask(subTaskDTO,files), HttpStatus.OK);
        }catch (SubTaskUpdateException subTaskUpdateException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(subTaskUpdateException.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping(ProjectURLConstant.GET_SUBTASK_ATTACHMENT_BY_ID)
    public ResponseEntity<?> viewFile(@PathVariable("sId")Long taskId, @RequestParam("aId") Long attachmentId){
        try {
            return subTaskService.viewFile(taskId,attachmentId);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in getting File." + e.getMessage());
        }
    }

    //todo Check ProjectId
    @PreAuthorize(PermissionConstants.MANAGE_SUB_TASKS)
    @DeleteMapping(ProjectURLConstant.DELETE_SUBTASK_ATTACHMENT_BY_ID)
    @CacheEvict()
    public ResponseEntity<?> deleteFile(@PathVariable("sId")Long taskID,@RequestParam("aId") Long attachmentId){
        try {
            Cache cache = cacheManager.getCache("subTaskCache");
            cache.clear();
            return ResponseEntity.ok().body(subTaskService.deleteFile(taskID,attachmentId));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in getting File." + e.getMessage());
        }
    }

    @PreAuthorize(PermissionConstants.MANAGE_SUB_TASKS)
    @DeleteMapping(value = ProjectURLConstant.DELETE_SUB_TASK_URI+"/{sId}")
    @CacheEvict()
    public ResponseEntity<?> deleteSubTask(@PathVariable("sId") Long id){
        try{
            Cache cache = cacheManager.getCache("subTaskCache");
            cache.clear();
            return new ResponseEntity<>(subTaskService.deleteSubTask(id),HttpStatus.OK);
        }catch (SubTaskDeleteException subTaskDeleteException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(subTaskDeleteException.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
