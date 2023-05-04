package com.asite.apo.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.sql.Timestamp;

@Getter
@Setter
public class CreateProjectItrDTO {

    @NotBlank(message = "verison-name-must-not-be-blank")
    @NotNull(message = "verison-name-must-not-be-null")
    private String versionName;

    private Long versionId;
    @NotBlank(message = "version-description-must-not-be-blank")
    @NotNull(message = "version-description-must-not-be-null")
    private String versionDesc;

    private String startDate;

    @NotNull(message = "project-id-must-not-be-null")
    @Min(value = 1, message = "project-id must not be null")
    private Long projectId;

}
