package ru.sstu.Mello.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity(name = "project")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    private String title;
    private String color;
    @JoinColumn(name = "created_at")
    private LocalDateTime createdAt;
    @JoinColumn(name = "is_active")
    private boolean isActive;
}
