# Identificador - Sistema de Gerenciamento de Entregas

## Descrição

O **Identificador** é uma aplicação backend desenvolvida em Spring Boot para gerenciamento completo de um sistema de entregas. O sistema permite o cadastro e gerenciamento de usuários, clientes, entregadores, lojas e entregas, com autenticação JWT, geração de QR codes e comunicação em tempo real via WebSocket.

## Funcionalidades Principais

### 👥 Gerenciamento de Usuários
- Cadastro e autenticação de usuários
- Controle de acesso baseado em roles (USER, ADMIN)
- Validação de dados e segurança

### 👤 Gerenciamento de Clientes
- Cadastro de clientes com validação de CPF e email
- Busca por CPF, email ou nome
- Atualização de dados pessoais

### 🚚 Gerenciamento de Entregadores
- Cadastro de entregadores com documentos (CPF, RG, CNH)
- Controle de status (OFFLINE, DISPONIVEL, EM_ROTA, etc.)
- Sistema de avaliação e aplicativos suportados
- Geração de QR codes únicos para identificação

### 🏪 Gerenciamento de Lojas
- Cadastro de lojas com CNPJ e dados comerciais
- Controle de horário de funcionamento
- Busca e atualização de informações

### 📦 Gerenciamento de Entregas
- Criação e acompanhamento de entregas
- Controle de status da entrega
- Cálculo de valor e gorjeta
- Estimativa de tempo de entrega

### 🔐 Segurança
- Autenticação JWT (JSON Web Tokens)
- Controle de acesso por roles
- CORS configurado para desenvolvimento

### 💬 Comunicação em Tempo Real
- Chat via WebSocket
- Endpoints para mensagens privadas e públicas

### 📊 Monitoramento
- Health checks
- Métricas com Micrometer e Prometheus
- Logs estruturados

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** (OAuth2 Resource Server)
- **Spring Data JPA** (PostgreSQL/MySQL)
- **Spring Data Redis**
- **Spring WebSocket**
- **Spring Validation**
- **SpringDoc OpenAPI** (Swagger)
- **Lombok**
- **ZXing** (QR Code)
- **Maven**

## Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- PostgreSQL ou MySQL
- Redis (opcional, para cache)

## Configuração

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/limongi1234/Identficador.git
   cd Identficador
   ```

2. **Configure o banco de dados:**
   - Edite `src/main/resources/application.properties`
   - Configure as conexões para PostgreSQL/MySQL e Redis

3. **Compile o projeto:**
   ```bash
   ./mvnw clean compile
   ```

4. **Execute a aplicação:**
   ```bash
   ./mvnw spring-boot:run
   ```

## API Endpoints

### Autenticação
- `POST /api/auth/login` - Login de usuário
- `POST /api/auth/register` - Registro de novo usuário

### Usuários
- `GET /api/users` - Listar todos os usuários (ADMIN)
- `GET /api/users/{id}` - Buscar usuário por ID
- `GET /api/users/email/{email}` - Buscar por email
- `GET /api/users/nome/{nome}` - Buscar por nome
- `PUT /api/users/{id}` - Atualizar usuário
- `DELETE /api/users/{id}` - Deletar usuário

### Clientes
- `POST /api/clientes/registro` - Registrar cliente
- `GET /api/clientes` - Listar clientes
- `GET /api/clientes/{id}` - Buscar cliente por ID
- `PUT /api/clientes/{id}` - Atualizar cliente
- `DELETE /api/clientes/{id}` - Deletar cliente

### Entregadores
- `POST /api/entregadores/registro` - Registrar entregador
- `GET /api/entregadores` - Listar entregadores
- `GET /api/entregadores/{id}` - Buscar entregador por ID
- `GET /api/entregadores/perfil/{id}` - Obter perfil público
- `GET /api/entregadores/qrcode/{uuid}` - Buscar por QR code
- `PUT /api/entregadores/{id}` - Atualizar entregador
- `DELETE /api/entregadores/{id}` - Deletar entregador

### Lojas
- `POST /api/lojas/registro` - Registrar loja
- `GET /api/lojas` - Listar lojas
- `GET /api/lojas/{id}` - Buscar loja por ID
- `PUT /api/lojas/{id}` - Atualizar loja
- `DELETE /api/lojas/{id}` - Deletar loja

### Entregas
- `POST /api/entregas` - Criar entrega
- `GET /api/entregas` - Listar entregas
- `GET /api/entregas/{id}` - Buscar entrega por ID
- `PUT /api/entregas/{id}/status` - Atualizar status
- `DELETE /api/entregas/{id}` - Cancelar entrega

### QR Code
- `GET /api/qrcode/{uuid}` - Gerar QR code
- `GET /api/qrcode/entregador/{id}` - QR code do entregador

### WebSocket
- `/ws/chat` - Endpoint para chat
- `/ws-privado/{userId}` - Chat privado

### Health Check
- `GET /api/health` - Status da aplicação
- `GET /actuator/health` - Health check detalhado

## Documentação da API

A documentação completa da API está disponível via Swagger UI:

**Link para o Swagger:** http://localhost:8080/swagger-ui/index.html

## Estrutura do Projeto

```
src/main/java/br/com/identificador/Back_end/
├── config/          # Configurações (Security, WebSocket, Swagger)
├── controller/      # Controladores REST
├── dto/            # Data Transfer Objects (Records)
├── exceptions/     # Tratamento de exceções
├── model/          # Entidades JPA
├── repository/     # Repositórios de dados
└── service/        # Lógica de negócio
```

## Segurança

- **JWT Tokens** para autenticação stateless
- **BCrypt** para hash de senhas
- **CORS** configurado para desenvolvimento
- **Role-based access control** (USER, ADMIN)

## Desenvolvimento

### Executando Testes
```bash
./mvnw test
```

### Build de Produção
```bash
./mvnw clean package
```

### Docker (se configurado)
```bash
docker-compose up
```

## Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## Contato

Para dúvidas ou sugestões, entre em contato com a equipe de desenvolvimento.
