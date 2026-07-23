<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const username = ref('')
const senha = ref('')
const mostrarSenha = ref(false)
const erro = ref('')
const carregando = ref(false)

const auth = useAuthStore()
const router = useRouter()

async function handleSubmit() {
  erro.value = ''
  carregando.value = true
  try {
    await auth.login(username.value.trim(), senha.value)
    router.push({ name: 'overview' })
  } catch (e) {
    erro.value = e.response?.data || 'Usuário ou senha incorretos.'
  } finally {
    carregando.value = false
  }
}
</script>

<template>
  <div class="auth-wrap">
    <div class="auth-card">
      <p class="auth-title">Intimissimi &amp; Calzedonia</p>
      <p class="auth-sub">Dashboard de pedidos &middot; Área restrita</p>
      <form @submit.prevent="handleSubmit">
        <div class="auth-field">
          <label>Usuário</label>
          <input type="text" v-model="username" placeholder="Digite seu usuário" required autofocus>
        </div>
        <div class="auth-field" style="position:relative;">
          <label>Senha</label>
          <input :type="mostrarSenha ? 'text' : 'password'" v-model="senha" placeholder="Digite sua senha" required>
          <button type="button" @click="mostrarSenha = !mostrarSenha" class="eye-btn">👁</button>
        </div>
        <div v-if="erro" class="auth-error">{{ erro }}</div>
        <button type="submit" class="btn btn-primary" style="width:100%;padding:12px;" :disabled="carregando">
          {{ carregando ? 'Entrando…' : 'Entrar no Dashboard' }}
        </button>
      </form>
      <p class="auth-note">🔒 Acesso restrito &middot; Intimissimi &amp; Calzedonia</p>
    </div>
  </div>
</template>

<style scoped>
.auth-wrap{min-height:100vh;display:flex;align-items:center;justify-content:center;padding:24px;}
.auth-card{
  background:#fff;border-radius:16px;box-shadow:0 20px 60px rgba(27,36,54,0.15);
  padding:38px 34px;max-width:380px;width:100%;text-align:center;
}
.auth-title{font-family:'Source Serif 4',serif;font-weight:700;font-size:21px;color:var(--brand-dark);margin:0 0 4px;}
.auth-sub{color:var(--ink-soft);font-size:13px;margin:0 0 24px;}
.auth-field{text-align:left;margin-bottom:14px;position:relative;}
.auth-field label{font-size:12px;font-weight:600;color:var(--ink-soft);display:block;margin-bottom:4px;}
.auth-field input{width:100%;padding:10px 12px;font-size:14px;border-radius:8px;border:1px solid var(--line);}
.eye-btn{position:absolute;right:10px;top:32px;background:none;border:none;cursor:pointer;color:var(--ink-soft);font-size:15px;}
.auth-error{color:var(--red);font-size:12.5px;margin:-6px 0 12px;text-align:left;}
.auth-note{font-size:11px;color:var(--ink-soft);margin-top:18px;}
button:disabled{opacity:0.6;cursor:default;}
</style>
