package com.asite.apo.controller;
import com.asite.apo.api.ProjectURLConstant;
import com.asite.apo.dto.CreateCommentDTO;
import com.asite.apo.dto.SubTaskCommentDTO;
import com.asite.apo.dto.TaskCommentDTO;
import com.asite.apo.dto.UpdateCommentDTO;
import com.asite.apo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequestMapping(ProjectURLConstant.COMMENT_API)
@CacheConfig(cacheNames = "commentCache")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    CacheManager cacheManager;

    @GetMapping(value = ProjectURLConstant.GET_COMMENTS_BY_COMMENT_ID + "/{cId}", produces = "application/json")
//    @Cacheable(key = "#cId")
    public ResponseEntity<?> getCommentByCommentId(@PathVariable(value = "cId") @Valid @Min(value = 1, message = "comment-id-must-greater-than-1") Long cId) {
        try {
            return ResponseEntity.ok().body(commentService.getCommentByCommentId(cId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @GetMapping(value = ProjectURLConstant.GET_COMMENTS_BY_TASK_ID + "/{tId}", produces = "application/json")
//    @Cacheable(key = "#tId")
    public ResponseEntity<?> getCommentsByTaskId(@Valid @Min(value = 1, message = "task-id-must-greater-than-0") @PathVariable(value = "tId") Long tId) {
        try {
            return ResponseEntity.ok().body(commentService.getCommentsByTaskId(tId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = ProjectURLConstant.GET_COMMENTS_BY_SUB_TASK_ID + "/{stId}", produces = "application/json")
//    @Cacheable(key = "#stId")
    public ResponseEntity<?> getCommentsBySubTaskId(@Valid @Min(value = 1, message = "sub-task-id-must-greater-than-1") @PathVariable(value = "stId") long stId) {
        try {
            return ResponseEntity.ok().body(commentService.getCommentsBySubTaskId(stId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping(value = ProjectURLConstant.CREATE_COMMENT)
//    @CachePut(key = "#createCommentDTO.commentId")
    public ResponseEntity<CreateCommentDTO> createComments(@Valid CreateCommentDTO createCommentDTO) {
        try {
            Cache cache = cacheManager.getCache("commentCache");
            cache.clear();
            return ResponseEntity.ok().body(commentService.createComment(createCommentDTO));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping(value = ProjectURLConstant.UPDATE_COMMENT, consumes = MediaType.APPLICATION_JSON_VALUE)
//    @CachePut()
    public ResponseEntity<?> updateComments(@Valid @RequestBody UpdateCommentDTO updateCommentDTO) {
        try {
//            Cache cache = cacheManager.getCache("commentCache");
//            cache.clear();
            return new ResponseEntity<>(commentService.updateComment(updateCommentDTO), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @DeleteMapping(value = ProjectURLConstant.DELETE_COMMENT + "/{cId}")
//    @CacheEvict()
    public ResponseEntity<?> deleteComment(@Valid @Min(value = 1, message = "comment-id-must-greater-than-1") @PathVariable(value = "cId") Long cId) {
        try {
//            Cache cache = cacheManager.getCache("commentCache");
//            cache.clear();
            return new ResponseEntity<>(commentService.deleteComment(cId), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
