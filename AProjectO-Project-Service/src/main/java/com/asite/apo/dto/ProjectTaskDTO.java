package com.asite.apo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProjectTaskDTO {
    @NotNull(message = "projectId is null or empty !!")
    private Long projectId;
    private String projectName;
    private String projectDesc;
}
