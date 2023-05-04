package com.asite.aprojecto.authentication.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.Set;

@Entity
@Table(name = "user_ms_permission_tbl")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "permission_id")
public class PermissionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long permission_id;
    @Column(nullable = false)
    private String permission_name;
    @JsonIgnore
    @ManyToMany(mappedBy = "permissions", cascade = CascadeType.PERSIST)
    Set<RoleModel> roles;
}
