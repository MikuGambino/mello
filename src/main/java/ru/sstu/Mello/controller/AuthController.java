package ru.sstu.Mello.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.sstu.Mello.model.dto.SignUpForm;
import ru.sstu.Mello.service.UserService;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @GetMapping("/login")
    public String login(Model model, @RequestParam(name = "success", required = false) boolean success) {
        if (success) {
            model.addAttribute("message", "Вы успешно зарегистрировались!");
        }

        return "login";
    }

    @GetMapping("/signup")
    public String signupView(Model model) {
        model.addAttribute("user", new SignUpForm());
        return "signup";
    }

    @PostMapping("/signup")
    public String registerUser(@Valid @ModelAttribute("user") SignUpForm signUpForm,
                               BindingResult bindingResult) {
        if (userService.existsByEmail(signUpForm.getEmail())) {
            bindingResult.rejectValue("email", "error.user", "Аккаунт с данной почтой уже зарегистрирован");
        }
        if (userService.existsByUsername(signUpForm.getUsername())) {
            bindingResult.rejectValue("username", "error.user", "Аккаунт с данным никнеймом уже существует");
        }

        if (bindingResult.hasErrors()) {
            return "signup";
        }

        userService.addUser(signUpForm);

        return "redirect:/login?success=true";
    }
}
