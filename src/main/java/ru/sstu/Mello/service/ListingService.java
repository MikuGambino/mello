package ru.sstu.Mello.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sstu.Mello.exception.BadRequestException;
import ru.sstu.Mello.exception.ResourceNotFoundException;
import ru.sstu.Mello.model.Listing;
import ru.sstu.Mello.model.Project;
import ru.sstu.Mello.model.Task;
import ru.sstu.Mello.model.User;
import ru.sstu.Mello.model.dto.ListingRequest;
import ru.sstu.Mello.model.dto.TaskRequest;
import ru.sstu.Mello.repository.ListingRepository;
import ru.sstu.Mello.repository.ProjectRepository;
import ru.sstu.Mello.repository.TaskRepository;
import ru.sstu.Mello.security.UserPrincipal;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListingService {
    private final ListingRepository listingRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public List<Listing> getListingsByProject(int projectId) {
        return listingRepository.findAll()
                .stream()
                .filter(listing -> listing.getProject().getId().equals(projectId))
                .peek(listing -> listing.setTasks(
                        listing.getTasks().stream()
                                .sorted(Comparator.comparingInt(Task::getPosition))
                                .collect(Collectors.toList())
                ))
                .sorted(Comparator.comparingInt(Listing::getId))
                .toList();
    }

    public Listing getListing(int id) {
        return listingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listing", "id", id));
    }

    public void changeOrder(int id, List<TaskRequest> taskRequests) {
        Listing listing = getListing(id);

        List<Task> tasks = listing.getTasks().stream().sorted(Comparator.comparingInt(Task::getId)).toList();
        taskRequests = taskRequests.stream().sorted(Comparator.comparingInt(TaskRequest::getId)).toList();

        for (int i = 0; i < tasks.size(); i++) {
            int newPosition = taskRequests.get(i).getPosition();
            tasks.get(i).setPosition(newPosition);
        }

        listingRepository.save(listing);
    }

    public void moveTask(int targetListId, int taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        Listing listing = getListing(targetListId);
        task.setList(listing);
        taskRepository.save(task);
    }

    public void addListing(int projectId, ListingRequest listingRequest, UserPrincipal currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        Listing listing = Listing.builder()
                .project(project)
                .title(listingRequest.getTitle())
                .build();

        listingRepository.save(listing);
    }

    public void deleteListing(int listingId, UserPrincipal currentUser) {
        Listing listing = getListing(listingId);

        if (!listing.getTasks().isEmpty()) {
            throw new BadRequestException("Список имеет задачи");
        }

        listingRepository.delete(listing);
    }

    public void changeTitle(int listingId, String title, UserPrincipal currentUser) {
        Listing listing = getListing(listingId);
        listing.setTitle(title);
        listingRepository.save(listing);
    }
}
