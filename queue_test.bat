@echo off
setlocal

set BASE=http://localhost:8080/api
set DATE=2023-07-10

echo ===== 1) Reset database =====
curl -s -X POST %BASE%/admin/reset
echo.

echo ===== 2) Create piers [150,200,250] =====
curl -s -X POST %BASE%/admin/create-piers -H "Content-Type: application/json" -d "[150,200,250]"
echo.

echo Calling status after creating piers...
call :printStatus "After creating piers" "%DATE%T09:00:00"

echo ===== 3) Planned arrivals =====

echo [DEBUG] arrival 1: MV-A001
curl -s -X POST %BASE%/operator/arrival -H "Content-Type: application/json" -d ^
  "{\"shipNumber\":\"MV-A001\",\"shipLength\":200,\"arrival\":\"%DATE%T10:00:00\",\"departure\":\"%DATE%T10:02:00\"}"
echo.
echo Calling status after arrival 1...
call :printStatus "After arrival 1" "%DATE%T10:00:00"

echo [DEBUG] arrival 2: SS-Blue
curl -s -X POST %BASE%/operator/arrival -H "Content-Type: application/json" -d ^
  "{\"shipNumber\":\"SS-Blue\",\"shipLength\":250,\"arrival\":\"%DATE%T10:00:30\",\"departure\":\"%DATE%T10:03:00\"}"
echo.
echo Calling status after arrival 2...
call :printStatus "After arrival 2" "%DATE%T10:00:30"

echo [DEBUG] arrival 3: TEST-150-A
curl -s -X POST %BASE%/operator/arrival -H "Content-Type: application/json" -d ^
  "{\"shipNumber\":\"TEST-150-A\",\"shipLength\":150,\"arrival\":\"%DATE%T10:01:00\",\"departure\":\"%DATE%T10:04:00\"}"
echo.
echo Calling status after arrival 3...
call :printStatus "After arrival 3" "%DATE%T10:01:00"

echo [DEBUG] arrival 4: TEST-200-A
curl -s -X POST %BASE%/operator/arrival -H "Content-Type: application/json" -d ^
  "{\"shipNumber\":\"TEST-200-A\",\"shipLength\":200,\"arrival\":\"%DATE%T10:01:30\",\"departure\":\"%DATE%T10:05:00\"}"
echo.
echo Calling status after arrival 4...
call :printStatus "After arrival 4" "%DATE%T10:01:30"

echo [DEBUG] arrival 5: QUEUE-SHIP
curl -s -X POST %BASE%/operator/arrival -H "Content-Type: application/json" -d ^
  "{\"shipNumber\":\"QUEUE-SHIP\",\"shipLength\":150,\"arrival\":\"%DATE%T10:02:00\",\"departure\":\"%DATE%T10:06:00\"}"
echo.
echo Calling status after arrival 5...
call :printStatus "After arrival 5" "%DATE%T10:02:00"

echo [DEBUG] arrival 6: EXTRA-200
curl -s -X POST %BASE%/operator/arrival -H "Content-Type: application/json" -d ^
  "{\"shipNumber\":\"EXTRA-200\",\"shipLength\":200,\"arrival\":\"%DATE%T10:05:00\",\"departure\":\"%DATE%T10:08:00\"}"
echo.
echo Calling status after arrival 6...
call :printStatus "After arrival 6" "%DATE%T10:05:00"

echo [DEBUG] arrival 7: EXTRA-250
curl -s -X POST %BASE%/operator/arrival -H "Content-Type: application/json" -d ^
  "{\"shipNumber\":\"EXTRA-250\",\"shipLength\":250,\"arrival\":\"%DATE%T10:05:30\",\"departure\":\"%DATE%T10:09:00\"}"
echo.
echo Calling status after arrival 7...
call :printStatus "After arrival 7" "%DATE%T10:05:30"

echo ===== 4) ActualArrival for QUEUE-SHIP (shipId=5) =====
curl -s -X POST %BASE%/operator/actualArrival -H "Content-Type: application/json" -d ^
  "{\"shipId\":5,\"actualArrival\":\"%DATE%T10:02:30\"}"
echo.
echo Calling status after actualArrival 5...
call :printStatus "After actualArrival 5" "%DATE%T10:02:30"

echo ===== 5) Departures =====

echo [DEBUG] departure 1
curl -s -X POST %BASE%/operator/departure -H "Content-Type: application/json" -d ^
  "{\"shipId\":1,\"departure\":\"%DATE%T10:02:00\"}"
echo.
echo Calling status after departure 1...
call :printStatus "After departure 1" "%DATE%T10:02:00"

echo [DEBUG] departure 2
curl -s -X POST %BASE%/operator/departure -H "Content-Type: application/json" -d ^
  "{\"shipId\":2,\"departure\":\"%DATE%T10:03:00\"}"
echo.
echo Calling status after departure 2...
call :printStatus "After departure 2" "%DATE%T10:03:00"

echo [DEBUG] departure 3
curl -s -X POST %BASE%/operator/departure -H "Content-Type: application/json" -d ^
  "{\"shipId\":3,\"departure\":\"%DATE%T10:04:00\"}"
echo.
echo Calling status after departure 3...
call :printStatus "After departure 3" "%DATE%T10:04:00"

echo [DEBUG] departure 4
curl -s -X POST %BASE%/operator/departure -H "Content-Type: application/json" -d ^
  "{\"shipId\":4,\"departure\":\"%DATE%T10:05:00\"}"
echo.
echo Calling status after departure 4...
call :printStatus "After departure 4" "%DATE%T10:05:00"

echo [DEBUG] departure 5
curl -s -X POST %BASE%/operator/departure -H "Content-Type: application/json" -d ^
  "{\"shipId\":5,\"departure\":\"%DATE%T10:06:00\"}"
echo.
echo Calling status after departure 5...
call :printStatus "After departure 5" "%DATE%T10:06:00"

echo [DEBUG] departure 6
curl -s -X POST %BASE%/operator/departure -H "Content-Type: application/json" -d ^
  "{\"shipId\":6,\"departure\":\"%DATE%T10:08:00\"}"
echo.
echo Calling status after departure 6...
call :printStatus "After departure 6" "%DATE%T10:08:00"

echo [DEBUG] departure 7
curl -s -X POST %BASE%/operator/departure -H "Content-Type: application/json" -d ^
  "{\"shipId\":7,\"departure\":\"%DATE%T10:09:00\"}"
echo.
echo Calling status after departure 7...
call :printStatus "After departure 7" "%DATE%T10:09:00"

echo ===== 6) Final state =====
echo Calling final status...
call :printStatus "Final" "%DATE%T10:10:00"

echo History:
curl -s -X GET "%BASE%/status/history" | jq .
echo.

endlocal
exit /b

:printStatus
set LABEL=%~1
set AT=%~2
echo --- %LABEL% @ %AT% ---
echo Piers:
curl -s -X GET "%BASE%/status/piers?at=%AT%" | jq .
echo Queue:
curl -s -X GET "%BASE%/status/queue?at=%AT%" | jq .
echo.
goto :eof
