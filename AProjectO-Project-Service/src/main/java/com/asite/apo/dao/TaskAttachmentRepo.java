package com.asite.apo.dao;

import com.asite.apo.model.TaskAttachmentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskAttachmentRepo extends JpaRepository<TaskAttachmentModel, Long> {
}
