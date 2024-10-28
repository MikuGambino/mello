package ru.sstu.Mello.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sstu.Mello.exception.AccessDeniedException;
import ru.sstu.Mello.exception.ResourceNotFoundException;
import ru.sstu.Mello.model.Subtask;
import ru.sstu.Mello.model.Task;
import ru.sstu.Mello.model.User;
import ru.sstu.Mello.model.dto.SubtaskRequest;
import ru.sstu.Mello.repository.SubtaskRepository;
import ru.sstu.Mello.repository.TaskRepository;
import ru.sstu.Mello.repository.UserRepository;
import ru.sstu.Mello.security.UserPrincipal;

@Service
@RequiredArgsConstructor
public class SubtaskService {
    private final SubtaskRepository subtaskRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectService projectService;

    public Subtask getSubtask(int id) {
        return subtaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subtask", "id", id));
    }

    public void addSubtask(int taskId, SubtaskRequest subtaskRequest, UserPrincipal currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        projectService.checkAccess(task.getList().getProject().getId(), currentUser);

        Subtask subtask = Subtask.builder()
                .title(subtaskRequest.getTitle())
                .isComplete(false)
                .task(task)
                .build();

        subtaskRepository.save(subtask);
    }

    public void deleteSubtask(int subtaskId, UserPrincipal currentUser) {
        Subtask subtask = getSubtask(subtaskId);
        projectService.checkAccess(subtask.getTask().getList().getProject().getId(), currentUser);

        subtaskRepository.delete(subtask);
    }

    public void updateStatus(int subtaskId, boolean status, UserPrincipal currentUser) {
        Subtask subtask = getSubtask(subtaskId);
        projectService.checkAccess(subtask.getTask().getList().getProject().getId(), currentUser);

        subtask.setComplete(status);
        subtaskRepository.save(subtask);
    }
}
