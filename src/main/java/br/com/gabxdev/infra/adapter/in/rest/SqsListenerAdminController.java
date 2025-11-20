package br.com.gabxdev.infra.adapter.in.rest;

import br.com.gabxdev.infra.adapter.in.sqs.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/sqs-listener")
@Slf4j
public class SqsListenerAdminController {

    private final SqsListener sqsListener;

    public SqsListenerAdminController(SqsListener sqsListener) {
        this.sqsListener = sqsListener;
    }

    @PostMapping("/enable")
    public ResponseEntity<Void> enable() {
        log.info("Enabling Sqs Listener");
        sqsListener.enable();
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/disable")
    public ResponseEntity<Void> disable() {
        sqsListener.disable();
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/pollers")
    public ResponseEntity<Void> updatePollers(@RequestParam int count) {
        sqsListener.updatePollerCount(count);
        return ResponseEntity.accepted().build();
    }
}
