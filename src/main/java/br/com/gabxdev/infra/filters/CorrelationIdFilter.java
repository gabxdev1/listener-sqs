package br.com.gabxdev.infra.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String CORRELATION_ID_MDC_KEY = "correlation_id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String correlationId = extractOrGenerateCorrelationId(request);

        try {
            // 1) Coloca no MDC (logs)
            MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
            org.jboss.logging.MDC.put(CORRELATION_ID_MDC_KEY, correlationId);

            // 2) Opcional: coloca em um atributo da request (caso queira acessar em Controllers)
            request.setAttribute(CORRELATION_ID_MDC_KEY, correlationId);

            // 3) Opcional: já devolve no header da response também
            response.setHeader(CORRELATION_ID_HEADER, correlationId);

            // segue o fluxo normal
            filterChain.doFilter(request, response);
        } finally {
            // importantíssimo pra não vazar entre requisições
            MDC.remove(CORRELATION_ID_MDC_KEY);
            org.jboss.logging.MDC.remove(CORRELATION_ID_HEADER);
        }
    }

    private String extractOrGenerateCorrelationId(HttpServletRequest request) {
        String headerValue = request.getHeader(CORRELATION_ID_HEADER);
        if (headerValue != null && !headerValue.isBlank()) {
            return headerValue;
        }
        return UUID.randomUUID().toString();
    }
}