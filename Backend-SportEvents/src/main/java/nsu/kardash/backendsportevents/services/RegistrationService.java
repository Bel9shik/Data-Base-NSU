package nsu.kardash.backendsportevents.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.dto.requests.RegistrationDTO;
import nsu.kardash.backendsportevents.dto.requests.VerifyAccountDTO;
import nsu.kardash.backendsportevents.dto.responses.positive.RegistrationResponse;
import nsu.kardash.backendsportevents.exceptions.Person.ConfirmEmailException;
import nsu.kardash.backendsportevents.exceptions.Person.PersonNotFoundException;
import nsu.kardash.backendsportevents.models.Person;
import nsu.kardash.backendsportevents.models.VerifyCode;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final PersonService personService;
    private final MailSenderService mailSenderService;
    private final RedisService redisService;

    public RegistrationResponse registration(@RequestBody @Valid RegistrationDTO registrationDTO, BindingResult bindingResult) {

        ValidationService.checkValidationErrors(bindingResult);

        Person person = personService.convertToPerson(registrationDTO);

        personService.registerPerson(person);

        return new RegistrationResponse("Registration complete");
    }

    public RegistrationResponse confirmEmail(@Valid @RequestBody String email, BindingResult bindingResult) {

        ValidationService.checkValidationErrors(bindingResult);

        if (!personService.isExistingPersonFromEmail(email)) {
            throw new PersonNotFoundException("Person with " + email + " not exists");
        }

        VerifyCode verifyCode = new VerifyCode(email, generateVerifyCode()); //5 min за счёт TTL Redis

        redisService.add(verifyCode);

        mailSenderService.sendNotifyEmail(verifyCode.getEmail(), verifyCode.getCode());

        return new RegistrationResponse("Email sent");
    }

    public RegistrationResponse confirmCode(@RequestBody @Valid VerifyAccountDTO verifyAccountDTO, BindingResult bindingResult) {

        ValidationService.checkValidationErrors(bindingResult);

        VerifyCode verifyCode = redisService.findVerifyCode(verifyAccountDTO.getEmail());

        if (verifyCode == null) {
             throw new ConfirmEmailException(Map.of("error", "Verification code expired"));
        }

        if (verifyCode.getCode() == verifyAccountDTO.getVerifyCode()) {
            personService.setEmailVerified(verifyAccountDTO.getEmail());
            return new RegistrationResponse("Email verified");
        } else {
            throw new ConfirmEmailException(Map.of("error", "Verification code invalid"));
        }

    }

    private int generateVerifyCode() {
        return (int)(Math.random() * 900000) + 100000; // in range [100000;999999]
    }

}
