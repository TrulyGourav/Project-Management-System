package com.asite.apo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class GetVersionDTO {

    private String versionName;
    private String versionDesc;
    private Long projectId;
    private LocalDate startDate;
    private LocalDate endDate;

}
