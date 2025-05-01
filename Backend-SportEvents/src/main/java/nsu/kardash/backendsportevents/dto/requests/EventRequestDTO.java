package nsu.kardash.backendsportevents.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Запрос добавления пользователя на мероприятие")
public class EventRequestDTO {

    @Min(1)
    @NotNull
    @Schema(example = "10")
    long eventId;

}
