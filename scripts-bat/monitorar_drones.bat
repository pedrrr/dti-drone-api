@echo off
echo ========================================
echo    MONITOR DE DRONES EM TEMPO REAL
echo ========================================
echo.
echo Pressione CTRL+C para parar o monitoramento
echo.

:loop
cls
echo ========================================
echo    STATUS DOS DRONES - %date% %time%
echo ========================================
echo.

curl -s http://localhost:8080/api/v1/drones > temp_drones.json 2>nul
if %errorlevel% neq 0 (
    echo ERRO: Nao foi possivel conectar com a aplicacao
    echo Verifique se ela esta rodando na porta 8080
    echo.
    pause
    exit /b 1
)

echo DRONES ATIVOS:
echo --------------
for /f "tokens=*" %%i in ('type temp_drones.json ^| findstr /C:"\"id\"" /C:"\"state\"" /C:"\"positionX\"" /C:"\"positionY\"" /C:"\"battery\"" /C:"\"weightLimit\""') do (
    echo %%i
)
echo.

echo PEDIDOS:
echo --------
curl -s http://localhost:8080/api/v1/pedidos > temp_pedidos.json 2>nul
for /f "tokens=*" %%i in ('type temp_pedidos.json ^| findstr /C:"\"id\"" /C:"\"destinationX\"" /C:"\"destinationY\"" /C:"\"priority\"" /C:"\"weight\""') do (
    echo %%i
)
echo.

echo Atualizando em 5 segundos...
timeout /t 5 /nobreak > nul
goto loop



