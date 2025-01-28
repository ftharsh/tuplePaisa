package org.harsh.tuple.paisa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class TuplePaisaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TuplePaisaApplication.class, args);
        System.out.println("Wallet Started and Running");
    }

}
