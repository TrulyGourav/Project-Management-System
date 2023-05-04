package com.asite.apo.service;

import com.asite.apo.dao.ProjectRepo;
import com.asite.apo.dao.SubTaskAttachmentRepo;
import com.asite.apo.dao.SubTaskRepo;
import com.asite.apo.dao.TaskRepo;
import com.asite.apo.dto.SubTaskDTO;
import com.asite.apo.dto.SubTaskListDTO;
import com.asite.apo.exception.*;
import com.asite.apo.model.*;
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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubTaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubTaskService.class);
    @Autowired
    private SubTaskRepo subTaskRepo;
    @Autowired
    private TaskRepo taskRepo;
    @Autowired
    private ProjectRepo projectRepo;
    @Autowired
    private SubTaskAttachmentRepo subTaskAttachmentRepo;

    /**
     * This method fetch the subtask by id
     * @param id
     * @return
     * @throws SubTaskGetException
     */
    public SubTaskDTO getSubTask(Long id) throws SubTaskGetException{
        LOGGER.debug("Getting subtask",id);
        SubTaskModel subTaskModel = subTaskRepo.findById(id).orElse(null);
        SubTaskDTO subTaskDTO = MappingHelper.getSubTaskDTO(subTaskModel);
        if(subTaskDTO==null)
            throw new SubTaskGetException("Sub Task With Id "+id+" Not Found");
        else
            return subTaskDTO;
    }

    /**
     * This method return list of subtask assigned to user
     * @param uid
     * @return
     * @throws SubTaskGetException
     */
    public List<SubTaskListDTO> getSubTaskByUserId(Long uid) throws SubTaskGetException {
        LOGGER.debug("Getting subtask by userid{}",uid);
        List<SubTaskModel> list = subTaskRepo.findAllBySubTaskAssignedTo(uid);
        if(list==null || list.isEmpty())
            throw new SubTaskGetException("Sub Task Not Found");
        else {
            return list.stream()
                    .map(MappingHelper::getSubTaskListDTO)
                    .collect(Collectors.toList());
        }
    }

    /**
     * This method return list of subtask in a task
     * @param id
     * @return
     * @throws SubTaskGetException
     * @throws ResourceNotFoundException
     */
    public List<SubTaskListDTO> getSubTaskByTaskId(Long id) throws SubTaskGetException, ResourceNotFoundException {
        LOGGER.debug("Getting subtask by taskid{}",id);
        if(!taskRepo.findById(id).isPresent()){
            throw new ResourceNotFoundException("Task Not Found");
        }else{
            return subTaskRepo.findAllByTaskId_taskId(id).stream()
                    .map(MappingHelper::getSubTaskListDTO)
                    .collect(Collectors.toList());
        }
    }

    /**
     * This method will create subtask.
     * @param subTask
     * @return SubTaskDTO of created sub task
     *
     * */
    @Transactional
    public SubTaskDTO createSubTask(@Valid SubTaskDTO subTask, List<MultipartFile> files) throws Exception {
        LOGGER.debug("Creating subtask: {}", subTask);
        TaskModel taskModel = taskRepo.findById(subTask.getTaskSubDTO().getTaskId()).orElse(null);
        if(taskModel == null){
            throw new ResourceNotFoundException("Task Not Found");
        }
        SubTaskModel subTaskModel = MappingHelper.getSubTaskModel(subTask);
        subTaskModel.setTaskId(taskModel);
        subTaskModel = subTaskRepo.save(subTaskModel);
        if (files != null && !files.isEmpty())
            subTaskModel.setSubTasksAttachments(updateAttachment(files, subTaskModel));
        subTaskRepo.save(subTaskModel);
        LOGGER.debug("SubTask created successfully with id: {}", subTaskModel.getSubTaskId());
        return MappingHelper.getSubTaskDTO(subTaskModel);
    }

    /**
     * This Method will update subtask
     * @param subTaskDTO
     * @param files
     * @return
     * @throws Exception
     */
    @Transactional
    public SubTaskDTO updateSubTask(@Valid SubTaskDTO subTaskDTO,List<MultipartFile> files) throws Exception {
        LOGGER.debug("Updating subtask with ID {}", subTaskDTO.getSubTaskId());
        if(!taskRepo.findById(subTaskDTO.getTaskSubDTO().getTaskId()).isPresent())
            throw new ResourceNotFoundException("Task Not Found");
        SubTaskModel currentModel = subTaskRepo.findById(subTaskDTO.getSubTaskId()).get();
        SubTaskModel updateModel = MappingHelper.getUpdatedModel(currentModel,subTaskDTO);
        updateModel.setSubTasksAttachments(updateAttachment(files,currentModel));
        subTaskRepo.save(updateModel);
        LOGGER.debug("Updated subtask with ID {}: {}", subTaskDTO.getSubTaskId());
        return MappingHelper.getSubTaskDTO(subTaskRepo.getReferenceById(updateModel.getSubTaskId()));
    }
    @Transactional
    public List<SubTaskAttachmentModel> updateAttachment(List<MultipartFile> files,SubTaskModel subTaskModel) throws IOException {
        LOGGER.debug("Updating subtask attachments for subtask with ID {}:", subTaskModel.getSubTaskId());
        List<SubTaskAttachmentModel> attachments = subTaskModel.getSubTasksAttachments();
        if(files==null||files.get(0).getOriginalFilename().equals(""))
            return attachments;
        if(attachments==null)
            attachments =new ArrayList<>();
        List<SubTaskAttachmentModel> attachmentsToDelete = new ArrayList<>();
        String pathOfFile = String.valueOf(new ClassPathResource("static/SubTask/"));
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            Path attachmentPath = Paths.get(pathOfFile+subTaskModel.getSubTaskId()+"/"+fileName).toAbsolutePath().normalize();
            Files.createDirectories(attachmentPath.getParent());
            Files.copy(file.getInputStream(), attachmentPath, StandardCopyOption.REPLACE_EXISTING);
            SubTaskAttachmentModel existingAttachment = MappingHelper.findSubTaskAttachmentByName(attachments, fileName);
            if (existingAttachment != null) {
                attachments.remove(existingAttachment);
                attachmentsToDelete.add(existingAttachment);
            }
            SubTaskAttachmentModel attachmentModel = new SubTaskAttachmentModel();
            attachmentModel.setSubTaskAttachmentName(file.getOriginalFilename());
            attachmentModel.setSubTaskId(subTaskModel);
            attachments.add(attachmentModel);
        }
        subTaskAttachmentRepo.deleteAllInBatch(attachmentsToDelete);
        LOGGER.debug("SubTask attachments for subtask with ID {} have been updated successfully", subTaskModel.getSubTaskId());
        return attachments;
    }

    /**
     * This method will delete subtask
     * @param id
     * @return
     * @throws SubTaskDeleteException
     * @throws IOException
     */
    public ResponseEntity<SubTaskModel> deleteSubTask(Long id) throws SubTaskDeleteException, IOException {
        LOGGER.debug("SubTask delete with subtaskId: {}",id);
        SubTaskModel subtask = subTaskRepo.findById(id).orElse(null);
        if(subtask!=null){
            Path path = Paths.get(String.valueOf(new ClassPathResource("static/SubTask/"))+id.toString());
            if(Files.exists(path)) {
                Files.walk(path)
                        .map(Path::toFile)
                        .forEach(java.io.File::delete);
                Files.delete(path);
            }
            subTaskRepo.delete(subtask);
            LOGGER.debug("SubTask deleted successfully with subtaskId: {}", id);
            return new ResponseEntity<>(subtask,HttpStatus.OK);
        }else {
            throw new SubTaskDeleteException("Cannot Find Sub task With id "+id);
        }
    }

    public ResponseEntity<?> viewFile(Long subTaskId, Long attachmentId) throws IOException {
        LOGGER.debug("ViewFile for subtaskId {} and attachmentId {}", subTaskId,attachmentId);
        SubTaskAttachmentModel subTaskAttachmentModel = subTaskAttachmentRepo.getReferenceById(attachmentId);
        String pathOfFile = String.valueOf(new ClassPathResource("static/SubTask/"));
        Path attachmentPath = Paths.get(pathOfFile + subTaskId + "/" + subTaskAttachmentModel.getSubTaskAttachmentName()).toAbsolutePath().normalize();
        Resource resource = new UrlResource(attachmentPath.toUri());
        if (resource.exists() && resource.isReadable()) {
            byte[] fileContent = FileCopyUtils.copyToByteArray(resource.getInputStream());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(resource.getURL().openConnection().getContentType()));
            headers.setContentDisposition(ContentDisposition.builder("inline").filename(subTaskAttachmentModel.getSubTaskAttachmentName()).build());
            LOGGER.debug("Sending Attachment successful for subtaskId {} and attachmentId {}", subTaskId,attachmentId);
            return new ResponseEntity<>(fileContent,headers,HttpStatus.OK);
        } else {
            throw new RuntimeException("File not found or not readable");
        }
    }

    /**
     * This method return list of subtask in a project assigned to user
     * @param uId
     * @param pId
     * @return
     * @throws ResourceNotFoundException
     */
    public List<SubTaskListDTO> getSubTaskByProject(Long uId, Long pId) throws ResourceNotFoundException {
        LOGGER.debug("Getting subtask list for project {}", uId, pId);
        if(!projectRepo.findById(pId).isPresent()){
            throw new ResourceNotFoundException("Not Found");
        }
        else {
            return subTaskRepo.findAllBySubTaskAssignedToAndTaskId_ProjectId_projectId(uId,pId).stream()
                    .map(MappingHelper::getSubTaskListDTO)
                    .collect(Collectors.toList());
        }
    }

    /**
     * This method return list of subtask in a task assigned to user
     * @param userId
     * @param taskId
     * @return
     * @throws ResourceNotFoundException
     */
    public List<SubTaskListDTO> getSubTaskByTask(Long userId, Long taskId) throws ResourceNotFoundException {
        LOGGER.debug("Getting subtask by task{}", userId, taskId);
        if(!taskRepo.findById(taskId).isPresent()){
            throw new ResourceNotFoundException("Not Found");
        }
        else {
            return subTaskRepo.findAllBySubTaskAssignedToAndTaskId_taskId(userId,taskId).stream()
                    .map(MappingHelper::getSubTaskListDTO)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public String deleteFile(Long subTaskId,Long attachmentId) {
        LOGGER.debug("Attachment delete with subtaskId: {} and attachmentId: {}", subTaskId, attachmentId);
        SubTaskAttachmentModel subTaskAttachmentModel = subTaskAttachmentRepo.getReferenceById(attachmentId);
        String pathOfFile = String.valueOf(new ClassPathResource("static/SubTask/"));
        try {
            Path attachmentPath = Paths.get(pathOfFile + subTaskId + "/" + subTaskAttachmentModel.getSubTaskAttachmentName()).toAbsolutePath().normalize();
            Files.deleteIfExists(attachmentPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        subTaskAttachmentRepo.delete(subTaskAttachmentModel);
        LOGGER.debug("Attachment deleted successfully with subtaskId: {} and attachmentId: {}", subTaskId, attachmentId);
        return "Attachment deleted sucessufully";
    }
}
