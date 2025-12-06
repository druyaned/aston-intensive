# Домашнее задание 8

1. Создать `docker-compose.yml`, который развернет всю микросервисную систему, включая `Kafka`,
`PostgreSQL`, `API Gateway`, `Service Discovery`, `External Configuration` и 2 микросервиса:
`user-service` и `notification-service`, - созданные ранее.
2. Проверить, что сервисы корректно взаимодействуют друг с другом в контейнерной среде.

## Подготовка

Каждый этап подготовки задокументирован и может быть найден с помощью поиску по проекту
`Prep#[0-9][0-9]` (например `Prep#01`).

1. Добавил интерфейс `UserServiceApi`, его реализует `UserController`. Теперь контроллер не
перегружен `Swagger` аннотациями, усилено соответствие принципам `SOLID`.
2. Оставил только `MailMessageHandler` (реализация `MessageHandler` для отправки сообщения на
почту). Работая с `Kafka` в `UserEventListener`), раньше сообщение выводилось в консоль по умолчанию
через `ConsoleMessageHandler`. Требования задания противоречат этому упрощению (но info-log об
отправке добавил).
3. Добавил `@ConfigurationProperties`: `MailCredentials`, `SmtpProperties`, `UserEventsProperties` -
и внедрил их (`MailConfig`, `KafkaConsumerConfig`, `KafkaProducerConfig`).
4. При автоматическом запуске сервисов через `docker-compose` нельзя сказать наверняка, какой из
сервисов (не контейнеров) запустится первым. Можно написать ключ `healthcheck` и использовать,
например, `curl` для проверочных запросов. Но я решил добавить создание `kafka.topics.user-events` в
каждом Kafka-клиенте. В случае наличия топика, Kafka не будет создавать его повторно. Проблема
отсутствия топика на момент запуска сервиса, какого-то Kafka-клиента, решена.

## Выполнение

Каждый этап выполнения задокументирован и может быть найден с помощью поиску по проекту
`Step#[0-9][0-9]` (например `Step#01`).

1. Подготовка к выполнению (см. выше).
2. Создал `docker-compose.yml` и переместил `kafka-1` и `kafka-2` из уже существующего
`kafka-cluster/docker-compose.yml`.
3. Решил разделить настройки по двум профилям: `local`, `docker`. В локальных `properties` файлах
будут настройки подключения к другим сервисам, например`t08-config-service`, `Kafka`,
`t08-discovery-service`. Для начала, изменил все настройки для каждого сервиса внутри
`t08-config-service`.
4. У всех сервисов, кроме `t08-config-service`, добавил `application-local.properties`,
`application-docker.properties` и изменил остальные настройки, включая и те, которые для тестов.
5. Настроил контейнер `postgres` (с `volumes: postgres_data`).
6. Настроил контейнеры `t08-config-service` и `t08-discovery-service`.
7. Настроил контейнеры `t08-user-service` и `t08-notification-service`.
8. Настроил контейнер `t08-api-gateway`.
9. Добавил `maven-shade-plugin` для генерации `fat-jar` всех сервисов, что удобно для написания
`Dockerfile`.
10. Написал `Dockerfile` для каждого сервиса.
11. Написал скрипт `build.sh`: компиляция и билд docker-образов. Можно было обойтись без
`Dockerfile` для каждого сервиса, использовав maven task `spring-boot:build-image`, но так дольше
работает запуск, лучше с `Dockerfile`.

---

## Запуск

1. Открыть `t08-user-service/src/main/resources/db-create.properties` и следовать инструкции по
созданию базы данных для `t08-user-service`.
2. Создать `t08-notification-service/src/main/resources/mail-connection.properties` и заполнить
следующим:
```
send.mail.addr=<SENDER_ADDRESS>
send.mail.pass=<SENDER_PASSWORD>
```
Необходимо заменить `<SENDER_ADDRESS>` и `<SENDER_PASSWORD>` данными почты. С адреса этой почты
будут отправляться сообщения по созданию или удалению пользователя в `t08-user-service`.

3. Запустить `./build.sh`: компиляция и билд docker-образов.
4. Запустить всю систему с помощью команды `docker compose up -d`.
5. Остановить cистему можно с помощью `docker compose down`. Для удаления данных в `postgres`
выполнить `docker compose down -v`.

## Примеры запросов

```
# Find all users
curl -X GET http://localhost:58085/users

# Create Joe
curl -X POST -H "Content-Type: application/json" \
-d '{"name": "Joe", "email": "joe@invalid.mail.ogo"}' \
http://localhost:58082/users

# Update Joe
# IMPORTANT: change ID at the end of the request
curl -X PUT -H "Content-Type: application/json" \
-d '{"name": "Joe", "email": "joe@invalid.ogo"}' \
http://localhost:58082/users/ID

# Delete Joe
# IMPORTANT: change ID at the end of the request
curl -X DELETE http://localhost:58082/users/ID
```
