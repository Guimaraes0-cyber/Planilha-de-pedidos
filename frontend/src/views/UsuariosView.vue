<script setup>
import { ref, onMounted } from 'vue'
import AppShell from '../components/AppShell.vue'
import usuariosApi from '../services/usuarios'

const usuarios = ref([])
const carregando = ref(true)
const showModal = ref(false)
const erro = ref('')

const form = ref(blank())
function blank() { return { username: '', senha: '', role: 'ADMINISTRATIVO' } }

async function carregar() {
  carregando.value = true
  usuarios.value = await usuariosApi.listar()
  carregando.value = false
}
onMounted(carregar)

function abrirNovo() { form.value = blank(); erro.value = ''; showModal.value = true }

async function salvar() {
  erro.value = ''
  try {
    await usuariosApi.criar(form.value)
    showModal.value = false
    await carregar()
  } catch (e) {
    erro.value = e.response?.data || 'Não foi possível criar o usuário.'
  }
}
async function desativar(u) {
  if (!confirm(`Desativar o acesso de "${u.username}"?`)) return
  await usuariosApi.desativar(u.id)
  await carregar()
}
async function redefinirSenha(u) {
  const nova = prompt(`Nova senha para "${u.username}":`)
  if (!nova) return
  await usuariosApi.redefinirSenha(u.id, nova)
  alert('Senha redefinida.')
}
</script>

<template>
  <AppShell>
    <template #actions>
      <button class="btn btn-primary" @click="abrirNovo">+ Novo usuário</button>
    </template>

    <div v-if="carregando" class="muted">Carregando…</div>
    <template v-else>
      <p class="section-title">Usuários</p>
      <p class="import-note">Gerencie quem tem acesso ao dashboard e com qual papel (permissões).</p>

      <div class="table-wrap">
        <table>
          <thead><tr><th>Usuário</th><th>Papel</th><th>Status</th><th></th></tr></thead>
          <tbody>
            <tr v-for="u in usuarios" :key="u.id">
              <td>{{ u.username }}</td>
              <td>{{ u.role }}</td>
              <td><span class="stamp" :class="u.ativo ? 'stamp-pago' : 'stamp-vencido'">{{ u.ativo ? 'Ativo' : 'Desativado' }}</span></td>
              <td class="row-actions">
                <button class="btn btn-ghost btn-sm" @click="redefinirSenha(u)">Redefinir senha</button>
                <button v-if="u.ativo" class="btn btn-danger btn-sm" @click="desativar(u)">Desativar</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>

    <div v-if="showModal" class="overlay open" @click.self="showModal=false">
      <div class="modal">
        <p class="modal-title">Novo usuário</p>
        <form @submit.prevent="salvar">
          <div class="form-grid">
            <div class="field span-2"><label>Usuário</label><input type="text" v-model="form.username" required></div>
            <div class="field span-2"><label>Senha</label><input type="password" v-model="form.senha" required></div>
            <div class="field span-2">
              <label>Papel</label>
              <select v-model="form.role">
                <option value="ADMIN">Admin (acesso master)</option>
                <option value="DIRETORIA">Diretoria (acesso total)</option>
                <option value="ADMINISTRATIVO">Administrativo (pedidos + DAE)</option>
                <option value="MARKETING">Marketing (só brindes)</option>
              </select>
            </div>
          </div>
          <div v-if="erro" class="auth-error">{{ erro }}</div>
          <div class="modal-actions">
            <button type="button" class="btn btn-ghost" @click="showModal=false">Cancelar</button>
            <button type="submit" class="btn btn-primary">Criar usuário</button>
          </div>
        </form>
      </div>
    </div>
  </AppShell>
</template>

<style scoped>
.import-note{font-size:11.5px;color:var(--ink-soft);margin:-6px 0 16px;}
.row-actions{display:flex;gap:6px;}
.overlay{position:fixed;inset:0;background:rgba(27,36,54,0.45);display:flex;align-items:flex-start;justify-content:center;padding:40px 16px;overflow-y:auto;z-index:50;}
.modal{background:#fff;border-radius:14px;max-width:420px;width:100%;padding:26px 28px 24px;box-shadow:0 20px 60px rgba(0,0,0,0.25);}
.modal-title{font-family:'Source Serif 4',serif;font-weight:600;font-size:19px;margin:0 0 4px;}
.form-grid{display:grid;grid-template-columns:1fr 1fr;gap:12px 14px;}
.field{display:flex;flex-direction:column;gap:4px;}
.field.span-2{grid-column:1/-1;}
.field label{font-size:12px;font-weight:600;color:var(--ink-soft);}
.auth-error{color:var(--red);font-size:12.5px;margin-top:8px;}
.modal-actions{display:flex;justify-content:flex-end;gap:10px;margin-top:20px;}
</style>
