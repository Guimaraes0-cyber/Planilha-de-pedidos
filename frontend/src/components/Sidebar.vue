<script setup>
import { useAuthStore } from '../stores/auth'
import { useRouter, useRoute } from 'vue-router'
import authApi from '../services/auth'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()

async function trocarMinhaSenha() {
  const atual = prompt('Sua senha atual:')
  if (!atual) return
  const nova = prompt('Nova senha:')
  if (!nova) return
  try {
    await authApi.trocarSenha(atual, nova)
    alert('Senha alterada com sucesso.')
  } catch (e) {
    alert(e.response?.data || 'Não foi possível trocar a senha.')
  }
}

const LOJAS = [
  { code: 'RM', shopping: 'Shopping RioMar', color: 'var(--store-rm)' },
  { code: 'IGT', shopping: 'Shopping Iguatemi', color: 'var(--store-igt)' },
  { code: 'CLZ', shopping: 'Shopping Via Bosque', color: 'var(--store-clz)' }
]

function isActive(name, param) {
  if (param) return route.name === name && route.params.loja === param
  return route.name === name
}

function logout() {
  auth.logout()
  router.push({ name: 'login' })
}
</script>

<template>
  <div class="sidebar">
    <div class="sidebar-label" style="display:flex;justify-content:space-between;">
      <span>{{ auth.role }}</span>
      <span @click="logout" style="cursor:pointer;text-decoration:underline;">Sair</span>
    </div>
    <div class="sidebar-item" @click="trocarMinhaSenha" style="cursor:pointer;">
      <span class="sidebar-icon">🔑</span><span>Trocar minha senha</span>
    </div>

    <router-link v-if="auth.perms.overview" :to="{name:'overview'}" class="sidebar-item" :class="{active: isActive('overview')}">
      <span class="sidebar-icon">🏢</span><span>Visão Geral</span>
    </router-link>

    <template v-if="auth.perms.lojas">
      <div class="sidebar-label">Lojas</div>
      <router-link v-for="l in LOJAS" :key="l.code" :to="{name:'loja', params:{loja:l.code}}"
        class="sidebar-item" :class="{active: isActive('loja', l.code)}">
        <span class="sidebar-icon" :style="{color: isActive('loja', l.code) ? '#fff' : l.color}">●</span>
        <span>{{ l.shopping }}</span>
      </router-link>
    </template>

    <template v-if="auth.perms.dae">
      <div class="sidebar-divider"></div>
      <div class="sidebar-label">Pagamentos de DAE</div>
      <router-link :to="{name:'dae-overview'}" class="sidebar-item" :class="{active: isActive('dae-overview')}">
        <span class="sidebar-icon">💳</span><span>DAE Visão Geral</span>
      </router-link>
      <router-link v-for="l in LOJAS" :key="'dae-'+l.code" :to="{name:'dae-loja', params:{loja:l.code}}"
        class="sidebar-item" :class="{active: isActive('dae-loja', l.code)}">
        <span class="sidebar-icon" :style="{color: isActive('dae-loja', l.code) ? '#fff' : l.color}">●</span>
        <span>DAE {{ l.shopping.replace('Shopping ', '') }}</span>
      </router-link>
    </template>

    <template v-if="auth.perms.brindes">
      <div class="sidebar-divider"></div>
      <div class="sidebar-label">Marketing</div>
      <router-link :to="{name:'brindes'}" class="sidebar-item" :class="{active: isActive('brindes')}">
        <span class="sidebar-icon">🎁</span><span>Estoque de Brindes</span>
      </router-link>
    </template>

    <template v-if="auth.perms.usuarios">
      <div class="sidebar-divider"></div>
      <div class="sidebar-label">Configurações</div>
      <router-link :to="{name:'configuracoes'}" class="sidebar-item" :class="{active: isActive('configuracoes')}">
        <span class="sidebar-icon">⚙️</span><span>Configurações</span>
      </router-link>
      <router-link :to="{name:'usuarios'}" class="sidebar-item" :class="{active: isActive('usuarios')}">
        <span class="sidebar-icon">👤</span><span>Usuários</span>
      </router-link>
    </template>
  </div>
</template>

<style scoped>
.sidebar{
  width:200px;flex-shrink:0;background:var(--paper-card);border-radius:12px;
  box-shadow:var(--shadow);padding:14px 10px;position:sticky;top:20px;
}
.sidebar-label{font-size:10.5px;text-transform:uppercase;letter-spacing:0.8px;color:var(--ink-soft);padding:8px 12px 4px;}
.sidebar-item{
  display:flex;align-items:center;gap:9px;padding:10px 12px;border-radius:8px;
  cursor:pointer;font-size:13px;font-weight:600;color:var(--ink-soft);margin-bottom:2px;
  text-decoration:none;
}
.sidebar-item:hover{background:#F5F6F8;color:var(--ink);}
.sidebar-item.active{background:var(--brand);color:#fff;}
.sidebar-icon{width:16px;text-align:center;flex-shrink:0;}
.sidebar-divider{height:1px;background:var(--line);margin:10px 4px;}
@media (max-width:820px){
  .sidebar{width:100%;position:static;display:flex;flex-wrap:wrap;gap:4px;}
  .sidebar-label{display:none;}
}
</style>
