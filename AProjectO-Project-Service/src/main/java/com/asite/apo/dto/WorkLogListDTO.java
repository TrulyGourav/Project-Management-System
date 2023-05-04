package com.asite.apo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class WorkLogListDTO {
    private Long worklogId;
    private Long userId;
    private Long roleId;
    private ProjectIterationWorkDTO projectIteration;
    private TaskWorkDTO task;
    private SubTaskWorkDTO subTask;
    private LocalDate dateWorked;
    private Double hoursWorked;
    private String worklogDesc;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
