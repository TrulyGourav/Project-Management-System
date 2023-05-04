package com.asite.apo.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "project_tx_iteration_tbl")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectIterationModel {

    @Id
    @Column(name="version_id")
    @GeneratedValue
    private Long versionId;

    @Column(name="version_name")
    private String versionName;

    @Column(name = "version_desc")
    private String versionDesc;

//    @Column(name = "project_id")
//    private long projectId;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @JoinColumn(name = "projectId")
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private ProjectModel project;


    //  Checked
    @JsonIgnore
    @OneToMany(mappedBy = "projectVersionId")
    List<TaskModel> tasks;

    //  Checked
    @JsonIgnore
    @OneToMany(mappedBy = "projectVersionId")
    List<WorkLogModel> worklogs;

}
