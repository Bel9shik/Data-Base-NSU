package nsu.kardash.backendsportevents.services;

import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.exceptions.Person.PersonNotFoundException;
import nsu.kardash.backendsportevents.repositories.PeopleRepository;
import nsu.kardash.backendsportevents.security.PersonDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonDetailsService implements UserDetailsService {

    private final PeopleRepository peopleRepository;

    public PersonDetails loadUserById(long id) {

        return new PersonDetails(peopleRepository.findById(id).orElseThrow(() -> new PersonNotFoundException("User not found")));

    }

    public PersonDetails loadUserByEmail(String email) {

        return new PersonDetails(peopleRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User with email: " + email + " not found")));

    }

    @Override
    public PersonDetails loadUserByUsername(String id) {
        return loadUserById(Long.parseLong(id));
    }
}
