package ru.sstu.Mello.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sstu.Mello.exception.AccessDeniedException;
import ru.sstu.Mello.exception.ResourceNotFoundException;
import ru.sstu.Mello.model.*;
import ru.sstu.Mello.model.dto.AddTaskRequest;
import ru.sstu.Mello.model.dto.EditTaskRequest;
import ru.sstu.Mello.repository.*;
import ru.sstu.Mello.security.UserPrincipal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ListingRepository listingRepository;
    private final SubtaskRepository subtaskRepository;
    private final UserRepository userRepository;
    private final ProjectService projectService;

    @Transactional
    public void addTask(int listingId, AddTaskRequest addTaskRequest, UserPrincipal currentUser) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing", "id", listingId));
        projectService.checkAccess(listing.getProject().getId(), currentUser);


        Task task = Task.builder()
                .title(addTaskRequest.getTitle())
                .list(listing)
                .description(addTaskRequest.getDescription())
                .position(listing.getTasks().size())
                .build();

        taskRepository.save(task);

        List<Subtask> subtasks = addTaskRequest.getSubtasks().stream()
                .map(tr ->
                        Subtask.builder()
                                .title(tr.getName())
                                .task(task)
                                .isComplete(false)
                                .build())
                .peek(subtaskRepository::save)
                .toList();
    }

    public Task getTask(int id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        task.getSubtasks().sort(Comparator.comparingInt(Subtask::getId));
        return task;
    }

    public void saveTask(int id, EditTaskRequest taskRequest, UserPrincipal currentUser) {
        Task task = getTask(id);
        projectService.checkAccess(task.getList().getProject().getId(), currentUser);
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());

        taskRepository.save(task);
    }

    public void deleteTask(int taskId, UserPrincipal currentUser) {
        Task task = getTask(taskId);
        projectService.checkAccess(task.getList().getProject().getId(), currentUser);
        taskRepository.delete(task);
    }
}
