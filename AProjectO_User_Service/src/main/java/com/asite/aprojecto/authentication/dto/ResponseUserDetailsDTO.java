package com.asite.aprojecto.authentication.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.*;
import java.util.Locale;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResponseUserDetailsDTO {

    @NotNull(message = "User ID cannot be null")
    private Long uid;
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters long")
    private String firstName;
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters long")
    private String lastName;
    @NotBlank(message = "Designation title is required")
    @Size(min = 2, max = 50, message = "Designation title must be between 2 and 50 characters long")
    private String designationTitle;
    @NotBlank(message = "Mobile number is required")
    @Size(min = 10, max = 15, message = "Mobile number must be between 10 and 15 digits long")
    private String mobileNumber;
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank(message = "Role is required")
    private String role;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String country=Locale.getDefault().getDisplayCountry();
    @JsonIgnore
    private String token;
}
