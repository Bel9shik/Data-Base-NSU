package nsu.kardash.backendsportevents.repositories;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import nsu.kardash.backendsportevents.models.Event;
import nsu.kardash.backendsportevents.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findTicketsByEvent(Event event);

    long countByEventAndStatusNot(Event event, String status);

    List<Ticket> findTicketsByStatusAndEvent_StartedAtBetween(String status, OffsetDateTime start, OffsetDateTime end);
}
