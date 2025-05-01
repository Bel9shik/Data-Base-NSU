package nsu.kardash.backendsportevents.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.dto.requests.ConfirmTicketDTO;
import nsu.kardash.backendsportevents.dto.requests.EventRequestDTO;
import nsu.kardash.backendsportevents.dto.responses.positive.*;
import nsu.kardash.backendsportevents.exceptions.Ticket.TicketNotCreatedException;
import nsu.kardash.backendsportevents.exceptions.Ticket.TicketNotFoundException;
import nsu.kardash.backendsportevents.models.*;
import nsu.kardash.backendsportevents.repositories.Specifications.TicketSpecifications;
import nsu.kardash.backendsportevents.repositories.TicketRepository;
import nsu.kardash.backendsportevents.security.PersonDetails;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final MailSenderService mailSenderService;
    private final EventService eventService;
    private final PersonService personService;

    public List<Ticket> getAllTickets(Event event) {
        return ticketRepository.findTicketsByEvent(event);
    }

    public long countTicketsByEventAndStatusNot(Event event, String status) {
        return ticketRepository.countByEventAndStatusNot(event, status);
    }

    public long createTicket(Event event, Person visitor) {

        if (ticketRepository.findByEventAndPerson(event, visitor).isPresent())
            throw new TicketNotCreatedException("Ticket already exists");

        Ticket ticket = new Ticket();
        ticket.setEvent(event);
        ticket.setPerson(visitor);

        ticketRepository.save(ticket);

        return ticket.getId();
    }

    public RegistrationOnEventResponse registerPersonOnEvent(@Valid EventRequestDTO eventRequest, BindingResult bindingResult) {

        ValidationService.checkValidationErrors(bindingResult);

        long id = createTicket(getEventById(eventRequest.getEventId()), personService.getPersonById(getCurrentId()));
        return new RegistrationOnEventResponse(id);
    }

    public Event getEventById(long id) {
        return eventService.getEventById(id);
    }

    private long getCurrentId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        return personDetails.getId();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ROOT')")
    public Page<ConfirmTicketResponse> confirmTickets(int page, int size) {

        Page<Ticket> tickets = ticketRepository.findTicketsByStatusIs(Constants.STATUS_PENDING, PageRequest.of(page, size, Sort.by("registrationTime")));

        return tickets.map(ticket -> {
            Person visitor = ticket.getPerson();
            Event event = ticket.getEvent();
            return new ConfirmTicketResponse(
                    ticket.getId(),
                    visitor.getFirstname(),
                    visitor.getSurname(),
                    visitor.getLastname(),
                    visitor.getEmail(),
                    visitor.isEmailVerified(),
                    event.getVenue().getName(),
                    event.getName()
            );
        });

    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ROOT')")
    public OkResponse changeTicketStatus(ConfirmTicketDTO confirmTicketDTO) {

        String status = confirmTicketDTO.isAccepted() ? Constants.STATUS_CONFIRMED : Constants.STATUS_CANCELLED;

        Ticket ticket = ticketRepository.findById(confirmTicketDTO.getTicketID()).orElseThrow(() -> new TicketNotFoundException("Ticket not found"));

        ticket.setStatus(status);
        ticketRepository.save(ticket);

        if (status.equals(Constants.STATUS_CONFIRMED)) {
            mailSenderService.sendNotifyEmail(ticket.getPerson().getEmail(), ticket.getEvent().getName());
        }

        return new OkResponse("Status changed");
    }

    public OkResponse deleteTicket(long ticketId) {

        ticketRepository.deleteById(ticketId);

        return new OkResponse("Ticket deleted");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    @Cacheable(
            value = "ticketsFilteredInfo",
            key = "#page + '-' + #size + '-' + T(org.springframework.util.StringUtils).collectionToDelimitedString(#allParams.entrySet(), ',')"
    )
    public Page<TicketResponse> findByFilters(Map<String, String> allParams, int page, int size, String[] sort) {

        var filters = new HashMap<>(allParams);
        filters.remove("page");
        filters.remove("size");
        filters.remove("sort");

        // создаём объект Sort
        Sort sortObj = Sort.by(
                Sort.Order.by(sort[0]).with(sort.length > 1 && sort[1].equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC)
        );
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Specification<Ticket> spec = Specification.where(null);

        for (var entry : filters.entrySet()) {
            spec = spec.and(TicketSpecifications.hasAttribute(entry.getKey(), entry.getValue()));
        }

        return ticketRepository.findAll(spec, pageable).map(this::convertTicketToResponse);
    }

    public TicketResponse convertTicketToResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getStatus(),
                ticket.getRegistrationTime(),
                eventService.convertEventToFullResponse(ticket.getEvent()),
                personService.convertPersonToResponse(ticket.getPerson())
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public Page<VenueAttendanceReport> generateRepost(OffsetDateTime startDate, OffsetDateTime endDate, List<Long> venueIds, int page, int pageSize) {
        Specification<Ticket> spec = Specification.where(TicketSpecifications.hasStatus(Constants.STATUS_CONFIRMED))
                .and(TicketSpecifications.eventDateBetween(startDate, endDate))
                .and(TicketSpecifications.hasVenueIds(venueIds));

        List<Ticket> tickets = ticketRepository.findAll(spec);

        // Группируем по venueID
        Map<Long, List<Ticket>> groupedByVenue = tickets.stream()
                .collect(Collectors.groupingBy(t -> t.getEvent().getVenue().getId()));

        List<VenueAttendanceReport> reports = groupedByVenue.entrySet().stream()
                .map(entry -> {
                    long venueID = entry.getKey();
                    List<Ticket> venueTickets = entry.getValue();
                    SportsVenue venue = venueTickets.getFirst().getEvent().getVenue();

                    return new VenueAttendanceReport(
                            venueID,
                            venue.getName(),
                            venueTickets.size()
                    );
                })
                .sorted(Comparator.comparing(VenueAttendanceReport::venueId)) // можно сортировку любую задать
                .toList();

        int total = reports.size();
        int fromIndex = Math.min(page * pageSize, total);
        int toIndex = Math.min(fromIndex + pageSize, total);

        List<VenueAttendanceReport> pageContent = reports.subList(fromIndex, toIndex);

        return new PageImpl<>(pageContent, PageRequest.of(page, pageSize), total);

    }
}
