FROM maven:3.8-openjdk-17-slim AS build
WORKDIR /app
COPY . .
RUN mvn clean package

FROM openjdk:17
# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем исполняемый jar файл в рабочую директорию контейнера
COPY ./target/diplom-backend-0.0.1-SNAPSHOT.jar /app/



# Открываем порт 8080 для внешних подключений к приложению
EXPOSE 8080

# Указываем команду для запуска приложения
# Обратите внимание, что для использования переменных окружения в команде, их необходимо будет передать через docker-compose или Kubernetes
CMD ["java", "-jar", "diplom-backend-0.0.1-SNAPSHOT.jar"]
