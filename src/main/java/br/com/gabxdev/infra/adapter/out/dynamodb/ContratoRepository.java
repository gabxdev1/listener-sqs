package br.com.gabxdev.infra.adapter.out.dynamodb;

import br.com.gabxdev.domain.model.Contrato;
import br.com.gabxdev.domain.ports.out.ContratoRepositoryUseCase;
import br.com.gabxdev.infra.entity.ContratoEntity;
import br.com.gabxdev.infra.mapper.ContratoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContratoRepository implements ContratoRepositoryUseCase {

    private final DynamoDbTable<ContratoEntity> table;

    private final ContratoMapper mapper;

    @Override
    public void save(Contrato contrato) {
        var contratoEntity = mapper.toContratoEntity(contrato);

        table.putItem(contratoEntity);
    }

    @Override
    public Optional<Contrato> findByCpf(String cpf) {
        var key = Key.builder()
                .partitionValue(cpf)
                .build();

        var foundContrato = table.getItem(r -> r.key(key));

        return Optional.ofNullable(foundContrato)
                .map(c -> mapper.toContrato(foundContrato));
    }

    @Override
    public List<Contrato> findByStatus(String status) {
        var index = table.index("status-index");

        var pages = index.query(r -> r.queryConditional(
                QueryConditional.keyEqualTo(k -> k.partitionValue(status))
        ));

        var entities = pages.stream()
                .flatMap(page -> page.items().stream())
                .toList();

        return mapper.toContratoList(entities);
    }

    @Override
    public boolean existsByCpf(String cpf) {
        Key key = Key.builder()
                .partitionValue(cpf)
                .build();

        return table.getItem(r -> r.key(key)) != null;
    }
}
