package nsu.kardash.backendsportevents.dto.responses.positive;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class EventShortResponse implements Serializable {
    private long id;
    private String name;
    private int cost;
    private OffsetDateTime startedAt;
    private OffsetDateTime endedAt;
}
