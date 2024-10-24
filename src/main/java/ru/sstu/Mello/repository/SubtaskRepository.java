package ru.sstu.Mello.repository;

import org.springframework.data.repository.ListCrudRepository;
import ru.sstu.Mello.model.Subtask;

public interface SubtaskRepository extends ListCrudRepository<Subtask, Integer> {

}
