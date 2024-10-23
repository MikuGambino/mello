package ru.sstu.Mello.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.sstu.Mello.model.dto.ProjectRequest;
import ru.sstu.Mello.model.dto.TaskRequest;
import ru.sstu.Mello.security.CurrentUser;
import ru.sstu.Mello.security.UserPrincipal;
import ru.sstu.Mello.service.ListingService;
import ru.sstu.Mello.service.ProjectService;

import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final ListingService listingService;

    @GetMapping
    public String projects(Model model, @CurrentUser UserPrincipal currentUser) {
        model.addAttribute("image", currentUser.getImage());

        return "projects/all-projects";
    }

    @GetMapping("/add")
    public String addProjectView(Model model, @CurrentUser UserPrincipal currentUser) {
        model.addAttribute("project", new ProjectRequest());
        model.addAttribute("image", currentUser.getImage());

        return "projects/add";
    }

    @PostMapping
    public String addProject(@ModelAttribute("project") @Valid ProjectRequest projectRequest,
                             BindingResult bindingResult, @CurrentUser UserPrincipal currentUser) {
        if (bindingResult.hasErrors()) {
            return "projects/add";
        }

        int id = projectService.addProject(projectRequest, currentUser);
        return "redirect:/projects/" + id;
    }

    @GetMapping("/{id}")
    public String projectView(Model model, @CurrentUser UserPrincipal currentUser,
                              @PathVariable int id) {
        model.addAttribute("image", currentUser.getImage());
        model.addAttribute("lists", listingService.getListingsByProject(id));

        return "projects/main";
    }
}
