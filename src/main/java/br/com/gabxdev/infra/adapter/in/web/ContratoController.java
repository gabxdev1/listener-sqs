package br.com.gabxdev.infra.adapter.in.web;

import br.com.gabxdev.domain.model.Contrato;
import br.com.gabxdev.domain.ports.in.ContratoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contrato")
@Slf4j
@RequiredArgsConstructor
public class ContratoController {

    private final ContratoUseCase service;

    @GetMapping("/{cpf}")
    public ResponseEntity<Contrato> findByCpf(@PathVariable String cpf) {
        return ResponseEntity.ok().body(service.findByCpf(cpf));
    }

    @GetMapping
    public ResponseEntity<List<Contrato>> findByStatus(@RequestParam String status) {
        return ResponseEntity.ok().body(service.findByStatus(status));
    }
}
