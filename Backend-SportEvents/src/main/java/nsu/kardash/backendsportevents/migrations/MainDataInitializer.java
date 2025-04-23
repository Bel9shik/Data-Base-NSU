package nsu.kardash.backendsportevents.migrations;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MainDataInitializer implements CommandLineRunner {

    private final RolesInitializer rolesInitializer;
    private final TrainerInitializer trainerInitializer;
    private final SportsVenueInitializer sportsVenueInitializer;
    private final EventInitializer eventInitializer;
    private final TicketInitializer ticketInitializer;
    private final PersonInitializer personInitializer;

    @Override
    public void run(String... args) {
        rolesInitializer.run();
        trainerInitializer.run();
        sportsVenueInitializer.run();
        eventInitializer.run();
        personInitializer.run();
        ticketInitializer.run();
    }
}
