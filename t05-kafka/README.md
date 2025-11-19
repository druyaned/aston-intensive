# Домашнее задание 5

Реализовать микросервис (notification-service) для отправки сообщения на почту при удалении или
добавлении пользователя.

## Требования
1. Использовать необходимые модули Spring и Kafka.
1. При удалении или создании юзера приложение, реализованное до этого (user-service), должно
    отправлять сообщение в Kafka, в котором содержится информация об операции (удаление или
    создание) и email юзера.
1. Новый микросервис (notification-service) должен получить сообщение из Kafka и отправить сообщение
    на почту юзера в зависимости от операции: удаление - "Здравствуйте! Ваш аккаунт был удалён",
    создание - "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан".
1. Также отдельно добавить API, которая будет отправлять сообщение на почту (почти тот же
    функционал, что и через кафку).
1. Написать интеграционные тесты для проверки отправки сообщения на почту.

Было разрешено использовать использовать отправку сообщений не на почти, а в консоль при работе с
    Kafka.

Кроме того, внимательно прислушиваюсь к комментариям из предыдущих pull-requests и исправляю
    недочеты. О них в выполнении не упоминается, так как выполняю текущую задачу.

## Выполнение
1. Изучил [RabbitMQ](https://www.rabbitmq.com/tutorials/tutorial-one-java), сделал конспект и
    закрепил на [практике](https://github.com/druyaned/rabbitmq-practice)
1. Приступил к основному заданию. Для начала изучал Kafka по многочисленным источникам, включая
    [официальную документацию](https://kafka.apache.org/documentation), различные туториалы, гайды,
    статьи, видео.
1. Далее изучил модуль [Spring Kafka](https://docs.spring.io/spring-kafka/reference/index.html)
1. Пришло время выполнения требований задания. Для начала установил Kafka и загрузил Docker image
    "apache/kafka:4.1.0"
1. Создал проект t05-kafka и его потомков: t05-user-service, t05-notification-service
1. Настроил KafkaProducerConfig. Для начала организовал работу с одним брокером внутри кластера.
    Изучил тонкости взаимодействия Spring Boot и Kafka. Решил реализовать более тонкую настройку в
    KafkaProducerConfig
1. Изменил работу UserController в методах create и delete. Добавил отправку UserEvent-ов с
    использованием KafkaTemplate
1. Изменил UserControllerTest. Добавив \@MockitoBean KafkaTemplate и внес изменения в тесты согласно
    новой функциональности отправки событий через Kafka
1. Приступил к t05-notification-service. Начал с настройки KafkaConsumerConfiguration
1. Создал новый компонент UserEventListener с логикой обработки событий из Kafka
1. В свою очередь UserEventListener зависит от MessageHandler, который является имеет 2 реализации:
    ConsoleMessageHandler и MailMessageHandler. ConsoleMessageHandler используюется в production
    среде и перенаправляет сообщения о создании и удалении юзеров в консоль
1. MailMessageHandler использует Jakarta Mail для отправки сообщения, используется в
    SendMailController
1. Создал SendMailController, который является отдельным API, которая отправляет сообщение на почту,
    согласно требованию задания
1. Все запустил и проверил работу
1. Добавил docker-compose.yml, обеспечивающий работу с двумя брокерами в кластере Kafka
1. Подправил KafkaConsumerConfiguration, KafkaProducerConfig
1. Написал интеграционные тесты UserEventListenerTest, SendMailControllerTest, согласно заданию
1. Приступил к данной документации и рефакторингу

## Запуск

1. База данных PostgreSQL должна быть установлена
1. Следовать инструкциям из "src/main/resources/db-create.sql" проекта user-service
1. Создать t05-kafka/t05-notification-service/src/main/resources/mail-connection.properties и
    заполнить его данными email аккаунта для отправки сообщения:
```
addr=<YOUR_EMAIL_ADDRESS>
pass=<YOUR_APPLICATION_PASSWORD>

```
1. Docker image apache/kafka:4.1.0 должен быть установлен
1. Перейти в директорию kafka-cluster и выполнить команду в консоли:
```
$ docker compose up -d
```
1. После запуска двух докер-контейнеров (kafka-1, kafka-2), можно приступить к запуску проекта из
    директории t05-kafka:
```
$ mvn clean install
```
1. Запустить t05-user-service
1. Запустить t05-notification-service
1. Выполнить несколько запросов:
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

# Send mail
# IMPORTANT: change YOUR_RECIPIENT under the email key
curl -X POST -H "Content-Type: application/json" \
-d '{"email": "YOUR_RECIPIENT", "message": "Some important message"}' \
http://localhost:8086/notification-service/send
```
1. При выходе удалить контейнеры:
```
$ docker compose down -v
```
