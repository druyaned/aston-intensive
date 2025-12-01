# Домашнее задание 8

1. Создать `docker-compose.yml`, который развернет всю микросервисную систему, включая `Kafka`,
`PostgreSQL`, `API Gateway`, `Service Discovery`, `External Configuration` и 2 микросервиса:
`user-service` и `notification-service`, - созданные ранее.
2. Проверить, что сервисы корректно взаимодействуют друг с другом в контейнерной среде.

## Подготовка

Каждый этап подготовки задокументирован и может быть найден с помощью поиску по проекту
`Prep#[0-9][0-9]` (например `Prep#01`).

1. TODO: continue

## Выполнение

Каждый этап выполнения задокументирован и может быть найден с помощью поиску по проекту
`Step#[0-9][0-9]` (например `Step#01`).

1. Подготовка к выполнению (см. выше)
2. TODO: continue

---

TODO: fix the following

## Запуск

1. База данных PostgreSQL должна быть установлена и Docker запущен
2. Следовать инструкциям из `t08-user-service/src/main/resources/db-create.sql`
3. Создать `t08-notification-service/src/main/resources/mail-connection.properties` и
заполнить его данными email аккаунта для отправки сообщения:
```
addr=<YOUR_EMAIL_ADDRESS>
pass=<YOUR_APPLICATION_PASSWORD>
```
4. Docker image `apache/kafka:4.1.0` должен быть установлен
5. Перейти в директорию `kafka-cluster` и выполнить команду в консоли:
```
$ docker compose up -d
```
6. После запуска двух докер-контейнеров (`kafka-1`, `kafka-2`), можно приступить к компиляции
`t08-patterns-of-microservices`:
```
$ mvn clean install
```
7. Последовательный запуск сервисов (`mvn spring-boot:run` в разных вкладках консоли, например):
    1. `t08-config-service`
    2. `t08-discovery-service`
    3. `t08-user-service`
    4. `t08-notification-service`
    5. `t08-api-gateway`
8. Выполнить запрос без `API Gateway`:
```
# Find all users
curl -X GET http://localhost:8085/user-service/users
```
9. Выполнить запросы через `API Gateway`:
```
# Find all users
curl -X GET http://localhost:8082/user-service/users

# Create Joe
curl -X POST -H "Content-Type: application/json" \
-d '{"name": "Joe", "email": "joe@example.com"}' \
http://localhost:8082/user-service/create

# Update Joe
# IMPORTANT: change ID at the end of the request
curl -X PUT -H "Content-Type: application/json" \
-d '{"name": "Joe", "email": "joe@jmail.org", "birthdate": "2003-06-15"}' \
http://localhost:8082/user-service/update/ID

# Delete Joe
# IMPORTANT: change ID at the end of the request
curl -X DELETE http://localhost:8082/user-service/delete/ID
```
10. Eureka Dashboard: http://localhost:8761/dashboard
11. Остановить приложения можно с помощью `Ctrl+C`
12. Остановить kafka-контейнеры можно, перейдя в `kafka-cluster`, выполнив `docker compose down -v`
