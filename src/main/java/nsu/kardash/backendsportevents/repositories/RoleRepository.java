package nsu.kardash.backendsportevents.repositories;

import nsu.kardash.backendsportevents.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String roleName);

    Optional<Role> findById(long id);

}
