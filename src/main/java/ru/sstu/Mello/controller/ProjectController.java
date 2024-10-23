package ru.sstu.Mello.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.sstu.Mello.model.dto.ListingRequest;
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
        model.addAttribute("image", currentUser.getImage());
        model.addAttribute("projectId", id);
        model.addAttribute("lists", listingService.getListingsByProject(id));

        return "projects/main";
    }

    @GetMapping("/{id}/add-listing")
    public String addListingView(Model model, @CurrentUser UserPrincipal currentUser,
                                 @PathVariable int id) {
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
}
