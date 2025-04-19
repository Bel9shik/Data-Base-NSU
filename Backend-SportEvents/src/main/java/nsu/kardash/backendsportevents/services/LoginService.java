package nsu.kardash.backendsportevents.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.dto.requests.AuthenticationDTO;
import nsu.kardash.backendsportevents.dto.responses.positive.AuthenticationResponse;
import nsu.kardash.backendsportevents.exceptions.Person.PersonNotFoundException;
import nsu.kardash.backendsportevents.security.JWTUtil;
import nsu.kardash.backendsportevents.security.PersonDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final PersonDetailsService personDetailsService;
    private final JWTUtil jwtUtil;

    public AuthenticationResponse login(@Valid @RequestBody AuthenticationDTO authenticationDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new PersonNotFoundException( "Email or password is incorrect");
        }

        PersonDetails personDetails =  personDetailsService.loadUserByEmail(authenticationDTO.getEmail());

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(personDetails.getUsername(), authenticationDTO.getPassword());

        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            throw new PersonNotFoundException( "Email or password is incorrect");
        }

        String accessToken = jwtUtil.generateAccessToken(authenticationToken);
        String refreshToken = jwtUtil.generateRefreshToken(authenticationToken);

        return new AuthenticationResponse(accessToken, refreshToken);
    }

}
