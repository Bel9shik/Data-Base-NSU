package nsu.kardash.backendsportevents.migrations;

import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.models.Event;
import nsu.kardash.backendsportevents.models.SportsVenue;
import nsu.kardash.backendsportevents.models.Trainer;
import nsu.kardash.backendsportevents.repositories.EventRepository;
import nsu.kardash.backendsportevents.repositories.SportVenueRepository;
import nsu.kardash.backendsportevents.repositories.TrainerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class EventInitializer {

    private final EventRepository events;
    private final SportVenueRepository venues;
    private final TrainerRepository trainers;
    private final Random rnd = new Random();

    public void run(String... args) {
        if (events.count() > 0) return;

        List<SportsVenue> allVenues = venues.findAll();
        List<Trainer> allTrainers = trainers.findAll();

        // на всякий случай — если площадок или тренеров нет, пропускаем
        if (allVenues.isEmpty() || allTrainers.isEmpty()) return;

        List<Event> eventList = new ArrayList<>();

        for (int i = 1; i <= 100; i++) {
            Event event = new Event();
            event.setVenue(allVenues.get(rnd.nextInt(allVenues.size())));
            event.setTrainer(allTrainers.get(rnd.nextInt(allTrainers.size())));
            event.setCost(500 + rnd.nextInt(4500)); // от 500 до 5000
            OffsetDateTime start = OffsetDateTime.now().plusDays(rnd.nextInt(5));
            event.setStartedAt(start);
            // длительность 1–3 часов
            event.setEndedAt(start.plusHours(1 + rnd.nextInt(3)));
            // заполняем количество мест <= maxCapacity
            int cap = event.getVenue().getMaxCapacity();
            event.setSeatCount(10 + rnd.nextInt(Math.max(1, cap - 9)));
            event.setName("Event" + i);
            eventList.add(event);
        }
        events.saveAll(eventList);
    }
}
