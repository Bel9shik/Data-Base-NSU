package nsu.kardash.backendsportevents.services;

import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.dto.responses.positive.CabinetResponse;
import nsu.kardash.backendsportevents.exceptions.Person.CustomAccessDeniedException;
import nsu.kardash.backendsportevents.exceptions.Person.PersonNotFoundException;
import nsu.kardash.backendsportevents.models.Person;
import nsu.kardash.backendsportevents.repositories.PeopleRepository;
import nsu.kardash.backendsportevents.security.PersonDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PeopleRepository peopleRepository;

    public CabinetResponse getCabinet(long personId) {

        long idFromAuth = getCurrentId();

        if (personId != idFromAuth) throw new CustomAccessDeniedException("Access denied");

        Person person = peopleRepository.findById(personId).orElseThrow(() -> new PersonNotFoundException("Person not found"));

        return new CabinetResponse(
                person.getFirstname(),
                person.getSurname(),
                person.getLastname(),
                person.getEmail()
        );
    }

    public boolean isExistingPersonFromEmail(String email) {
        return peopleRepository.findByEmail(email).isPresent();
    }

    public void setEmailVerified(String email) {
        Person person = peopleRepository.findByEmail(email).orElseThrow(() -> new PersonNotFoundException( "Person not found"));
        person.setEmailVerified(true);
        person.setUpdatedAt(OffsetDateTime.now());
        peopleRepository.save(person);
    }

    private long getCurrentId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        return personDetails.getId();
    }

}
