package nsu.kardash.backendsportevents.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.dto.requests.RegistrationDTO;
import nsu.kardash.backendsportevents.dto.requests.UpdatePersonDTO;
import nsu.kardash.backendsportevents.dto.responses.positive.CabinetResponse;
import nsu.kardash.backendsportevents.dto.responses.positive.OkResponse;
import nsu.kardash.backendsportevents.dto.responses.positive.PersonResponse;
import nsu.kardash.backendsportevents.exceptions.Person.CustomAccessDeniedException;
import nsu.kardash.backendsportevents.exceptions.Person.PersonNotFoundException;
import nsu.kardash.backendsportevents.exceptions.Person.VerifyCodeException;
import nsu.kardash.backendsportevents.exceptions.Role.RoleNotFoundException;
import nsu.kardash.backendsportevents.models.Person;
import nsu.kardash.backendsportevents.models.VerifyCode;
import nsu.kardash.backendsportevents.repositories.PeopleRepository;
import nsu.kardash.backendsportevents.repositories.RoleRepository;
import nsu.kardash.backendsportevents.repositories.Specifications.PersonSpecifications;
import nsu.kardash.backendsportevents.security.PersonDetails;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PeopleRepository peopleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final RedisService redisService;
    private final MailSenderService mailSenderService;

    public Person getPersonById(long id) {
        return peopleRepository.findById(id).orElseThrow(() -> new PersonNotFoundException("Person not found"));
    }

    public Person getPersonByEmail(String email) {
        return peopleRepository.findByEmail(email).orElseThrow(() -> new PersonNotFoundException("Person not found"));
    }

    public OkResponse resetPassword(String email) {

        Person person = getPersonByEmail(email);

        VerifyCode verifyCode = new VerifyCode(email, RegistrationService.generateVerifyCode(), false); //5 min за счёт TTL Redis

        redisService.addVerifyCode(verifyCode);

        mailSenderService.sendNotifyEmailResetPassword(verifyCode.getEmail(), verifyCode.getCode());

        return new OkResponse("Email sent");

    }

    public OkResponse changePassword(String email, String newPassword) {

        VerifyCode verifyCode = redisService.findVerifyCode(email);

        if (verifyCode == null) {
            throw new VerifyCodeException("Verification code expired");
        }

        Person person = getPersonByEmail(email);

        if (!verifyCode.isVerified()) throw new VerifyCodeException("Verification code not verified");

        person.setPassword(passwordEncoder.encode(newPassword));

        peopleRepository.save(person);

        redisService.deleteVerifyCode(email);

        return new OkResponse("Password changed successfully");

    }

    public CabinetResponse getCabinet(long personId) {

        long idFromAuth = getCurrentId();

        if (personId != idFromAuth) throw new CustomAccessDeniedException("Access denied");

        Person person = peopleRepository.findById(personId).orElseThrow(() -> new PersonNotFoundException("Person not found"));

        return new CabinetResponse(
                person.getFirstname(),
                person.getSurname(),
                person.getLastname(),
                person.getEmail()
        );
    }

    public boolean isExistingPersonFromEmail(String email) {
        return peopleRepository.findByEmail(email).isPresent();
    }

    public void setEmailVerified(String email) {
        Person person = peopleRepository.findByEmail(email).orElseThrow(() -> new PersonNotFoundException("Person not found"));
        person.setEmailVerified(true);
        person.setUpdatedAt(OffsetDateTime.now());
        peopleRepository.save(person);
    }

    private long getCurrentId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        return personDetails.getId();
    }

    public void registerPerson(Person person) {

        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setEmailVerified(false);
        enrichPerson(person);
        if (person.getRole() == null)
            person.setRole(roleRepository.findByName("USER").orElseThrow(() -> new RoleNotFoundException("Role not found")));
        peopleRepository.save(person);
    }

    public Person convertToPerson(@Valid RegistrationDTO registrationDTO) {
        return modelMapper.map(registrationDTO, Person.class);
    }

    private void enrichPerson(Person person) {
        person.setCreatedAt(OffsetDateTime.now());
        person.setUpdatedAt(OffsetDateTime.now());
    }

    public OkResponse updatePerson(@Valid UpdatePersonDTO updatePersonDTO, BindingResult bindingResult) {

        ValidationService.checkValidationErrors(bindingResult);

        Person person = peopleRepository.getReferenceById(getCurrentId());

        if (updatePersonDTO.getEmail() != null && !updatePersonDTO.getEmail().isBlank())
            person.setEmail(updatePersonDTO.getEmail());
        if (updatePersonDTO.getFirstname() != null && !updatePersonDTO.getFirstname().isBlank())
            person.setFirstname(updatePersonDTO.getFirstname());
        if (updatePersonDTO.getLastname() != null && !updatePersonDTO.getLastname().isBlank())
            person.setLastname(updatePersonDTO.getLastname());
        if (updatePersonDTO.getSurname() != null && !updatePersonDTO.getSurname().isBlank())
            person.setSurname(updatePersonDTO.getSurname());
        if (updatePersonDTO.getPassword() != null && !updatePersonDTO.getPassword().isBlank())
            person.setPassword(passwordEncoder.encode(updatePersonDTO.getPassword()));

        person.setUpdatedAt(OffsetDateTime.now());

        peopleRepository.save(person);

        return new OkResponse("Person updated successfully");
    }

    public OkResponse deletePerson() {

        peopleRepository.deleteById(getCurrentId());

        return new OkResponse("Person deleted successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    @Cacheable(
            value = "personFilteredInfo",
            key = "#page + '-' + #size + '-' + T(org.springframework.util.StringUtils).collectionToDelimitedString(#allParams.entrySet(), ',')"
    )
    public Page<PersonResponse> findByFilters(Map<String, String> allParams, int page, int size, String[] sort) {
        // выделяем из allParams только filter-поля, убираем page,size,sort
        var filters = new HashMap<>(allParams);
        filters.remove("page");
        filters.remove("size");
        filters.remove("sort");

        // создаём объект Sort
        Sort sortObj = Sort.by(
                Sort.Order.by(sort[0]).with(sort.length > 1 && sort[1].equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC)
        );
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Specification<Person> spec = Specification.where(null);

        for (var entry : filters.entrySet()) {
            spec = spec.and(PersonSpecifications.hasAttribute(entry.getKey(), entry.getValue()));
        }

        return peopleRepository.findAll(spec, pageable).map(this::convertPersonToResponse);
    }

    public PersonResponse convertPersonToResponse(Person person) {
        return new PersonResponse(
                person.getId(),
                person.getRole().getName(),
                person.getFirstname(),
                person.getSurname(),
                person.getLastname(),
                person.getEmail(),
                person.isEmailVerified(),
                person.getCreatedAt(),
                person.getUpdatedAt()
        );
    }
}
