package ru.sstu.Mello.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sstu.Mello.model.dto.SignUpForm;
import ru.sstu.Mello.model.roles.Role;

import java.util.List;

@Data
@Entity(name = "users")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String image;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "invitations",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    private List<Project> invitations;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;

    public User(SignUpForm form) {
        this.username = form.getUsername();
        this.password = form.getPassword();
        this.email = form.getEmail();
    }
}
