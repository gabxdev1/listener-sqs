package br.com.gabxdev.infra.adapter.in.web;

import br.com.gabxdev.domain.ports.out.PollerConfigPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/sqs-listener")
public class SqsListenerAdminController implements PollerConfigPort {

    private int desiredNumPollers = 0;

    @PostMapping("/pollers")
    public ResponseEntity<Void> updatePollers(@RequestParam int count) {
        desiredNumPollers = count;
        return ResponseEntity.accepted().build();
    }

    @Override
    public int getDesiredNumPollers() {
        return desiredNumPollers;
    }
}
