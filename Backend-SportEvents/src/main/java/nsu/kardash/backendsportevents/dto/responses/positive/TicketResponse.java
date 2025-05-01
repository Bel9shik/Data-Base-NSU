package nsu.kardash.backendsportevents.dto.responses.positive;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class TicketResponse {

    private String status;
    private OffsetDateTime timestamp;
    private EventFullResponse event;
    private PersonResponse person;


}
