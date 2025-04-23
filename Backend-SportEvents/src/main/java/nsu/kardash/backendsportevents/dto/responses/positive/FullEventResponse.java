package nsu.kardash.backendsportevents.dto.responses.positive;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class FullEventResponse implements Serializable {
    private final long id;
    private final String name;
    private final int cost;
    private final OffsetDateTime startedAt, endedAt;
    private final int seatCount, freeSeatCount;
}
