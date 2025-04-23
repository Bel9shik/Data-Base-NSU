package nsu.kardash.backendsportevents.migrations;

import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.models.Role;
import nsu.kardash.backendsportevents.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RolesInitializer {

    private final RoleRepository roleRepository;

    public void run(String... args) {
        if (roleRepository.count() == 0) {
            roleRepository.saveAll(List.of(
                            new Role("ROOT"),
                            new Role("ADMIN"),
                            new Role("USER"),
                            new Role("ANONYMOUS")
                    )
            );
        }
    }

}
