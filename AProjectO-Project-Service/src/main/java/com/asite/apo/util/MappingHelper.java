package com.asite.apo.util;

import com.asite.apo.dto.*;
import com.asite.apo.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.Valid;
import java.beans.PropertyDescriptor;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MappingHelper {

    @PersistenceContext
    private static EntityManager entityManager;


    public static CreateProjectItrDTO projectModelDTOToCreateProjectItrDTO(ProjectModelDTO projectModelDTO,CreateProjectItrDTO createProjectItrDTO)
    {
        createProjectItrDTO.setProjectId(projectModelDTO.getProjectId());
        createProjectItrDTO.setStartDate(projectModelDTO.getStartDate());
        createProjectItrDTO.setVersionName(projectModelDTO.getProjectName()+" v1");
        createProjectItrDTO.setVersionDesc(projectModelDTO.getProjectDesc());
        return createProjectItrDTO;
    }

        public static @Valid TaskDTO getTaskDTOFromParam(String str)
        {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                return mapper.readValue(str, TaskDTO.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        public static String[] getNullPropertyNames(Object source) {
            BeanWrapper src = new BeanWrapperImpl(source);
            PropertyDescriptor[] pds = src.getPropertyDescriptors();

            return Stream.of(pds)
                    .map(PropertyDescriptor::getName)
                    .filter(name -> src.getPropertyValue(name) == null)
                    .toArray(String[]::new);
        }
        public static TaskListDTO getTaskListDTO(TaskModel task)
        {
            TaskListDTO dto=new TaskListDTO();
            BeanUtils.copyProperties(task,dto);
            return dto;
        }
        public static TaskModel getTaskModel(TaskDTO task)
        {
            TaskModel model = new TaskModel();
            BeanUtils.copyProperties(task,model,getNullPropertyNames(task));
            return model;
        }
        public static TaskDTO getTaskDTO(TaskModel taskModel)
        {
            TaskDTO dto= new TaskDTO();
            BeanUtils.copyProperties(taskModel,dto);
            dto.setProjectTaskDTO(getProjectTaskDTO(taskModel.getProjectId()));
            dto.setProjectVersionDTO(getProjectVersionDTO(taskModel.getProjectVersionId()));
            List<TaskAttachmentModel> attachmentModelList = taskModel.getTaskAttachments();
            if(attachmentModelList!=null&&!attachmentModelList.isEmpty())
            {
                List<TaskAttachmentDTO> taskAttachmentsDTO = attachmentModelList.stream()
                        .map(MappingHelper::getTaskAttachmentDTO)
                        .collect(Collectors.toList());
                dto.setTaskAttachmentDTOList(taskAttachmentsDTO);
            }
            if(taskModel.getSubtasks()!=null)
                dto.setSubTaskDTOList(taskModel.getSubtasks().stream().map(MappingHelper::getSubTaskListDTO).collect(Collectors.toList()));
            return dto;
        }

        public static TaskListResDTO getTaskListResDTO(List<TaskModel> list)
        {
            List<TaskListDTO> backLogList = new ArrayList<>();
            List<TaskListDTO> inProgressList = new ArrayList<>();
            List<TaskListDTO> completedList = new ArrayList<>();
            list.forEach(taskModel -> {
                if (taskModel.getTaskStatus()==null||taskModel.getTaskStatus().equalsIgnoreCase("Backlog"))
                    backLogList.add(getTaskListDTO(taskModel));
                else if (taskModel.getTaskStatus().equalsIgnoreCase("Done"))
                    completedList.add(getTaskListDTO(taskModel));
                else
                    inProgressList.add(getTaskListDTO(taskModel));
            });
            return getTaskListResDTO(backLogList,inProgressList,completedList);
        }

        public static TaskListResDTO getTaskListResDTO(List<TaskListDTO> backlog, List<TaskListDTO> inProgress,List<TaskListDTO> completed){
            TaskListResDTO taskListResDTO = new TaskListResDTO();
            taskListResDTO.setBacklogTaskList(backlog);
            taskListResDTO.setInProgressTaskList(inProgress);
            taskListResDTO.setCompletedTaskList(completed);
            return taskListResDTO;
        }

        private static ProjectTaskDTO getProjectTaskDTO(ProjectModel project) {
            ProjectTaskDTO dto = new ProjectTaskDTO();
            BeanUtils.copyProperties(project,dto);
            return dto;
        }

        public static TaskAttachmentDTO getTaskAttachmentDTO(TaskAttachmentModel attach) {
            TaskAttachmentDTO dto = new TaskAttachmentDTO();
            BeanUtils.copyProperties(attach,dto);
            return dto;
        }
        public static TaskModel getUpdatedModel(TaskModel oldModel, TaskDTO dto){
            BeanUtils.copyProperties(dto,oldModel,getNullPropertyNames(dto));
            return oldModel;
        }

        public static TaskAttachmentModel findAttachmentByName(List<TaskAttachmentModel> attachments, String fileName) {
            for (TaskAttachmentModel attachment : attachments) {
                if (attachment.getTaskAttachmentName().equals(fileName)) {
                    return attachment;
                }
            }
            return null;
        }

    public static SubTaskModel getTaskModel(SubTaskDTO task)
    {
        SubTaskModel model = new SubTaskModel();
        BeanUtils.copyProperties(task,model,getNullPropertyNames(task));
        return model;
    }

    public static SubTaskListDTO getSubTaskListDTO(SubTaskModel subTask)
    {
        SubTaskListDTO dto=new SubTaskListDTO();
        BeanUtils.copyProperties(subTask,dto);
        return dto;
    }
    public static SubTaskAttachmentModel getAttachmentModel(SubTaskAttachmentDTO dto)
    {
        SubTaskAttachmentModel model = new SubTaskAttachmentModel();
        BeanUtils.copyProperties(dto,model);
        return model;
    }

    public static CreateCommentDTO getCreateCommentDTOFromCommentModel(CommentModel commentModel) {
        CreateCommentDTO createCommentDTO = new CreateCommentDTO();
        BeanUtils.copyProperties(commentModel, createCommentDTO);
        return createCommentDTO;
    }

    public static CommentModel getCommentModelFromCreateCommentDTO(CreateCommentDTO createCommentDTO) {
        CommentModel commentModel = new CommentModel();
        BeanUtils.copyProperties(createCommentDTO, commentModel, getNullPropertyNames(createCommentDTO));
        return commentModel;
    }





    public static WorkLogModel getWorklogModel(WorkLogDto workLogDto)
    {
        WorkLogModel model = new WorkLogModel();
        BeanUtils.copyProperties(workLogDto,model,getNullPropertyNames(workLogDto));
        return model;
    }

    public static TaskSubDTO getTaskSubDTO(TaskModel taskModel){
        TaskSubDTO dto=new TaskSubDTO();
        BeanUtils.copyProperties(taskModel,dto);
        return dto;
    }
    public static SubTaskDTO getSubTaskDTOFromParam(String str)
    {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.readValue(str,SubTaskDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static SubTaskModel getSubTaskModel(SubTaskDTO task)
    {
        SubTaskModel model = new SubTaskModel();
        BeanUtils.copyProperties(task,model,getNullPropertyNames(task));
        return model;
    }
    public static SubTaskDTO getSubTaskDTO(SubTaskModel taskModel)
    {
        SubTaskDTO dto= new SubTaskDTO();
        BeanUtils.copyProperties(taskModel,dto);
        dto.setTaskSubDTO(getTaskSubDTO(taskModel.getTaskId()));
        List<SubTaskAttachmentModel> attachmentModelList = taskModel.getSubTasksAttachments();
        if(attachmentModelList!=null&&!attachmentModelList.isEmpty())
        {
            List<SubTaskAttachmentDTO> subTaskAttachmentDTOS = attachmentModelList.stream()
                    .map(MappingHelper::getSubTaskAttachmentDTO)
                    .collect(Collectors.toList());
            dto.setSubTasksAttachments(subTaskAttachmentDTOS);
        }
        return dto;
    }

    private static SubTaskAttachmentDTO getSubTaskAttachmentDTO(SubTaskAttachmentModel attachmentModel) {
        SubTaskAttachmentDTO subTaskAttachmentDTO = new SubTaskAttachmentDTO();
        BeanUtils.copyProperties(attachmentModel,subTaskAttachmentDTO);
        return subTaskAttachmentDTO;

    }

    public static SubTaskAttachmentModel findSubTaskAttachmentByName(List<SubTaskAttachmentModel> attachments, String fileName) {
        for (SubTaskAttachmentModel attachment : attachments) {
            if (attachment.getSubTaskAttachmentName().equals(fileName)) {
                return attachment;
            }
        }
        return null;
    }
    public static SubTaskModel getUpdatedModel(SubTaskModel oldModel, SubTaskDTO dto){
        BeanUtils.copyProperties(dto,oldModel,getNullPropertyNames(dto));
        return oldModel;
    }

    public static ProjectModel projectModelDTOToProjectModel(ProjectModel projectModel, ProjectModelDTO projectModelDTO) throws Exception {
        BeanUtils.copyProperties(projectModelDTO,projectModel);
        projectModel.setDeadline(GeneralUtil.dateFormat.parse(projectModelDTO.getDeadline()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        projectModel.setStartDate(GeneralUtil.dateFormat.parse(projectModelDTO.getStartDate()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        if (projectModelDTO.getEndDate() != null)
            projectModel.setEndDate(GeneralUtil.dateFormat.parse(projectModelDTO.getEndDate()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        if (!projectModel.getStartDate().isBefore(projectModel.getDeadline()))
        {
            throw new Exception("Start Date is behind Deadline");
        }

        return projectModel;
    }

    public static ProjectModelDTO projectModelToProjectModelDTO(ProjectModel projectModel, ProjectModelDTO projectModelDTO)
    {
        BeanUtils.copyProperties(projectModel,projectModelDTO);
        projectModelDTO.setStartDate(projectModel.getStartDate().toString());
        projectModelDTO.setStartDate(projectModel.getDeadline().toString());
        return projectModelDTO;
    }

    public static ProjectIterationModel getProjectItrModelFromCreateProjectItrDTO(CreateProjectItrDTO createProjectItrDTO) {
        ProjectIterationModel projectIterationModel = new ProjectIterationModel();
        BeanUtils.copyProperties(createProjectItrDTO, projectIterationModel, getNullPropertyNames(createProjectItrDTO));
        return projectIterationModel;
    }

    public static GetVersionDTO getVersionDTOFromProjectItrModel(ProjectIterationModel iteration) {

        GetVersionDTO getVersionDTO = new GetVersionDTO();
        getVersionDTO.setProjectId(iteration.getProject().getProjectId());
        BeanUtils.copyProperties(iteration, getVersionDTO, getNullPropertyNames(iteration));
        return getVersionDTO;
    }

    public static List<GetByProjectDTO> getGetByProjectDTOsFromProjectModel(List<ProjectIterationModel> projectIterationModels) {

        List<GetByProjectDTO> getByProjectDTOs = new ArrayList<>();
        for (ProjectIterationModel pim : projectIterationModels) {
            GetByProjectDTO getByProjectDTO = new GetByProjectDTO();
            BeanUtils.copyProperties(pim, getByProjectDTO);
            getByProjectDTOs.add(getByProjectDTO);
        }
        return getByProjectDTOs;
    }

    public static ProjectIterationModel getUpdatedModelFromUpdateProjectDTO(UpdateProjectItrDTO updateProjectItrDTO) {

        ProjectIterationModel updatedProjectItr = new ProjectIterationModel();
        BeanUtils.copyProperties(updateProjectItrDTO, updatedProjectItr);
        return updatedProjectItr;

    }

    public static WorkLogListDTO getWorkLogListDTO(WorkLogModel workLogModel)
    {
        WorkLogListDTO dto=new WorkLogListDTO();
        dto.setProjectIteration(getProjectIterationWorkDTO(workLogModel.getProjectVersionId()));
        dto.setTask(getTaskWorkDTO(workLogModel.getTaskId()));
        dto.setSubTask(getSubTaskWorkDTO(workLogModel.getSubTaskId()));
        BeanUtils.copyProperties(workLogModel,dto);
        return dto;
    }

    private static ProjectVersionDTO getProjectVersionDTO(ProjectIterationModel projectIterationModel)
    {
        ProjectVersionDTO projectVersionDTO = new ProjectVersionDTO();
        BeanUtils.copyProperties(projectIterationModel,projectVersionDTO);
        return projectVersionDTO;
    }

    public static WorkLogModel getUpdatedModel(WorkLogModel oldModel, WorkLogDto dto){
        BeanUtils.copyProperties(dto,oldModel,getNullPropertyNames(dto));
        return oldModel;
    }

    public static WorkLogDto getWorkLogDTOFromParam(String str)
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.readValue(str,WorkLogDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    private static TaskWorkDTO getTaskWorkDTO(TaskModel taskModel) {
        TaskWorkDTO dto = new TaskWorkDTO();
        BeanUtils.copyProperties(taskModel,dto);
        return dto;
    }
    private static SubTaskWorkDTO getSubTaskWorkDTO(SubTaskModel subTaskModel) {
        SubTaskWorkDTO dto = new SubTaskWorkDTO();
        BeanUtils.copyProperties(subTaskModel,dto);
        return dto;
    }
    private static ProjectIterationWorkDTO getProjectIterationWorkDTO(ProjectIterationModel projectVersionId) {
        ProjectIterationWorkDTO dto = new ProjectIterationWorkDTO();
        dto.setProjectId(projectVersionId.getProject().getProjectId());
        BeanUtils.copyProperties(projectVersionId,dto);
        return dto;
    }
    public static WorkLogDto getWorkLogDto(WorkLogModel workLogModel)
    {
        WorkLogDto dto= new WorkLogDto();
        BeanUtils.copyProperties(workLogModel,dto);

        dto.setProjectIteration(getProjectIterationWorkDTO(workLogModel.getProjectVersionId()));
        dto.setTask(getTaskWorkDTO(workLogModel.getTaskId()));
        dto.setSubTask(getSubTaskWorkDTO(workLogModel.getSubTaskId()));
        return dto;
    }

    public static List<DashboardProjectDTO> getDashboardProjectDTOsFromProjectModel(List<ProjectModel> projectModels) {

        List<DashboardProjectDTO> dashboardProjectDTOs = new ArrayList<>();
        for (ProjectModel projectModel : projectModels) {
            DashboardProjectDTO dashboardProjectDTO = new DashboardProjectDTO();
            BeanUtils.copyProperties(projectModel, dashboardProjectDTO);
            dashboardProjectDTOs.add(dashboardProjectDTO);
        }
        return dashboardProjectDTOs;




    }
}
