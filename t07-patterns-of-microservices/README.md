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
2. Дабавил `Spring Cloud BOM` (управлениe версиями подпроектов Spring Cloud)
3. Написал `t07-api-gateway/pom.xml` и `ApiGatewayApp.java`
4. Настроил `API Gateway`
5. Добавил `Circuit Breaker` в `API Gateway`
6. Настроил `Circuit Breaker` для `t07-user-service`
7. Написал и настроил `FallbackController`
8. Написал `t07-discovery-service/pom.xml` и `DiscoveryServiceApp.java`
9. Настроил `Discovery Service` (a.k.a `Eureka Server`)
10. Добавил клиентам зависимость `Eureka Client`
11. Настроил у каждого клиента `Eureka Server Path`
12. Написал `t07-config-service/pom.xml` и `ConfigServiceApp.java`
13. Настроил `Config Service`
14. Добавил клиентам зависимость `Config Client` и перенес их настройки
15. Изменил тесты и их настройки в `t07-user-service` и `t07-notification-service`

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
6. После запуска двух докер-контейнеров (`kafka-1`, `kafka-2`), можно приступить к компиляции
`t07-patterns-of-microservices`:
```
$ mvn clean install
```
7. Последовательный запуск сервисов (`mvn spring-boot:run` в разных вкладках консоли, например):
    1. `t07-config-service`
    2. `t07-discovery-service`
    3. `t07-user-service`
    4. `t07-notification-service`
    5. `t07-api-gateway`
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
