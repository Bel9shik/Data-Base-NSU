package nsu.kardash.backendsportevents.dto.responses.errors;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FilterErrorResponse {

    private String message;

    private long timestamp;
}
