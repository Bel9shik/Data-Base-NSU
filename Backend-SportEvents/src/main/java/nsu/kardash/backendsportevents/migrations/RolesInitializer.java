package nsu.kardash.backendsportevents.migrations;

import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.models.Role;
import nsu.kardash.backendsportevents.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RolesInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            roleRepository.saveAll(List.of(
                            new Role("ROLE_ROOT"),
                            new Role("ROLE_ADMIN"),
                            new Role("ROLE_USER"),
                            new Role("ROLE_ANONYMOUS")
                    )
            );
        }
    }

}
