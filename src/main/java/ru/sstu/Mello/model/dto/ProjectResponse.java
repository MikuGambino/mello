package ru.sstu.Mello.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {
    private int id;
    private boolean isOwner;
    private String title;
    private String color;
    private boolean isActive;
    private boolean isLiked;
}
