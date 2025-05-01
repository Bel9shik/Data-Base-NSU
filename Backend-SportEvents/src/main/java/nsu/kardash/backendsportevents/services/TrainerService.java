package nsu.kardash.backendsportevents.services;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.dto.responses.positive.TrainerResponse;
import nsu.kardash.backendsportevents.exceptions.Trainer.TrainerNotFound;
import nsu.kardash.backendsportevents.models.Trainer;
import nsu.kardash.backendsportevents.repositories.TrainerRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerService {

    private final TrainerRepository trainerRepository;

    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public List<TrainerResponse> findAll() {
        List<Trainer> trainers =  trainerRepository.findAll();
        List<TrainerResponse> trainerResponses = new ArrayList<>();

        for (Trainer trainer : trainers) {

            trainerResponses.add(new TrainerResponse(
                    trainer.getId(),
                    trainer.getFirstname(),
                    trainer.getSurname(),
                    trainer.getLastname()
            ));
        }

        return trainerResponses;
    }

    public Trainer findById(long id) {
        return trainerRepository.findById(id).orElseThrow(() -> new TrainerNotFound("Trainer not found"));
    }

}
