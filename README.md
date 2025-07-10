### Система управления морским портом - Документация

#### 📌 Обзор системы

Система автоматизации работы морского порта на базе Spring Boot. Управляет:
- Причалами и их емкостью
- Очередью судов
- Грузооборотом контейнеров
- Пользователями с ролевой моделью

Технологии:
- IDE: IntelliJ IDEA Community edition
- **Java 17**, Spring Boot 3.x
- **Spring Security** с JWT-аутентификацией
- **Spring Data JPA** (Hibernate)
- Lombok, MapStruct
- Spring Scheduler


#### 👥 Ролевая модель

| Роль                | Возможности                                                                 |
|---------------------|-----------------------------------------------------------------------------|
| **АДМИНИСТРАТОР**   | Сброс БД, создание причалов/судов/контейнеров, управление пользователями    |
| **ОПЕРАТОР ТЕРМИНАЛА** | Управление расписанием судов, назначение причалов, просмотр статуса       |
| **УЧЁТЧИК ГРУЗОВ**  | Фиксация фактического времени операций, перемещение контейнеров             |

#### 🚀 Запуск системы

Запуск осуществляется при помощи Docker

Для запуска и приложения(сервер и БД) достаточно выполнить команду:

```shell
docker-compose up --build -d
```

Для подробного просмотра логов приложения используется:

```shell
docker-compose logs -f seaport-service
```

Для перехода к консоли БД можно использовать команду;

```shell
docker-compose exec -it postgres bash
```

Для перехода к psql интерфейсу использовать команду

```shell
psql -U postgres -d seaport
```

По умолчанию запускается на `http://localhost:8080` (прописано в docker-compose.yml)


#### 🔑 Инициализация пользователей

Система создает при запуске тестовых пользователей:

```json
// ADMIN
{"email":"admin@example.com","password":"admin123"}

// OPERATOR
{"email":"operator@example.com","password":"operator123"}

// TALLYMAN
{"email":"tallyman@example.com","password":"tallyman123"}
```


#### 🚢 Типовые сценарии работы

**1. Полный цикл обработки контейнера**

```bash
# 1. Сброс БД
POST /api/admin/reset

# 2. Создание причалов
POST /api/admin/create-piers
Body: [100, 200, 300]

# 3. Создание судна
POST /api/admin/create-ship
Body: {
  "shipNumber": "SEA-2025-001",
  "shipLength": 150,
  "containerCount": 120,
  "scheduledArrival": "2025-07-10T08:00:00",
  "scheduledDeparture": "2025-07-10T18:00:00"
}

# 4. Фиксация прибытия судна
POST /api/stevedore/ship/arrival
Body: {
  "shipId": 1,
  "actualShipArrival": "2025-07-10T09:15:00"
}

# 5. Создание контейнера
POST /api/admin/create-container
Body: {
  "shipId": 1,
  "damageStatus": false,
  "storageType": "REGULAR",
  "departureType": "TRUCK",
  "scheduledArrivalDate": "2025-07-10T10:00:00",
  "scheduledDepartureDate": "2025-07-10T14:00:00"
}

# 6. Перемещение контейнера
POST /api/stevedore/container/1/move
Body: {"locationType": "WAREHOUSE_REGULAR"}
```

**2. Управление очередью судов**

```bash
# 1. Добавление судов в очередь
POST /api/operator/arrival
Body: {
  "shipNumber": "MV-A001",
  "shipLength": 200,
  "arrival": "2025-07-10T10:00:00",
  "departure": "2025-07-10T12:00:00"
}

# 2. Просмотр очереди
GET /api/status/queue?at=2025-07-10T10:30:00

# 3. Фиксация отбытия
POST /api/operator/departure
Body: {"shipId": 1, "departure": "2025-07-10T11:45:00"}
```

**3. Мониторинг состояния порта**

```bash
# Статус причалов в указанное время
GET /api/status/piers?at=2025-07-10T12:00:00

# История операций
GET /api/status/history
```


#### 📊 Примеры ответов API

**Статус причалов:**
```json
[
  {
    "pierId": 1,
    "occupied": true,
    "shipId": 15,
    "shipNumber": "SEA-2025-001",
    "maxShipLength": 200,
    "currentShipLength": 150
  },
  {
    "pierId": 2,
    "occupied": false,
    "maxShipLength": 300
  }
]
```

**Очередь судов:**

```json
[
  {
    "shipId": 16,
    "shipNumber": "MV-A001",
    "arrivalTs": "2025-07-10T11:30:00",
    "shipLength": 250
  }
]
```


#### 🧪 Тестирование

В проекте включены тестовые скрипты:
1. `container_test.bat` - Полный цикл работы с контейнером
2. `operator_test.bat` - Сценарии оператора терминала
3. `queue_test.bat` - Тестирование системы очередей

Для запуска:

```bash
.\container_test.bat
.\operator_test.bat
.\queue_test.bat
```
