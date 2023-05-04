package com.asite.apo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UpdateCommentDTO {

    @NotNull(message = "comment-id-must-not-be-null")
    @Min(value = 1,message = "comment-id-must-greater-than-1")
    private Long commentId;
    @NotNull(message = "comment-description-must-not-be-null")
    @NotBlank(message = "comment-description-must-not-be-blank")
    private String desc;


}
