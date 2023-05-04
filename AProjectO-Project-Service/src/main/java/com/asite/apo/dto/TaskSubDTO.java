package com.asite.apo.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TaskSubDTO implements Serializable {
    private Long taskId;
    private String taskName;
    private String taskDesc;
    private LocalDateTime startDate;
    private LocalDateTime taskDeadline;
}