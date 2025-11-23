# Dockerfile
FROM eclipse-temurin:21-jre as base
WORKDIR /app

ADD https://dtdg.co/latest-java-tracer dd-java-agent.jar

COPY target/listener-sqs-0.0.1-SNAPSHOT.jar app.jar

ENV DD_SERVICE=listener-sqs \
    DD_ENV=dev \
    DD_VERSION=1.0.0 \
    DD_AGENT_HOST=dd-agent \
    DD_TRACE_AGENT_PORT=8126 \
    DD_LOGS_INJECTION=true \
    DD_PROFILING_ENABLED=true \
    DD_PROFILING_ALLOCATION_ENABLED=true \
    DD_PROFILING_EXCEPTION_ENABLED=true \
    DD_LOGS_CONFIG_AUTO_MULTI_LINE_DETECTION=true \
    DD_ANALYTICS_ENABLED=true \
    DDD_TRACE_SAMPLING_RULES='[{"sample_rate": 1.0}]' \
    DD_TRACE_SAMPLE_RATE=1.0 \
    DD_TRACE_RATE_LIMIT=100000 \
    DD_TRACE_AGENT_MAX_PAYLOAD_SIZE=10485760

EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_TOOL_OPTIONS -jar app.jar"]