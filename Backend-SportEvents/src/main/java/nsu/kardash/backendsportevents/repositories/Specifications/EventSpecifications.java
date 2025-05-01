package nsu.kardash.backendsportevents.repositories.Specifications;

import nsu.kardash.backendsportevents.exceptions.Filters.FilterNotFoundException;
import nsu.kardash.backendsportevents.models.Event;
import org.springframework.data.jpa.domain.Specification;

public class EventSpecifications {

    public static Specification<Event> hasAttribute(String key, String value) {
        return (root, query, cb) -> {
            // запрещаем фильтрацию по другим полям
            return switch (key) {
                case "cost" -> cb.equal(root.get("cost"), value);
                case "seatCount" -> cb.equal(root.get("seatCount"), value);
                case "name" -> cb.like(cb.lower(root.get("name")), "%" + value.toLowerCase() + "%");
                case "venueID" -> cb.equal(root.get("venue").get("id"), value);
                case "trainerID" -> cb.equal(root.get("trainer").get("id"), value);
                case "startedAt" -> cb.greaterThan(root.get("updatedAt"), value);
                case "endedAt" -> cb.lessThan(root.get("createdAt"), value);
                default -> throw new FilterNotFoundException("Unknown filter attribute: " + key);
            };
        };
    }
}
