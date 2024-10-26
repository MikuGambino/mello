package ru.sstu.Mello.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.sstu.Mello.model.User;
import ru.sstu.Mello.model.dto.EditProfileRequest;
import ru.sstu.Mello.security.CurrentUser;
import ru.sstu.Mello.security.UserPrincipal;
import ru.sstu.Mello.service.ProjectService;
import ru.sstu.Mello.service.UserService;

import java.util.Objects;
import java.util.logging.Logger;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {
    private final UserService userService;
    private final ProjectService projectService;

    @GetMapping
    public String profile(Model model, @CurrentUser UserPrincipal currentUser) {
        model.addAttribute("profile", currentUser);
        model.addAttribute("image", currentUser.getImage());

        return "profile/main";
    }

    @PostMapping
    public String editProfile(Model model,
                              @ModelAttribute("profile") @Valid EditProfileRequest profileRequest,
                              BindingResult bindingResult,
                              @RequestParam(value = "image", required = false) MultipartFile file,
                              @CurrentUser UserPrincipal currentUser) {

        if (!Objects.equals(profileRequest.getUsername(), currentUser.getUsername()) &&
                userService.existsByUsername(profileRequest.getUsername())) {
            bindingResult.rejectValue("username", "error.profile", "Аккаунт с данным никнеймом уже существует");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("image", currentUser.getImage());
            return "profile/main";
        }

        userService.updateUser(currentUser.getId(), profileRequest, file, currentUser);
        return "redirect:/profile";
    }

    @GetMapping("/requests")
    public String profileRequestsView(Model model, @CurrentUser UserPrincipal currentUser) {
        model.addAttribute("profile", currentUser);
        model.addAttribute("image", currentUser.getImage());
        model.addAttribute("invitations", userService.getInvitations(currentUser.getUsername()));

        return "profile/requests";
    }
}
