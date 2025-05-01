package nsu.kardash.backendsportevents.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateSportVenueDTO {

    @NotBlank
    @Schema(example = "name")
    private String name;

    @NotBlank
    @Pattern(regexp = "^[А-Яа-яЁё]+, ул. [А-Яа-яЁё]+, д. \\d+\\w?$")
    @Schema(example = "Новосибирск, ул. Спортивная, д. 123")
    private String address;

    @NotNull
    @Schema(example = "12")
    private int capacity;
}
