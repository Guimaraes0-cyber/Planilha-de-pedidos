# Guia de Deploy — Windows Server + IIS

Controle de Pedidos & DAE — Intimissimi & Calzedonia

Esta aplicação tem 3 partes:
1. **Frontend** (Vue.js) — arquivos estáticos servidos pelo IIS.
2. **Backend** (Spring Boot / Java) — roda como um processo Java separado, na porta 8080.
3. **Banco de dados** (SQL Server) — as tabelas são criadas automaticamente na primeira vez que o backend sobe.

O IIS serve o site (frontend) e encaminha as chamadas de API (`/api/...`) pro processo Java, através do módulo **URL Rewrite + Application Request Routing (ARR)**.

---

## Pré-requisitos no servidor

- **Java 17** instalado (JDK, não só JRE) — para rodar o backend.
- **SQL Server** já instalado e acessível (pode ser no mesmo servidor ou em outro).
- **IIS** com os módulos:
  - **URL Rewrite** — https://www.iis.net/downloads/microsoft/url-rewrite
  - **Application Request Routing (ARR) 3.0** — https://www.iis.net/downloads/microsoft/application-request-routing
- Depois de instalar o ARR, habilite o proxy uma vez:
  `IIS Manager` → (clique no nome do servidor, nível raiz) → `Application Request Routing Cache` → `Server Proxy Settings` (painel à direita) → marque **"Enable proxy"** → Aplicar.

---

## Passo 1 — Banco de dados SQL Server

1. Peça ao DBA/TI para criar um banco vazio (ex: `ControleDae`) e um usuário com permissão de leitura/escrita/DDL nesse banco.
2. Não precisa criar tabelas manualmente — o backend faz isso sozinho na primeira vez que sobe (usando Flyway, que já vem configurado). Se preferirem revisar antes, o script está em
   `backend/src/main/resources/db/migration/V1__schema_inicial.sql`.

---

## Passo 2 — Configurar e compilar o backend

1. Abra `backend/src/main/resources/application.properties` e preencha com os dados reais:
   ```properties
   spring.datasource.url=jdbc:sqlserver://HOST:1433;databaseName=ControleDae;encrypt=true;trustServerCertificate=true
   spring.datasource.username=SEU_USUARIO
   spring.datasource.password=SUA_SENHA

   app.jwt.secret=TROQUE_POR_UMA_STRING_ALEATORIA_LONGA_E_SECRETA
   app.admin.username=admin
   app.admin.senha-inicial=Adm@2026
   ```
   > **Importante:** troque `app.jwt.secret` por um valor único e secreto (pode gerar uma string aleatória de 40+ caracteres). Troque também `app.admin.senha-inicial` — essa é a senha do usuário master criado automaticamente, então troque **por uma senha forte antes de subir em produção**, ou troque pelo próprio dashboard assim que logar (menu lateral → "Trocar minha senha").

2. Compile o projeto (precisa de Maven — `mvn` — instalado, ou use o `mvnw` incluso se houver):
   ```
   cd backend
   mvn clean package -DskipTests
   ```
   Isso gera o arquivo `target/controle-dae.jar`.

3. Copie esse `.jar` pra uma pasta no servidor, ex: `C:\apps\controle-dae\controle-dae.jar`.

### Rodar o backend como um Serviço do Windows (recomendado)

Pra não depender de deixar uma janela de terminal aberta, use o **WinSW** (gratuito, da comunidade .NET) pra rodar o `.jar` como um serviço de verdade:

1. Baixe o WinSW: https://github.com/winsw/winsw/releases (pegue o `WinSW-x64.exe`)
2. Renomeie pra `controle-dae-service.exe` e coloque na mesma pasta do `.jar`.
3. Crie um arquivo `controle-dae-service.xml` do lado, com:
   ```xml
   <service>
     <id>controle-dae-backend</id>
     <name>Controle DAE - Backend</name>
     <description>API do Controle de Pedidos e DAE</description>
     <executable>java</executable>
     <arguments>-jar "C:\apps\controle-dae\controle-dae.jar"</arguments>
     <log mode="roll"></log>
   </service>
   ```
4. No PowerShell (como administrador), na pasta do serviço:
   ```
   .\controle-dae-service.exe install
   .\controle-dae-service.exe start
   ```
5. Confirme que subiu acessando no navegador do próprio servidor:
   `http://localhost:8080/api/auth/login` (deve responder erro 405/400, não "conexão recusada" — isso já confirma que o backend está no ar).

---

## Passo 3 — Compilar e publicar o frontend

1. No seu computador (ou em qualquer máquina com Node.js), dentro da pasta `frontend`:
   ```
   npm install
   npm run build
   ```
   Isso gera a pasta `frontend/dist`.

2. Copie **todo o conteúdo** de `frontend/dist` pra pasta do site no IIS (ex: `C:\inetpub\wwwroot\controle-dae`).

3. Copie o arquivo `deploy/web.config` (incluso nesse pacote) pra dentro dessa mesma pasta, junto com `index.html`.

4. No **IIS Manager**:
   - Clique com o botão direito em "Sites" → **Adicionar site**.
   - Nome: `Controle DAE`
   - Caminho físico: a pasta onde você copiou os arquivos (`C:\inetpub\wwwroot\controle-dae`)
   - Porta: 80 (ou a que preferir)
   - Confirme.

5. Acesse pelo navegador: `http://SEU-SERVIDOR/` — deve aparecer a tela de login.

---

## Passo 4 — Primeiro acesso

- Usuário: o que você configurou em `app.admin.username` (padrão: `admin`)
- Senha: o que você configurou em `app.admin.senha-inicial`

Assim que entrar, vá em **"Trocar minha senha"** no menu lateral e troque pela senha definitiva. Depois, em **"Usuários"** (só o admin master vê essa opção), você pode cadastrar os acessos de diretoria, administrativo e marketing.

---

## Checklist de segurança antes de ir pra produção

- [ ] Trocou `app.jwt.secret` por um valor único e secreto
- [ ] Trocou a senha inicial do admin master
- [ ] `spring.datasource.password` não está em texto puro num lugar acessível publicamente (considere variável de ambiente em vez do arquivo `.properties`, se possível)
- [ ] CORS em `SecurityConfig.java` está restrito à URL real do frontend (troque `setAllowedOriginPatterns(List.of("*"))` pela URL exata, ex: `http://intranet.empresa.com`)
- [ ] HTTPS configurado no IIS (certificado interno ou público), se o acesso sair da rede interna

---

## Solução de problemas comuns

**Tela de login abre, mas dá erro ao tentar entrar (network error / CORS)**
→ Confira se o backend está rodando (`http://localhost:8080` no próprio servidor) e se o `web.config` está mesmo na pasta do site com a regra de proxy pro `/api/`.

**"Enable proxy" não aparece no ARR**
→ Precisa estar no nível do servidor (não dentro de um site específico) no IIS Manager.

**Erro de conexão com o SQL Server**
→ Confira usuário/senha/porta em `application.properties`. Se o SQL Server usar autenticação do Windows em vez de usuário/senha, fale com o TI — a configuração muda (precisa de driver com integratedSecurity).

**Quero migrar os dados que já tínhamos no dashboard antigo (HTML)**
→ Não migramos automaticamente, pois vocês pediram pra começar do zero. Se mudarem de ideia, é possível escrever um script de importação lendo o histórico e inserindo via API — me avise.
