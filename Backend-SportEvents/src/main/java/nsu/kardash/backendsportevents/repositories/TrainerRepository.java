package nsu.kardash.backendsportevents.repositories;

import nsu.kardash.backendsportevents.models.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
}
