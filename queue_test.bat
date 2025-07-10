@echo off
setlocal ENABLEDELAYEDEXPANSION

rem ----------------------------------------
rem Конфигурация
rem ----------------------------------------
set BASE=http://localhost:8080/api
set DOCK_DATE=2023-07-10

rem ----------------------------------------
rem 0) Аутентификация всех ролей
rem ----------------------------------------
echo ===== 0) Login as ADMIN, OPERATOR and TALLYMAN =====

rem -- ADMIN --
for /f "delims=" %%i in (
  'curl -s -X POST %BASE%/auth/login ^
      -H "Content-Type: application/json" ^
      -d "{\"email\":\"admin@example.com\",\"password\":\"admin123\"}" ^| jq -r .token'
) do set ADMIN_TOKEN=%%i
echo [INFO] ADMIN_TOKEN=!ADMIN_TOKEN!

rem -- OPERATOR --
for /f "delims=" %%i in (
  'curl -s -X POST %BASE%/auth/login ^
      -H "Content-Type: application/json" ^
      -d "{\"email\":\"operator@example.com\",\"password\":\"operator123\"}" ^| jq -r .token'
) do set OP_TOKEN=%%i
echo [INFO] OPERATOR_TOKEN=!OP_TOKEN!

rem -- TALLYMAN --
for /f "delims=" %%i in (
  'curl -s -X POST %BASE%/auth/login ^
      -H "Content-Type: application/json" ^
      -d "{\"email\":\"tallyman@example.com\",\"password\":\"tallyman123\"}" ^| jq -r .token'
) do set TAL_TOKEN=%%i
echo [INFO] TALLYMAN_TOKEN=!TAL_TOKEN!
echo.

rem ----------------------------------------
rem 1) Сброс базы (ROLE_ADMIN)
rem ----------------------------------------
echo ===== 1) Reset database =====
curl -s -X POST %BASE%/admin/reset ^
     -H "Authorization: Bearer %ADMIN_TOKEN%" ^
     -H "Content-Type: application/json"
echo.

rem ----------------------------------------
rem 2) Создание причалов [150,200,250] (ROLE_ADMIN)
rem ----------------------------------------
echo ===== 2) Create piers [150,200,250] =====
curl -s -X POST %BASE%/admin/create-piers ^
     -H "Authorization: Bearer %ADMIN_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "[150,200,250]"
echo.

echo Calling status after creating piers...
call :printStatus "After creating piers" "%DOCK_DATE%T09:00:00"

rem ----------------------------------------
rem 3) Planned arrivals (ROLE_TERMINAL_OPERATOR)
rem ----------------------------------------
echo ===== 3) Planned arrivals =====

for /f "delims=" %%i in (
  'curl -s -X POST %BASE%/operator/arrival ^
      -H "Authorization: Bearer %OP_TOKEN%" ^
      -H "Content-Type: application/json" ^
      -d "{\"shipNumber\":\"MV-A001\",\"shipLength\":200,\"arrival\":\"%DOCK_DATE%T10:00:00\",\"departure\":\"%DOCK_DATE%T10:02:00\"}"'
) do set SHIP1=%%i
echo [INFO] MV-A001 assigned shipId=!SHIP1!
call :printStatus "After arrival 1" "%DOCK_DATE%T10:00:00"
echo.

for /f "delims=" %%i in (
  'curl -s -X POST %BASE%/operator/arrival ^
      -H "Authorization: Bearer %OP_TOKEN%" ^
      -H "Content-Type: application/json" ^
      -d "{\"shipNumber\":\"SS-Blue\",\"shipLength\":250,\"arrival\":\"%DOCK_DATE%T10:00:30\",\"departure\":\"%DOCK_DATE%T10:03:00\"}"'
) do set SHIP2=%%i
echo [INFO] SS-Blue assigned shipId=!SHIP2!
call :printStatus "After arrival 2" "%DOCK_DATE%T10:00:30"
echo.

for /f "delims=" %%i in (
  'curl -s -X POST %BASE%/operator/arrival ^
      -H "Authorization: Bearer %OP_TOKEN%" ^
      -H "Content-Type: application/json" ^
      -d "{\"shipNumber\":\"TEST-150-A\",\"shipLength\":150,\"arrival\":\"%DOCK_DATE%T10:01:00\",\"departure\":\"%DOCK_DATE%T10:04:00\"}"'
) do set SHIP3=%%i
echo [INFO] TEST-150-A assigned shipId=!SHIP3!
call :printStatus "After arrival 3" "%DOCK_DATE%T10:01:00"
echo.

for /f "delims=" %%i in (
  'curl -s -X POST %BASE%/operator/arrival ^
      -H "Authorization: Bearer %OP_TOKEN%" ^
      -H "Content-Type: application/json" ^
      -d "{\"shipNumber\":\"TEST-200-A\",\"shipLength\":200,\"arrival\":\"%DOCK_DATE%T10:01:30\",\"departure\":\"%DOCK_DATE%T10:05:00\"}"'
) do set SHIP4=%%i
echo [INFO] TEST-200-A assigned shipId=!SHIP4!
call :printStatus "After arrival 4" "%DOCK_DATE%T10:01:30"
echo.

for /f "delims=" %%i in (
  'curl -s -X POST %BASE%/operator/arrival ^
      -H "Authorization: Bearer %OP_TOKEN%" ^
      -H "Content-Type: application/json" ^
      -d "{\"shipNumber\":\"QUEUE-SHIP\",\"shipLength\":150,\"arrival\":\"%DOCK_DATE%T10:02:00\",\"departure\":\"%DOCK_DATE%T10:06:00\"}"'
) do set SHIP5=%%i
echo [INFO] QUEUE-SHIP assigned shipId=!SHIP5!
call :printStatus "After arrival 5" "%DOCK_DATE%T10:02:00"
echo.

for /f "delims=" %%i in (
  'curl -s -X POST %BASE%/operator/arrival ^
      -H "Authorization: Bearer %OP_TOKEN%" ^
      -H "Content-Type: application/json" ^
      -d "{\"shipNumber\":\"EXTRA-200\",\"shipLength\":200,\"arrival\":\"%DOCK_DATE%T10:05:00\",\"departure\":\"%DOCK_DATE%T10:08:00\"}"'
) do set SHIP6=%%i
echo [INFO] EXTRA-200 assigned shipId=!SHIP6!
call :printStatus "After arrival 6" "%DOCK_DATE%T10:05:00"
echo.

for /f "delims=" %%i in (
  'curl -s -X POST %BASE%/operator/arrival ^
      -H "Authorization: Bearer %OP_TOKEN%" ^
      -H "Content-Type: application/json" ^
      -d "{\"shipNumber\":\"EXTRA-250\",\"shipLength\":250,\"arrival\":\"%DOCK_DATE%T10:05:30\",\"departure\":\"%DOCK_DATE%T10:09:00\"}"'
) do set SHIP7=%%i
echo [INFO] EXTRA-250 assigned shipId=!SHIP7!
call :printStatus "After arrival 7" "%DOCK_DATE%T10:05:30"
echo.

rem ----------------------------------------
rem 4) ActualArrival for QUEUE-SHIP (ROLE_TALLYMAN)
rem ----------------------------------------
echo ===== 4) ActualArrival for QUEUE-SHIP =====
curl -s -X POST %BASE%/stevedore/ship/arrival ^
     -H "Authorization: Bearer %TAL_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "{\"shipId\":!SHIP5!,\"actualShipArrival\":\"%DOCK_DATE%T10:02:30\"}"
echo.
call :printStatus "After actualArrival QUEUE-SHIP" "%DOCK_DATE%T10:02:30"
echo.

rem ----------------------------------------
rem 5) Departures (ROLE_TERMINAL_OPERATOR)
rem ----------------------------------------
echo ===== 5) Departures =====

echo [DEBUG] departure 1 (shipId=!SHIP1!):
curl -s -X POST %BASE%/operator/departure ^
     -H "Authorization: Bearer %OP_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "{\"shipId\":!SHIP1!,\"departure\":\"%DOCK_DATE%T10:02:00\"}"
call :printStatus "After departure 1" "%DOCK_DATE%T10:02:00"
echo.

echo [DEBUG] departure 2 (shipId=!SHIP2!):
curl -s -X POST %BASE%/operator/departure ^
     -H "Authorization: Bearer %OP_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "{\"shipId\":!SHIP2!,\"departure\":\"%DOCK_DATE%T10:03:00\"}"
call :printStatus "After departure 2" "%DOCK_DATE%T10:03:00"
echo.

echo [DEBUG] departure 3 (shipId=!SHIP3!):
curl -s -X POST %BASE%/operator/departure ^
     -H "Authorization: Bearer %OP_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "{\"shipId\":!SHIP3!,\"departure\":\"%DOCK_DATE%T10:04:00\"}"
call :printStatus "After departure 3" "%DOCK_DATE%T10:04:00"
echo.

echo [DEBUG] departure 4 (shipId=!SHIP4!):
curl -s -X POST %BASE%/operator/departure ^
     -H "Authorization: Bearer %OP_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "{\"shipId\":!SHIP4!,\"departure\":\"%DOCK_DATE%T10:05:00\"}"
call :printStatus "After departure 4" "%DOCK_DATE%T10:05:00"
echo.

echo [DEBUG] departure 5 (shipId=!SHIP5!):
curl -s -X POST %BASE%/operator/departure ^
     -H "Authorization: Bearer %OP_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "{\"shipId\":!SHIP5!,\"departure\":\"%DOCK_DATE%T10:06:00\"}"
call :printStatus "After departure 5" "%DOCK_DATE%T10:06:00"
echo.

echo [DEBUG] departure 6 (shipId=!SHIP6!):
curl -s -X POST %BASE%/operator/departure ^
     -H "Authorization: Bearer %OP_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "{\"shipId\":!SHIP6!,\"departure\":\"%DOCK_DATE%T10:08:00\"}"
call :printStatus "After departure 6" "%DOCK_DATE%T10:08:00"
echo.

echo [DEBUG] departure 7 (shipId=!SHIP7!):
curl -s -X POST %BASE%/operator/departure ^
     -H "Authorization: Bearer %OP_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "{\"shipId\":!SHIP7!,\"departure\":\"%DOCK_DATE%T10:09:00\"}"
call :printStatus "After departure 7" "%DOCK_DATE%T10:09:00"
echo.

rem ----------------------------------------
rem 6) Финальное состояние
rem ----------------------------------------
echo ===== 6) Final state =====
call :printStatus "Final" "%DOCK_DATE%T10:10:00"
echo.

endlocal
exit /b

:printStatus
set LABEL=%~1
set AT=%~2
echo --- %LABEL% @ %AT% ---
echo Piers:
curl -s -X GET "%BASE%/status/piers?at=%AT%" ^
     -H "Authorization: Bearer %OP_TOKEN%" | jq .
echo Queue:
curl -s -X GET "%BASE%/status/queue?at=%AT%" ^
     -H "Authorization: Bearer %OP_TOKEN%" | jq .
echo.
goto :eof
