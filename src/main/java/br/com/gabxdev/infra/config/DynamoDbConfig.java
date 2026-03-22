package br.com.gabxdev.infra.config;

import br.com.gabxdev.infra.entity.ContratoEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;
import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.secondaryPartitionKey;
import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.secondarySortKey;

@Configuration
public class DynamoDbConfig {

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.SA_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .httpClientBuilder(
                        ApacheHttpClient.builder()
                                .maxConnections(256)
                )
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Bean
    public DynamoDbTable<ContratoEntity> contratoDynamoTable(DynamoDbEnhancedClient enhancedClient) {
        return enhancedClient.table(
                "tb_contrato",
                contratoTableSchema()
        );
    }

    private static TableSchema<ContratoEntity> contratoTableSchema() {
        return StaticTableSchema.builder(ContratoEntity.class)
                .newItemSupplier(ContratoEntity::new)
                .addAttribute(String.class, a -> a.name("cpf")
                        .getter(ContratoEntity::getCpf)
                        .setter(ContratoEntity::setCpf)
                        .tags(primaryPartitionKey(), secondarySortKey("status-index")))
                .addAttribute(String.class, a -> a.name("status")
                        .getter(ContratoEntity::getStatus)
                        .setter(ContratoEntity::setStatus)
                        .tags(secondaryPartitionKey("status-index")))
                .addAttribute(String.class, a -> a.name("id")
                        .getter(ContratoEntity::getId)
                        .setter(ContratoEntity::setId))
                .addAttribute(String.class, a -> a.name("nome")
                        .getter(ContratoEntity::getNome)
                        .setter(ContratoEntity::setNome))
                .addAttribute(String.class, a -> a.name("cidade")
                        .getter(ContratoEntity::getCidade)
                        .setter(ContratoEntity::setCidade))
                .addAttribute(String.class, a -> a.name("rua")
                        .getter(ContratoEntity::getRua)
                        .setter(ContratoEntity::setRua))
                .addAttribute(String.class, a -> a.name("cep")
                        .getter(ContratoEntity::getCep)
                        .setter(ContratoEntity::setCep))
                .build();
    }
}
