package nsu.kardash.backendsportevents.dto.responses.positive;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OkResponse {

    @NotBlank
    private String message;

}
