package com.asite.aprojecto.authentication.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
@Setter
public class UserUpdateDetailsDTO {
    @NotNull(message = "uid must not be null")
    private Long uid;
    @NotBlank(message = "firstName must not be blank")
    @Size(max = 50, message = "firstName must be less than or equal to {max} characters")
    private String firstName;
    @NotBlank(message = "lastName must not be blank")
    @Size(max = 50, message = "lastName must be less than or equal to {max} characters")
    private String lastName;
    @NotBlank(message = "mobileNumber must not be blank")
    @Size(min = 10, max = 10, message = "mobileNumber must be {max} digits")
    private String mobileNumber;
    @NotBlank(message = "designationTitle must not be blank")
    @Size(max = 100, message = "designationTitle must be less than or equal to {max} characters")
    private String designationTitle;
    @NotBlank(message = "role must not be blank")
    @Pattern(regexp = "ADMIN|USER", message = "Invalid role")
    private String role;
}
