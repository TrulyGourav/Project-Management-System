package com.asite.apo.dto;

import com.asite.apo.model.ProjectIterationModel;
import com.asite.apo.model.SubTaskModel;
import com.asite.apo.model.TaskModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ser.impl.MapEntrySerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class WorkLogDto {

    private Long worklogId;
    @NotNull(message = "User Id Cannot be Null")
    @Min(value = 1,message = "User Id Must be Greater Than Zero")
    private Long userId;

    @NotNull(message = "Role Id Cannot be Null")
    @Min(value = 1, message = "Role Id Cannot Be Zero")
    private Long roleId;

    private ProjectIterationWorkDTO projectIteration;
    private TaskWorkDTO task;
    private SubTaskWorkDTO subTask;

    private LocalDate dateWorked;

    //@NotNull(message = "Hours Worked Cannot Null")
    @Max(value = 24,message = "Work Hours cannot Be greater than 24")
    private Double hoursWorked;

    //@NotNull(message = "Work Description Cannot Null")
    private String worklogDesc;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
