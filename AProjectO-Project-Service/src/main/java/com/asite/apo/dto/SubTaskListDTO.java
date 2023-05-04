package com.asite.apo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubTaskListDTO {
    private Long subTaskId;
    private String subTaskName;
    private String subTaskDesc;
    private LocalDateTime subTaskDeadline;
    private String subTaskType;
    private String subTaskStatus;
    private String subTaskPriority;
//    List<CommentModel> comments;
    List<SubTaskAttachmentDTO> subTasksAttachments;
//    List<WorkLogModel> worklogs;
}
