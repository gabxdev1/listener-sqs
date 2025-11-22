package br.com.gabxdev.infra.adapter.in.web;

import br.com.gabxdev.domain.ports.out.PollerConfigPort;
import br.com.gabxdev.infra.dto.AppConfigDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/sqs-listener")
public class SqsListenerAdminController implements PollerConfigPort {

    private AppConfigDto appConfig;

    @PostMapping("/pollers")
    public ResponseEntity<Void> updatePollers(@RequestBody AppConfigDto appConfigDto) {
        appConfig = appConfigDto;
        return ResponseEntity.accepted().build();
    }

    @Override
    public AppConfigDto getDesiredNumPollers() {
        return appConfig;
    }
}
