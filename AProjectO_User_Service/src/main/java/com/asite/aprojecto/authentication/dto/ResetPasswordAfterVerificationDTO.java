package com.asite.aprojecto.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordAfterVerificationDTO {
    @NotBlank(message = "newPassword must not be blank")
    @Size(min = 8, message = "newPassword must be at least 8 characters")
    private String newPassword;
    @NotBlank(message = "newConfirmedPassword must not be blank")
    @Size(min = 8, message = "newConfirmedPassword must be at least 8 characters")
    private String newConfirmPassword;
}
