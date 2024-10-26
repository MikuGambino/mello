package ru.sstu.Mello.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sstu.Mello.exception.ResourceNotFoundException;
import ru.sstu.Mello.model.Listing;
import ru.sstu.Mello.model.Project;
import ru.sstu.Mello.model.Task;
import ru.sstu.Mello.model.User;
import ru.sstu.Mello.model.dto.ProjectRequest;
import ru.sstu.Mello.repository.ListingRepository;
import ru.sstu.Mello.repository.ProjectRepository;
import ru.sstu.Mello.repository.TaskRepository;
import ru.sstu.Mello.repository.UserRepository;
import ru.sstu.Mello.security.UserPrincipal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final TaskRepository taskRepository;

    public Project getProject(int id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
    }

    @Transactional
    public int addProject(ProjectRequest projectRequest, UserPrincipal userPrincipal) {
        User user = userRepository.findByUsername(userPrincipal.getUsername());

        Project project = Project.builder()
                .color("#e9edc9")
                .title(projectRequest.getTitle())
                .owner(user)
                .members(new ArrayList<>(List.of(user)))
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        projectRepository.save(project);

        Listing todo = Listing.builder()
                .project(project)
                .title("ToDo")
                .build();
        listingRepository.save(todo);

        Task exampleTask1 = Task.builder()
                .title("Example 1")
                .description("It's example")
                .position(0)
                .list(todo)
                .build();
        taskRepository.save(exampleTask1);

        Task exampleTask2 = Task.builder()
                .title("Example 2")
                .description("It's example")
                .position(1)
                .list(todo)
                .build();
        taskRepository.save(exampleTask2);

        Listing inAction = Listing.builder()
                .project(project)
                .title("В работе")
                .build();
        listingRepository.save(inAction);

        Listing complete = Listing.builder()
                .project(project)
                .title("Готово")
                .build();
        listingRepository.save(complete);

        return project.getId();
    }

    public List<User> getMembers(int id) {
        Project project = getProject(id);

        List<User> members = project.getMembers();
        members.remove(project.getOwner());

        return members;
    }

    public boolean isOwner(String username, int id) {
        User user = userRepository.findByUsername(username);
        Project project = getProject(id);

        return user.getUsername().equals(project.getOwner().getUsername());
    }

    public void sendInvite(int id, String username, UserPrincipal currentUser) {
        Project project = getProject(id);
        User user = userRepository.findByUsername(username);
        project.getInvitations().add(user);
        projectRepository.save(project);
    }

    public boolean hasInvitation(int id, String username) {
        Project project = getProject(id);
        User user = userRepository.findByUsername(username);
        return project.getInvitations().contains(user);
    }

    public boolean isMember(int id, String username) {
        Project project = getProject(id);
        User user = userRepository.findByUsername(username);
        return project.getMembers().contains(user);
    }

    public void deleteInvitation(int projectId, int userId, UserPrincipal currentUser) {
        Project project = getProject(projectId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        project.getInvitations().remove(user);
        projectRepository.save(project);
    }

    public void rejectRequest(int projectId, UserPrincipal currentUser) {
        Project project = getProject(projectId);
        User user = userRepository.findByUsername(currentUser.getUsername());
        project.getInvitations().remove(user);
        projectRepository.save(project);
    }

    public void acceptRequest(int projectId, UserPrincipal currentUser) {
        Project project = getProject(projectId);
        User user = userRepository.findByUsername(currentUser.getUsername());
        project.getInvitations().remove(user);
        project.getMembers().add(user);
        projectRepository.save(project);
    }

    public void kickMember(int projectId, int userId, UserPrincipal currentUser) {
        Project project = getProject(projectId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        project.getMembers().remove(user);
        projectRepository.save(project);
    }
}
