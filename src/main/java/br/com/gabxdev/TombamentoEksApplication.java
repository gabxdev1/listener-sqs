package br.com.gabxdev;

import br.com.gabxdev.infra.dto.AppConfigDto;
import br.com.gabxdev.infra.dto.ContratoEventConsumer;
import br.com.gabxdev.infra.dto.EnderecoGetResponse;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan
@RegisterReflectionForBinding({
        ContratoEventConsumer.class,
        EnderecoGetResponse.class,
        AppConfigDto.class
})
public class TombamentoEksApplication {

    public static void main(String[] args) {
        SpringApplication.run(TombamentoEksApplication.class, args);
    }

}
