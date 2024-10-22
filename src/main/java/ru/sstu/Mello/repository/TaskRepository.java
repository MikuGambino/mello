package ru.sstu.Mello.repository;

import org.springframework.data.repository.ListCrudRepository;
import ru.sstu.Mello.model.Task;

public interface TaskRepository extends ListCrudRepository<Task, Integer> {
}
