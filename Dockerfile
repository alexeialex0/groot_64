FROM maven:3.8.6-openjdk-11 AS builder
WORKDIR /app
# Копируем только POM сначала для оптимизации кэширования
COPY pom.xml .
RUN mvn dependency:go-offline -B
# Копируем исходный код
COPY src ./src
# Собираем приложение
RUN mvn clean package -DfinalName=groot-bot -DskipTests

# Финальный образ
FROM openjdk:11-jre-slim
WORKDIR /app
# Копируем собранный JAR из стадии builder
COPY --from=builder /app/target/groot-bot.jar .
# Используем точку входа с явным указанием класса
ENTRYPOINT ["java", "-jar", "groot-bot.jar"]