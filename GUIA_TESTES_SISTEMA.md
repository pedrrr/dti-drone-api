# 🚀 GUIA COMPLETO DE TESTES - SISTEMA DE DRONES

## 📋 **PRÉ-REQUISITOS**
- IntelliJ IDEA aberto com o projeto
- Postman instalado (ou usar curl)
- Aplicação rodando na porta 8080

## 🎯 **SEQUÊNCIA DE TESTES RECOMENDADA**

### **1. INICIAR A APLICAÇÃO**
```bash
# No terminal do IntelliJ ou cmd:
mvn spring-boot:run
```
**✅ Esperado:** Aplicação inicia sem erros na porta 8080

---

### **2. CRIAR DRONES** 
```http
POST http://localhost:8080/api/v1/drones
Content-Type: application/json

{
  "weightLimit": 50,
  "distancePerCargo": 20,
  "positionX": 1,
  "positionY": 1
}
```

**Repita 3 vezes** com diferentes capacidades:
```json
// Drone 2
{
  "weightLimit": 30,
  "distancePerCargo": 15,
  "positionX": 1,
  "positionY": 1
}

// Drone 3
{
  "weightLimit": 70,
  "distancePerCargo": 25,
  "positionX": 1,
  "positionY": 1
}
```

**✅ Esperado:** Drones criados com ID, bateria 100%, estado IDLE

---

### **3. VERIFICAR DRONES CRIADOS**
```http
GET http://localhost:8080/api/v1/drones
```

**✅ Esperado:** Lista com 3 drones, todos na posição (1,1), bateria 100%, estado IDLE

---

### **4. CRIAR PEDIDOS (TESTE DE ALOCAÇÃO AUTOMÁTICA)**

#### **Pedido 1 - Alta Prioridade**
```http
POST http://localhost:8080/api/v1/pedidos
Content-Type: application/json

{
  "destinationX": 5,
  "destinationY": 5,
  "weight": 10,
  "priority": "HIGH"
}
```

#### **Pedido 2 - Média Prioridade**
```http
POST http://localhost:8080/api/v1/pedidos
Content-Type: application/json

{
  "destinationX": 10,
  "destinationY": 8,
  "weight": 15,
  "priority": "MEDIUM"
}
```

#### **Pedido 3 - Baixa Prioridade**
```http
POST http://localhost:8080/api/v1/pedidos
Content-Type: application/json

{
  "destinationX": 3,
  "destinationY": 3,
  "weight": 8,
  "priority": "LOW"
}
```

**✅ Esperado:** Cada pedido é automaticamente alocado no melhor drone disponível

---

### **5. VERIFICAR ALOCAÇÃO DOS PEDIDOS**
```http
GET http://localhost:8080/api/v1/drones
```

**✅ Esperado:** Drones mostram `orderList` com pedidos alocados

---

### **6. INICIAR VOOS (TESTE DA SIMULAÇÃO)**
```http
POST http://localhost:8080/api/v1/drones/fly
```

**✅ Esperado:** Resposta com quantidade de drones que iniciaram voo
```json
{
  "message": "Comando de voo executado com sucesso",
  "dronesWithOrders": 3,
  "dronesStarted": 3,
  "timestamp": "2024-01-15T10:30:00"
}
```

---

### **7. MONITORAR SIMULAÇÃO EM TEMPO REAL**

**Execute repetidamente (a cada 5-10 segundos):**
```http
GET http://localhost:8080/api/v1/drones
```

**✅ Você deve observar:**
- Drones mudando de `IDLE` → `IN_FLIGHT`
- Posições `positionX` e `positionY` mudando gradualmente
- Bateria `battery` diminuindo conforme movimento
- Estados mudando: `IN_FLIGHT` → `DELIVERING` → `RETURNING_TO_BASE` → `IDLE`

---

### **8. TESTAR VALIDAÇÕES (PEDIDOS INVÁLIDOS)**

#### **Pedido que excede peso máximo:**
```http
POST http://localhost:8080/api/v1/pedidos
Content-Type: application/json

{
  "destinationX": 5,
  "destinationY": 5,
  "weight": 100,
  "priority": "HIGH"
}
```

**✅ Esperado:** Erro 400 - "Peso não pode exceder 1000 kg"

#### **Drone com posição inválida:**
```http
POST http://localhost:8080/api/v1/drones
Content-Type: application/json

{
  "weightLimit": 50,
  "distancePerCargo": 20,
  "positionX": 0,
  "positionY": 1
}
```

**✅ Esperado:** Erro 400 - "Posição X deve ser maior que 0"

---

### **9. TESTAR RECARGA AUTOMÁTICA**

**Para testar recarga:**
1. Crie um drone com bateria baixa (simule no código ou aguarde uso natural)
2. Observe quando bateria < 80% e drone está na base (1,1)
3. Verifique mudança para estado `RECHARGING`
4. Aguarde alguns segundos e veja bateria voltar a 100%

---

### **10. TESTE DE STRESS - MÚLTIPLOS PEDIDOS**

**Crie vários pedidos rapidamente:**
```http
POST http://localhost:8080/api/v1/pedidos
Content-Type: application/json

{
  "destinationX": 7,
  "destinationY": 7,
  "weight": 12,
  "priority": "HIGH"
}
```

**Repita 5-10 vezes com coordenadas diferentes**

**✅ Esperado:** Sistema realoca automaticamente todos os pedidos de forma otimizada

---

## 🔍 **OBSERVAÇÕES IMPORTANTES**

### **Logs para Monitorar:**
- ✅ Pedidos alocados: `"✅ Pedido X alocado no drone Y"`
- ⚠️ Pedidos não alocados: `"⚠ Pedido X não pôde ser alocado"`
- 🚁 Movimento: `"Drone X moveu de (a,b) para (c,d)"`
- 🔋 Mudança de estado: `"Drone X iniciou voo"`, `"Drone X entregou pedido"`

### **Estados dos Drones:**
- `IDLE`: Disponível para pedidos
- `IN_FLIGHT`: Movendo para destino
- `DELIVERING`: Entregando no destino
- `RETURNING_TO_BASE`: Voltando para base
- `RECHARGING`: Carregando bateria

### **Pontos de Verificação:**
1. ✅ Alocação automática funciona
2. ✅ Simulação de movimento funciona
3. ✅ Estados mudam corretamente
4. ✅ Bateria drena e recarrega
5. ✅ Validações impedem dados inválidos
6. ✅ Sistema otimiza distribuição

---

## 🚨 **SOLUÇÃO DE PROBLEMAS**

### **Se a aplicação não iniciar:**
1. Verifique se Java 17 está instalado
2. Execute `mvn clean compile` primeiro
3. Verifique logs de erro no console

### **Se pedidos não forem alocados:**
1. Verifique se drones estão em estado `IDLE`
2. Confirme se drones estão na base (1,1)
3. Verifique se bateria >= 20%

### **Se simulação não funcionar:**
1. Aguarde alguns segundos após `/fly`
2. Verifique logs para mensagens de erro
3. Confirme se drones têm pedidos alocados

---

## 📊 **RESULTADO ESPERADO FINAL**

Após todos os testes, você deve ter:
- ✅ Sistema funcionando perfeitamente
- ✅ Drones se movendo pela malha
- ✅ Entregas sendo realizadas
- ✅ Bateria sendo gerenciada
- ✅ Validações funcionando
- ✅ Otimização de alocação ativa

**🎉 Sistema 100% funcional e testado!**



