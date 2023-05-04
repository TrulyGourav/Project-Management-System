package com.asite.apo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TaskAttachmentDTO {
    private Long attachmentId;
    private String taskAttachmentName;
}