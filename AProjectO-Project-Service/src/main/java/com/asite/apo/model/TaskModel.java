package com.asite.apo.model;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "project_tx_task_tbl")
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "taskId")
@SequenceGenerator(name="seq", initialValue=6, allocationSize=1)
public class TaskModel {

    @Id
    @Column(name = "task_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "seq")
    private Long taskId;

    @Column(name = "task_name")
    private String taskName;

    @Column(name = "task_desc")
    private String taskDesc;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "deadline")
    private LocalDateTime taskDeadline;

    @Column(name = "task_priority")
    private String taskPriority;

    //Todo Link to User
    @Column(name = "task_assigned_to")
    private Long taskAssignee;

    //Todo Link to User
    @Column(name = "task_reporter")
    private Long taskReporter;

    @Column(name = "status")
    private String taskStatus;

    //  Checked
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private ProjectModel projectId;

    //  Checked
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "version_id")
    private ProjectIterationModel projectVersionId;

    //  Checked
    @JsonIgnore
    @OneToMany(mappedBy = "tasks")
    List<CommentModel> comments;

    //  Checked
    @JsonIgnore
    @OneToMany(mappedBy = "taskId")
    List<SubTaskModel> subtasks;

    //  Checked
    @JsonIgnore
    @OneToMany(mappedBy = "taskId",cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.REMOVE})
    List<TaskAttachmentModel> taskAttachments;

    //  Checked
    @JsonIgnore
    @OneToMany(mappedBy = "taskId")
    List<WorkLogModel> worklogs;

    @Override
    public String toString() {
        return "TaskModel{" +
                "taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", taskDesc='" + taskDesc + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", taskDeadline=" + taskDeadline +
                ", taskPriority='" + taskPriority + '\'' +
                ", taskAssignee=" + taskAssignee +
                ", taskReporter=" + taskReporter +
                ", taskStatus='" + taskStatus + '\'' +
                ", projectId=" + projectId +
                ", projectVersionId=" + projectVersionId +
                ", comments=" + comments +
                ", subtasks=" + subtasks +
                ", taskAttachments=" + taskAttachments +
                ", worklogs=" + worklogs +
                '}';
    }
}

