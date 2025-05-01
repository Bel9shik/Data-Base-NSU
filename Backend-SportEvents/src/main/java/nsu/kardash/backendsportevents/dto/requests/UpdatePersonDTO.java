package nsu.kardash.backendsportevents.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdatePersonDTO {

    @NotNull
    @Size(max = 100)
    private String firstname;

    @NotNull
    @Size(max = 100)
    private String surname;

    @Size(max = 100)
    private String lastname = null;

    @NotNull
    @Email
    @Size(max = 100)
    private String email;

    @NotNull
    private String password;

}
