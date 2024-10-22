package ru.sstu.Mello.repository;

import org.springframework.data.repository.ListCrudRepository;
import ru.sstu.Mello.model.Project;

public interface ProjectRepository extends ListCrudRepository<Project, Integer> {
}
