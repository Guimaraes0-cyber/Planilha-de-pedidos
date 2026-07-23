<script setup>
import { ref, computed, onMounted } from 'vue'
import AppShell from '../components/AppShell.vue'
import BrindeModal from '../components/BrindeModal.vue'
import brindesApi from '../services/brindes'
import { fmtDateBR } from '../services/utils'

const itens = ref([])
const estoque = ref({})
const movimentos = ref([])
const carregando = ref(true)

const showModal = ref(false)
const itemPreset = ref(null)
const showHistorico = ref(false)
const itemHistorico = ref(null)
const historicoMovs = ref([])

async function carregar() {
  carregando.value = true
  itens.value = await brindesApi.itens()
  estoque.value = await brindesApi.estoque()
  movimentos.value = (await brindesApi.movimentos()).slice(0, 15)
  carregando.value = false
}
onMounted(carregar)

function ultimaMov(item) {
  return null // detalhe completo disponível no histórico; lista resumida já mostra "movimentos recentes" abaixo
}

function abrirNovo(item) { itemPreset.value = item || null; showModal.value = true }
async function salvar(mov) {
  await brindesApi.registrar(mov)
  showModal.value = false
  await carregar()
}
async function abrirHistorico(item) {
  itemHistorico.value = item
  historicoMovs.value = await brindesApi.historico(item)
  showHistorico.value = true
}
async function excluirMov(id) {
  if (!confirm('Excluir esta movimentação?')) return
  await brindesApi.excluir(id)
  historicoMovs.value = await brindesApi.historico(itemHistorico.value)
  await carregar()
}
</script>

<template>
  <AppShell>
    <template #actions>
      <button class="btn btn-primary" @click="abrirNovo(null)">+ Registrar movimentação</button>
    </template>

    <div v-if="carregando" class="muted">Carregando…</div>
    <template v-else>
      <p class="section-title">Estoque de Brindes</p>
      <p class="import-note">Área da equipe de marketing — controle o que entra e sai de cada item pra manter o estoque organizado.</p>

      <div class="table-wrap" style="margin-bottom:26px;">
        <table>
          <thead><tr><th>Item</th><th>Estoque atual</th><th></th></tr></thead>
          <tbody>
            <tr v-for="item in itens" :key="item">
              <td>{{ item }}</td>
              <td class="num" :style="{fontWeight:600, color: estoque[item] < 0 ? 'var(--red)' : ''}">{{ estoque[item] ?? 0 }}</td>
              <td class="row-actions">
                <button class="btn btn-ghost btn-sm" @click="abrirHistorico(item)">Ver histórico</button>
                <button class="btn btn-primary btn-sm" @click="abrirNovo(item)">+ Movimentação</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <p class="section-title">Movimentações recentes</p>
      <div class="table-wrap">
        <table>
          <thead><tr><th>Data</th><th>Item</th><th>Tipo</th><th>Qtd.</th><th>Destino</th><th>Observações</th></tr></thead>
          <tbody>
            <tr v-if="!movimentos.length"><td colspan="6" class="muted" style="padding:24px;text-align:center;">Nenhuma movimentação lançada.</td></tr>
            <tr v-for="m in movimentos" :key="m.id">
              <td>{{ fmtDateBR(m.data) }}</td>
              <td>{{ m.item }}</td>
              <td><span class="stamp" :class="m.tipo === 'entrada' ? 'stamp-pago' : 'stamp-pendente'">{{ m.tipo === 'entrada' ? 'Entrada' : 'Saída' }}</span></td>
              <td class="num">{{ m.quantidade }}</td>
              <td>{{ m.destino || '—' }}</td>
              <td class="muted">{{ m.observacoes || '—' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>

    <BrindeModal :show="showModal" :itens="itens" :item-preset="itemPreset" @close="showModal=false" @save="salvar" />

    <div v-if="showHistorico" class="overlay open" @click.self="showHistorico=false">
      <div class="modal modal-lg">
        <p class="modal-title">Histórico — {{ itemHistorico }}</p>
        <p class="modal-sub">Estoque atual: <b>{{ estoque[itemHistorico] ?? 0 }}</b></p>
        <div class="table-wrap">
          <table>
            <thead><tr><th>Data</th><th>Tipo</th><th>Qtd.</th><th>Destino</th><th>Observações</th><th></th></tr></thead>
            <tbody>
              <tr v-if="!historicoMovs.length"><td colspan="6" class="muted" style="padding:24px;text-align:center;">Nenhuma movimentação ainda.</td></tr>
              <tr v-for="m in historicoMovs" :key="m.id">
                <td>{{ fmtDateBR(m.data) }}</td>
                <td><span class="stamp" :class="m.tipo === 'entrada' ? 'stamp-pago' : 'stamp-pendente'">{{ m.tipo === 'entrada' ? 'Entrada' : 'Saída' }}</span></td>
                <td class="num">{{ m.quantidade }}</td>
                <td>{{ m.destino || '—' }}</td>
                <td class="muted">{{ m.observacoes || '—' }}</td>
                <td><button class="btn btn-danger btn-sm" @click="excluirMov(m.id)">Excluir</button></td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="modal-actions">
          <button type="button" class="btn btn-ghost" @click="showHistorico=false">Fechar</button>
        </div>
      </div>
    </div>
  </AppShell>
</template>

<style scoped>
.import-note{font-size:11.5px;color:var(--ink-soft);margin:-6px 0 16px;}
.row-actions{display:flex;gap:6px;}
.overlay{position:fixed;inset:0;background:rgba(27,36,54,0.45);display:flex;align-items:flex-start;justify-content:center;padding:40px 16px;overflow-y:auto;z-index:50;}
.modal{background:#fff;border-radius:14px;max-width:560px;width:100%;padding:26px 28px 24px;box-shadow:0 20px 60px rgba(0,0,0,0.25);}
.modal-lg{max-width:720px;}
.modal-title{font-family:'Source Serif 4',serif;font-weight:600;font-size:19px;margin:0 0 4px;}
.modal-sub{color:var(--ink-soft);font-size:13px;margin:0 0 18px;}
.modal-actions{display:flex;justify-content:flex-end;gap:10px;margin-top:20px;}
</style>
