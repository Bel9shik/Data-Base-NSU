package nsu.kardash.backendsportevents.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateSportVenueDTO {

    @NotNull
    @Min(1)
    private long id;

    @Schema(example = "name")
    private String name;

    @Pattern(regexp = "^[А-Яа-яЁё]+, ул. [А-Яа-яЁё]+, д. \\d+\\w?$")
    @Schema(example = "Новосибирск, ул. Спортивная, д. 123")
    private String address;

    @Schema(example = "12")
    private Integer capacity;

}
