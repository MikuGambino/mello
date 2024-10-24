package ru.sstu.Mello.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AddTaskRequest {
    @NotBlank
    private String title;
    private String description;
    private List<AddSubtaskRequest> subtasks = new ArrayList<>();
}
