package com.asite.apo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import reactor.util.annotation.NonNull;

import javax.validation.constraints.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCommentDTO {

    private Long commentId;
    @NotNull(message = "comment-description-must-not-be-null")
    private String commentDesc;
    @Min(value = 1, message = "id-must-be-greater-than-0")
    private Long id;
    @NotNull(message = "flag-must-not-be-null")
    @Pattern(regexp = "^true$|^false$", message = "allowed input: true or false")
    private String flag;
    @Min(value = 1, message = "comment-by-must-be-greater-than-0")
    private Long commentBy;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String commentByName;
}