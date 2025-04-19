package nsu.kardash.backendsportevents.dto.responses.errors;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventErrorResponse {

    private String message;

    private long timestamp;

}
