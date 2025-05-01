package nsu.kardash.backendsportevents.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class CreateEventDTO {

    @NotNull
    @Min(1)
    private int cost;

    @NotNull
    private OffsetDateTime startedAt;

    @NotNull
    private OffsetDateTime endedAt;

    @NotNull
    @Min(1)
    private int seatCount;

    @NotBlank
    private String name;

    @NotNull
    @Min(1)
    private long venueID;

    @NotNull
    @Min(1)
    private long trainerID;

}
