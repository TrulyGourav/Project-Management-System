package com.asite.apo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaskCommentDTO {

    private long commentId;
    private String commentDesc;
    private long commentBy;
    private String commentByName;
    private LocalDateTime createdAt;
}
