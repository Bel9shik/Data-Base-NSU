package nsu.kardash.backendsportevents.repositories;


import nsu.kardash.backendsportevents.models.VerifyCode;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisRepository {

    void add(VerifyCode verifyCode);

    void delete(String email);

    VerifyCode findVerifyCode(String email);

}
