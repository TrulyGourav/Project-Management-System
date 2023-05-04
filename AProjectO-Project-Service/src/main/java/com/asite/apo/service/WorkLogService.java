package com.asite.apo.service;

import com.asite.apo.api.ExceptionHandlingConstants;
import com.asite.apo.dao.*;
import com.asite.apo.dto.WorkLogDto;
import com.asite.apo.dto.WorkLogListDTO;
import com.asite.apo.exception.ResourceNotFoundException;
import com.asite.apo.exception.WorkLogCreateException;
import com.asite.apo.exception.WorkLogDeleteException;
import com.asite.apo.exception.WorkLogUpdateException;
import com.asite.apo.model.*;
import com.asite.apo.util.GeneralUtil;
import com.asite.apo.util.MappingHelper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.text.ParseException;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class WorkLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkLogService.class);
    @Autowired
    private ProjectRepo projectRepo;
    @Autowired
    private ProjectIterationRepo projectIterationRepo;
    @Autowired
    private WorkLogRepo workLogRepo;
    @Autowired
    private TaskRepo taskRepo;
    @Autowired
    private SubTaskRepo subTaskRepo;
    @Autowired
    private ModelMapper modelMapper;

    public List<WorkLogDto> getWorklog() {
        LOGGER.debug("Getting Worklog");
        return workLogRepo.findAll()
                .stream()
                .map(MappingHelper::getWorkLogDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public WorkLogDto createWorkLog(WorkLogDto workLogDto) throws WorkLogCreateException {

        LOGGER.debug("Creating Worklog",workLogDto);
        ProjectIterationModel projectIterationModel = projectIterationRepo.findById(workLogDto.getProjectIteration().getVersionId()).get();
        TaskModel taskModel = taskRepo.findById(workLogDto.getTask().getTaskId()).get();
        SubTaskModel subTaskModel = subTaskRepo.findById(workLogDto.getSubTask().getSubTaskId()).get();

        if(workLogDto.getStartTime()==null){
            throw new WorkLogCreateException("Start Time Cannot be Null");
        }else {
            WorkLogModel workLogModel = MappingHelper.getWorklogModel(workLogDto);
            workLogModel.setProjectVersionId(projectIterationModel);
            workLogModel.setTaskId(taskModel);
            workLogModel.setSubTaskId(subTaskModel);

            workLogRepo.save(workLogModel);
            LOGGER.debug("Worklog Created", workLogDto);
            return MappingHelper.getWorkLogDto(workLogModel);
        }
    }

    @Transactional
    public WorkLogDto updateWorkLog(Long id,WorkLogDto workLogDto) throws WorkLogUpdateException {

        LOGGER.debug("Updating Worklog",id,workLogDto);
        if(!projectIterationRepo.findById(workLogDto.getProjectIteration().getVersionId()).isPresent() || !taskRepo.findById(workLogDto.getTask().getTaskId()).isPresent() || !subTaskRepo.findById(workLogDto.getSubTask().getSubTaskId()).isPresent()){
            throw new WorkLogUpdateException("Not Found");
        } else if (workLogDto.getEndTime()==null || workLogDto.getHoursWorked()==null) {
            throw new WorkLogUpdateException("End time or hours worked cannot be null");
        }else {
            WorkLogModel workLogModel = workLogRepo.findById(id).get();
            WorkLogModel updated = MappingHelper.getUpdatedModel(workLogModel, workLogDto);
            workLogRepo.save(updated);
            LOGGER.debug("Worklog Updated", id, workLogDto);
            return MappingHelper.getWorkLogDto(workLogRepo.getReferenceById(id));
        }
    }

    @Transactional
    public ResponseEntity<?> deleteWorkLog(Long id) throws WorkLogDeleteException {
        LOGGER.debug("Deleting Worklog",id);
        WorkLogModel workLogModel = workLogRepo.findById(id).orElse(null);
        if(workLogModel!=null){
            workLogRepo.delete(workLogModel);
            LOGGER.debug("Worklog Deleted",id);
            return new ResponseEntity<>("Worklog Deleted Successfully",HttpStatus.OK);
        }else {
            throw new WorkLogDeleteException(ExceptionHandlingConstants.WORKLOGID_NOT_FOUND);
        }
    }

    public List<WorkLogListDTO> getByVersion(Long id) throws ResourceNotFoundException {
        LOGGER.debug("Getting Worklog by Version",id);
        if(!projectIterationRepo.findById(id).isPresent()){
            throw new ResourceNotFoundException(ExceptionHandlingConstants.VERSIONID_NOT_FOUND);
        }
        else{
            LOGGER.debug("Found Worklog by Version",id);
            return workLogRepo.findByProjectVersionId_versionId(id).stream()
                    .map(MappingHelper::getWorkLogListDTO)
                    .collect(Collectors.toList());
        }
    }

    public List<WorkLogListDTO> getByUser(Long id) throws ResourceNotFoundException {
            LOGGER.debug("Getting Worklog by User",id);
//        if(!.findById(id).isPresent()){
//            throw new ResourceNotFoundException(ExceptionHandlingConstants.VERSIONID_NOT_FOUND);
//        }
            LOGGER.debug("Found Worklog by User",id);
            return workLogRepo.findByUserId(id).stream()
                    .map(MappingHelper::getWorkLogListDTO)
                    .collect(Collectors.toList());

    }

    public List<WorkLogListDTO> getByTask(Long id) throws ResourceNotFoundException {
            LOGGER.debug("Getting Worklog by Task",id);
        if(!taskRepo.findById(id).isPresent()){
            throw new ResourceNotFoundException(ExceptionHandlingConstants.TASKID_NOT_FOUND);
        }
        else {
            LOGGER.debug("Found Worklog by Task",id);
            return workLogRepo.findByTaskId_taskId(id).stream()
                    .map(MappingHelper::getWorkLogListDTO)
                    .collect(Collectors.toList());
        }
    }

    public List<WorkLogListDTO> getBySubTask(Long id) throws ResourceNotFoundException {
            LOGGER.debug("Getting Worklog by SubTask",id);
        SubTaskModel subTaskModel = subTaskRepo.findById(id).orElse(null);
        if(!subTaskRepo.findById(id).isPresent()){
            throw new ResourceNotFoundException(ExceptionHandlingConstants.TASKID_SUBTASKID_NOT_FOUND);
        }
        else {
            LOGGER.debug("Found Worklog by SubTask",id);
            return workLogRepo.findBySubTaskId_subTaskId(id).stream()
                    .map(MappingHelper::getWorkLogListDTO)
                    .collect(Collectors.toList());
        }
    }

    public List<WorkLogListDTO> getByProject(String date,Long id) throws ParseException, ResourceNotFoundException {
        LOGGER.debug("Getting Worklog by Project",id,date);
        if(!projectRepo.findById(id).isPresent()){
            throw new ResourceNotFoundException("Project Version Not Found");
        }
        else {
            Date dateFormat = GeneralUtil.dateFormat.parse(date);
            LOGGER.debug("Found Worklog by Project",id,date);
            return workLogRepo.getByProject(dateFormat,id).stream()
                    .map(MappingHelper::getWorkLogListDTO)
                    .collect(Collectors.toList());
        }
    }

    public List<WorkLogListDTO> getWorklogByTask(String date, Long id) throws ParseException, ResourceNotFoundException {
            LOGGER.debug("Getting Total Hours by Task",id,date);
        if(!taskRepo.findById(id).isPresent()){
            throw new ResourceNotFoundException(ExceptionHandlingConstants.TASKID_NOT_FOUND);
        }
        else {
            Date dateFormat = GeneralUtil.dateFormat.parse(date);
            LOGGER.debug("Found Total Hours by Task",id,date);
            return workLogRepo.getByTask(dateFormat,id).stream()
                    .map(MappingHelper::getWorkLogListDTO)
                    .collect(Collectors.toList());
        }
    }

    public List<WorkLogListDTO> getWorklogBySubtask(String date,Long id) throws ParseException, ResourceNotFoundException {
            LOGGER.debug("Getting Total Hours by SubTask",id,date);
        if(!subTaskRepo.findById(id).isPresent()){
            throw new ResourceNotFoundException(ExceptionHandlingConstants.TASKID_SUBTASKID_NOT_FOUND);
        }
        else {
            LOGGER.debug("Found Total Hours by SubTask",id,date);
            Date dateFormat = GeneralUtil.dateFormat.parse(date);
            return workLogRepo.getBySubtask(dateFormat,id).stream()
                    .map(MappingHelper::getWorkLogListDTO)
                    .collect(Collectors.toList());
        }
    }

    public Double getTotalHours(Long id) {
        Double total = workLogRepo.getTotalHoursByProject(id);
        return total;
    }

    public Double getTotalHoursOfTask(Long tId,Long pId) {
        Double total = workLogRepo.getTotalHoursByTask(tId,pId);
        return total;
    }

    public Double getTotalHoursOfSubTask(Long sId,Long pId) {
        Double total = workLogRepo.getTotalHoursBySubTask(sId,pId);
        return total;
    }

    public Double getTotalHoursOfUser(String date, Long id,Long tid) throws ParseException {
        Date dateFormat = GeneralUtil.dateFormat.parse(date);
        Double total = workLogRepo.getTotalHoursOfUser(id,dateFormat,tid);
        return total;
    }

    public Double getTotalHoursOfUserOnProject(String date, Long id,Long vid) throws ParseException {
        Date dateFormat = GeneralUtil.dateFormat.parse(date);
        Double total = workLogRepo.getTotalHoursOfUserOnProject(id,dateFormat,vid);
        return total;
    }

    public Double getTotalHoursOfUserOnSubTask(String date, Long id, Long sid) throws ParseException {
        Date dateFormat = GeneralUtil.dateFormat.parse(date);
        Double total = workLogRepo.getTotalHoursOfUserOnSubTask(id,dateFormat,sid);
        return total;
    }

    public List<WorkLogListDTO> getIntervalOfProjectByUser(Long uid, String start, String end,Long pId) throws ParseException {
        Date startDate = GeneralUtil.dateFormat.parse(start);
        Date endDate = GeneralUtil.dateFormat.parse(end);

        return workLogRepo.getIntervalOfProjectByUser(uid,startDate,endDate,pId).stream()
                .map(MappingHelper::getWorkLogListDTO)
                .collect(Collectors.toList());
    }

    public List<WorkLogListDTO> getIntervalOfTaskByUser(Long uId, String start, String end,Long tId) throws ParseException {
        Date startDate = GeneralUtil.dateFormat.parse(start);
        Date endDate = GeneralUtil.dateFormat.parse(end);

        return workLogRepo.getIntervalOfTaskByUser(uId,startDate,endDate,tId).stream()
                .map(MappingHelper::getWorkLogListDTO)
                .collect(Collectors.toList());
    }

    public List<WorkLogListDTO> getIntervalOfSubTaskByUser(Long uId, String start, String end, Long sId) throws ParseException {
        Date startDate = GeneralUtil.dateFormat.parse(start);
        Date endDate = GeneralUtil.dateFormat.parse(end);

        return workLogRepo.getIntervalOfSubTaskByUser(uId,startDate,endDate,sId).stream()
                .map(MappingHelper::getWorkLogListDTO)
                .collect(Collectors.toList());
    }

    public String getStatusBySubTask(Long sId,Long pId) throws ParseException {
        String status = "";
        SubTaskModel subTaskModel = subTaskRepo.findById(sId).orElse(null);
        if(subTaskModel!=null){
            String deadLine = String.valueOf(subTaskModel.getDeadLine());
            String startDate = String.valueOf(subTaskModel.getStartDate());
            Double timeSpent = getTotalHoursOfSubTask(sId,pId);
            Date start = GeneralUtil.timeFormat.parse(startDate);
            Date end = GeneralUtil.timeFormat.parse(deadLine);

            double difference = end.getTime()-start.getTime();
            double hours = difference/(60*60*1000);
            if(hours<=timeSpent){
                status = "Off Track";
            }
            if(hours>timeSpent){
                status = "On Track";
            }
        }
        return status;
    }

    public String getStatusByTask(Long id,Long pId) throws ParseException {
        String status = "";
        TaskModel taskModel = taskRepo.findById(id).orElse(null);
        if(taskModel!=null){
            String deadLine = String.valueOf(taskModel.getTaskDeadline());
            String startDate = String.valueOf(taskModel.getStartDate());
            Double timeSpent = getTotalHoursOfTask(id,pId);
            Date start = GeneralUtil.timeFormat.parse(startDate);
            Date end = GeneralUtil.timeFormat.parse(deadLine);

            double difference = end.getTime()-start.getTime();
            double hours = difference/(60*60*1000);
            if(hours<=timeSpent){
                status = "Off Track";
            }
            if(hours>timeSpent){
                status = "On Track";
            }
        }
        return status;
    }

    public String getStatusOfTask(Long tId) throws ParseException {
        String status = "";
        TaskModel taskModel = taskRepo.findById(tId).orElse(null);
        if(taskModel!=null){
            List<SubTaskModel> subTaskModels = taskModel.getSubtasks();

            List<Long> subTaskIds = new ArrayList<>();
            for(SubTaskModel subTask : subTaskModels){
                subTaskIds.add(subTask.getSubTaskId());
            }
            String deadLine = String.valueOf(taskModel.getTaskDeadline());
            String startDate = String.valueOf(taskModel.getStartDate());
            Double timeSpent = workLogRepo.getTotalHoursOfTask(subTaskIds);
            Date start = GeneralUtil.timeFormat.parse(startDate);
            Date end = GeneralUtil.timeFormat.parse(deadLine);

            double difference = end.getTime()-start.getTime();
            double hours = difference/(60*60*1000);
            if(hours<=timeSpent){
                status = "Off Track";
            }
            if(hours>timeSpent){
                status = "On Track";
            }
        }
        return status;
    }


    public List<WorkLogListDTO> getWorklogOfUserByTask(Long uId, Long tId) throws ResourceNotFoundException {
        if(!taskRepo.findById(tId).isPresent()){
            throw new ResourceNotFoundException(ExceptionHandlingConstants.TASKID_NOT_FOUND);
        }else {
            return workLogRepo.findAllByUserIdAndTaskId_taskId(uId,tId).stream()
                    .map(MappingHelper::getWorkLogListDTO)
                    .collect(Collectors.toList());
        }
    }

    public List<WorkLogListDTO> getWorklogOfUserBySubTask(Long uId, Long sId) throws ResourceNotFoundException {
        if(!subTaskRepo.findById(sId).isPresent()){
            throw new ResourceNotFoundException(ExceptionHandlingConstants.TASKID_SUBTASKID_NOT_FOUND);
        }else {
            return workLogRepo.findAllByUserIdAndSubTaskId_subTaskId(uId,sId).stream()
                    .map(MappingHelper::getWorkLogListDTO)
                    .collect(Collectors.toList());
        }
    }

    public List<WorkLogListDTO> getWorklogOfUserByProject(Long uId, Long pId) throws ResourceNotFoundException {
        if(!projectRepo.findById(pId).isPresent()){
            throw new ResourceNotFoundException(ExceptionHandlingConstants.VERSIONID_NOT_FOUND);
        }else{
            return workLogRepo.findAllByUserIdAndProjectVersionId_versionId(uId,pId).stream()
                    .map(MappingHelper::getWorkLogListDTO)
                    .collect(Collectors.toList());
        }
    }

    public String getStatusByProject(Long id) throws ParseException {
        String status = "";
        ProjectModel projectModel = projectRepo.findById(id).orElse(null);
        if(projectModel!=null){
            List<TaskModel> taskModels = projectModel.getTasks();

            List<Long> taskIds = new ArrayList<>();
            for(TaskModel task : taskModels){
                // Added
                if (task.getTaskStatus().equals("Done")){
                    taskIds.add(task.getTaskId());
                }
            }

            Double timeSpent = workLogRepo.getTotalHoursWorkedByTask(taskIds);

            LocalDate start = GeneralUtil.dateFormat.parse(String.valueOf(projectModel.getStartDate())).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate end = GeneralUtil.dateFormat.parse(String.valueOf(projectModel.getDeadline())).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if (timeSpent!=null) {
                double days = DAYS.between(end, start);
                double total = days * 24;

                System.out.println("Total Time Spent "+timeSpent);
                System.out.println("Total Time"+total);
                System.out.println("Total Time"+days);

                if (total <= timeSpent) {
                    status = "On Track";
                } else {
                    status = "Off Track";
                }
            }
            else
                status = "On Track";
        }
        return status;
    }

    public Double getTotalHoursOfUserByTask(Long userId, Long taskId) throws ResourceNotFoundException {
        if(!taskRepo.findById(taskId).isPresent()){
            throw new ResourceNotFoundException(ExceptionHandlingConstants.TASKID_NOT_FOUND);
        }else {
            return workLogRepo.getTotalHoursOfUserByTask(userId, taskId);
        }
    }


}

