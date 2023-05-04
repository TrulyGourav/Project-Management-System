package com.asite.apo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SubTaskCommentDTO {

    private long commentId;
    private String commentDesc;
    private long commentBy;
    private LocalDateTime createdAt;
    private String commentByName;
}
