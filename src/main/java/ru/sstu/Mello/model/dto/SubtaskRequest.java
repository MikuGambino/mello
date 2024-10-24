package ru.sstu.Mello.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubtaskRequest {
    @NotBlank
    private String title;
}
