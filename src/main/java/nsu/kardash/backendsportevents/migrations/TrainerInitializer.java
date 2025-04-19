package nsu.kardash.backendsportevents.migrations;

import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.models.Trainer;
import nsu.kardash.backendsportevents.repositories.TrainerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainerInitializer implements CommandLineRunner {

    private final TrainerRepository trainers;

    @Override
    public void run(String... args) {
        if (trainers.count() > 0) return;
        for (int i = 1; i <= 10; i++) {
            Trainer t = new Trainer();
            t.setFirstname("Имя" + i);
            t.setSurname("Фамилия" + i);
            t.setLastname(i % 3 == 0 ? "Отчество" + i : null);
            t.setEmail("trainer" + i + "@example.com");
            trainers.save(t);
        }
    }
}
