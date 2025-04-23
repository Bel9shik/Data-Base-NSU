package nsu.kardash.backendsportevents.migrations;

import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.models.SportsVenue;
import nsu.kardash.backendsportevents.repositories.SportVenueRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SportsVenueInitializer {

    private final SportVenueRepository sportsVenueRepository;

    public void run(String... args) {
        if (sportsVenueRepository.count() > 0) return;

        List<SportsVenue> sportsVenues = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            SportsVenue venue = new SportsVenue();
            venue.setName("Площадка №" + i);
            venue.setAddress("Город, улица Спортивная, д. " + (10 + i));
            venue.setMaxCapacity(30 + i * 5);
            sportsVenues.add(venue);
        }

        sportsVenueRepository.saveAll(sportsVenues);

    }
}
