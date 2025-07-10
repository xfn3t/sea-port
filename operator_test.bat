@echo off
setlocal enabledelayedexpansion

echo Operator test script
echo.

set BASE_URL=http://localhost:8080
set DATE=2025-07-10

:: 1. Сброс базы данных
echo [1] Resetting database...
curl -sS -X POST "%BASE_URL%/api/admin/reset"
echo.
timeout /t 1 > nul

:: 2. Создание причалов
echo [2] Creating piers (150, 200, 250)...
curl -sS -X POST "%BASE_URL%/api/admin/create-piers" -H "Content-Type: application/json" -d "[150,200,250]"
echo.
timeout /t 1 > nul

:: 3. Создание судов и сохранение их ID
echo [3] Creating ships and storing their IDs...

:: Создаем корабли и сохраняем их ID в переменные
curl -sS -X POST "%BASE_URL%/api/admin/create-ship" -H "Content-Type: application/json" -d "{\"shipNumber\":\"MV-A001\",\"shipLength\":200,\"containerCount\":100,\"scheduledArrival\":\"%DATE%T08:00:00\",\"scheduledDeparture\":\"%DATE%T10:00:00\"}" > ship1.txt
set /p SHIP1_ID=<ship1.txt

curl -sS -X POST "%BASE_URL%/api/admin/create-ship" -H "Content-Type: application/json" -d "{\"shipNumber\":\"SS-Blue\",\"shipLength\":250,\"containerCount\":150,\"scheduledArrival\":\"%DATE%T09:00:00\",\"scheduledDeparture\":\"%DATE%T11:00:00\"}" > ship2.txt
set /p SHIP2_ID=<ship2.txt

curl -sS -X POST "%BASE_URL%/api/admin/create-ship" -H "Content-Type: application/json" -d "{\"shipNumber\":\"TEST-150-A\",\"shipLength\":150,\"containerCount\":80,\"scheduledArrival\":\"%DATE%T10:00:00\",\"scheduledDeparture\":\"%DATE%T12:00:00\"}" > ship3.txt
set /p SHIP3_ID=<ship3.txt

echo Ship IDs: !SHIP1_ID!, !SHIP2_ID!, !SHIP3_ID!
timeout /t 1 > nul

:: 4. Бронирование причалов
echo [4] Booking piers for ships...

echo [4.1] MV-A001
curl -sS -X POST "%BASE_URL%/api/operator/arrival" -H "Content-Type: application/json" -d "{\"shipId\":!SHIP1_ID!,\"shipNumber\":\"MV-A001\",\"shipLength\":200,\"arrival\":\"%DATE%T10:00:00\",\"departure\":\"%DATE%T10:02:00\"}"
echo.

echo [4.2] SS-Blue
curl -sS -X POST "%BASE_URL%/api/operator/arrival" -H "Content-Type: application/json" -d "{\"shipId\":!SHIP2_ID!,\"shipNumber\":\"SS-Blue\",\"shipLength\":250,\"arrival\":\"%DATE%T10:00:30\",\"departure\":\"%DATE%T10:03:00\"}"
echo.

echo [4.3] TEST-150-A
curl -sS -X POST "%BASE_URL%/api/operator/arrival" -H "Content-Type: application/json" -d "{\"shipId\":!SHIP3_ID!,\"shipNumber\":\"TEST-150-A\",\"shipLength\":150,\"arrival\":\"%DATE%T10:01:00\",\"departure\":\"%DATE%T10:04:00\"}"
echo.

:: 5. Регистрация фактического прибытия
echo [5] Recording actual arrivals...

echo [5.1] MV-A001
curl -sS -X POST "%BASE_URL%/api/operator/actualarrival" -H "Content-Type: application/json" -d "{\"shipId\":!SHIP1_ID!,\"actualArrival\":\"%DATE%T10:02:30\"}"
echo.

:: 6. Регистрация отбытия
echo [6] Recording departures...

echo [6.1] MV-A001
curl -sS -X POST "%BASE_URL%/api/operator/departure" -H "Content-Type: application/json" -d "{\"shipId\":!SHIP1_ID!,\"departure\":\"%DATE%T10:02:00\"}"
echo.

:: 7. Проверка статуса
echo [7] Checking status at %DATE%T10:03:00...
curl -sS -X GET "%BASE_URL%/api/status/piers?at=%DATE%T10:03:00"
echo.

:: 8. Просмотр истории
echo [8] Viewing history...
curl -sS -X GET "%BASE_URL%/api/status/history"
echo.

:: Cleanup
del ship1.txt ship2.txt ship3.txt

endlocal
echo Operator test sequence completed