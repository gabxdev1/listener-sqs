package br.com.gabxdev.infra.config;

import br.com.gabxdev.application.ContratoService;
import br.com.gabxdev.domain.ports.in.ContratoUseCase;
import br.com.gabxdev.domain.ports.out.BrasilApiUseCase;
import br.com.gabxdev.domain.ports.out.ContratoRepositoryUseCase;
import br.com.gabxdev.domain.service.ContratoValidator;
import br.com.gabxdev.infra.mapper.ContratoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeansConfig {

    @Bean
    public ContratoValidator contratoValidator() {
        return new ContratoValidator();
    }

    @Bean
    public ContratoUseCase contratoUseCase(ContratoRepositoryUseCase contratoRepositoryUseCase,
                                           BrasilApiUseCase brasilApiUseCase,
                                           ContratoValidator contratoValidator,
                                           ContratoMapper contratoMapper

    ) {
        return new ContratoService(contratoRepositoryUseCase, brasilApiUseCase, contratoValidator, contratoMapper);
    }
}
