# =========================
# Stage 1 - build nativo
# =========================
FROM ghcr.io/graalvm/native-image-community:21 AS build
WORKDIR /workspace

RUN microdnf install -y gzip tar findutils

ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw

# cache de dependências
RUN ./mvnw -q -DskipTests dependency:go-offline

COPY src src

# baixa o tracer do Datadog para ser usado no build nativo
ADD https://dtdg.co/latest-java-tracer /workspace/dd-java-agent.jar

# gera o binário nativo
RUN ./mvnw -e -X clean -Pnative native:compile -Dmaven.test.skip=true

# debug opcional
RUN ls -lah /workspace/target

# =========================
# Stage 2 - runtime mínimo
# =========================
FROM debian:bookworm-slim
WORKDIR /app

RUN apt-get update && apt-get install -y ca-certificates && rm -rf /var/lib/apt/lists/*

COPY --from=build /workspace/target/app /app/app
RUN ls -lah /app
RUN chmod +x /app/app

EXPOSE 8080

ENTRYPOINT ["/app/app"]
