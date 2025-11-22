package br.com.gabxdev.domain.ports.out;

import br.com.gabxdev.infra.dto.AppConfigDto;

public interface PollerConfigPort {
    AppConfigDto getDesiredNumPollers();
}
