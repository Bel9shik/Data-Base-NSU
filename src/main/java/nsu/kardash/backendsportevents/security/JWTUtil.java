package nsu.kardash.backendsportevents.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.exceptions.Person.PersonNotFoundException;
import nsu.kardash.backendsportevents.models.Person;
import nsu.kardash.backendsportevents.repositories.PeopleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JWTUtil {

    @Value("${jwt_access_secret}")
    private String JWTAccessSecret;

    @Value("${jwt_refresh_secret}")
    private String JWTRefreshSecret;

    private final PeopleRepository peopleRepository;

    private String generateJWT(Person person, Date expirationDate, String JWTSecret) {

        return JWT.create()
                .withSubject("Person details")
                .withClaim("id", person.getId())
                .withClaim("role", person.getRole().getName())
                .withIssuer("spring-app")
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(JWTSecret));
    }

    public String generateAccessToken(Authentication authentication) {
        Date expirationDate = Date.from(OffsetDateTime.now().plusHours(1).toInstant());

        Person person = peopleRepository.findById(Long.parseLong(authentication.getName())).orElseThrow(() -> new PersonNotFoundException("User not found"));

        return generateJWT(person, expirationDate, JWTAccessSecret);
    }

    public String generateRefreshToken(Authentication authentication) {
        Date expirationDate = Date.from(OffsetDateTime.now().plusDays(7).toInstant());

        Person person = peopleRepository.findById(Long.parseLong(authentication.getName())).orElseThrow(() -> new PersonNotFoundException("User not found"));

        return generateJWT(person, expirationDate, JWTRefreshSecret);
    }

    private DecodedJWT verifyJWT(String token, JWTTypes jwtType) {

        String secret = switch (jwtType) {
            case JWTTypes.accessToken -> JWTAccessSecret;
            case JWTTypes.refreshToken -> JWTRefreshSecret;
        };

        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject("Person details")
                .withIssuer("spring-app")
                .build();

        return verifier.verify(token); // тут происходит валидность JWT токена
    }

    public Claim extractClaim(String token, JWTTypes jwtType, String claim) {

        DecodedJWT jwt = verifyJWT(token, jwtType);
        return jwt.getClaim(claim);
    }

}
