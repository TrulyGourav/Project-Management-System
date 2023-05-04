package com.asite.apo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;



@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskListDTO {
    private Long taskId;
    private String taskName;
    private String taskDesc;
    private LocalDateTime taskDeadline;
    private String taskType;
    private String taskStatus;
    private String taskPriority;
}
