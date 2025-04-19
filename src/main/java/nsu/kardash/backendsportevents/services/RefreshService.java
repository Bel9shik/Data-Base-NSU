package nsu.kardash.backendsportevents.services;

import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.dto.requests.RefreshDTO;
import nsu.kardash.backendsportevents.dto.responses.positive.AuthenticationResponse;
import nsu.kardash.backendsportevents.exceptions.Person.RefreshTokenNotFound;
import nsu.kardash.backendsportevents.security.JWTTypes;
import nsu.kardash.backendsportevents.security.JWTUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
@RequiredArgsConstructor
public class RefreshService {

    private final JWTUtil jwtUtil;

    public AuthenticationResponse refresh(RefreshDTO refreshDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new RefreshTokenNotFound("Refresh token is incorrect");
        }

        String email = jwtUtil.extractClaim(refreshDTO.getRefreshToken(), JWTTypes.refreshToken, "email").asString();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, null, null);

        String accessToken = jwtUtil.generateAccessToken(authenticationToken);
        String refreshToken = jwtUtil.generateRefreshToken(authenticationToken);

        return new AuthenticationResponse(accessToken, refreshToken);
    }

}
