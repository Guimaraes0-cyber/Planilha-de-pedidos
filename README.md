# Controle de Pedidos & DAE — Intimissimi & Calzedonia

Aplicação full-stack: **Spring Boot** (backend) + **SQL Server** (banco) + **Vue.js** (frontend), pronta pra rodar em **Windows Server com IIS**.

## Estrutura

```
backend/    → API Java Spring Boot (Maven)
frontend/   → Interface Vue.js (Vite)
deploy/     → web.config do IIS + guia de deploy (DEPLOY.md)
```

## Comece aqui

- Quer testar rápido, sem mexer no servidor da empresa ainda? Leia **`RENDER.md`** (deploy gratuito no Render.com).
- Vai direto pro servidor definitivo da empresa? Leia **`deploy/DEPLOY.md`** (Windows Server + IIS + SQL Server).

O código já vem pronto pros dois caminhos — dá pra testar no Render agora e migrar pro servidor da empresa depois sem reescrever nada.

## Resumo rápido (pra quem já manja)

**Backend:**
```
cd backend
# edite src/main/resources/application.properties com os dados do SQL Server
mvn clean package -DskipTests
java -jar target/controle-dae.jar
```

**Frontend:**
```
cd frontend
npm install
npm run build
# copie o conteúdo de dist/ + deploy/web.config pra pasta do site no IIS
```

## O que já vem pronto

- 3 papéis de acesso: **ADMIN** (master, gerencia usuários), **DIRETORIA** (acesso total),
  **ADMINISTRATIVO** (pedidos + DAE), **MARKETING** (só estoque de brindes)
- Usuário admin master criado automaticamente no primeiro start (configurável)
- Pedidos por loja (Riomar, Iguatemi, Calzedonia/Via Bosque) com cálculo automático de DAE (16%)
- Boletos de DAE por loja, com conciliação automática dos pedidos ao salvar
- Estoque de brindes com entradas/saídas e histórico
- Autenticação via JWT

## Antes de subir pro GitHub

Já dá pra subir com segurança — os arquivos de configuração usam variáveis de
ambiente pra qualquer dado sensível (senha do banco, chave JWT, senha do admin),
então nenhum segredo real fica escrito no código. Só fique de olho em:

- [ ] Se em algum momento você editar o `application.properties` colocando a
      senha real do SQL Server diretamente nele (em vez de usar variável de
      ambiente), **não** faça commit desse arquivo assim — prefira sempre
      configurar via variável de ambiente (`DB_PASSWORD`, `JWT_SECRET`, etc.)
      no próprio servidor.
- [ ] O `.gitignore` incluso já ignora `node_modules/`, `target/`, `dist/` e
      arquivos de IDE — não precisa (nem deve) commitar essas pastas.
- [ ] Se o repositório for público, considere se as regras de negócio (nomes
      das lojas, fornecedor, etc.) podem ficar visíveis publicamente — se não
      puder, crie o repositório como **privado**.

## Como subir

```
cd fullstack
git init
git add .
git commit -m "Versão inicial - Controle de Pedidos e DAE"
git branch -M main
git remote add origin https://github.com/SEU-USUARIO/SEU-REPOSITORIO.git
git push -u origin main
```


## O que NÃO veio pronto (próximos passos possíveis)

- Importação automática de PDF (DANFE) — o dashboard anterior em HTML tinha essa
  função rodando no navegador; pode ser portada pro Vue se for prioridade.
- Migração do histórico do dashboard anterior — combinado que começaríamos do zero.
- HTTPS/certificado — configure no IIS conforme a política de segurança da empresa.
