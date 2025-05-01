package nsu.kardash.backendsportevents.repositories;

import nsu.kardash.backendsportevents.models.SportsVenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SportVenueRepository extends JpaRepository<SportsVenue, Long> {

    Optional<SportsVenue> findByNameAndAddress(String name, String address);

}
