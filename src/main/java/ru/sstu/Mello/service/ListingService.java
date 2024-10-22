package ru.sstu.Mello.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sstu.Mello.repository.ListingRepository;

@Service
@RequiredArgsConstructor
public class ListingService {
    private final ListingRepository listingRepository;

}
