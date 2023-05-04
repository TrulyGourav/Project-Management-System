package com.asite.apo.dto;

import com.asite.apo.model.CommentModel;
import com.asite.apo.model.SubTaskAttachmentModel;
import com.asite.apo.model.TaskModel;
import com.asite.apo.model.WorkLogModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SubTaskDTO {
    private Long subTaskId;

    @NotNull(message = "Sub Task Name is null !!")
    @NotEmpty(message = "Sub Task Name is empty !!")
    @Size(max = 100, message = "Max size for Sub Task Name is 100 characters")
    private String subTaskName;

    @NotNull(message = "Sub Task Priority is null !!")
    @NotEmpty(message = "Sub Task Priority is empty !!")
    @Pattern(regexp = "Low|Medium|High", message = "Valid sub task priority are Normal or Medium or High")
    private String subTaskPriority;

    @NotNull(message = "Sub Task Description is null !!")
    @NotEmpty(message = "Sub Task Description is empty !!")
    @Size(max = 1000, message = "Max size for Sub Task Description is 1000 characters")
    private String subTaskDesc;

    @NotNull(message = "Sub Task Assignee is null !!")
    @Min(value = 1)
    private Long subTaskAssignedTo;

    @NotNull(message = "Sub Task Reporter is null !!")
    @Min(1)
    private Long subTaskReporter;

    @NotNull(message = "Sub Task Deadline is null !!")
    private LocalDateTime deadLine;

    @NotNull(message = "Start Date is null !!")
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @NotNull(message = "Sub Task Type is null !!")
    @NotEmpty(message = "Sub Task Type is empty !!")
    @Pattern(regexp = "Bug|Other|Feature|Story", message = "Valid sub task status are Bug or Other or Feature or Story")
    private String subTaskType;

    private String link;
    private TaskSubDTO taskSubDTO;
    List<SubTaskAttachmentDTO> subTasksAttachments;
}
