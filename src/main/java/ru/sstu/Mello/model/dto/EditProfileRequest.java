package ru.sstu.Mello.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EditProfileRequest {
    @NotBlank
    @Size(min = 3, max = 40)
    private String username;
}
