package com.asite.apo.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "project_tx_pdetails_tbl")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectModel {
    @Id
    @Column(name = "project_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @Column(name = "project_name", length = 100)
    private String projectName;

    @Column(name = "project_desc", length = 1000)
    private String projectDesc;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column
    private LocalDate deadline;

    @Column
    private String status;

    @Column
    private String priority;

    //Todo Link to user model
    @Column(name = "project_reporter")
    private Long projectReporter;

//  Checked

    @OneToMany(mappedBy = "project")
    List<ProjectIterationModel> iterations;

    //  Checked
//    @JsonIgnore
    @OneToMany(mappedBy = "projectId")
    List<TaskModel> tasks;

    @Column(name = "current_iteration_id")
//    @OneToOne(mappedBy = "iterationId")
    @JoinColumn(name = "versionId",table = "ProjectIterationModel")
    private Long currentIterationId;
}
