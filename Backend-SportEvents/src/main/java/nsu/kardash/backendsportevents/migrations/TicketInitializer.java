package nsu.kardash.backendsportevents.migrations;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.models.Constants;
import nsu.kardash.backendsportevents.models.Event;
import nsu.kardash.backendsportevents.models.Person;
import nsu.kardash.backendsportevents.models.Ticket;
import nsu.kardash.backendsportevents.repositories.EventRepository;
import nsu.kardash.backendsportevents.repositories.PeopleRepository;
import nsu.kardash.backendsportevents.repositories.TicketRepository;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.*;

import static java.util.stream.Collectors.toMap;

@Component
@RequiredArgsConstructor
public class TicketInitializer {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final PeopleRepository peopleRepository;
    private final EntityManager em;  // чтобы получать прокси, а не полные объекты

    private final Random rnd = new Random();

    @Transactional
    public void run(String... args) {
        if (ticketRepository.count() > 0) return;

        List<Event> events = eventRepository.findAll();
        Map<Long, Integer> capacityByEvent = events.stream()
                .collect(toMap(Event::getId, Event::getSeatCount));

        Map<Long, Integer> usedSeats = new HashMap<>();
        capacityByEvent.keySet().forEach(id ->
                usedSeats.put(id, 0));

        List<Person> people = peopleRepository.findAll();
        int peopleSize = people.size();

        List<Ticket> buffer = new ArrayList<>(200);
        int target = events.size() * peopleSize / 2;
        for (int i = 0; i < target; i++) {
            Event ev = events.get(rnd.nextInt(events.size()));
            long evId = ev.getId();

            if (usedSeats.get(evId) >= capacityByEvent.get(evId)) {
                continue;
            }

            Ticket t = new Ticket();
            String status = randomStatus();

            // вместо full fetch мы берём прокси, чтобы не дергать БД на JOIN'ы
            t.setEvent(em.getReference(Event.class, evId));
            t.setPerson(people.get(rnd.nextInt(peopleSize)));
            t.setRegistrationTime(OffsetDateTime.now().minusDays(rnd.nextInt(30)));
            t.setStatus(status);

            buffer.add(t);

            if (!status.equals(Constants.cancelled)) usedSeats.put(evId, usedSeats.get(evId) + 1);

            if (buffer.size() >= 200) {
                ticketRepository.saveAll(buffer);
                buffer.clear();
                // явно сбрасываем контекст, чтобы Hibernate не держал их в PersistenceContext
                em.flush();
                em.clear();
            }
        }
        if (!buffer.isEmpty()) {
            ticketRepository.saveAll(buffer);
            em.flush();
            em.clear();
        }
    }

    private String randomStatus() {
        return switch (rnd.nextInt(3)) {
            case 0 -> Constants.pending;
            case 1 -> Constants.confirmed;
            default -> Constants.cancelled;
        };
    }
}
