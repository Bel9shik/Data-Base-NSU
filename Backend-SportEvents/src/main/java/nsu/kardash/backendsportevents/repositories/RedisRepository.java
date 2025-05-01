package nsu.kardash.backendsportevents.repositories;


import nsu.kardash.backendsportevents.models.VerifyCode;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisRepository {

    void addVerifyCode(VerifyCode verifyCode);

    void deleteVerifyCode(String email);

    VerifyCode findVerifyCode(String email);

}
