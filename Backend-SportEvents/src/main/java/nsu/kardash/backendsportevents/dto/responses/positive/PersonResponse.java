package nsu.kardash.backendsportevents.dto.responses.positive;

import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class PersonResponse implements Serializable {

    @Id
    private long id;

    @NotNull
    private String roleName;

    @NotNull
    @Size(max = 100)
    private String firstname;

    @NotNull
    @Size(max = 100)
    private String surname;

    @Size(max = 100)
    private String lastname;

    @NotNull
    @Email
    @Size(max = 100)
    private String email;

    @NotNull
    private boolean isEmailVerified;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private OffsetDateTime updatedAt;

}
