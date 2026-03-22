# Dockerfile-certo
FROM eclipse-temurin:21-jre as base
WORKDIR /app

ADD https://dtdg.co/latest-java-tracer dd-java-agent.jar

COPY ../target/listener-sqs-0.0.1-SNAPSHOT.jar app.jar

#ENV DD_SERVICE=listener-sqs \
#    DD_ENV=dev \
#    DD_VERSION=1.0.0 \
#    DD_AGENT_HOST=dd-agent \
#    DD_TRACE_AGENT_PORT=8126 \
#    DD_LOGS_INJECTION=true \
#    DD_PROFILING_ENABLED=true \
#    DD_PROFILING_ALLOCATION_ENABLED=true \
#    DD_PROFILING_EXCEPTION_ENABLED=true \
#    DD_LOGS_CONFIG_AUTO_MULTI_LINE_DETECTION=true \
#    DD_ANALYTICS_ENABLED=true \
#    DDD_TRACE_SAMPLING_RULES='[{"sample_rate": 1.0}]' \
#    DD_TRACE_SAMPLE_RATE=1.0 \
#    DD_TRACE_RATE_LIMIT=100000 \
#    DD_TRACE_AGENT_MAX_PAYLOAD_SIZE=10485760

EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_TOOL_OPTIONS -jar app.jar"]

#docker run -d --name dd-agent -p 8126:8126 -p 8125:8125/udp -e DD_API_KEY= -e DD_SITE="us5.datadoghq.com" -e DD_DOGSTATSD_NON_LOCAL_TRAFFIC=true -e DD_APM_ENABLED=true -e DD_APM_NON_LOCAL_TRAFFIC=true -e DD_APM_RECEIVER_SOCKET=/var/run/datadog/apm.socket -e DD_DOGSTATSD_SOCKET=/var/run/datadog/dsd.socket -v /var/run/datadog:/var/run/datadog  -e DD_LOGS_ENABLED=true  -e DD_LOGS_CONFIG_AUTO_MULTI_LINE_DETECTION=true -e DD_LOGS_CONFIG_CONTAINER_COLLECT_ALL=true -e DD_CONTAINER_EXCLUDE_LOGS="name:dd-agent"  -v /opt/datadog-agent/run:/opt/datadog-agent/run:rw  -v /var/run/docker.sock:/var/run/docker.sock:ro  -v /proc/:/host/proc/:ro  -v /sys/fs/cgroup/:/host/sys/fs/cgroup:ro  -v /var/lib/docker/containers:/var/lib/docker/containers:ro  gcr.io/datadoghq/agent:7
