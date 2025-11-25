# Домашнее задание 7

Добавить к существующей системе паттерны:
1. [API Gateway](https://microservices.io/patterns/apigateway)
2. [Server-side service discovery](https://microservices.io/patterns/server-side-discovery)
3. [Circuit breaker](https://microservices.io/patterns/reliability/circuit-breaker)
4. [Externalized configuration](https://microservices.io/patterns/externalized-configuration)

Реализации данных паттернов можно найти в модулях Spring Cloud.

## Выполнение

Каждый этап выполнения и подготовки задокументирован и может быть найден с помощью поиску по проекту
`Step#[0-9][0-9]` (например `Step#01`) и `Prep#[0-9][0-9]` (например `Prep#01`) соответственно.

1. Подготовка к выполнению (см. ниже)
2. TODO: continue

## Подготовка к выполнению

1. Внедрил AOP для Kafka: написал `KafkaAspect`
2. Настроил и написал тесты `KafkaAspectTest`
3. Вынес `UserEvent` в отдельный модуль `t07-user-events`, чтоб и `t07-user-service`, и
`t07-notification-service` могли от него зависеть для отправки и получения событий через Kafka
4. Внес изменения и дописал документацию во всех `t07-*` проектах

---

## Запуск

1. База данных PostgreSQL должна быть установлена и Docker запущен
2. Следовать инструкциям из `t07-user-service/src/main/resources/db-create.sql`
3. Создать `t07-notification-service/src/main/resources/mail-connection.properties` и
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
6. После запуска двух докер-контейнеров (`kafka-1`, `kafka-2`), можно приступить к компиляции:
```
$ mvn clean install
```
7. Открыть новую вкладку консоли и перейти в `t07-user-service` и запустить приложение командой
`mvn spring-boot:run`
8. Открыть новую вкладку консоли и перейти в `t07-notification-service` и запустить приложение
командой `mvn spring-boot:run`. Таким образом, запущены 2 kafka-контейнера и 2 приложения
9. Выполнить несколько запросов:
```
# Find all users
curl -X GET http://localhost:8085/user-service/users

# Create Joe
curl -X POST -H "Content-Type: application/json" \
-d '{"name": "Joe", "email": "joe@example.com"}' \
http://localhost:8085/user-service/create

# Update Joe
# IMPORTANT: change ID at the end of the request
curl -X PUT -H "Content-Type: application/json" \
-d '{"name": "Joe", "email": "joe@jmail.org", "birthdate": "2003-06-15"}' \
http://localhost:8085/user-service/update/ID

# Delete Joe
# IMPORTANT: change ID at the end of the request
curl -X DELETE http://localhost:8085/user-service/delete/ID
```
10. Остановить приложения можно с помощью `Ctrl+C`
11. Остановить kafka-контейнеры можно, перейдя в `kafka-cluster`, выполнив `docker compose down -v`
