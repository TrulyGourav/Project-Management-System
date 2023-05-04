package com.asite.apo.dto;

import com.asite.apo.model.ProjectIterationModel;
import com.asite.apo.model.TaskModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectListDTO {
    private Long projectId;
    private String projectName;
    private String projectDesc;
    private LocalDate startDate;
    private LocalDate deadline;
    private String status;
    private String priority;
    private Long projectReporter;
    private String projectStatus;
    private int numberOfTasks;
    private int numberOfUsers;
}
