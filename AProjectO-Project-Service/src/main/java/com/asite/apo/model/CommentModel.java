package com.asite.apo.model;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "project_tx_comment_tbl")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "commentId")
@Builder
public class CommentModel {
    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(name = "comment_desc")
    private String commentDesc;

    //Todo  link User
    @Column(name = "commented_by")
    private Long commentBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

//  Checked
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(name = "project_lk_comment_sub_task_tbl",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "sub_task_id"))
    private SubTaskModel subtasks;


//  Checked
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(name = "project_lk_comment_task_tbl",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id"))
    private TaskModel tasks;
}
