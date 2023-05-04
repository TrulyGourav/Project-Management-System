package com.asite.apo.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProjectVersionDTO {

    @NotNull(message = "versionId is null or empty !!")
    private Long versionId;
    private String versionName;
    private String versionDesc;
}
