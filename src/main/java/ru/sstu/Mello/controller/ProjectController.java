package ru.sstu.Mello.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.sstu.Mello.model.Project;
import ru.sstu.Mello.model.dto.*;
import ru.sstu.Mello.security.CurrentUser;
import ru.sstu.Mello.security.UserPrincipal;
import ru.sstu.Mello.service.*;

import java.util.logging.Logger;

@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final ListingService listingService;
    private final TaskService taskService;
    private final UserService userService;

    @GetMapping
    public String projects(Model model, @CurrentUser UserPrincipal currentUser) {
        model.addAttribute("image", currentUser.getImage());
        model.addAttribute("projects", projectService.getAllProjectResponses(currentUser));

        return "projects/all-projects";
    }

    @GetMapping("/liked")
    public String likedProjects(Model model, @CurrentUser UserPrincipal currentUser) {
        model.addAttribute("image", currentUser.getImage());
        model.addAttribute("projects", projectService.getLikedProjectResponses(currentUser));

        return "projects/liked-projects";
    }

    @GetMapping("/archived")
    public String archivedProjects(Model model, @CurrentUser UserPrincipal currentUser) {
        model.addAttribute("image", currentUser.getImage());
        model.addAttribute("projects", projectService.getArchivedProjectResponses(currentUser));

        return "projects/archived-projects";
    }

    @GetMapping("/add")
    public String addProjectView(Model model, @CurrentUser UserPrincipal currentUser) {
        model.addAttribute("project", new ProjectRequest());
        model.addAttribute("image", currentUser.getImage());

        return "projects/add";
    }

    @PostMapping("/add")
    public String addProject(Model model,
                             @ModelAttribute("project") @Valid ProjectRequest projectRequest,
                             BindingResult bindingResult, @CurrentUser UserPrincipal currentUser) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("image", currentUser.getImage());
            return "projects/add";
        }

        int id = projectService.addProject(projectRequest, currentUser);
        return "redirect:/projects/" + id;
    }

    @GetMapping("/{id}")
    public String projectView(Model model, @CurrentUser UserPrincipal currentUser,
                              @PathVariable int id) {
        projectService.checkAccess(id, currentUser);
        model.addAttribute("image", currentUser.getImage());
        model.addAttribute("projectId", id);
        model.addAttribute("isOwner", projectService.isOwner(currentUser.getUsername(), id));
        model.addAttribute("projectPage", true);
        model.addAttribute("projectTitle", projectService.getProject(id).getTitle());
        model.addAttribute("lists", listingService.getListingsByProject(id));

        return "projects/main";
    }

    @GetMapping("/{id}/add-listing")
    public String addListingView(Model model, @CurrentUser UserPrincipal currentUser,
                                 @PathVariable int id) {
        projectService.checkAccess(id, currentUser);
        model.addAttribute("listing", new ListingRequest());
        model.addAttribute("image", currentUser.getImage());
        model.addAttribute("projectId", id);

        return "projects/add-listing";
    }

    @PostMapping("/{id}/add-listing")
    public String addListing(Model model,
                             @ModelAttribute("listing") @Valid ListingRequest listingRequest,
                             BindingResult bindingResult, @CurrentUser UserPrincipal currentUser,
                             @PathVariable int id) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("image", currentUser.getImage());
            model.addAttribute("projectId", id);
            return "projects/add-listing";
        }

        listingService.addListing(id, listingRequest, currentUser);
        return "redirect:/projects/{id}";
    }

    @PostMapping("/{id}/lists/{listingId}/delete")
    public String deleteListing(@PathVariable int listingId, @CurrentUser UserPrincipal currentUser) {
        listingService.deleteListing(listingId, currentUser);

        return "redirect:/projects/{id}";
    }

    @PostMapping("/{id}/lists/{listingId}/change")
    public String changeListingTitle(@PathVariable int listingId,
                                     @CurrentUser UserPrincipal currentUser,
                                     @RequestParam("newListName") String newListName) {
        listingService.changeTitle(listingId, newListName, currentUser);

        return "redirect:/projects/{id}";
    }

    @GetMapping("/{id}/lists/{listingId}/tasks/new")
    public String createTaskView(Model model, @CurrentUser UserPrincipal currentUser,
                                 @PathVariable int id, @PathVariable int listingId) {
        projectService.checkAccess(id, currentUser);
        model.addAttribute("image", currentUser.getImage());
        model.addAttribute("task", new AddTaskRequest());
        model.addAttribute("projectId", id);
        model.addAttribute("listingId", listingId);

        return "projects/add-task";
    }

    @PostMapping("/{id}/lists/{listingId}/tasks/new")
    public String createTask(@ModelAttribute @Valid AddTaskRequest task, BindingResult bindingResult,
                             @PathVariable int id, @PathVariable int listingId,
                             @CurrentUser UserPrincipal currentUser, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("projectId", id);
            model.addAttribute("listingId", listingId);
            model.addAttribute("image", currentUser.getImage());
            return "projects/add-task";
        }

        taskService.addTask(listingId, task, currentUser);
        return "redirect:/projects/{id}";
    }

    @GetMapping("/{id}/members")
    public String members(@PathVariable int id, Model model, @CurrentUser UserPrincipal currentUser) {
        projectService.checkAccess(id, currentUser);
        Project project = projectService.getProject(id);
        model.addAttribute("image", currentUser.getImage());
        model.addAttribute("projectId", id);
        model.addAttribute("members", projectService.getMembers(id));
        model.addAttribute("isOwner", projectService.isOwner(currentUser.getUsername(), id));
        model.addAttribute("owner", project.getOwner());
        model.addAttribute("request", new UsernameRequest());
        model.addAttribute("invitations", project.getInvitations());

        return "projects/members";
    }

    @PostMapping("/{id}/members")
    public String sendInvite(@PathVariable int id, Model model, @CurrentUser UserPrincipal currentUser,
                             @ModelAttribute("request") UsernameRequest request, BindingResult bindingResult) {
        if (!userService.existsByUsername(request.getUsername())) {
            bindingResult.rejectValue("username", "error.request",
                    "Данного пользователя не существует");
        } else {
            if (projectService.hasInvitation(id, request.getUsername())) {
                bindingResult.rejectValue("username", "error.request",
                        "Пользователь уже имеет приглашение");
            }
            if (projectService.isMember(id, request.getUsername())) {
                bindingResult.rejectValue("username", "error.request",
                        "Пользователь уже является участником");
            }
        }

        if (bindingResult.hasErrors()) {
            Project project = projectService.getProject(id);
            model.addAttribute("image", currentUser.getImage());
            model.addAttribute("projectId", id);
            model.addAttribute("members", projectService.getMembers(id));
            model.addAttribute("isOwner", projectService.isOwner(currentUser.getUsername(), id));
            model.addAttribute("owner", project.getOwner());
            model.addAttribute("invitations", project.getInvitations());

            return "projects/members";
        }

        projectService.sendInvite(id, request.getUsername(), currentUser);
        return "redirect:/projects/{id}/members";
    }

    @PostMapping("/{id}/members/{userId}/delete")
    public String deleteInvitation(@PathVariable int id, Model model, @CurrentUser UserPrincipal currentUser,
                                   @PathVariable int userId) {
        projectService.deleteInvitation(id, userId, currentUser);

        return "redirect:/projects/{id}/members";
    }

    @PostMapping("/{id}/requests/reject")
    public String rejectRequest(@PathVariable int id, @CurrentUser UserPrincipal currentUser) {
        projectService.rejectRequest(id, currentUser);

        return "redirect:/profile/requests";
    }

    @PostMapping("/{id}/requests/accept")
    public String acceptRequest(@PathVariable int id, @CurrentUser UserPrincipal currentUser) {
        projectService.acceptRequest(id, currentUser);

        return "redirect:/profile/requests";
    }

    @PostMapping("/{id}/kick/{userId}")
    public String kickUser(@PathVariable int id, @PathVariable int userId,
                           @CurrentUser UserPrincipal currentUser) {
        projectService.kickMember(id, userId, currentUser);

        return "redirect:/projects/{id}/members";
    }

    @GetMapping("/{id}/edit")
    public String updateProject(@PathVariable int id, Model model, @CurrentUser UserPrincipal currentUser) {
        projectService.checkOwnerAccess(id, currentUser);
        model.addAttribute("image", currentUser.getImage());
        model.addAttribute("projectId", id);
        model.addAttribute("isActive", projectService.getProject(id).isActive());
        model.addAttribute("project", projectService.getProjectRequest(id));

        return "projects/edit-project";
    }

    @PostMapping("/{id}/delete")
    public String deleteProject(@PathVariable int id, @CurrentUser UserPrincipal currentUser) {
        projectService.deleteProject(id, currentUser);

        return "redirect:/projects";
    }

    @PostMapping("/{id}/edit")
    public String editProject(@PathVariable int id, @CurrentUser UserPrincipal currentUser,
                                @ModelAttribute("project") ProjectRequest projectRequest,
                                BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("image", currentUser.getImage());
            model.addAttribute("projectId", id);
            return "projects/edit-project";
        }

        projectService.editProject(id, projectRequest, currentUser);
        return "redirect:/projects/{id}";
    }

    @PostMapping("/{id}/archive")
    public String archiveProject(@PathVariable int id, @CurrentUser UserPrincipal currentUser,
                                 HttpServletRequest request) {
        projectService.archiveProject(id, currentUser);

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/projects");
    }

    @PostMapping("/{id}/unarchive")
    public String unarchiveProject(@PathVariable int id, @CurrentUser UserPrincipal currentUser,
                                   HttpServletRequest request) {
        projectService.unarchiveProject(id, currentUser);

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/projects");
    }

    @PostMapping("/{id}/like")
    public String likeProject(@PathVariable int id, @CurrentUser UserPrincipal currentUser,
                              HttpServletRequest request) {
        projectService.likeProject(id, currentUser);

        String referer = request.getHeader("Referer");
        Logger.getAnonymousLogger().info("redirect:" + (referer != null ? referer : "/projects"));
        return "redirect:" + (referer != null ? referer : "/projects");
    }

    @PostMapping("/{id}/unlike")
    public String unlikeProject(@PathVariable int id, @CurrentUser UserPrincipal currentUser,
                              HttpServletRequest request) {
        projectService.unlikeProject(id, currentUser);

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/projects");
    }
}
