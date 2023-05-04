package com.asite.apo.model;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;

@Entity(name = "project_tx_sub_task_attachment_tbl")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "subTaskAttachmentId")
public class SubTaskAttachmentModel {

    @Id
    @Column(name="sub_task_attachment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subTaskAttachmentId;

//    Link
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="sub_task_id")
    private SubTaskModel subTaskId;

    @Column(name="sub_task_attachment_path")
    private String subTaskAttachmentName;
}
