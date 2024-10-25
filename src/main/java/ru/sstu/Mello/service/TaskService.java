package ru.sstu.Mello.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sstu.Mello.exception.ResourceNotFoundException;
import ru.sstu.Mello.model.Listing;
import ru.sstu.Mello.model.Project;
import ru.sstu.Mello.model.Subtask;
import ru.sstu.Mello.model.Task;
import ru.sstu.Mello.model.dto.AddTaskRequest;
import ru.sstu.Mello.model.dto.EditTaskRequest;
import ru.sstu.Mello.repository.ListingRepository;
import ru.sstu.Mello.repository.ProjectRepository;
import ru.sstu.Mello.repository.SubtaskRepository;
import ru.sstu.Mello.repository.TaskRepository;
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
    private final ProjectRepository projectRepository;

    @Transactional
    public void addTask(int listingId, AddTaskRequest addTaskRequest, UserPrincipal currentUser) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing", "id", listingId));

        Task task = Task.builder()
                .title(addTaskRequest.getTitle())
                .list(listing)
                .description(addTaskRequest.getDescription())
                .position(listing.getTasks().size())
                .build();

        taskRepository.save(task);

        addTaskRequest.getSubtasks().stream()
                .map(tr ->
                        Subtask.builder()
                                .title(tr.getName())
                                .task(task)
                                .isComplete(false)
                                .build())
                .peek(subtaskRepository::save);
    }

    public Task getTask(int id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        task.getSubtasks().sort(Comparator.comparingInt(Subtask::getId));
        return task;
    }

    public void saveTask(int id, EditTaskRequest taskRequest, UserPrincipal currentUser) {
        Task task = getTask(id);

        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());

        taskRepository.save(task);
    }
}
