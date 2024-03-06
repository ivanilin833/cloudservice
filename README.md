# Облачное хранилище
## Описание задания: [ссылка](https://github.com/netology-code/jd-homeworks/blob/master/diploma/cloudservice.md)
## Описание решения
Проект REST-сервиса на Spring для загрузки файлов и вывода списка уже загруженных файлов пользователя. Backend сервис для хранения файлов, взаимодействует с [frontend](https://github.com/netology-code/jd-homeworks/tree/master/diploma/netology-diplom-frontend). Особенностью проекта является
применение JWT аутентификации, реализация которой выполнена внутри самого сервиса.
## Используемые технологии
- Spring Boot 3.x,
- Java 17
- Docker
- MySql
- JUnit
- Mockito
- Testcontainers.
## Пользователь для тестирования
| Login | Password |
|-------|----------|
| user1 | user_one |

## Запуск
В корневой папке выполнить команду:
```
docker-compose up
```