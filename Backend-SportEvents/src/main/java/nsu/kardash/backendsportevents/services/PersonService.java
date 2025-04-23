package nsu.kardash.backendsportevents.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.dto.requests.RegistrationDTO;
import nsu.kardash.backendsportevents.dto.responses.positive.CabinetResponse;
import nsu.kardash.backendsportevents.exceptions.Person.CustomAccessDeniedException;
import nsu.kardash.backendsportevents.exceptions.Person.PersonNotFoundException;
import nsu.kardash.backendsportevents.exceptions.Role.RoleNotFoundException;
import nsu.kardash.backendsportevents.models.Person;
import nsu.kardash.backendsportevents.repositories.PeopleRepository;
import nsu.kardash.backendsportevents.repositories.RoleRepository;
import nsu.kardash.backendsportevents.security.PersonDetails;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PeopleRepository peopleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

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

    public void registerPerson(Person person) {

        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setEmailVerified(false);
        enrichPerson(person);
        if (person.getRole() == null) person.setRole(roleRepository.findByName("USER").orElseThrow(() -> new RoleNotFoundException("Role not found")));
        peopleRepository.save(person);
    }

    public Person convertToPerson(@Valid RegistrationDTO registrationDTO) {
        return modelMapper.map(registrationDTO, Person.class);
    }

    private void enrichPerson(Person person) {
        person.setCreatedAt(OffsetDateTime.now());
        person.setUpdatedAt(OffsetDateTime.now());
    }

}
