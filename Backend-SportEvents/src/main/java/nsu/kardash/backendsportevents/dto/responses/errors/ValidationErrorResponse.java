package nsu.kardash.backendsportevents.dto.responses.errors;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ValidationErrorResponse {

    private Map<String, Object> errors;

    private long timestamp;

}
