package com.asite.aprojecto.authentication.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleDTO {
    @NotNull(message = "userId must not be null")
    @Min(value = 1,message = "Not less than 1")
    private Long userId;
    @NotNull(message = "roleId must not be null")
    @Min(value = 1,message = "Not less than 1")
    private Long roleId;
}