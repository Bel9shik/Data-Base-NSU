package nsu.kardash.backendsportevents.repositories;

import nsu.kardash.backendsportevents.models.Event;
import nsu.kardash.backendsportevents.models.Person;
import nsu.kardash.backendsportevents.models.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {

    List<Ticket> findTicketsByEvent(Event event);

    long countByEventAndStatusNot(Event event, String status);

    List<Ticket> findTicketsByStatusAndEvent_StartedAtBetween(String status, OffsetDateTime start, OffsetDateTime end);

    List<Ticket> findAllByStatusIs(String status);

    Page<Ticket> findTicketsByStatusIs(String status, Pageable pageable);

    Optional<Ticket> findByEventAndPerson(Event event, Person person);

    Page<Ticket> findAll(Specification<Ticket> spec, Pageable pageable);
    List<Ticket> findAll(Specification<Ticket> spec);
}
