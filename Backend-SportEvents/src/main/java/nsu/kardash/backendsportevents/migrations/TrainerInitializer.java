package nsu.kardash.backendsportevents.migrations;

import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.models.Trainer;
import nsu.kardash.backendsportevents.repositories.TrainerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TrainerInitializer {

    private final TrainerRepository trainers;

    public void run(String... args) {
        if (trainers.count() > 0) return;

        List<Trainer> trainerList = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            Trainer t = new Trainer();
            t.setFirstname("Имя" + i);
            t.setSurname("Фамилия" + i);
            t.setLastname(i % 3 == 0 ? "Отчество" + i : null);
            t.setEmail("trainer" + i + "@example.com");
            trainerList.add(t);
        }
        trainers.saveAll(trainerList);
    }
}
