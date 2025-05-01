package nsu.kardash.backendsportevents.repositories.Specifications;

import nsu.kardash.backendsportevents.exceptions.Filters.FilterNotFoundException;
import nsu.kardash.backendsportevents.models.Ticket;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.List;

public class TicketSpecifications {

    public static Specification<Ticket> hasAttribute(String key, String value) {
        return (root, query, cb) -> {
            // запрещаем фильтрацию по другим полям
            return switch (key) {
                case "status" -> cb.like(cb.lower(root.get("status")), "%" + value.toLowerCase() + "%");
                case "eventID" -> cb.equal(root.get("event").get("id"), value);
                case "personID" -> cb.equal(root.get("person").get("id"), value);
                case "registrationTime" -> cb.greaterThan(root.get("registrationTime"), value);
                default -> throw new FilterNotFoundException("Unknown filter attribute: " + key);
            };
        };
    }

    public static Specification<Ticket> hasStatus(String status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Ticket> eventDateBetween(OffsetDateTime start, OffsetDateTime end) {
        if (start == null && end == null) {
            return (root, query, cb) -> cb.conjunction(); // пустой фильтр
        } else if (start == null) {
            end.plusMinutes(30);
            return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("event").get("endedAt"), end);
        } else if (end == null) {
            start.minusMinutes(30);
            return (root, query, cb) -> cb.greaterThan(root.get("event").get("startedAt"), start);
        } else {
            start.minusMinutes(30);
            end.plusMinutes(30);
            return (root, query, cb) ->
                    cb.between(root.get("event").get("startedAt"), start, end);
        }
    }

    public static Specification<Ticket> hasVenueIds(List<Long> venueIds) {
        if (venueIds == null || venueIds.isEmpty()) {
            return (root, query, cb) -> cb.conjunction(); // пустой фильтр
        }
        return (root, query, cb) -> root.get("event").get("venue").get("id").in(venueIds);
    }

}

