# ============================================================
# Dockerfile do backend — usado pelo Render (e por qualquer outro serviço
# que rode containers Docker) pra compilar e rodar a API Spring Boot.
# ============================================================

# ---------- Etapa 1: compilar com Maven ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# baixa as dependências antes de copiar o código, pra aproveitar cache do Docker
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# ---------- Etapa 2: imagem final, só com o .jar e o Java ----------
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/controle-dae.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
