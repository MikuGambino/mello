package ru.sstu.Mello.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sstu.Mello.exception.ResourceNotFoundException;
import ru.sstu.Mello.model.Subtask;
import ru.sstu.Mello.model.Task;
import ru.sstu.Mello.model.User;
import ru.sstu.Mello.model.dto.SubtaskRequest;
import ru.sstu.Mello.repository.SubtaskRepository;
import ru.sstu.Mello.repository.TaskRepository;
import ru.sstu.Mello.security.UserPrincipal;

@Service
@RequiredArgsConstructor
public class SubtaskService {
    private final SubtaskRepository subtaskRepository;
    private final TaskRepository taskRepository;

    public Subtask getSubtask(int id) {
        return subtaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subtask", "id", id));
    }

    public void addSubtask(int taskId, SubtaskRequest subtaskRequest, UserPrincipal currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        Subtask subtask = Subtask.builder()
                .title(subtaskRequest.getTitle())
                .isComplete(false)
                .task(task)
                .build();

        subtaskRepository.save(subtask);
    }

    public void deleteSubtask(int subtaskId, UserPrincipal currentUser) {
        Subtask subtask = getSubtask(subtaskId);
        subtaskRepository.delete(subtask);
    }

    public void updateStatus(int subtaskId, boolean status, UserPrincipal currentUser) {
        Subtask subtask = getSubtask(subtaskId);
        subtask.setComplete(status);
        subtaskRepository.save(subtask);
    }
}
