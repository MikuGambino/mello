package ru.sstu.Mello.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpForm {
    @NotBlank
    @Size(min = 3, max = 40)
    private String username;
    @NotBlank
    @Size(max = 60)
    private String email;
    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
}
