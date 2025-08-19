## [REST API](http://localhost:8080/doc)

## Концепция:

- Spring Modulith
    - [Spring Modulith: достигли ли мы зрелости модульности](https://habr.com/ru/post/701984/)
    - [Introducing Spring Modulith](https://spring.io/blog/2022/10/21/introducing-spring-modulith)
    - [Spring Modulith - Reference documentation](https://docs.spring.io/spring-modulith/docs/current-SNAPSHOT/reference/html/)

```
  url: jdbc:postgresql://localhost:5432/jira
  username: jira
  password: JiraRush
```

- Есть 2 общие таблицы, на которых не fk
    - _Reference_ - справочник. Связь делаем по _code_ (по id нельзя, тк id привязано к окружению-конкретной базе)
    - _UserBelong_ - привязка юзеров с типом (owner, lead, ...) к объекту (таска, проект, спринт, ...). FK вручную будем
      проверять

## Аналоги

- https://java-source.net/open-source/issue-trackers

## Тестирование

- https://habr.com/ru/articles/259055/

Список выполненных задач:

•Видалено соціальні мережі (VK, Yandex).

•Перенесено логіни, паролі, OAuth ID та налаштування пошти у конфіг файл, зчитування через змінні оточення.

•Написано тести для всіх публічних методів ProfileRestController (success та failure paths).

•Проведено рефакторинг FileUtil#upload з використанням сучасного API для файлової системи.

•Додано функціонал тегів до завдань (REST API + сервіс).

•Реалізовано підрахунок часу перебування завдань у роботі та тестуванні (методи сервісу, зміни у changelog.sql).

•Створено Dockerfile для сервера.

•Додано локалізацію на 3 мови для листів і стартової сторінки.