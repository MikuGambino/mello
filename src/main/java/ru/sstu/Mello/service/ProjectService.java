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
}
