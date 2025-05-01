package nsu.kardash.backendsportevents.repositories;

import nsu.kardash.backendsportevents.models.Event;
import nsu.kardash.backendsportevents.models.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Optional<Event> findById(long id);

    Page<Event> findAll(Pageable pageable);

    Page<Event> findAll(Specification<Event> spec, Pageable pageable);

}
