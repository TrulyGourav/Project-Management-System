package com.asite.apo.service;


import com.asite.apo.dao.ProjectIterationRepo;
import com.asite.apo.dao.ProjectRepo;
import com.asite.apo.dao.TaskAttachmentRepo;
import com.asite.apo.dao.TaskRepo;
import com.asite.apo.dto.TaskDTO;
import com.asite.apo.dto.TaskListResDTO;
import com.asite.apo.exception.ResourceNotFoundException;
import com.asite.apo.exception.TaskCreateException;
import com.asite.apo.model.ProjectIterationModel;
import com.asite.apo.model.ProjectModel;
import com.asite.apo.model.TaskAttachmentModel;
import com.asite.apo.model.TaskModel;
import com.asite.apo.util.MappingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);
    @Autowired
    private TaskRepo taskRepo;
    @Autowired
    private ProjectRepo projectRepo;
    @Autowired
    private ProjectIterationRepo projectIterationRepo;
    @Autowired
    private TaskAttachmentRepo taskAttachmentRepo;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This method return the list of tasks assigned to user.
     * @param uId
     * @return List of Task by UserId
     */


    public TaskListResDTO getTaskListByUserId(Long uId) throws Exception{
        LOGGER.debug("Getting task list for user with id: {}", uId);
        List<TaskModel> list = taskRepo.findAllBytaskAssignee(uId);
        if(list == null || list.isEmpty()) {
            throw new ResourceNotFoundException("Not Found");
        }
        LOGGER.debug("Found {} tasks for user with id: {}", list.size(), uId);
        return MappingHelper.getTaskListResDTO(list);
    }

    /**
     * This method will return the list of tasks in Project.
     * @param pId
     * @return List of Task by ProjectId
     */
    public TaskListResDTO getTaskListByProjectId(Long pId) throws Exception {
        LOGGER.debug("Getting task list for project with id: {}", pId);
        if (!projectRepo.findById(pId).isPresent()) {
            throw new ResourceNotFoundException("Project not found");
        }
        List<TaskModel> tasks = taskRepo.findAllByProjectId_projectId(pId);
        LOGGER.debug("Found {} tasks for project with id: {}", tasks.size(), pId);
        return MappingHelper.getTaskListResDTO(tasks);
    }

    /**
     * This method will create task and return the TaskDTO of created task.
     * @param tasksuccessfully
     * @param files
     * @return Success Message
     */
    @Transactional
    public String createTask(@Valid TaskDTO task, List<MultipartFile> files) throws Exception{
        LOGGER.debug("Creating task: {}", task);
        Optional<ProjectModel> optionalProjectModel = projectRepo.findById(task.getProjectTaskDTO().getProjectId());
        ProjectModel projectModel;
        if (optionalProjectModel.isPresent())
            projectModel = optionalProjectModel.get();
        else
            throw new ResourceNotFoundException("Invalid projectId Found");

        Optional<ProjectIterationModel> optionalProjectIterationModel = projectIterationRepo.findById((task.getProjectVersionDTO().getVersionId()));
        ProjectIterationModel projectIterationModel;
        if (optionalProjectIterationModel.isPresent())
            projectIterationModel = optionalProjectIterationModel.get();
        else
            throw new ResourceNotFoundException("Invalid iterationId Passed");

        if (task.getTaskId()!=null)
            throw new TaskCreateException("Task Id cannot be passed when creating task !!");

        TaskModel model = MappingHelper.getTaskModel(task);
        model.setProjectId(projectModel);
        model.setProjectVersionId(projectIterationModel);
        model=taskRepo.save(model);
        if (files != null && !files.isEmpty())
            model.setTaskAttachments(updateAttachment(files,model));
        taskRepo.save(model);
        LOGGER.debug("Task created successfully with id: {}", model.getTaskId());
        return "Task Created successfully";
    }

    /**
     * This method will return the Task details by TaskId.
     * @param taskId
     * @return TaskDTO of the TaskId
     */
    public TaskDTO getTaskDetails(Long taskId) throws Exception{
        LOGGER.debug("Getting task details for task ID {}", taskId);
        TaskModel model = taskRepo.findById(taskId).orElseThrow(()->new ResourceNotFoundException("Task not found"));
        HashMap opjson = new HashMap<>();
        TaskDTO taskDTO = MappingHelper.getTaskDTO(model);
//        opjson.put("TaskDTO", taskDTO);
//        opjson.put("taskReporterName", entityManager.createNativeQuery("select first_name from user_udetails_tbl where uid = " + taskDTO.getTaskReporter()).getFirstResult());
//        opjson.put("taskAssigneeName", entityManager.createNativeQuery("select first_name from user_udetails_tbl where uid = " + taskDTO.getTaskAssignee()).getFirstResult());
        LOGGER.debug("Got task details for task ID {}: {}", taskId, taskDTO);
        return taskDTO;
    }

    /**
     * This method will update the Task Details.
     * @param task
     * @return Success Message
     */
    @Transactional
    public String updateTask(@Valid TaskDTO task,List<MultipartFile> files) throws Exception {
        LOGGER.debug("Updating task with ID {}", task.getTaskId());
        if(!projectRepo.findById(task.getProjectTaskDTO().getProjectId()).isPresent())
            throw new ResourceNotFoundException("Project Not Found");
        else if(!projectIterationRepo.findById((task.getProjectVersionDTO().getVersionId())).isPresent())
            throw new ResourceNotFoundException("Project Iteration not found");
        TaskModel currentModel =taskRepo.findById(task.getTaskId()).orElseThrow(()->new ResourceNotFoundException("Task not found"));
        TaskModel updateModel = MappingHelper.getUpdatedModel(currentModel,task);
        updateModel.setTaskAttachments(updateAttachment(files,currentModel));
        taskRepo.save(updateModel);
        LOGGER.debug("Updated task with ID {}: {}", task.getTaskId(), MappingHelper.getTaskDTO(updateModel));
        return "Task Updated successfully";
    }

    /**
     * This method will update attachments for tasks
     * @param files
     * @param taskModel
     * @return List of Attachments
     * @throws IOException
     */

    @Transactional
    public List<TaskAttachmentModel> updateAttachment(List<MultipartFile> files,TaskModel taskModel) throws IOException {
        LOGGER.debug("Updating task attachments for task with ID {}:", taskModel.getTaskId());
        List<TaskAttachmentModel> attachments = taskModel.getTaskAttachments();
        if(files==null||files.get(0).getOriginalFilename().equals(""))
            return attachments;
        if(attachments==null)
            attachments =new ArrayList<>();
        List<TaskAttachmentModel> attachmentsToDelete = new ArrayList<>();
        String pathOfFile = String.valueOf(new ClassPathResource("static/Task/"));
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            Path attachmentPath = Paths.get(pathOfFile+taskModel.getTaskId()+"/"+fileName).toAbsolutePath().normalize();
            Files.createDirectories(attachmentPath.getParent());
            Files.copy(file.getInputStream(), attachmentPath, StandardCopyOption.REPLACE_EXISTING);
            TaskAttachmentModel existingAttachment = MappingHelper.findAttachmentByName(attachments, fileName);
            if (existingAttachment != null) {
                attachments.remove(existingAttachment);
                attachmentsToDelete.add(existingAttachment);
            }
            TaskAttachmentModel attachmentModel = new TaskAttachmentModel();
            attachmentModel.setTaskAttachmentName(file.getOriginalFilename());
            attachmentModel.setTaskId(taskModel);
            attachmentModel.setProjectVersionId(taskModel.getProjectVersionId());
            attachments.add(attachmentModel);
        }
        taskAttachmentRepo.deleteAllInBatch(attachmentsToDelete);
        LOGGER.debug("Task attachments for task with ID {} have been updated successfully", taskModel.getTaskId());
        return attachments;
    }

    /**
     * This method return the file contents
     * @param taskId, attachmentId
     * @return Success message
     * @throws Exception
     */
    public ResponseEntity<?> viewFile(Long taskId, Long attachmentId) throws IOException {
        LOGGER.debug("ViewFile for taskId {} and attachmentId {}", taskId,attachmentId);
        TaskAttachmentModel taskAttachmentModel = taskAttachmentRepo.getReferenceById(attachmentId);
        String pathOfFile = String.valueOf(new ClassPathResource("static/Task/"));
        Path attachmentPath = Paths.get(pathOfFile + taskId + "/" + taskAttachmentModel.getTaskAttachmentName()).toAbsolutePath().normalize();
        Resource resource = new UrlResource(attachmentPath.toUri());
        if (resource.exists() && resource.isReadable()) {
            byte[] fileContent =FileCopyUtils.copyToByteArray(resource.getInputStream());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(resource.getURL().openConnection().getContentType()));
            headers.setContentDisposition(ContentDisposition.builder("inline").filename(taskAttachmentModel.getTaskAttachmentName()).build());
            LOGGER.debug("Sending Attachment successful for taskId {} and attachmentId {}", taskId,attachmentId);
            return new ResponseEntity<>(fileContent,headers,HttpStatus.OK);
        } else {
            throw new RuntimeException("File not found or not readable");
        }
    }

    /**
     * This method will delete task and its attachments
     * @param tId
     * @return Success message
     * @throws Exception
     */
    @Transactional
    public ResponseEntity<?> deleteTask(Long tId) throws Exception{
        LOGGER.debug("Task delete with taskId: {}", tId);
        Path path = Paths.get(String.valueOf(new ClassPathResource("static/Task/"))+tId.toString());
        if(Files.exists(path)){
            Files.walk(path)
                    .map(Path::toFile)
                    .forEach(java.io.File::delete);
            Files.delete(path);
        }
        taskRepo.deleteById(tId);
        LOGGER.debug("Task deleted successfully with taskId: {}", tId);
        return new ResponseEntity<>("Task Deleted successfully", HttpStatus.OK);
    }

    /**
     * This method will delete task attachment
     * @param taskId, attachmentId
     * @return Success message
     * @throws Exception
     */
    @Transactional
    public String deleteFile(Long taskId,Long attachmentId) {
        LOGGER.debug("Attachment delete with taskId: {} and attachmentId: {}", taskId, attachmentId);
        TaskAttachmentModel taskAttachmentModel = taskAttachmentRepo.getReferenceById(attachmentId);
        String pathOfFile = String.valueOf(new ClassPathResource("static/Task/"));
        try {
            Path attachmentPath = Paths.get(pathOfFile + taskId + "/" + taskAttachmentModel.getTaskAttachmentName()).toAbsolutePath().normalize();
            Files.deleteIfExists(attachmentPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        taskAttachmentRepo.delete(taskAttachmentModel);
        LOGGER.debug("Attachment deleted successfully with taskId: {} and attachmentId: {}", taskId, attachmentId);
        return "Attachment deleted successfully";
    }

    /**
     * This method will return the list of task assigned to user in one project
     * @param userId
     * @param projectId
     * @return
     * @throws ResourceNotFoundException
     */
    public TaskListResDTO getTaskListByUserAndProjectId(Long userId, Long projectId) throws ResourceNotFoundException {
        LOGGER.debug("Getting task list for user {} and project {}", userId, projectId);
        if(!projectRepo.findById(projectId).isPresent())
            throw new ResourceNotFoundException("Project not found");
        return MappingHelper.getTaskListResDTO(taskRepo.findAllBytaskAssigneeAndProjectId_projectId(userId,projectId));
    }

    public TaskListResDTO getTaskList() throws Exception {
        LOGGER.debug("Getting task list");
        List<TaskModel> list = taskRepo.findAll();
        if(list == null || list.isEmpty()) {
            throw new ResourceNotFoundException("Not Found");
        }
        LOGGER.debug("Found tasks List");
        return MappingHelper.getTaskListResDTO(list);
    }

    public String updateStatus(Long pId, Long tId, String status) throws Exception{
        LOGGER.debug("Updating task status");
        if(!projectRepo.findById(pId).isPresent())
            throw new ResourceNotFoundException("Project not found");
        TaskModel taskModel = taskRepo.getReferenceById(tId);
        if(taskModel==null)
            throw new ResourceNotFoundException("Task not found");
        taskModel.setTaskStatus(status);
        taskRepo.save(taskModel);
        return "Updated Successfully";
    }
}