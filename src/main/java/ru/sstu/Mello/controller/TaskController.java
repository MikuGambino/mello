package ru.sstu.Mello.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.sstu.Mello.model.dto.EditTaskRequest;
import ru.sstu.Mello.model.dto.SubtaskRequest;
import ru.sstu.Mello.security.CurrentUser;
import ru.sstu.Mello.security.UserPrincipal;
import ru.sstu.Mello.service.ProjectService;
import ru.sstu.Mello.service.SubtaskService;
import ru.sstu.Mello.service.TaskService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/projects/{id}/tasks")
public class TaskController {
    private final TaskService taskService;
    private final SubtaskService subtaskService;
    private final ProjectService projectService;

    @GetMapping("/{taskId}")
    public String editTaskView(@PathVariable int id, @PathVariable int taskId,
                               Model model, @CurrentUser UserPrincipal currentUser) {
        projectService.checkAccess(id, currentUser);
        model.addAttribute("image", currentUser.getImage());
        model.addAttribute("task", taskService.getTask(taskId));
        model.addAttribute("projectId", id);

        return "/projects/task";
    }

    @PostMapping("/{taskId}")
    public String editTask(@PathVariable int id, @PathVariable int taskId,
                           Model model, @CurrentUser UserPrincipal currentUser,
                           @ModelAttribute @Valid EditTaskRequest task, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("image", currentUser.getImage());
            model.addAttribute("task", taskService.getTask(taskId));
            model.addAttribute("projectId", id);
            return "projects/add-task";
        }

        taskService.saveTask(taskId, task, currentUser);

        return "redirect:/projects/{id}/tasks/{taskId}";
    }

    @GetMapping("/{taskId}/subtasks/new")
    public String newSubtaskView(Model model, @CurrentUser UserPrincipal currentUser,
                                 @PathVariable int id, @PathVariable int taskId) {
        projectService.checkAccess(id, currentUser);
        model.addAttribute("image", currentUser.getImage());
        model.addAttribute("subtask", new SubtaskRequest());
        model.addAttribute("projectId", id);
        model.addAttribute("taskId", taskId);

        return "projects/add-subtask";
    }

    @PostMapping("/{taskId}/subtasks/new")
    public String newSubtask(@PathVariable int id, @PathVariable int taskId,
                             Model model, @CurrentUser UserPrincipal currentUser,
                             @ModelAttribute @Valid SubtaskRequest subtask, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("image", currentUser.getImage());
            model.addAttribute("projectId", id);
            model.addAttribute("taskId", taskId);
            return "projects/add-subtask";
        }

        subtaskService.addSubtask(taskId, subtask, currentUser);

        return "redirect:/projects/{id}/tasks/{taskId}";
    }

    @PostMapping("/{taskId}/subtasks/{subtaskId}/delete")
    public String deleteSubtask(@PathVariable int id, @PathVariable int taskId, @PathVariable int subtaskId,
                                @CurrentUser UserPrincipal currentUser) {
        subtaskService.deleteSubtask(subtaskId, currentUser);

        return "redirect:/projects/{id}/tasks/{taskId}";
    }

    @ResponseBody
    @PostMapping("/{taskId}/subtasks/{subtaskId}/update-status")
    public ResponseEntity<Void> updateStatus(@PathVariable int subtaskId,
                                             @RequestParam("status") boolean status,
                                             @CurrentUser UserPrincipal currentUser) {
        subtaskService.updateStatus(subtaskId, status, currentUser);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{taskId}/delete")
    public String deleteTask(@PathVariable int id, @PathVariable int taskId,
                             @CurrentUser UserPrincipal currentUser) {
        taskService.deleteTask(taskId, currentUser);

        return "redirect:/projects/{id}";
    }
}
