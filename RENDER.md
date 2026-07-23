# Guia de Deploy — Render.com

Esse é o deploy rápido/temporário, enquanto o servidor Windows da empresa não
está pronto. Quando chegar a hora, use o `deploy/DEPLOY.md` (IIS) — o código
já está preparado pros dois.

## O erro que você teve

O Render (assim como qualquer serviço de hospedagem de verdade) precisa
**construir a aplicação a partir do código-fonte**, não de um arquivo `.zip`
enviado direto. Por isso apareceram aqueles bytes estranhos na tela — o
Render só devolveu o conteúdo cru do zip, sem processar nada.

O jeito certo é: **conectar o Render ao repositório do GitHub** que você já
subiu (não fazer upload manual de arquivo nenhum).

---

## Passo a passo

### 1. Confirme que o código está no GitHub
Se você já seguiu a conversa anterior, o repositório já deve estar lá. Se
não, volte no `README.md` (seção "Como subir") e faça o `git push` primeiro.

### 2. Crie a conta no Render
Acesse **render.com** → crie uma conta (dá pra usar login do GitHub direto,
o que já facilita a conexão).

### 3. Suba tudo de uma vez com o Blueprint
Este projeto já vem com um arquivo `render.yaml` na raiz, que descreve os
3 serviços necessários (banco, backend, frontend) prontos.

1. No painel do Render, clique em **"New +"** → **"Blueprint"**.
2. Escolha o repositório do GitHub (`controle-dae-fullstack` ou o nome que
   você deu).
3. O Render vai ler o `render.yaml` sozinho e mostrar os 3 serviços que vai
   criar:
   - `controle-dae-db` (banco PostgreSQL)
   - `controle-dae-backend` (API Java, via Docker)
   - `controle-dae-frontend` (site Vue)
4. Clique em **"Apply"** pra criar tudo.

### 4. Defina a senha inicial do admin
O Render vai pedir (ou você define depois em **Environment** do serviço
`controle-dae-backend`) a variável `ADMIN_SENHA_INICIAL` — coloque uma senha
segura, essa será a senha do usuário `admin` na primeira vez que o sistema
subir.

### 5. Conecte o frontend ao backend
Isso precisa de 2 cliques manuais, porque o Render só sabe a URL do backend
depois que ele é criado pela primeira vez:

1. Abra o serviço `controle-dae-backend` no painel do Render e copie a URL
   pública dele (algo como `https://controle-dae-backend.onrender.com`).
2. Abra o serviço `controle-dae-frontend` → **Environment** → edite a
   variável `VITE_API_BASE_URL` colocando essa URL **+ `/api`**, ex:
   `https://controle-dae-backend.onrender.com/api`
3. Clique em **"Manual Deploy" → "Clear build cache & deploy"** no
   `controle-dae-frontend` pra ele recompilar já apontando pro backend certo.

### 6. Acesse
Abra a URL pública do `controle-dae-frontend` (ex:
`https://controle-dae-frontend.onrender.com`) — deve aparecer a tela de
login.

- Usuário: `admin`
- Senha: a que você definiu em `ADMIN_SENHA_INICIAL`

---

## Sobre o plano gratuito do Render

- Serviços no plano **free** "dormem" depois de um tempo sem uso — a primeira
  requisição depois disso demora uns 30-60 segundos pra "acordar". Normal,
  não é erro.
- O banco Postgres gratuito do Render expira depois de 90 dias (o Render
  avisa por e-mail antes). Pra uso contínuo de verdade, será preciso um
  plano pago ou migrar pro servidor da empresa.

---

## Diferenças entre o deploy do Render e o do servidor da empresa (IIS)

| | Render (agora) | Servidor da empresa (depois) |
|---|---|---|
| Banco de dados | PostgreSQL (gerenciado pelo Render) | SQL Server |
| Backend | Container Docker | Processo Java rodando como Serviço do Windows |
| Frontend | Site estático do Render | IIS |
| Configuração | `render.yaml` + `application-render.properties` | `web.config` + `application.properties` |

O código já tem os dois caminhos prontos — quando for migrar pro servidor da
empresa, é só seguir o `deploy/DEPLOY.md`, sem precisar mudar nada no código.

**Atenção:** os dados lançados no Render (PostgreSQL) **não são migrados
automaticamente** pro SQL Server depois — são bancos diferentes. Se quiser
levar os dados de teste do Render pro banco final, me avise que preparo um
jeito de exportar/importar.
