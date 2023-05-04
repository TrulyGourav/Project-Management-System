package com.asite.apo.dto;

import com.asite.apo.model.CommentModel;
import com.asite.apo.model.SubTaskModel;
import com.asite.apo.model.TaskAttachmentModel;
import com.asite.apo.model.WorkLogModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TaskDTO implements Serializable {
    private Long taskId;
    @NotNull(message = "taskName is null !!")
    @NotEmpty(message = "taskName is empty !!")
    @Size(max = 100, message = "Max size for taskName is 100 characters")
    private String taskName;
    @NotNull(message = "taskDesc is null !!")
    @NotEmpty(message = "taskDesc is empty !!")
    @Size(max = 1000, message = "Max size for taskName is 1000 characters")
    private String taskDesc;
    @NotNull(message = "startDate is null !!")
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @NotNull(message = "taskDeadline is null !!")
    private LocalDateTime taskDeadline;
    @NotNull(message = "taskPriority is null !!")
    @NotEmpty(message = "taskPriority is empty !!")
    @Pattern(regexp = "Low|Medium|High")
    private String taskPriority;
    @NotNull(message = "taskAssignee is null !!")
    @Min(1)
    private Long taskAssignee;
    @NotNull(message = "taskReporter is null !!")
    @Min(1)
    private Long taskReporter;
    @NotNull(message = "taskStatus is null !!")
    @NotEmpty(message = "taskName is empty !!")
    @Pattern(regexp = "Backlog|Done|In Progress", message = "Valid task status are Backlog or Closed or Ready For Live or Technical Analysis or Requirement Gathering And Analysis")
    private String taskStatus;

    @NotNull(message = "taskType is null !!")
    @NotEmpty(message = "taskName is empty !!")
    @Pattern(regexp = "Bug|Other|Feature|Story", message = "Valid task status are Bug or Other or Feature or Story")
    private String taskType;
    private ProjectTaskDTO projectTaskDTO;
    private ProjectVersionDTO projectVersionDTO;
    private List<TaskAttachmentDTO> taskAttachmentDTOList;
    private List<SubTaskListDTO> subTaskDTOList;

}
