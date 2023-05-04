package com.asite.apo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskListResDTO {

    private List<TaskListDTO> backlogTaskList;
    private List<TaskListDTO> inProgressTaskList;
    private List<TaskListDTO> completedTaskList;
}
