package de.smartsquare.cuzoo;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
@EnableScheduling
public class Application {

    public static void main(final String[] args) {
        run(Application.class, args);
    }
}
