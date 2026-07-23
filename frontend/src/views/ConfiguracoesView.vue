<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import AppShell from '../components/AppShell.vue'

const router = useRouter()
const auth = useAuthStore()
</script>

<template>
  <AppShell>
    <p class="section-title">Configurações</p>
    <p class="import-note">Área do administrador master — acessos, permissões e ajustes gerais do sistema.</p>

    <div class="config-grid">
      <div class="card config-card" @click="router.push({name:'usuarios'})">
        <div class="config-icon">👤</div>
        <p class="config-title">Usuários</p>
        <p class="config-desc">Criar acessos, definir papéis (Diretoria, Administrativo, Marketing), desativar ou redefinir senhas.</p>
      </div>

      <div class="card config-card" @click="router.push({name:'usuarios'})">
        <div class="config-icon">🔑</div>
        <p class="config-title">Minha conta</p>
        <p class="config-desc">Logado como <b>{{ auth.username }}</b> ({{ auth.role }}). Use "Trocar minha senha" no menu lateral.</p>
      </div>

      <div class="card config-card disabled">
        <div class="config-icon">🗄️</div>
        <p class="config-title">Backup &amp; dados</p>
        <p class="config-desc">Em breve — exportação de dados e backup do banco.</p>
      </div>
    </div>
  </AppShell>
</template>

<style scoped>
.import-note{font-size:11.5px;color:var(--ink-soft);margin:-6px 0 20px;}
.config-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:16px;}
@media (max-width:820px){ .config-grid{grid-template-columns:1fr;} }
.config-card{cursor:pointer;transition:box-shadow .15s ease;}
.config-card:hover{box-shadow:0 2px 6px rgba(27,36,54,0.1), 0 8px 20px rgba(27,36,54,0.1);}
.config-card.disabled{cursor:default;opacity:0.55;}
.config-card.disabled:hover{box-shadow:var(--shadow);}
.config-icon{font-size:22px;margin-bottom:10px;}
.config-title{font-family:'Source Serif 4',serif;font-weight:600;font-size:16px;margin:0 0 6px;}
.config-desc{font-size:12.5px;color:var(--ink-soft);margin:0;line-height:1.5;}
</style>
