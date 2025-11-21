package br.com.gabxdev.infra.mapper;

import br.com.gabxdev.domain.model.Contrato;
import br.com.gabxdev.infra.dto.ContratoEventConsumer;
import br.com.gabxdev.infra.dto.EnderecoGetResponse;
import br.com.gabxdev.infra.entity.ContratoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ContratoMapper {
    ContratoEntity toContratoEntity(Contrato contrato);

    Contrato toContrato(ContratoEntity contratoEntity);

    @Mappings({
            @Mapping(target = "cidade", source = "endereco.city"),
            @Mapping(target = "rua", source = "endereco.street")
    })
    Contrato toContrato(ContratoEventConsumer request, EnderecoGetResponse endereco, String id, String status);

    List<Contrato> toContratoList(List<ContratoEntity> contratoEntities);
}
