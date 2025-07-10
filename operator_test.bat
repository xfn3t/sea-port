@echo off
setlocal ENABLEDELAYEDEXPANSION

rem ----------------------------------------
rem Конфигурация
rem ----------------------------------------
set BASE_URL=http://localhost:8080/api
set DATE=2025-07-10

rem ----------------------------------------
rem 0) Аутентификация ролей: ADMIN и OPERATOR
rem ----------------------------------------
echo ===== 0) Logging in as ADMIN and OPERATOR =====

rem -- ADMIN --
for /f "delims=" %%T in (
  'curl -s -X POST "%BASE_URL%/auth/login" ^
      -H "Content-Type: application/json" ^
      -d "{\"email\":\"admin@example.com\",\"password\":\"admin123\"}" ^| jq -r .token'
) do set ADMIN_TOKEN=%%T
echo [INFO] ADMIN_TOKEN=!ADMIN_TOKEN!

rem -- OPERATOR --
for /f "delims=" %%T in (
  'curl -s -X POST "%BASE_URL%/auth/login" ^
      -H "Content-Type: application/json" ^
      -d "{\"email\":\"operator@example.com\",\"password\":\"operator123\"}" ^| jq -r .token'
) do set OP_TOKEN=%%T
echo [INFO] OPERATOR_TOKEN=!OP_TOKEN!
echo.

rem ----------------------------------------
rem 1) Сброс базы данных (ROLE_ADMIN)
rem ----------------------------------------
echo [1] Resetting database...
curl -sS -X POST "%BASE_URL%/admin/reset" ^
     -H "Authorization: Bearer %ADMIN_TOKEN%" ^
     -H "Content-Type: application/json"
echo.
timeout /t 1 > nul

rem ----------------------------------------
rem 2) Создание причалов (ROLE_ADMIN)
rem ----------------------------------------
echo [2] Creating piers (150, 200, 250)...
curl -sS -X POST "%BASE_URL%/admin/create-piers" ^
     -H "Authorization: Bearer %ADMIN_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "[150,200,250]"
echo.
timeout /t 1 > nul

rem ----------------------------------------
rem 3) Создание судов и сохранение их ID  (ROLE_ADMIN)
rem ----------------------------------------
echo [3] Creating ships and storing their IDs...

curl -sS -X POST "%BASE_URL%/admin/create-ship" ^
     -H "Authorization: Bearer %ADMIN_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "{\"shipNumber\":\"MV-A001\",\"shipLength\":200,\"containerCount\":100,\"scheduledArrival\":\"%DATE%T08:00:00\",\"scheduledDeparture\":\"%DATE%T10:00:00\"}" > ship1.txt
set /p SHIP1_ID=<ship1.txt

curl -sS -X POST "%BASE_URL%/admin/create-ship" ^
     -H "Authorization: Bearer %ADMIN_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "{\"shipNumber\":\"SS-Blue\",\"shipLength\":250,\"containerCount\":150,\"scheduledArrival\":\"%DATE%T09:00:00\",\"scheduledDeparture\":\"%DATE%T11:00:00\"}" > ship2.txt
set /p SHIP2_ID=<ship2.txt

curl -sS -X POST "%BASE_URL%/admin/create-ship" ^
     -H "Authorization: Bearer %ADMIN_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "{\"shipNumber\":\"TEST-150-A\",\"shipLength\":150,\"containerCount\":80,\"scheduledArrival\":\"%DATE%T10:00:00\",\"scheduledDeparture\":\"%DATE%T12:00:00\"}" > ship3.txt
set /p SHIP3_ID=<ship3.txt

echo [INFO] Ship IDs: !SHIP1_ID!, !SHIP2_ID!, !SHIP3_ID!
echo.
timeout /t 1 > nul

rem ----------------------------------------
rem 4) Бронирование причалов (ROLE_TERMINAL_OPERATOR)
rem ----------------------------------------
echo [4] Booking piers for ships...

echo [4.1] MV-A001
curl -sS -X POST "%BASE_URL%/operator/arrival" ^
     -H "Authorization: Bearer %OP_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "{\"shipId\":!SHIP1_ID!,\"shipNumber\":\"MV-A001\",\"shipLength\":200,\"arrival\":\"%DATE%T10:00:00\",\"departure\":\"%DATE%T10:02:00\"}"
echo.

echo [4.2] SS-Blue
curl -sS -X POST "%BASE_URL%/operator/arrival" ^
     -H "Authorization: Bearer %OP_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "{\"shipId\":!SHIP2_ID!,\"shipNumber\":\"SS-Blue\",\"shipLength\":250,\"arrival\":\"%DATE%T10:00:30\",\"departure\":\"%DATE%T10:03:00\"}"
echo.

echo [4.3] TEST-150-A
curl -sS -X POST "%BASE_URL%/operator/arrival" ^
     -H "Authorization: Bearer %OP_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "{\"shipId\":!SHIP3_ID!,\"shipNumber\":\"TEST-150-A\",\"shipLength\":150,\"arrival\":\"%DATE%T10:01:00\",\"departure\":\"%DATE%T10:04:00\"}"
echo.
timeout /t 1 > nul

rem ----------------------------------------
rem 5) Регистрация фактического прибытия (ROLE_TERMINAL_OPERATOR)
rem ----------------------------------------
echo [5] Recording actual arrivals...

echo [5.1] MV-A001
curl -sS -X POST "%BASE_URL%/operator/actualArrival" ^
     -H "Authorization: Bearer %OP_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "{\"shipId\":!SHIP1_ID!,\"actualArrival\":\"%DATE%T10:02:30\"}"
echo.
timeout /t 1 > nul

rem ----------------------------------------
rem 6) Регистрация отбытия (ROLE_TERMINAL_OPERATOR)
rem ----------------------------------------
echo [6] Recording departures...

echo [6.1] MV-A001
curl -sS -X POST "%BASE_URL%/operator/departure" ^
     -H "Authorization: Bearer %OP_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "{\"shipId\":!SHIP1_ID!,\"departure\":\"%DATE%T10:02:00\"}"
echo.
timeout /t 1 > nul

rem ----------------------------------------
rem 7) Проверка статуса (ROLE_TERMINAL_OPERATOR)
rem ----------------------------------------
echo [7] Checking status at %DATE%T10:03:00...
curl -sS -X GET "%BASE_URL%/status/piers?at=%DATE%T10:03:00" ^
     -H "Authorization: Bearer %OP_TOKEN%"
echo.
timeout /t 1 > nul

rem ----------------------------------------
rem 8) Просмотр истории (ROLE_TERMINAL_OPERATOR)
rem ----------------------------------------
echo [8] Viewing history...
curl -sS -X GET "%BASE_URL%/status/history" ^
     -H "Authorization: Bearer %OP_TOKEN%"
echo.

rem ----------------------------------------
rem Cleanup temp files
rem ----------------------------------------
del ship1.txt ship2.txt ship3.txt 2>nul

endlocal
echo.
echo Operator test sequence completed.
