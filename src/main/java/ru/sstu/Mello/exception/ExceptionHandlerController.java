package ru.sstu.Mello.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.sstu.Mello.security.CurrentUser;
import ru.sstu.Mello.security.UserPrincipal;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String notFound(ResourceNotFoundException ex, Model model, @CurrentUser UserPrincipal userPrincipal) {
        model.addAttribute("code", 404);
        model.addAttribute("description", ex.getMessage());
        model.addAttribute("image", userPrincipal.getImage());
        return "error_page";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String forbidden(AccessDeniedException ex, Model model, @CurrentUser UserPrincipal userPrincipal) {
        model.addAttribute("code", 403);
        model.addAttribute("description", "Access denied");
        model.addAttribute("image", userPrincipal.getImage());
        return "error_page";
    }

    @ExceptionHandler(BadRequestException.class)
    public String badRequest(BadRequestException ex, Model model, @CurrentUser UserPrincipal userPrincipal) {
        model.addAttribute("code", 400);
        model.addAttribute("description", ex.getMessage());
        model.addAttribute("image", userPrincipal.getImage());
        return "error_page";
    }
}
