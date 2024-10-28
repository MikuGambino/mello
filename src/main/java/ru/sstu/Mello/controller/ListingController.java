package ru.sstu.Mello.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.sstu.Mello.model.dto.TaskRequest;
import ru.sstu.Mello.security.CurrentUser;
import ru.sstu.Mello.security.UserPrincipal;
import ru.sstu.Mello.service.ListingService;

import java.util.List;

@Controller
@RequestMapping("/lists")
@RequiredArgsConstructor
public class ListingController {
    private final ListingService listingService;

    @ResponseBody
    @PostMapping("/{listId}/change-order")
    public ResponseEntity<Void> saveListingState(@PathVariable int listId, @RequestBody List<TaskRequest> tasks,
                                                 @CurrentUser UserPrincipal currentUser) {
        listingService.changeOrder(listId, tasks, currentUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping("/{targetListId}/move/{taskId}")
    public ResponseEntity<Void> moveTask(@PathVariable int targetListId, @PathVariable int taskId,
                                         @CurrentUser UserPrincipal currentUser) {
        // todo добавить проверку
        listingService.moveTask(targetListId, taskId, currentUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
