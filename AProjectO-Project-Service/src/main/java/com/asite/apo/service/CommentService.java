package com.asite.apo.service;

import com.asite.apo.dao.CommentRepo;
import com.asite.apo.dao.SubTaskRepo;
import com.asite.apo.dao.TaskRepo;
import com.asite.apo.dto.*;
import com.asite.apo.exception.ResourceNotFoundException;
import com.asite.apo.model.CommentModel;
import com.asite.apo.model.SubTaskModel;
import com.asite.apo.model.TaskModel;
import com.asite.apo.util.MappingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
public class CommentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentService.class);
    @Autowired
    private CommentRepo commentRepo;
    @Autowired
    private TaskRepo taskRepo;
    @Autowired
    private SubTaskRepo subTaskRepo;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This method creates a comment
     *
     * @return Object of comment
     */
    @Transactional
    public CreateCommentDTO createComment(CreateCommentDTO createCommentDTO) throws Exception {
        LOGGER.debug("Creating Comments",createCommentDTO);
        CommentModel commentModel = MappingHelper.getCommentModelFromCreateCommentDTO(createCommentDTO);
        if (createCommentDTO.getCommentId() != null)
            throw new Exception("Id cannot be accepted while creating entity");
        commentModel.setCreatedAt(LocalDateTime.now());
        if (Objects.equals(createCommentDTO.getFlag(), "true")) {
            TaskModel existingTaskModel = taskRepo.findById(createCommentDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("Task does not exist"));
            commentModel.setTasks(existingTaskModel);
            commentModel = commentRepo.save(commentModel);
            existingTaskModel.getComments().add(commentModel);
            taskRepo.save(existingTaskModel);
            createCommentDTO.setCommentId(commentModel.getCommentId());
        } else {
            SubTaskModel existingSubTaskModel = subTaskRepo.findBySubTaskId(createCommentDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("SubTask does not exist"));
            commentModel.setSubtasks(existingSubTaskModel);
            commentModel = commentRepo.save(commentModel);
            existingSubTaskModel.getComments().add(commentModel);
            subTaskRepo.save(existingSubTaskModel);
            createCommentDTO.setCommentId(commentModel.getCommentId());
        }
        LOGGER.debug("Creating Comments",createCommentDTO);
        return createCommentDTO;
    }

    /**
     * This method returns comment by comment id
     *
     * @return Comment
     */

    public CreateCommentDTO getCommentByCommentId(Long cId) throws Exception {
        LOGGER.debug("Getting Comments by comment Id",cId);
        CommentModel commentModel = commentRepo.findById(cId).orElseThrow(() -> new ResourceNotFoundException("Comment does not exist"));
        CreateCommentDTO commentDetailsDTO = MappingHelper.getCreateCommentDTOFromCommentModel(commentModel);
        commentDetailsDTO.setCommentByName((String) entityManager.createNativeQuery("select first_name from user_udetails_tbl where uid = " + commentDetailsDTO.getCommentBy()).getSingleResult());
        if (commentModel.getTasks() != null) {
            commentDetailsDTO.setId(commentModel.getTasks().getTaskId());
            commentDetailsDTO.setFlag(String.valueOf(true));
        } else {
            commentDetailsDTO.setId(commentModel.getSubtasks().getSubTaskId());
            commentDetailsDTO.setFlag(String.valueOf(false));
        }
        LOGGER.debug("Found Comments by comment Id",cId);
        return commentDetailsDTO;
    }

    /**
     * This method returns Updated comment
     *
     * @return Updated comment
     */
    @Transactional
    public CommentModel updateComment(UpdateCommentDTO updateCommentDTO) throws Exception {
        LOGGER.debug("Updating Comments",updateCommentDTO);
        CommentModel commentModel = commentRepo.findById(updateCommentDTO.getCommentId()).orElseThrow(() -> new ResourceNotFoundException("Comment does not exist"));
        commentModel.setCommentDesc(updateCommentDTO.getDesc());
        commentRepo.save(commentModel);
        LOGGER.debug("Comments Updated",updateCommentDTO);
        return commentModel;
    }

    /**
     * This method deletes an existing comment
     */
    @Transactional
    public String deleteComment(Long cid) throws Exception {
        LOGGER.debug("Deleting Comment",cid);
        if (commentRepo.existsById(cid)) {
            commentRepo.deleteById(cid);
            LOGGER.debug("Comments Deleted",cid);
            return "Comment is deleted";
        } else {
            throw new ResourceNotFoundException("Comment does not exist");
        }
    }

    /**
     * This method gives comments under a specific task
     */
    public List<TaskCommentDTO> getCommentsByTaskId(long tId) throws Exception {
        LOGGER.debug("Getting Comments by Task Id",tId);
        TaskModel taskModel = taskRepo.findById(tId).orElseThrow(() -> new ResourceNotFoundException("Task does not exist"));
        List<CommentModel> commentModel = taskModel.getComments();
        if (commentModel == null) {
            throw new Exception("There are no comments under this task");
        } else {
            LOGGER.debug("Found Comments by Task Id",tId);
            return this.getTaskDTOsFromCommentModel(commentModel);
        }
    }

    /**
     * This method gives comments under a specific sub-task
     */
    public List<SubTaskCommentDTO> getCommentsBySubTaskId(long stId) throws Exception {
        LOGGER.debug("Getting Comments by SubTask Id",stId);
        SubTaskModel subTaskModel = subTaskRepo.findBySubTaskId(stId).orElseThrow(() -> new ResourceNotFoundException("SubTask does not exist"));
        List<CommentModel> commentModel = subTaskModel.getComments();
        if (commentModel == null) {
            throw new Exception("There are no comments under this sub-task");
        } else {
            return this.getSubTaskDTOsFromCommentModel(commentModel);
        }
    }

    public List<TaskCommentDTO> getTaskDTOsFromCommentModel(List<CommentModel> commentModel) {

        List<TaskCommentDTO> taskCommentDTOs = new ArrayList<>();
        for (CommentModel cm : commentModel) {
            TaskCommentDTO taskCommentDTO = new TaskCommentDTO();
            BeanUtils.copyProperties(cm, taskCommentDTO);
            taskCommentDTO.setCommentByName((String) entityManager.createNativeQuery("select first_name from user_udetails_tbl where uid = " + taskCommentDTO.getCommentBy()).getSingleResult());
            taskCommentDTOs.add(taskCommentDTO);
        }
        return taskCommentDTOs;
    }


    public List<SubTaskCommentDTO> getSubTaskDTOsFromCommentModel(List<CommentModel> commentModel) {

        List<SubTaskCommentDTO> subTaskCommentDTOs = new ArrayList<>();
        for (CommentModel cm : commentModel) {
            SubTaskCommentDTO subTaskCommentDTO = new SubTaskCommentDTO();
            BeanUtils.copyProperties(cm, subTaskCommentDTO);
            subTaskCommentDTO.setCommentByName((String) entityManager.createNativeQuery("select first_name from user_udetails_tbl where uid = " + subTaskCommentDTO.getCommentBy()).getSingleResult());
            subTaskCommentDTOs.add(subTaskCommentDTO);
        }
        return subTaskCommentDTOs;
    }
}
