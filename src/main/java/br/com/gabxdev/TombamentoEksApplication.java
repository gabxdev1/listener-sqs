package br.com.gabxdev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TombamentoEksApplication {

    public static void main(String[] args) {
        SpringApplication.run(TombamentoEksApplication.class, args);
    }

}
