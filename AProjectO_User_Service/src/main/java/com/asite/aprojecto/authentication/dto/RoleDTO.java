package com.asite.aprojecto.authentication.dto;

import com.asite.aprojecto.authentication.models.PermissionModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class RoleDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long roleId;
    @NotBlank(message = "roleName must not be blank")
    @Size(max = 50, message = "roleName must be at most 50 characters")
    private String roleName;
    @NotBlank(message = "roleDesc must not be blank")
    @Size(max = 255, message = "roleDesc must be at most 255 characters")
    private String roleDesc;
    @NotNull(message = "permissions must not be null")
    private Set<PermissionModel> permissions;
    public RoleDTO(String roleName, String roleDesc, Set<PermissionModel> permissions) {
        this.roleName = roleName;
        this.roleDesc = roleDesc;
        this.permissions = permissions;
    }
}