package nsu.kardash.backendsportevents.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyAccountDTO {

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email should not be empty")
    @Schema(description = "Электронная почта пользователя", example = "a.kardash@g.nsu.ru")
    private String email;

    @Min(value = 100000, message = "Verify code can not be less than 100000")
    @Max(value = 999999, message = "Verify code can not be greater 999999")
    @Schema(description = "Проверочный код, присылаемый пользователю на почту", example = "123456")
    private int verifyCode;

}
