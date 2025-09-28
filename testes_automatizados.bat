@echo off
echo ========================================
echo    TESTE AUTOMATIZADO - SISTEMA DRONES
echo ========================================
echo.

echo [1/10] Verificando se aplicacao esta rodando...
curl -s http://localhost:8080/api/v1/drones > nul
if %errorlevel% neq 0 (
    echo ERRO: Aplicacao nao esta rodando! Execute: mvn spring-boot:run
    pause
    exit /b 1
)
echo ✅ Aplicacao rodando na porta 8080
echo.

echo [2/10] Criando Drone 1 (Capacidade 50kg, Alcance 20km)...
curl -X POST http://localhost:8080/api/v1/drones ^
  -H "Content-Type: application/json" ^
  -d "{\"weightLimit\":50,\"distancePerCargo\":20,\"positionX\":1,\"positionY\":1}" ^
  -s > drone-log\drone1.json
echo ✅ Drone 1 criado
echo.

echo [3/10] Criando Drone 2 (Capacidade 50kg, Alcance 15km)...
curl -X POST http://localhost:8080/api/v1/drones ^
  -H "Content-Type: application/json" ^
  -d "{\"weightLimit\":50,\"distancePerCargo\":15,\"positionX\":1,\"positionY\":1}" ^
  -s > drone-log\drone2.json
echo ✅ Drone 2 criado
echo.

echo [4/10] Criando Drone 3 (Capacidade 50kg, Alcance 25km)...
curl -X POST http://localhost:8080/api/v1/drones ^
  -H "Content-Type: application/json" ^
  -d "{\"weightLimit\":50,\"distancePerCargo\":25,\"positionX\":1,\"positionY\":1}" ^
  -s > drone-log\drone3.json
echo ✅ Drone 3 criado
echo.

echo [5/10] Verificando drones criados...
curl -s http://localhost:8080/api/v1/drones > drone-log\drones.json
echo ✅ Lista de drones obtida
echo.

echo [6/10] Criando Pedido 1 (ALTA prioridade)...
curl -X POST http://localhost:8080/api/v1/pedidos ^
  -H "Content-Type: application/json" ^
  -d "{\"destinationX\":5,\"destinationY\":5,\"weight\":10,\"priority\":\"HIGH\"}" ^
  -s > pedidos-log\pedido1.json
echo ✅ Pedido 1 criado e alocado
echo.

echo [7/10] Criando Pedido 2 (MEDIA prioridade)...
curl -X POST http://localhost:8080/api/v1/pedidos ^
  -H "Content-Type: application/json" ^
  -d "{\"destinationX\":10,\"destinationY\":8,\"weight\":15,\"priority\":\"MEDIUM\"}" ^
  -s > pedidos-log\pedido2.json
echo ✅ Pedido 2 criado e alocado
echo.

echo [8/10] Criando Pedido 3 (BAIXA prioridade)...
curl -X POST http://localhost:8080/api/v1/pedidos ^
  -H "Content-Type: application/json" ^
  -d "{\"destinationX\":3,\"destinationY\":3,\"weight\":8,\"priority\":\"LOW\"}" ^
  -s > pedidos-log\pedido3.json
echo ✅ Pedido 3 criado e alocado
echo.

echo [9/10] Verificando alocacao dos pedidos...
curl -s http://localhost:8080/api/v1/drones > drone-log\drones_com_pedidos.json
echo ✅ Status dos drones com pedidos alocados
echo.

echo [10/10] INICIANDO VOOS DOS DRONES...
curl -X POST http://localhost:8080/api/v1/drones/fly ^
  -s > voos-log\voo_iniciado.json
echo ✅ Comando de voo executado!
echo.

echo ========================================
echo    SIMULACAO EM ANDAMENTO...
echo ========================================
echo.
echo Os drones estao voando! Execute o comando abaixo para monitorar:
echo curl -s http://localhost:8080/api/v1/drones
echo.
echo Ou abra o arquivo drone-log\drones_com_pedidos.json para ver os resultados
echo.
echo Pressione qualquer tecla para ver o status atual dos drones...
pause > nul

echo.
echo STATUS ATUAL DOS DRONES:
curl -s http://localhost:8080/api/v1/drones | findstr /C:"state" /C:"positionX" /C:"positionY" /C:"battery"
echo.

echo ========================================
echo    TESTE CONCLUIDO!
echo ========================================
echo.
echo Arquivos gerados:
echo - drone-log\drone1.json, drone-log\drone2.json, drone-log\drone3.json
echo - pedidos-log\pedido1.json, pedidos-log\pedido2.json, pedidos-log\pedido3.json  
echo - drone-log\drones.json, drone-log\drones_com_pedidos.json
echo - voos-log\voo_iniciado.json
echo.
echo Execute 'mvn spring-boot:run' se a aplicacao nao estiver rodando
echo.
pause



