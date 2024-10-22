package ru.sstu.Mello.repository;

import org.springframework.data.repository.ListCrudRepository;
import ru.sstu.Mello.model.Listing;

public interface ListingRepository extends ListCrudRepository<Listing, Integer> {

}
