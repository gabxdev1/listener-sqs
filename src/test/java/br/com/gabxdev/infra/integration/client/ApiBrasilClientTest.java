package br.com.gabxdev.infra.integration.client;


import br.com.gabxdev.infra.dto.EnderecoGetResponse;
import br.com.gabxdev.infra.integration.interceptors.BrasilApiInterceptor;
import br.com.gabxdev.infra.properties.BrasilApiProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestClient;

@RestClientTest(value = ApiBrasilClient.class)
@Import({
        BrasilApiProperties.class,
        ApiBrasilClientTest.TestConfig.class,
        BrasilApiInterceptor.class,
})
class ApiBrasilClientTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RestClient brasilApiRestClient(
                BrasilApiInterceptor brasilApiInterceptor,
                BrasilApiProperties props,
                RestClient.Builder restClientBuilder
        ) {
            return restClientBuilder
                    .baseUrl(props.getBaseUrl())
                    .requestInterceptor(brasilApiInterceptor)
                    .build();
        }
    }

    @Autowired
    private ApiBrasilClient apiBrasilClient;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private BrasilApiProperties props;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void reset() {
        server.reset();
    }

    @Test
    void testSuccess() throws JsonProcessingException {

        var response = new EnderecoGetResponse(
                "Test Street",
                "Test Neighborhood"
        );

        var expectedJson = objectMapper.writeValueAsString(response);

        var requestTo = MockRestRequestMatchers.requestTo(props.getBaseUrl() + "/37048330");
        var expect = MockRestRequestMatchers.method(HttpMethod.GET);
        var expectCount = ExpectedCount.once();
        var withSuccess = MockRestResponseCreators.withSuccess(expectedJson, MediaType.APPLICATION_JSON);

        server.expect(expectCount, requestTo)
                .andExpect(expect)
                .andRespond(withSuccess);

        var endereco = apiBrasilClient.buscarEndereco("37048330");

        Assertions.assertThat(endereco.city())
                .isEqualTo(response.city());

        Assertions.assertThat(endereco.street())
                .isEqualTo(response.street());

        server.verify();
    }
}