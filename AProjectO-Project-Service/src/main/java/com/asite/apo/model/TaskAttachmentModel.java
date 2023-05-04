package com.asite.apo.model;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;

@Entity(name = "project_tx_task_attachment_tbl")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "attachmentId")
public class TaskAttachmentModel {

    @Id
    @Column(name = "attachment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attachmentId;

//  Checked
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "version_id")
    private ProjectIterationModel projectVersionId;

//  Checked
    @JoinColumn(name = "task_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private TaskModel taskId;

    @Column(name = "task_attachment_path")
    private String taskAttachmentName;

}
