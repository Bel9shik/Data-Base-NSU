package nsu.kardash.backendsportevents.services;

import lombok.AllArgsConstructor;
import nsu.kardash.backendsportevents.models.VerifyCode;
import nsu.kardash.backendsportevents.repositories.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class RedisService implements RedisRepository {
    private static final String KEY_PREFIX = "verifyCode:"; // Каждый VerifyCode хранится в своем ключе
    private final RedisTemplate<String, VerifyCode> redisTemplate;

    @Override
    public void add(final VerifyCode verifyCode) {
        String key = KEY_PREFIX + verifyCode.getEmail();
        System.out.println("key = " + key + ", value = " + verifyCode.getCode());
        // Сохраняем объект с TTL в 5 минут
        redisTemplate.opsForValue().set(key, verifyCode, 5, TimeUnit.MINUTES);
    }

    @Override
    public void delete(final String email) {
        String key = KEY_PREFIX + email;
        redisTemplate.delete(key);
    }

    @Override
    public VerifyCode findVerifyCode(String email) {
        String key = KEY_PREFIX + email;
        return redisTemplate.opsForValue().get(key);
    }
}
