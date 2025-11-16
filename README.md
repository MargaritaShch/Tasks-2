# Product Mock Application

Приложение-заглушка для выполнения CRUD-операций над сущностью Product, разработанное на Spring Boot.
Сервис предназначен для использования в нагрузочном тестировании, демонстрирует работу с двумя вариантами доступа к данным — JPA и JDBC, а также экспортирует метрики в Prometheus для мониторинга производительности.

---

## Функционал
- **REST-интерфейс** для CRUD-операций над сущностью Product
- **POST /api/products** — создание нового продукта
- **GET /api/products/{id}** — получение продукта по ID
- **PUT /api/products/{id}** — обновление продукта
- **DELETE /api/products/{id}** — удаление продукта
- Поддержка двух библиотек доступа к данным:
	- **JPA (Hibernate)** — объектно-реляционное отображение
	- **JDBC (JdbcTemplate)** — прямое взаимодействие с SQL
- Автоматическая генерация тестовых данных (seed) при запуске
- Переключаемая конфигурация через application.yml
- Экспорт метрик через /actuator/prometheus
- Поддержка Docker и интеграция с Prometheus + Grafana

---

## Архитектура

Приложение реализует чистую архитектуру с разделением на слои:

	adapter/         — адаптеры JPA и JDBC
	config/          — конфигурация seed-инициализации и свойств
	controller/      — REST-контроллеры
	model/           — доменные сущности
	port/            — интерфейсы портов (ProductPort)
	repo/            — JPA-репозиторий
	service/         — сервисный слой

Архитектурный паттерн: Hexagonal Architecture (Ports and Adapters)
БД: PostgreSQL или MySQL (в зависимости от конфигурации)


**Тип БД:** PostgreSQL или MySQL (в зависимости от конфигурации)

---

## Технологии

- Java 17  
- Spring Boot 3.2  
- Spring Data JPA  
- Spring JDBC  
- PostgreSQL / MySQL  
- Prometheus + Grafana  
- Docker / Docker Compose  
- Maven  
- Gatling (Load Testing)

---

## Запуск приложения

### Требования

- Java 17+
- Maven 3.6+
- PostgreSQL или MySQL
- Docker и Docker Compose (для мониторинга)

---

### Локальный запуск
1. Запуск PostgreSQL:

   ```bash
   docker run --name productdb \
     -e POSTGRES_USER=product \
     -e POSTGRES_PASSWORD=productpass \
     -e POSTGRES_DB=productdb \
     -p 5433:5432 -d postgres:14
   ```
docker run --name productdb -e POSTGRES_USER=product -e POSTGRES_PASSWORD=productpass -e POSTGRES_DB=productdb -p 5433:5432 -d postgres:14


2.	Настройка src/main/resources/application.yml:

		spring:
		datasource:
		url: jdbc:postgresql://localhost:5433/productdb
		username: product
		password: productpass
		jpa:
		hibernate:
		ddl-auto: update
		open-in-view: false
		
		app:
		db:
		lib: jpa         # jpa или jdbc
		seed:
		enabled: true
		reset: true
		target: 200
		batch-size: 200


3.	Запуск приложение:

		mvn spring-boot:run

или через IntelliJ IDEA → Run → ProductMockApplication

4.	Приложение будет доступно по адресу:

		http://localhost:8080


5.  Запуск через Docker Compose

Для удобства мониторинга можно использовать встроенный стек Prometheus + Grafana:

	cd monitoring
	docker-compose up -d

Будут запущены контейнеры:
•	product-mock — приложение
•	postgres — база данных
•	prometheus — сборщик метрик
•	grafana — визуализация


6. Переключение между JPA и JDBC

В application.yml изменить одно свойство:

	app:
	db:
	lib: jpa     # или jdbc

Для JPA → spring.jpa.hibernate.ddl-auto: update
Для JDBC → spring.jpa.hibernate.ddl-auto: none (используется SQL-скрипт)

---

## Инициализация данных (Seed)

При запуске приложение автоматически создаёт тестовые данные.

	Параметр	Описание	Пример
	app.seed.enabled	Включить сид	true
	app.seed.reset	Очистить таблицу перед вставкой	true
	app.seed.target	Общее количество записей	200
	app.seed.impl	Реализация сидера (jpa или jdbc)	jdbc

Пример SQL-схемы (resources/schema-postgresql.sql):

	CREATE TABLE IF NOT EXISTS products (
	id SERIAL PRIMARY KEY,
	name VARCHAR(255) NOT NULL,
	category VARCHAR(100) NOT NULL,
	price NUMERIC(10,2) NOT NULL CHECK (price >= 0),
	stock INTEGER NOT NULL CHECK (stock >= 0)
	);


---

## Мониторинг

Экспорт метрик включён по адресу:

	http://localhost:8080/actuator/prometheus

• Prometheus собирает данные и передаёт в Grafana.
• В Grafana доступны дашборды:
• JVM (CPU, Heap, GC)
• PostgreSQL
• Node Exporter (системные ресурсы)

---

## Нагрузочное тестирование (Gatling)

Каталог: gatling/src/test/java/pref

Структура:
•	Actions.java — HTTP-запросы
•	Chains.java — последовательности действий
•	Scenarios.java — сценарии (GET, POST, PUT, DELETE)
•	ProductSimulation.java — полная симуляция теста

Сценарии:

		Название	Endpoint	Описание
		TC01_GET_product_by_id	GET /api/products/{id}	Чтение
		TC02_POST_create_product	POST /api/products	Создание
		TC03_PUT_update_product	PUT /api/products/{id}	Обновление
		TC04_DELETE_product	DELETE /api/products/{id}	Удаление


---

## Профиль нагрузки

	Тест	RPS	Длительность шага
	Read (GET)	50	1 минута
	Update (PUT)	10	1 минута
	Create (POST)	5	1 минута
	Delete (DELETE)	2.5	1 минута

Нагрузка увеличивается по ступеням, фиксируется 95-й и 99-й перцентили времени отклика.

---

## Пример API-запросов

Создание продукта

	curl -X POST http://localhost:8080/api/products \
	-H "Content-Type: application/json" \
	-d '{"name": "Item-001", "category": "LoadTest", "price": 12.50, "stock": 100}'

Получение продукта

	curl http://localhost:8080/api/products/1

Обновление продукта

	curl -X PUT http://localhost:8080/api/products/1 \
	-H "Content-Type: application/json" \
	-d '{"name": "Item-001-Updated", "category": "Test", "price": 15.00, "stock": 80}'

Удаление продукта

	curl -X DELETE http://localhost:8080/api/products/1


⸻

## Структура проекта

	product-mock/
	├── src/
	│   ├── main/java/com/example/product_mock/
	│   │   ├── adapter/           # JPA и JDBC адаптеры
	│   │   ├── config/            # SeedConfig и настройки
	│   │   ├── controller/        # REST контроллер
	│   │   ├── model/             # Сущность Product
	│   │   ├── port/              # Интерфейс ProductPort
	│   │   ├── repo/              # JPA репозиторий
	│   │   └── service/           # Логика сервиса
	│   └── resources/
	│       ├── schema-postgresql.sql
	│       └── application.yml
	│
	├── gatling/
	│   └── src/test/java/pref/    # Gatling-сценарии
	│
	├── monitoring/
	│   ├── prometheus.yml
	│   ├── docker-compose.yml
	│   └── pgdata/
	│
	├── pom.xml
	└── README.md


---

## Сборка и тестирование

mvn clean package
mvn test


---

## Цель проекта

Демонстрация влияния выбора библиотеки доступа к данным (JPA vs JDBC) и типа СУБД на производительность при нагрузке.
Приложение используется как стенд для нагрузочного тестирования и анализа поведения систем под высокой нагрузкой.

---
