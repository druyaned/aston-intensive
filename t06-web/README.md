# Домашнее задание 6

Добавление Swagger-документации и HATEOAS в API.

## Требования

1. Задокументировать существующее API (из t04-spring) с помощью Swagger (Springdoc OpenAPI), чтобы
можно было легко изучить и тестировать API через веб-интерфейс
1. Добавить поддержку HATEOAS, чтобы API предоставляло ссылки для навигации по ресурсам

## Выполнение

Каждый этап выполнения задокументирован и может быть найден с помощью поиску по проекту
`Step#[0-9][0-9]`, например `Step#01`.

1. Добавил стартер `openapi`. `Swagger UI` и `api-docs` стали доступны по дефолтным путям:
`localhost:8085/swagger-ui`, `localhost:8085/v3/api-docs`.
2. Поменял пути, описанные выше, в `application.properties`
3. Написал конфигурацию `OpenAPI`: объект info в самом начале `api-docs`
4. Применил swagger-аннотации к каждому методу контроллера
5. Добавил стартер `hateoas`
6. Написал `UserModelAssembler`, предоставляющий сбор ссылок для моего User DTO
7. Добавил бин `UserModelAssembler` в контроллер и использовал его в read-методах
8. Поправил тесты измененных методов в `UserControllerTest`

---

## Запуск

1. База данных PostgreSQL должна быть установлена и Docker запущен
1. Следовать инструкциям из `src/main/resources/db-create.sql`
1. Компиляция и запуск:
```
$ mvn clean install; mvn spring-boot:run
```
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
```
1. Остановить приложение можно с помощью `Ctrl+C`
