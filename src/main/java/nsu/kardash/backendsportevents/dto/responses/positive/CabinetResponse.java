package nsu.kardash.backendsportevents.dto.responses.positive;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CabinetResponse {

    @Schema(description = "Имя пользователя", example = "Aleksandr")
    private String username;

    @Schema(description = "Фамилия пользователя", example = "Kardash")
    private String surname;

    @Schema(description = "Отчество пользователя", nullable = true, example = "Vitalievich")
    private String lastname;

    @Schema(description = "Адрес электронной почты пользователя", example = "a.kardash@g.nsu.ru")
    private String email;

}
