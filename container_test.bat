@echo off
setlocal enabledelayedexpansion

echo Starting container test script...
echo.

set BASE_URL=http://localhost:8080

:: Основной скрипт
echo [1] Resetting database...
curl -sS -X POST "%BASE_URL%/api/admin/reset"
call :delay
echo.

echo [2] Creating piers (100,200,300)...
curl -sS -X POST "%BASE_URL%/api/admin/create-piers" -H "Content-Type: application/json" -d "[100,200,300]"
call :delay
echo.

echo [3] Creating ship...
set "SHIP_JSON={\"shipNumber\":\"SEA-2025-001\",\"shipLength\":150,\"containerCount\":120,\"scheduledArrival\":\"2025-07-10T08:00:00\",\"scheduledDeparture\":\"2025-07-12T18:00:00\"}"
for /f "tokens=*" %%i in ('curl -sS -X POST "%BASE_URL%/api/admin/create-ship" -H "Content-Type: application/json" -d "!SHIP_JSON!"') do (
    set "SHIP_ID=%%i"
    echo Created Ship ID: !SHIP_ID!
)
call :delay
echo.

echo [4] Recording ACTUAL ship arrival...
set "SHIP_ARRIVAL_JSON={\"shipId\":!SHIP_ID!,\"actualShipArrival\":\"2025-07-10T09:15:00\"}"
curl -sS -X POST "%BASE_URL%/api/stevedore/ship/arrival" -H "Content-Type: application/json" -d "!SHIP_ARRIVAL_JSON!" | jq .
call :delay
echo.

echo [5] Creating container...
set "CONTAINER_JSON={\"shipId\":!SHIP_ID!,\"damageStatus\":false,\"storageType\":\"REGULAR\",\"departureType\":\"TRUCK\",\"scheduledArrivalDate\":\"2025-07-10T10:00:00\",\"scheduledDepartureDate\":\"2025-07-12T14:00:00\"}"
for /f "tokens=*" %%i in ('curl -sS -X POST "%BASE_URL%/api/admin/create-container" -H "Content-Type: application/json" -d "!CONTAINER_JSON!"') do (
    set "CONTAINER_ID=%%i"
    echo Created Container ID: !CONTAINER_ID!
)
call :delay
echo.

echo [6] Recording container arrival...
set "CONTAINER_ARRIVAL_JSON={\"containerId\":!CONTAINER_ID!,\"actualContainerArrival\":\"2025-07-10T11:30:00\"}"
curl -sS -X POST "%BASE_URL%/api/stevedore/container/arrival" -H "Content-Type: application/json" -d "!CONTAINER_ARRIVAL_JSON!" | jq .
call :delay
echo.

echo [7] Moving container to warehouse...
set "MOVE_JSON={\"locationType\":\"WAREHOUSE_REGULAR\"}"
curl -sS -X POST "%BASE_URL%/api/stevedore/container/!CONTAINER_ID!/move" -H "Content-Type: application/json" -d "!MOVE_JSON!"
call :delay
echo.

echo [8] Processing container movement...
curl -sS -X POST "%BASE_URL%/api/stevedore/container/process-movement"
call :delay
echo.

echo [9] Getting container location...
curl -sS -X GET "%BASE_URL%/api/stevedore/container/!CONTAINER_ID!/location" | jq .
call :delay
echo.

echo [9.1] Getting current container location...
curl -sS -X GET "%BASE_URL%/api/stevedore/container/!CONTAINER_ID!/location" | jq .
call :delay
echo.

echo [10] Moving container to departure...
set "DEPARTURE_JSON={\"locationType\":\"DEPARTURE_TRUCK\"}"
curl -sS -X POST "%BASE_URL%/api/stevedore/container/!CONTAINER_ID!/move" -H "Content-Type: application/json" -d "!DEPARTURE_JSON!"
call :delay
echo.

echo [10.1] Getting current container location...
curl -sS -X GET "%BASE_URL%/api/stevedore/container/!CONTAINER_ID!/location" | jq .
call :delay
echo.

echo [11] Recording container departure...
set "CONTAINER_DEPARTURE_JSON={\"containerId\":!CONTAINER_ID!,\"actualContainerDeparture\":\"2025-07-12T13:45:00\"}"
curl -sS -X POST "%BASE_URL%/api/stevedore/container/departure" -H "Content-Type: application/json" -d "!CONTAINER_DEPARTURE_JSON!" | jq .
call :delay
echo.

echo [12] Recording ship departure...
set "SHIP_DEPARTURE_JSON={\"shipId\":!SHIP_ID!,\"actualShipDeparture\":\"2025-07-12T15:30:00\"}"
curl -sS -X POST "%BASE_URL%/api/stevedore/ship/departure" -H "Content-Type: application/json" -d "!SHIP_DEPARTURE_JSON!" | jq .
call :delay
echo.

echo [13] Getting container event history...
curl -sS -X GET "%BASE_URL%/api/stevedore/container/!CONTAINER_ID!/event" | jq .
call :delay
echo.

echo [14] Getting ship events...
curl -sS -X GET "%BASE_URL%/api/stevedore/ship/!SHIP_ID!/events" | jq .
call :delay
echo.

echo [15] Checking pier status...
curl -sS -X GET "%BASE_URL%/api/status/piers" | jq .
call :delay
echo.

echo [16] Getting final container location...
curl -sS -X GET "%BASE_URL%/api/stevedore/container/!CONTAINER_ID!/location" | jq .
call :delay
echo.

endlocal
echo Test sequence completed

:: Функция для паузы
:delay
echo Delaying for 3 seconds...
ping -n 3 127.0.0.1 > nul
goto :eof