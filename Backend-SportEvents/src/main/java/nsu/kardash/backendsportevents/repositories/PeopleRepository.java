package nsu.kardash.backendsportevents.repositories;

import nsu.kardash.backendsportevents.models.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {

    Optional<Person> findByEmail(String email);

    Optional<Person> findById(long id);

    Page<Person> findAll(Pageable pageable);

//    Page<Person> findAll(Specification<Person> spec,Pageable pageable);
}
