package nsu.kardash.backendsportevents.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class UpdateEventDTO {

    @NotNull
    private long id;

    @Min(1)
    private Integer cost;

    private OffsetDateTime startedAt;

    private OffsetDateTime endedAt;

    @Min(1)
    private Integer seatCount;

    private String name;

    @Min(1)
    private Long venueID;

    @Min(1)
    private Long trainerID;

}
