Мобильное приложение для организации мероприятий.

Архитектура:
• Клиент: Android (Java) – отдельный репозиторий
• Бэкенд: Spring Boot – отдельный репозиторий
• База данных: MySQL

Требования:
• JDK 17
• Android Studio
• MySQL 8.x
• Gradle

Запуск проекта
Бэкенд (Spring Boot)
Клонируйте репозиторий бэкенда

Настройте application.properties:

properties
spring.datasource.url=jdbc:mysql://localhost:3306/common_ground
spring.datasource.username=ваш_логин
spring.datasource.password=ваш_пароль
Запустите: ./gradlew bootRun (сервер на http://localhost:8080)

Клиент (Android)
Клонируйте репозиторий клиента

Откройте в Android Studio

В коде настройте BASE_URL на адрес вашего сервера

Соберите и запустите приложение

Примечание: Проект разделён на два репозитория. Для работы нужны оба.
