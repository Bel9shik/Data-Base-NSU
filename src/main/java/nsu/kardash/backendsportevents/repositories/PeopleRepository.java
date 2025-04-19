package nsu.kardash.backendsportevents.repositories;

import nsu.kardash.backendsportevents.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByEmail(String email);

    Optional<Person> findById(long id);

}
