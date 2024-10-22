package ru.sstu.Mello.repository;

import org.springframework.data.repository.ListCrudRepository;
import ru.sstu.Mello.model.roles.Role;
import ru.sstu.Mello.model.roles.RoleName;

import java.util.Optional;

public interface RoleRepository extends ListCrudRepository<Role, Integer> {
    Optional<Role> findByTitle(RoleName title);
}
