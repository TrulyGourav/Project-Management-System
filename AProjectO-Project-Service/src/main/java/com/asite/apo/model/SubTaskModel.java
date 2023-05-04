package com.asite.apo.model;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "project_tx_sub_task_tbl")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "subTaskId")
public class SubTaskModel {
    @Id
    @Column(name = "sub_task_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subTaskId;

    @Column(name = "sub_task_name")
    private String subTaskName;

    @Column(name = "sub_task_priority")
    private String subTaskPriority;

    @Column(name = "sub_task_desc")
    private String subTaskDesc;

    //Todo Link User
    @Column(name = "sub_task_assigned_to")
    private Long subTaskAssignedTo;

    //Todo Link User
    @Column(name = "sub_task_reporter")
    private Long subTaskReporter;

    @Column(name = "sub_task_deadline")
    private LocalDateTime deadLine;

    @Column(name = "sub_task_start_date")
    private LocalDateTime startDate;

    @Column(name = "sub_task_end_date")
    private LocalDateTime endDate;

    @Column(name = "sub_task_type")
    private String subTaskType;

    @Column(name = "sub_task_link")
    private String link;

//  Checked
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_id")
    private TaskModel taskId;

//  Checked
    @JsonIgnore
    @OneToMany(mappedBy = "subtasks")
    List<CommentModel> comments;

//  Checked
    @JsonIgnore
    @OneToMany(mappedBy = "subTaskId",cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.REMOVE})
    List<SubTaskAttachmentModel> subTasksAttachments;

//  Checked
    @JsonIgnore
    @OneToMany(mappedBy = "subTaskId")
    List<WorkLogModel> worklogs;

}
