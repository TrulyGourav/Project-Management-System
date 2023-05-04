package com.asite.apo.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class UpdateProjectItrDTO {

    @NotNull(message = "version-id-must-not-be-null")
    @Min(value = 1,message = "version-id-must-greater-than-1")
    private Long versionId;

    @NotNull(message = "version-name-must-not-be-null")
    @NotBlank(message = "version-name-must-not-be-blank")
    private String versionName;

    @NotNull(message = "version-description-must-not-be-null")
    @NotBlank(message = "version-description-must-not-be-blank")
    private String versionDesc;
}
