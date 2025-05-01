package nsu.kardash.backendsportevents.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.dto.requests.CreateEventDTO;
import nsu.kardash.backendsportevents.dto.requests.UpdateEventDTO;
import nsu.kardash.backendsportevents.dto.responses.positive.EventFullResponse;
import nsu.kardash.backendsportevents.dto.responses.positive.EventShortResponse;
import nsu.kardash.backendsportevents.dto.responses.positive.OkResponse;
import nsu.kardash.backendsportevents.exceptions.Event.EventNotFoundException;
import nsu.kardash.backendsportevents.models.Event;
import nsu.kardash.backendsportevents.repositories.EventRepository;
import nsu.kardash.backendsportevents.repositories.Specifications.EventSpecifications;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final TrainerService trainerService;
    private final SportVenueService sportVenueService;

    public Event getEventById(long id) {
        return eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException("Event not found"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public OkResponse createEvent(CreateEventDTO createEventDTO, BindingResult bindingResult) {

        ValidationService.checkValidationErrors(bindingResult);

        Event event = new Event();

        event.setName(createEventDTO.getName());
        event.setCost(createEventDTO.getCost());
        event.setSeatCount(createEventDTO.getSeatCount());
        event.setStartedAt(createEventDTO.getStartedAt());
        event.setEndedAt(createEventDTO.getEndedAt());
        event.setTrainer(trainerService.findById(createEventDTO.getTrainerID()));
        event.setVenue(sportVenueService.findSportVenueById(createEventDTO.getVenueID()));
        eventRepository.save(event);

        return new OkResponse("Event created");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public OkResponse updateEvent(@Valid UpdateEventDTO updateEventDTO, BindingResult bindingResult) {

        ValidationService.checkValidationErrors(bindingResult);

        Event event = getEventById(updateEventDTO.getId());

        if (updateEventDTO.getCost() != null) event.setCost(updateEventDTO.getCost());
        if (updateEventDTO.getName() != null && !updateEventDTO.getName().isBlank()) event.setName(updateEventDTO.getName());
        if (updateEventDTO.getCost() != null) event.setCost(updateEventDTO.getCost());
        if (updateEventDTO.getStartedAt() != null) event.setStartedAt(updateEventDTO.getStartedAt());
        if (updateEventDTO.getEndedAt() != null) event.setEndedAt(updateEventDTO.getEndedAt());
        if (updateEventDTO.getTrainerID() != null && event.getTrainer().getId() != updateEventDTO.getTrainerID()) event.setTrainer(trainerService.findById(updateEventDTO.getTrainerID()));
        if (updateEventDTO.getVenueID() != null && event.getVenue().getId() != updateEventDTO.getVenueID()) event.setVenue(sportVenueService.findSportVenueById(updateEventDTO.getVenueID()));

        eventRepository.save(event);
        return new OkResponse("Event updated");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public OkResponse deleteEvent(long eventId) {

        eventRepository.deleteById(eventId);

        return new OkResponse("Event deleted");
    }

    @Cacheable(
            value = "fullEventInfo",
            key = "#page + '-' + #size + '-' + T(org.springframework.util.StringUtils).collectionToDelimitedString(#allParams.entrySet(), ',')"
    )
    public Page<EventFullResponse> fullEventInfoWithFilters(Map<String, String> allParams, int page, int size, String[] sort) {

        var filters = new HashMap<>(allParams);
        filters.remove("page");
        filters.remove("size");
        filters.remove("sort");

        // создаём объект Sort
        Sort sortObj = Sort.by(
                Sort.Order.by(sort[0]).with(sort.length>1 && sort[1].equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC)
        );
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Specification<Event> spec = Specification.where(null);

        for (var entry : filters.entrySet()) {
            spec = spec.and(EventSpecifications.hasAttribute(entry.getKey(), entry.getValue()));
        }

        Page<Event> events = eventRepository.findAll(spec, pageable);

        return events.map(this::convertEventToFullResponse);
    }

    @Cacheable(
            value = "shortEventInfo",
            key = "#page + '-' + #size + '-' + T(org.springframework.util.StringUtils).collectionToDelimitedString(#allParams.entrySet(), ',')"
    )
    public Page<EventShortResponse> shortEventInfoWithFilters(Map<String, String> allParams, int page, int size, String[] sort) {

        var filters = new HashMap<>(allParams);
        filters.remove("page");
        filters.remove("size");
        filters.remove("sort");

        // создаём объект Sort
        Sort sortObj = Sort.by(
                Sort.Order.by(sort[0]).with(sort.length>1 && sort[1].equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC)
        );
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Specification<Event> spec = Specification.where(null);

        for (var entry : filters.entrySet()) {
            spec = spec.and(EventSpecifications.hasAttribute(entry.getKey(), entry.getValue()));
        }

        Page<Event> events = eventRepository.findAll(spec, pageable);

        return events.map(this::convertEventToShortResponse);
    }

    public EventFullResponse convertEventToFullResponse(Event event) {
        return new EventFullResponse(
                event.getId(),
                event.getName(),
                event.getCost(),
                event.getSeatCount(),
                event.getStartedAt(),
                event.getEndedAt(),
                Map.of("name", event.getVenue().getName(),
                        "address", event.getVenue().getAddress(),
                        "maxCapacity", String.valueOf(event.getVenue().getMaxCapacity())),
                Map.of("firstname", event.getTrainer().getFirstname(),
                        "surname", event.getTrainer().getSurname(),
                        "lastname", event.getTrainer().getLastname() == null ? "" : event.getTrainer().getLastname())
        );
    }

    public EventShortResponse convertEventToShortResponse(Event event) {
        return new EventShortResponse(
                event.getId(),
                event.getName(),
                event.getCost(),
                event.getStartedAt(),
                event.getEndedAt()
        );
    }
}
