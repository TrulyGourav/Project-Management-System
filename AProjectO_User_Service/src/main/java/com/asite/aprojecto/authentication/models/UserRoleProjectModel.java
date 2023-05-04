package com.asite.aprojecto.authentication.models;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "user_lk_project_role_user_tbl")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleProjectModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long uid;
    @Column(name = "role_id")
    private Long roleId;
    @Column(name = "project_id")
    private Long projectId;
    @Column(name = "is_deleted")
    boolean isDeleted;
}
