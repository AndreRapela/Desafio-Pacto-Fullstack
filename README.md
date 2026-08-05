# Pacto Carreiras

Aplicação web de recrutamento interno desenvolvida como desafio técnico Full Stack com Java e Angular.

O sistema permite que colaboradores pesquisem vagas internas, realizem candidaturas e acompanhem seus processos. Administradores podem cadastrar vagas, avaliar candidatos, atualizar status e enviar feedbacks.

## Funcionalidades

### Autenticação e autorização

- Login com JWT.
- Controle de acesso por perfil.
- Perfil `ADMIN` para gerenciamento de vagas e avaliação de candidatos.
- Perfil `CANDIDATE` para consulta de vagas, candidaturas e notificações.
- Proteção das rotas no backend.
- Interceptor no frontend para envio do token.
- Sessão armazenada no navegador.

### Gerenciamento de vagas

O administrador pode:

- cadastrar vagas;
- informar título, descrição, requisitos e tempo mínimo de empresa;
- definir o status da vaga;
- editar vagas;
- excluir vagas;
- pesquisar vagas por texto;
- filtrar vagas por status.

### Candidaturas

O candidato pode:

- consultar vagas disponíveis;
- verificar se atende ao tempo mínimo de empresa;
- candidatar-se a uma vaga aberta;
- visualizar quando uma candidatura já foi enviada;
- acompanhar suas candidaturas.

O backend impede:

- candidatura em vaga fechada;
- candidatura duplicada;
- candidatura sem o tempo mínimo de empresa exigido.

### Notificações

O sistema envia notificações:

- ao responsável pela vaga quando uma candidatura é criada;
- ao candidato quando a candidatura é registrada;
- ao candidato quando o status ou feedback é atualizado.

As notificações podem ser abertas individualmente e marcadas como lidas.

### Painel do candidato

O candidato pode acompanhar:

- vaga;
- status da candidatura;
- data da candidatura;
- feedback do responsável;
- notificações relacionadas ao processo.

### Avaliação de candidatos

O administrador pode:

- selecionar uma vaga;
- visualizar os candidatos inscritos;
- filtrar por tempo mínimo de empresa;
- atualizar o status da candidatura;
- registrar feedback;
- salvar a avaliação;
- notificar automaticamente o candidato.

Status disponíveis:

- `SUBMITTED`: enviada;
- `UNDER_REVIEW`: em análise;
- `APPROVED`: aprovada;
- `REJECTED`: não aprovada.

## Tecnologias

### Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- PostgreSQL
- Flyway
- Bean Validation
- Springdoc OpenAPI / Swagger
- JUnit 5
- Mockito
- Maven

### Frontend

- Angular
- TypeScript
- Angular Router
- Angular Signals
- FormsModule e Reactive Forms
- HttpClient
- Vitest
- HTML
- CSS responsivo
- Nginx

### Infraestrutura

- Docker
- Docker Compose
- PostgreSQL em container
- Build separado para backend e frontend

## Estrutura do projeto

```text
Desafio-Pacto-Fullstack/
├── BackEnd - Spring/
│   └── Recrutamento/
│       ├── src/
│       ├── pom.xml
│       └── Dockerfile
├── FrontEnd - Angular/
│   └── Recrutamento/
│       ├── src/
│       ├── package.json
│       └── Dockerfile
├── docker-compose.yml
└── README.md
```

O frontend e o backend foram desenvolvidos como módulos separados.

## Requisitos para execução

Para executar a aplicação completa, é necessário ter instalado:

- Docker Desktop;
- Docker Compose.

Não é necessário instalar Java, Maven, Node.js, npm ou PostgreSQL localmente para executar a aplicação com Docker.

## Como executar

Abra um terminal na pasta raiz do projeto:

```powershell
cd "Desafio-Pacto-Fullstack"
```

Construa as imagens e inicie os containers:

```powershell
docker compose up -d --build
```

Verifique o estado dos serviços:

```powershell
docker compose ps
```

Os serviços esperados são:

- `postgres`;
- `backend`;
- `frontend`.

## Endereços da aplicação

Após iniciar os containers:

- Frontend: `http://localhost`
- Backend: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`
- OpenAPI: `http://localhost:8080/v3/api-docs`

## Usuários de demonstração

### Administrador

```text
E-mail: admin@admin.com
Senha: admin123
```

### Candidato

```text
E-mail: candidato@candidato.com
Senha: candidato123
```

Os usuários são criados automaticamente quando a aplicação é iniciada com um banco limpo.

## Como parar a aplicação

```powershell
docker compose down
```

Para apagar também os dados persistidos no PostgreSQL:

```powershell
docker compose down -v
```

> O comando com `-v` remove o volume do banco de dados.

## Recriar a aplicação do zero

```powershell
docker compose down -v
docker compose build --no-cache
docker compose up -d
docker compose ps
```

## Banco de dados

A aplicação utiliza PostgreSQL.

As alterações de estrutura do banco são controladas pelo Flyway por meio de migrations localizadas no backend.

O banco é iniciado pelo Docker Compose e possui verificação de saúde antes da inicialização do backend.

## Documentação da API

A documentação dos endpoints está disponível no Swagger:

```text
http://localhost:8080/swagger-ui.html
```

Para testar endpoints protegidos:

1. faça login pelo endpoint de autenticação;
2. copie o token JWT retornado;
3. clique em **Authorize**;
4. informe o token no formato:

```text
Bearer SEU_TOKEN
```

## Principais endpoints

### Autenticação

```text
POST /api/auth/login
POST /api/auth/register
```

### Vagas

```text
GET    /api/jobs
GET    /api/jobs/{id}
POST   /api/jobs
PUT    /api/jobs/{id}
DELETE /api/jobs/{id}
```

### Candidaturas

```text
POST  /api/applications/jobs/{jobId}
GET   /api/applications/me
GET   /api/applications/job/{jobId}
PATCH /api/applications/{id}/status
```

### Notificações

```text
GET   /api/notifications
GET   /api/notifications/{id}
PATCH /api/notifications/{id}/read
```

## Testes do backend

Os testes do backend cobrem:

- e-mail duplicado;
- registro de candidato;
- login;
- criação de vaga;
- elegibilidade por tempo de empresa;
- candidatura em vaga fechada;
- candidatura duplicada;
- tempo mínimo insuficiente;
- criação de candidatura;
- envio de notificações;
- atualização de status e feedback.

Na pasta raiz do projeto, execute:

```powershell
docker run --rm `
  -v "${PWD}/BackEnd - Spring/Recrutamento:/app" `
  -v pacto-maven-cache:/root/.m2 `
  -w /app `
  maven:3.9.9-eclipse-temurin-21 `
  mvn test
```

Resultado esperado:

```text
Failures: 0
Errors: 0
BUILD SUCCESS
```

## Testes do frontend

Os testes do frontend cobrem:

- chamadas HTTP de vagas;
- atualização de status e feedback;
- estado inicial da autenticação;
- login;
- persistência da sessão;
- logout;
- salvamento da avaliação;
- estado do botão após salvar;
- reativação do botão após alteração.

Crie o volume de dependências uma vez:

```powershell
docker volume create pacto-node-modules
```

Execute os testes:

```powershell
docker run --rm `
  -v "${PWD}/FrontEnd - Angular/Recrutamento:/app" `
  -v pacto-node-modules:/app/node_modules `
  -w /app `
  node:24-alpine `
  sh -c "if [ ! -d node_modules/@angular ]; then npm ci; fi && npm test -- --watch=false"
```

Resultado esperado:

```text
Test Files passed
Tests passed
```

## Responsividade e UX

A interface foi adaptada para diferentes tamanhos de tela.

Foram aplicadas práticas como:

- navegação responsiva;
- formulários organizados;
- estados de carregamento;
- mensagens de erro e sucesso;
- botões desabilitados durante operações;
- identificação visual de notificações não lidas;
- feedback visual após candidatura ou avaliação;
- textos amigáveis para os status;
- prevenção de ações inválidas;
- layout adaptado para celulares e tablets.

## Segurança

As principais medidas aplicadas foram:

- autenticação com JWT;
- senhas armazenadas com hash;
- autorização por perfil no backend;
- API stateless;
- validação dos dados recebidos;
- proteção das operações administrativas;
- validação da propriedade das notificações;
- prevenção de candidaturas duplicadas.

## Decisões técnicas

- Identificadores das entidades utilizando `Long`.
- Frontend e backend separados.
- DTOs para entrada e saída de dados.
- Regras de negócio concentradas nos services.
- Persistência com Spring Data JPA.
- Migrations com Flyway.
- Filtros de vagas implementados com Specifications.
- Comunicação entre frontend e backend por API REST.
- Execução completa disponibilizada com Docker Compose.

## Fluxo de demonstração

### Candidato

1. Entre com o usuário candidato.
2. Acesse **Vagas**.
3. Pesquise uma vaga.
4. Consulte os requisitos e o tempo mínimo.
5. Clique em **Candidatar-se**.
6. Acesse **Candidaturas**.
7. Acesse **Notificações** para visualizar atualizações e feedbacks.

### Administrador

1. Entre com o usuário administrador.
2. Acesse **Vagas**.
3. Cadastre, edite ou exclua uma vaga.
4. Acesse **Candidatos**.
5. Selecione uma vaga.
6. Filtre os candidatos pelo tempo de empresa.
7. Atualize o status e informe um feedback.
8. Salve a avaliação.
9. O candidato receberá uma notificação.

## Observações

- O filtro textual de vagas pesquisa título, descrição e requisitos.
- A avaliação de candidatos possui filtro por tempo de empresa.
- Os dados permanecem armazenados enquanto o volume do PostgreSQL não for removido.
- O projeto atende aos requisitos funcionais, bônus e diferenciais propostos no desafio.

## Autor

Desenvolvido por André Demetrio Queiroz Rapela para o desafio técnico Full Stack Java e Angular.
