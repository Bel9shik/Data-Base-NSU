package nsu.kardash.backendsportevents.dto.responses.positive;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
public class EventFullResponse implements Serializable {

    private long id;
    private String name;
    private int cost;
    private int seatCount;
    private OffsetDateTime startedAt;
    private OffsetDateTime endedAt;
    private Map<String, String> sportVenue;
    private Map<String, String> trainer;

}
