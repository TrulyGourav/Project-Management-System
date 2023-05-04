package com.asite.apo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;


@Getter
@Setter
public class ProjectModelDTO {
    private Long projectId;

    @NotNull(message = "Project Name null or empty!!")
    @Size(message = "projectName Invalid Size", max = 100)
    private String projectName;

    @NotNull(message = "Project Description null or empty!!")
    @Size(message = "projectDesc Invalid Size", max = 1000)
    private String projectDesc;

    @NotNull(message = "Start Date null or empty!!")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private String startDate;

    @NotNull(message = "Deadline null or empty!!")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private String deadline;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private String endDate;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String status;
    @NotNull(message = "Priority null or empty!!")
    @Pattern(regexp = "High|Urgent|Normal")
    private String priority;
    @NotNull(message = "Project Reporter null or empty!!")
    @Min(value = 0, message = "projectReporter cannot be negative")
    private Long projectReporter;

    private Long currentIterationId;
}
