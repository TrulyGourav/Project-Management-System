package com.asite.apo.dao;

import com.asite.apo.model.CommentModel;
import com.asite.apo.model.TaskModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepo extends JpaRepository<CommentModel, Long> {
    CommentModel findByCommentId(Long cid);
    CommentModel getByCommentId(long cid);
    List<CommentModel> findAllByTasks_taskId(Long tid);
}
