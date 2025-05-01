package nsu.kardash.backendsportevents.repositories.Specifications;

import nsu.kardash.backendsportevents.exceptions.Filters.FilterNotFoundException;
import nsu.kardash.backendsportevents.models.Person;
import org.springframework.data.jpa.domain.Specification;

public class PersonSpecifications {

    public static Specification<Person> hasAttribute(String key, String value) {
        return (root, query, cb) -> {
            // запрещаем фильтрацию по другим полям
            return switch (key) {
                case "firstname" -> cb.like(root.get("firstname"), "%" + value.toLowerCase() + "%");
                case "surname" -> cb.like(root.get("surname"), "%" + value.toLowerCase() + "%");
                case "lastname" -> cb.like(root.get("lastname"), "%" + value.toLowerCase() + "%");
                case "email" -> cb.equal(root.get("email"), value);
                case "role" ->
                        cb.equal(cb.upper(root.get("role").get("name")), value.toUpperCase());
                case "isEmailVerified" -> {
                    boolean b = Boolean.parseBoolean(value);
                    yield cb.equal(root.get("isEmailVerified"), b);
                }
                case "updatedAt" -> cb.greaterThan(root.get("updatedAt"), value);
                case "createdAt" -> cb.greaterThan(root.get("createdAt"), value);
                default -> throw new FilterNotFoundException("Unknown filter attribute: " + key);
            };
        };
    }
}
