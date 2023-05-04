package com.asite.apo.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

//@AllArgsConstructor
//@NoArgsConstructor
@Getter
@Setter
//@Component
public class CommentDetailsDTO {

    private String desc;
    private Long commentBy;
    private Long taskId;
    private Long subTaskId;

}
