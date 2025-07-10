@echo off
setlocal ENABLEDELAYEDEXPANSION

echo Starting container test script...
echo.

rem ----------------------------------------
rem Конфигурация
rem ----------------------------------------
set BASE_URL=http://localhost:8080/api
set DATE=2025-07-10

rem ----------------------------------------
rem 0) Аутентификация: ADMIN и TALLYMAN
rem ----------------------------------------
echo [0] Logging in as ADMIN and TALLYMAN...

rem -- ADMIN --
for /f "delims=" %%T in (
  'curl -sS -X POST "%BASE_URL%/auth/login" ^
      -H "Content-Type: application/json" ^
      -d "{\"email\":\"admin@example.com\",\"password\":\"admin123\"}" ^| jq -r .token'
) do set ADMIN_TOKEN=%%T
echo [INFO] ADMIN_TOKEN=!ADMIN_TOKEN!

rem -- TALLYMAN --
for /f "delims=" %%T in (
  'curl -sS -X POST "%BASE_URL%/auth/login" ^
      -H "Content-Type: application/json" ^
      -d "{\"email\":\"tallyman@example.com\",\"password\":\"tallyman123\"}" ^| jq -r .token'
) do set TAL_TOKEN=%%T
echo [INFO] TALLYMAN_TOKEN=!TAL_TOKEN!
echo.

call :delay

rem ----------------------------------------
rem 1) Reset database (ROLE_ADMIN)
rem ----------------------------------------
echo [1] Resetting database...
curl -sS -X POST "%BASE_URL%/admin/reset" ^
     -H "Authorization: Bearer %ADMIN_TOKEN%" ^
     -H "Content-Type: application/json"
echo.
call :delay

rem ----------------------------------------
rem 2) Create piers (100,200,300) (ROLE_ADMIN)
rem ----------------------------------------
echo [2] Creating piers (100, 200, 300)...
curl -sS -X POST "%BASE_URL%/admin/create-piers" ^
     -H "Authorization: Bearer %ADMIN_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "[100,200,300]"
echo.
call :delay

rem ----------------------------------------
rem 3) Create ship (ROLE_ADMIN)
rem ----------------------------------------
echo [3] Creating ship...
set "SHIP_JSON={\"shipNumber\":\"SEA-2025-001\",\"shipLength\":150,\"containerCount\":120,\"scheduledArrival\":\"%DATE%T08:00:00\",\"scheduledDeparture\":\"%DATE%T18:00:00\"}"
for /f "tokens=*" %%i in ('
  curl -sS -X POST "%BASE_URL%/admin/create-ship" ^
       -H "Authorization: Bearer %ADMIN_TOKEN%" ^
       -H "Content-Type: application/json" ^
       -d "!SHIP_JSON!"
') do (
  set "SHIP_ID=%%i"
  echo [INFO] Created Ship ID: !SHIP_ID!
)
echo.
call :delay

rem ----------------------------------------
rem 4) Record actual ship arrival (ROLE_TALLYMAN)
rem ----------------------------------------
echo [4] Recording ACTUAL ship arrival...
set "SHIP_ARRIVAL_JSON={\"shipId\":!SHIP_ID!,\"actualShipArrival\":\"%DATE%T09:15:00\"}"
curl -sS -X POST "%BASE_URL%/stevedore/ship/arrival" ^
     -H "Authorization: Bearer %TAL_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "!SHIP_ARRIVAL_JSON!" | jq .
echo.
call :delay

rem ----------------------------------------
rem 5) Create container (ROLE_ADMIN)
rem ----------------------------------------
echo [5] Creating container...
set "CONTAINER_JSON={\"shipId\":!SHIP_ID!,\"damageStatus\":false,\"storageType\":\"REGULAR\",\"departureType\":\"TRUCK\",\"scheduledArrivalDate\":\"%DATE%T10:00:00\",\"scheduledDepartureDate\":\"%DATE%T14:00:00\"}"
for /f "tokens=*" %%i in ('
  curl -sS -X POST "%BASE_URL%/admin/create-container" ^
       -H "Authorization: Bearer %ADMIN_TOKEN%" ^
       -H "Content-Type: application/json" ^
       -d "!CONTAINER_JSON!"
') do (
  set "CONTAINER_ID=%%i"
  echo [INFO] Created Container ID: !CONTAINER_ID!
)
echo.
call :delay

rem ----------------------------------------
rem 6) Record actual container arrival (ROLE_TALLYMAN)
rem ----------------------------------------
echo [6] Recording container arrival...
set "CONTAINER_ARRIVAL_JSON={\"containerId\":!CONTAINER_ID!,\"actualContainerArrival\":\"%DATE%T11:30:00\"}"
curl -sS -X POST "%BASE_URL%/stevedore/container/arrival" ^
     -H "Authorization: Bearer %TAL_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "!CONTAINER_ARRIVAL_JSON!" | jq .
echo.
call :delay

rem ----------------------------------------
rem 7) Move container to warehouse (ROLE_TALLYMAN)
rem ----------------------------------------
echo [7] Moving container to warehouse...
set "MOVE_JSON={\"locationType\":\"WAREHOUSE_REGULAR\"}"
curl -sS -X POST "%BASE_URL%/stevedore/container/!CONTAINER_ID!/move" ^
     -H "Authorization: Bearer %TAL_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "!MOVE_JSON!"
echo.
call :delay

rem ----------------------------------------
rem 8) Process container movement (ROLE_TALLYMAN)
rem ----------------------------------------
echo [8] Processing container movement...
curl -sS -X POST "%BASE_URL%/stevedore/container/process-movement" ^
     -H "Authorization: Bearer %TAL_TOKEN%"
echo.
call :delay

rem ----------------------------------------
rem 9) Get container location (ROLE_TALLYMAN)
rem ----------------------------------------
echo [9] Getting container location...
curl -sS -X GET "%BASE_URL%/stevedore/container/!CONTAINER_ID!/location" ^
     -H "Authorization: Bearer %TAL_TOKEN%" | jq .
echo.
call :delay

rem ----------------------------------------
rem 10) Move to departure (ROLE_TALLYMAN)
rem ----------------------------------------
echo [10] Moving container to departure...
set "DEPARTURE_JSON={\"locationType\":\"DEPARTURE_TRUCK\"}"
curl -sS -X POST "%BASE_URL%/stevedore/container/!CONTAINER_ID!/move" ^
     -H "Authorization: Bearer %TAL_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "!DEPARTURE_JSON!"
echo.
call :delay

rem ----------------------------------------
rem 11) Get current container location (ROLE_TALLYMAN)
rem ----------------------------------------
echo [11] Getting current container location...
curl -sS -X GET "%BASE_URL%/stevedore/container/!CONTAINER_ID!/location" ^
     -H "Authorization: Bearer %TAL_TOKEN%" | jq .
echo.
call :delay

rem ----------------------------------------
rem 12) Record container departure (ROLE_TALLYMAN)
rem ----------------------------------------
echo [12] Recording container departure...
set "CONTAINER_DEP_JSON={\"containerId\":!CONTAINER_ID!,\"actualContainerDeparture\":\"2025-07-12T13:45:00\"}"
curl -sS -X POST "%BASE_URL%/stevedore/container/departure" ^
     -H "Authorization: Bearer %TAL_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "!CONTAINER_DEP_JSON!" | jq .
echo.
call :delay

rem ----------------------------------------
rem 13) Record ship departure (ROLE_TALLYMAN)
rem ----------------------------------------
echo [13] Recording ship departure...
set "SHIP_DEP_JSON={\"shipId\":!SHIP_ID!,\"actualShipDeparture\":\"2025-07-12T15:30:00\"}"
curl -sS -X POST "%BASE_URL%/stevedore/ship/departure" ^
     -H "Authorization: Bearer %TAL_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -d "!SHIP_DEP_JSON!" | jq .
echo.
call :delay

rem ----------------------------------------
rem 14) Get container event history (ROLE_TALLYMAN)
rem ----------------------------------------
echo [14] Getting container event history...
curl -sS -X GET "%BASE_URL%/stevedore/container/!CONTAINER_ID!/event" ^
     -H "Authorization: Bearer %TAL_TOKEN%" | jq .
echo.
call :delay

rem ----------------------------------------
rem 15) Get ship events (ROLE_TALLYMAN)
rem ----------------------------------------
echo [15] Getting ship events...
curl -sS -X GET "%BASE_URL%/stevedore/ship/!SHIP_ID!/events" ^
     -H "Authorization: Bearer %TAL_TOKEN%" | jq .
echo.
call :delay

rem ----------------------------------------
rem 16) Check pier status (ROLE_TERMINAL_OPERATOR or TALLYMAN)
rem ----------------------------------------
echo [16] Checking pier status...
curl -sS -X GET "%BASE_URL%/status/piers" ^
     -H "Authorization: Bearer %TAL_TOKEN%" | jq .
echo.
call :delay

echo Test sequence completed.
exit /b

:delay
echo Delaying for 3 seconds...
ping -n 3 127.0.0.1 > nul
goto :eof
