package ru.sstu.Mello.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.sstu.Mello.exception.AccessDeniedException;
import ru.sstu.Mello.exception.ResourceNotFoundException;
import ru.sstu.Mello.model.Project;
import ru.sstu.Mello.model.User;
import ru.sstu.Mello.model.dto.EditProfileRequest;
import ru.sstu.Mello.model.dto.SignUpForm;
import ru.sstu.Mello.model.roles.Role;
import ru.sstu.Mello.model.roles.RoleName;
import ru.sstu.Mello.repository.RoleRepository;
import ru.sstu.Mello.repository.UserRepository;
import ru.sstu.Mello.security.CurrentUser;
import ru.sstu.Mello.security.UserPrincipal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static java.nio.file.Files.deleteIfExists;

@Service
@RequiredArgsConstructor
public class UserService {
    private final ImageService imageService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public User getByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void addUser(SignUpForm form) {
        User user = new User(form);

        List<Role> roles = new ArrayList<>();
        roles.add(
                roleRepository.findByTitle(RoleName.ROLE_USER).orElseThrow(() -> new RuntimeException("User role not set"))
        );

        user.setRoles(roles);
        user.setImage("default.png");
        user.setPassword(passwordEncoder.encode(form.getPassword()));

        userRepository.save(user);
    }

    public void updateUser(int id, EditProfileRequest request, MultipartFile file,
                             UserPrincipal currentUser) {
        User user = getUser(id);

        if (!currentUser.getId().equals(user.getId()) &&
                !currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.name()))) {
            throw new AccessDeniedException();
        }
        if (existsByUsername(request.getUsername()) &&
                !Objects.equals(request.getUsername(), currentUser.getUsername())) {
            throw new RuntimeException("Данный никнейм занят");
        }

        if (file != null && !file.isEmpty()) {
            if (!Objects.equals(currentUser.getImage(), "default.png")) {
                imageService.deleteImage(currentUser.getImage());
            }
            String image = imageService.saveImage(file);
            currentUser.setImage(image);
            user.setImage(image);
        }

        user.setUsername(request.getUsername());
        userRepository.save(user);
    }

    public User getUser(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public List<Project> getInvitations(String username) {
        User user = getByUsername(username);

        return user.getInvitations();
    }
}
