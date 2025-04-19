package nsu.kardash.backendsportevents.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "persons")
@Getter
@Setter
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "roleid")
    private Role role;

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

    @Column(name = "isemailverified")
    private boolean isEmailVerified = false;

    @NotNull
    private String password;

    @Column(name = "createdat")
    private OffsetDateTime createdAt;

    @Column(name = "updatedat")
    private OffsetDateTime updatedAt;

}

