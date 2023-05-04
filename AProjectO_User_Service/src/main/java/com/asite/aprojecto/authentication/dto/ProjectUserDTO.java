package com.asite.aprojecto.authentication.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectUserDTO {
    @NotNull(message = "Cannot be null")
    private List<UserRoleDTO> users;
}