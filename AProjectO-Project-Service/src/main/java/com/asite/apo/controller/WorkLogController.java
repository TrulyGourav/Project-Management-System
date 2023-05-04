package com.asite.apo.controller;

import com.asite.apo.api.ProjectURLConstant;
import com.asite.apo.dto.WorkLogDto;
import com.asite.apo.exception.ResourceNotFoundException;
import com.asite.apo.exception.WorkLogCreateException;
import com.asite.apo.exception.WorkLogDeleteException;
import com.asite.apo.exception.WorkLogUpdateException;
import com.asite.apo.service.WorkLogService;
import com.asite.apo.util.MappingHelper;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;

@RestController
@RequestMapping(ProjectURLConstant.WORKLOG_API)
public class WorkLogController {
    @Autowired
    WorkLogService workLogService;

    @GetMapping("/projectStatus/{projectId}")
    private ResponseEntity<?> getStatusByProject(@PathVariable("projectId") Long projectId) throws ParseException
    {
        String status = workLogService.getStatusByProject(projectId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<?> getWorkLog(){
        return new ResponseEntity<>(workLogService.getWorklog(),HttpStatus.OK);
    }

    @GetMapping(ProjectURLConstant.GET_WORKLOG_OF_PROJECT_BY_DATE+"/{date}/{id}")
    public ResponseEntity<?> getByProject(@PathVariable("date")String date,@PathVariable("id") Long id) throws ParseException, ResourceNotFoundException {
        return new ResponseEntity<>(workLogService.getByProject(date,id),HttpStatus.OK);
    }

    @GetMapping(ProjectURLConstant.GET_WORKLOG_OF_TASK_BY_DATE+"/{date}/{id}")
    public ResponseEntity<?> getWorklogByTask(@PathVariable("date")String date,@PathVariable("id") Long id) throws ParseException, ResourceNotFoundException {
        return new ResponseEntity<>(workLogService.getWorklogByTask(date,id),HttpStatus.OK);
    }

    @GetMapping(ProjectURLConstant.GET_WORKLOG_OF_SUBTASK_BY_DATE+"/{date}/{id}")
    public ResponseEntity<?> getWorklogBySubtask(@PathVariable("date")String date,@PathVariable("id") Long id) throws ParseException, ResourceNotFoundException {
        return new ResponseEntity<>(workLogService.getWorklogBySubtask(date,id),HttpStatus.OK);
    }

    @GetMapping(ProjectURLConstant.GET_TOTAL_HOURS_OF_USER_BY_DATE_ON_TASK+"/{date}/{id}/{tid}")
    public ResponseEntity<?> getTotalHoursOfUser(@PathVariable("date")String date,@PathVariable("id") Long id,@PathVariable("tid")Long tid) throws ParseException {
        return new ResponseEntity<>(workLogService.getTotalHoursOfUser(date,id,tid),HttpStatus.OK);
    }

    @GetMapping(ProjectURLConstant.GET_TOTAL_HOURS_OF_USER_BY_DATE_ON_PROJECT+"/{date}/{id}/{vid}")
    public ResponseEntity<?> getTotalHoursOfUserOnProject(@PathVariable("date")String date,@PathVariable("id") Long id,@PathVariable("vid")Long vid) throws ParseException {
        return new ResponseEntity<>(workLogService.getTotalHoursOfUserOnProject(date,id,vid),HttpStatus.OK);
    }

    @GetMapping(ProjectURLConstant.GET_TOTAL_HOURS_OF_USER_BY_DATE_ON_SUB_TASK+"/{date}/{id}/{sid}")
    public ResponseEntity<?> getTotalHoursOfUserOnSubTask(@PathVariable("date")String date,@PathVariable("id") Long id,@PathVariable("sid")Long sid) throws ParseException {
        return new ResponseEntity<>(workLogService.getTotalHoursOfUserOnSubTask(date,id,sid),HttpStatus.OK);
    }
    @GetMapping(ProjectURLConstant.GET_TOTAL_HOURS_OF_PROJECT+"/{id}")
    public ResponseEntity<?> getTotalHours(@PathVariable("id") Long id){
        return new ResponseEntity<>(workLogService.getTotalHours(id),HttpStatus.OK);
    }
    @GetMapping(ProjectURLConstant.GET_TOTAL_HOURS_OF_PROJECT_BY_TASK+"/{tid}/{pid}")
    public ResponseEntity<?> getTotalHoursOfTask(@PathVariable("tid") Long tId,@PathVariable("pid") Long pId){
        return new ResponseEntity<>(workLogService.getTotalHoursOfTask(tId,pId),HttpStatus.OK);
    }

    @GetMapping(ProjectURLConstant.GET_TOTAL_HOURS_OF_USER_BY_TASK+"/{userId}/{taskId}")
    public ResponseEntity<?> getTotalHoursOfUserByTask(@PathVariable("userId") Long userId,@PathVariable("taskId") Long taskId){
        try{
            return new ResponseEntity<>(workLogService.getTotalHoursOfUserByTask(userId,taskId),HttpStatus.OK);
        }catch(ResourceNotFoundException resourceNotFoundException){
            return new ResponseEntity<>(resourceNotFoundException.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(ProjectURLConstant.GET_TOTAL_HOURS_OF_PROJECT_BY_SUBTASK+"/{sid}/{pid}")
    public ResponseEntity<?> getTotalHoursofSubTask(@PathVariable("sid") Long sId,@PathVariable("pid") Long pId){
        return new ResponseEntity<>(workLogService.getTotalHoursOfSubTask(sId,pId),HttpStatus.OK);
    }

    @GetMapping(ProjectURLConstant.GET_STATUS+"/{id}/{pid}")
    public ResponseEntity<?> getStatus(@PathVariable("id")Long id,@PathVariable("pid") Long pId) throws ParseException {
        return new ResponseEntity<>(workLogService.getStatusByTask(id,pId), HttpStatus.OK);
    }

    @GetMapping(ProjectURLConstant.GET_INTERVAL_OF_PROJECT_BY_USER+"/{uid}/{start}/{end}/{pid}")
    public ResponseEntity<?> getIntervalOfProjectByUser(@PathVariable("uid") Long uId,@PathVariable("start") String start,@PathVariable("end") String end,@PathVariable("pid") Long pId) throws ParseException {
        return new ResponseEntity<>(workLogService.getIntervalOfProjectByUser(uId,start,end,pId),HttpStatus.OK);
    }

    @GetMapping(ProjectURLConstant.GET_INTERVAL_OF_TASK_BY_USER+"/{uid}/{start}/{end}/{tid}")
    public ResponseEntity<?> getIntervalOfTaskByUser(@PathVariable("uid") Long uId,@PathVariable("start") String start,@PathVariable("end") String end,@PathVariable("tid") Long tId) throws ParseException {
        return new ResponseEntity<>(workLogService.getIntervalOfTaskByUser(uId,start,end,tId),HttpStatus.OK);
    }
    @GetMapping(ProjectURLConstant.GET_INTERVAL_OF_SUBTASK_BY_USER+"/{uid}/{start}/{end}/{sid}")
    public ResponseEntity<?> getIntervalOfSubTaskByUser(@PathVariable("uid") Long uId,@PathVariable("start") String start,@PathVariable("end") String end,@PathVariable("sid") Long sId) throws ParseException {
        return new ResponseEntity<>(workLogService.getIntervalOfSubTaskByUser(uId,start,end,sId),HttpStatus.OK);
    }
    @GetMapping(ProjectURLConstant.GET_WORKLOG_BY_VERSION_ID+"/{id}")
    public ResponseEntity<?> getByVersion(@PathVariable("id") Long id){
        try{
            return new ResponseEntity<>(workLogService.getByVersion(id),HttpStatus.OK);
        }catch (ResourceNotFoundException resourceNotFoundException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resourceNotFoundException.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping(ProjectURLConstant.GET_WORKLOG_BY_TASK_ID+"/{id}")
    public ResponseEntity<?> getByTask(@PathVariable("id") Long id){
        try{
            return new ResponseEntity<>(workLogService.getByTask(id),HttpStatus.OK);
        }catch (ResourceNotFoundException resourceNotFoundException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resourceNotFoundException.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping(ProjectURLConstant.GET_WORKLOG_BY_USER_ID+"/{id}")
    public ResponseEntity<?> getByUser(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(workLogService.getByUser(id), HttpStatus.OK);
        } catch (ResourceNotFoundException resourceNotFoundException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resourceNotFoundException.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping(ProjectURLConstant.GET_WORKLOG_BY_SUB_TASK_ID+"/{id}")
    public ResponseEntity<?> getBySubTask(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(workLogService.getBySubTask(id), HttpStatus.OK);
        } catch (ResourceNotFoundException resourceNotFoundException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resourceNotFoundException.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping(ProjectURLConstant.GET_WORKLOG_OF_USER_BY_TASK+"/{uid}/{tid}")
    public ResponseEntity<?> getWorklogOfUserByTask(@PathVariable("uid") Long uId,@PathVariable("tid") Long tId){
        try{
            return new ResponseEntity<>(workLogService.getWorklogOfUserByTask(uId,tId),HttpStatus.OK);
        }catch (ResourceNotFoundException resourceNotFoundException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resourceNotFoundException.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping(ProjectURLConstant.GET_WORKLOG_OF_USER_BY_PROJECT+"/{uid}/{pid}")
    public ResponseEntity<?> getWorkLogByProject(@PathVariable("uid") Long uId,@PathVariable("pid") Long pId){
        try{
            return new ResponseEntity<>(workLogService.getWorklogOfUserByProject(uId,pId),HttpStatus.OK);
        }catch (ResourceNotFoundException resourceNotFoundException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resourceNotFoundException.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping(ProjectURLConstant.GET_WORKLOG_OF_USER_BY_SUB_TASK+"/{uid}/{sid}")
    public ResponseEntity<?> getWorklogofUserBySubTask(@PathVariable("uid") Long uId,@PathVariable("sid") Long sId){
        try {
            return new ResponseEntity<>(workLogService.getWorklogOfUserBySubTask(uId,sId), HttpStatus.OK);
        }catch (ResourceNotFoundException resourceNotFoundException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resourceNotFoundException.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping()
    public ResponseEntity<?> createWorklog(@Valid @RequestBody WorkLogDto workLogDto){
        try {
            return new ResponseEntity<>(workLogService.createWorkLog(workLogDto), HttpStatus.OK);
        }catch (WorkLogCreateException workLogCreateException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(workLogCreateException.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWorkLog(@PathVariable("id") Long id,@Valid @RequestBody WorkLogDto workLog){
        try {
            return new ResponseEntity<>(workLogService.updateWorkLog(id,workLog), HttpStatus.OK);
        }catch (WorkLogUpdateException workLogUpdateException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(workLogUpdateException.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWorkLog(@PathVariable("id") Long id){
        try {
            return new ResponseEntity<>(workLogService.deleteWorkLog(id), HttpStatus.OK);
        }catch (WorkLogDeleteException workLogDeleteException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(workLogDeleteException.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
