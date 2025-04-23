package nsu.kardash.backendsportevents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackendSportEventsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendSportEventsApplication.class, args);
    }

}
