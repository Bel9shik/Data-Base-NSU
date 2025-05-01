package nsu.kardash.backendsportevents.services;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.dto.requests.CreateSportVenueDTO;
import nsu.kardash.backendsportevents.dto.requests.UpdateSportVenueDTO;
import nsu.kardash.backendsportevents.dto.responses.positive.OkResponse;
import nsu.kardash.backendsportevents.dto.responses.positive.SportVenueResponse;
import nsu.kardash.backendsportevents.dto.responses.positive.VenueAttendanceReport;
import nsu.kardash.backendsportevents.exceptions.SportVenue.SportVenueException;
import nsu.kardash.backendsportevents.models.SportsVenue;
import nsu.kardash.backendsportevents.repositories.SportVenueRepository;
import nsu.kardash.backendsportevents.repositories.TicketRepository;
import org.apache.commons.lang3.concurrent.BackgroundInitializer;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SportVenueService {

    private final SportVenueRepository sportVenueRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public OkResponse createSportVenue (CreateSportVenueDTO sportVenueDTO, BindingResult bindingResult) {

        ValidationService.checkValidationErrors(bindingResult);

        if (sportVenueRepository.findByNameAndAddress(sportVenueDTO.getName(), sportVenueDTO.getAddress()).isPresent()) {
            throw new SportVenueException("SportVenue already exists");
        }

        SportsVenue sportsVenue = new SportsVenue();

        sportsVenue.setName(sportVenueDTO.getName());
        sportsVenue.setAddress(sportVenueDTO.getAddress());
        sportsVenue.setMaxCapacity(sportVenueDTO.getCapacity());

        sportVenueRepository.save(sportsVenue);

        return new OkResponse("SportVenue created");
    }

    @PreAuthorize("hasRole('ADMIN')")
    public SportVenueResponse showSportVenues () {

        return new SportVenueResponse(sportVenueRepository.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public SportVenueResponse updateSportVenue (UpdateSportVenueDTO updateSportVenueDTO, BindingResult bindingResult) {

        ValidationService.checkValidationErrors(bindingResult);

        SportsVenue sportsVenue = sportVenueRepository.findById(updateSportVenueDTO.getId()).orElseThrow(() -> new SportVenueException("SportVenue not found"));

        if (updateSportVenueDTO.getAddress() != null && !updateSportVenueDTO.getAddress().isBlank()) {
            sportsVenue.setAddress(updateSportVenueDTO.getAddress());
        }

        if (updateSportVenueDTO.getName() != null && !updateSportVenueDTO.getName().isBlank()) {
            sportsVenue.setName(updateSportVenueDTO.getName());
        }

        if (updateSportVenueDTO.getCapacity() != null) {
            sportsVenue.setMaxCapacity(updateSportVenueDTO.getCapacity());
        }

        sportVenueRepository.save(sportsVenue);

        return new SportVenueResponse(List.of(sportsVenue));

    }

    @PreAuthorize("hasRole('ADMIN')")
    public OkResponse deleteSportVenue(@NotNull long sportVenueId) {

        SportsVenue sportsVenue = sportVenueRepository.findById(sportVenueId).orElseThrow(() -> new SportVenueException("SportVenue not found"));

        sportVenueRepository.delete(sportsVenue);

        return new OkResponse("SportVenue deleted");
    }

    public SportsVenue findSportVenueById(long sportVenueId) {
        return sportVenueRepository.findById(sportVenueId).orElseThrow(() -> new SportVenueException("SportVenue not found"));
    }
}
