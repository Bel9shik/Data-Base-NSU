package nsu.kardash.backendsportevents.dto.responses.errors;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventErrorResponse {

    private String message;

    private long timestamp;

}
