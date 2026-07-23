<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppShell from '../components/AppShell.vue'
import pedidosApi from '../services/pedidos'
import { fmtBRL, fmtDateBR, statusVencimento, statusLabel, statusSelagem } from '../services/utils'

const LOJAS = [
  { code: 'RM', nome: 'Intimissimi Riomar', color: 'var(--store-rm)' },
  { code: 'IGT', nome: 'Intimissimi Iguatemi', color: 'var(--store-igt)' },
  { code: 'CLZ', nome: 'Calzedonia Via Bosque', color: 'var(--store-clz)' }
]

const router = useRouter()
const pedidosPorLoja = ref({ RM: [], IGT: [], CLZ: [] })
const carregando = ref(true)

onMounted(async () => {
  for (const l of LOJAS) pedidosPorLoja.value[l.code] = await pedidosApi.listar(l.code)
  carregando.value = false
})

function statsLoja(code) {
  const list = pedidosPorLoja.value[code]
  const pendentes = list.filter(p => p.status !== 'pago')
  const totalPendente = pendentes.reduce((s, p) => s + (Number(p.valorDae) || 0), 0)
  const comErro = list.filter(p => statusSelagem(p) === 'erro')
  const proximos = pendentes.filter(p => p.vencimento).sort((a, b) => a.vencimento.localeCompare(b.vencimento))
  return { totalPendente, countPendente: pendentes.length, comErro, proximo: proximos[0] || null }
}

const cards = computed(() => LOJAS.map(l => ({ ...l, ...statsLoja(l.code) })))

const globalPendente = computed(() => cards.value.reduce((s, c) => s + c.totalPendente, 0))
const globalErro = computed(() => {
  let all = []
  LOJAS.forEach(l => statsLoja(l.code).comErro.forEach(p => all.push({ ...p, _loja: l.code, _lojaNome: l.nome })))
  return all
})
const thisMonth = new Date().toISOString().slice(0, 7)
const globalPagoMes = computed(() => LOJAS.reduce((s, l) =>
  s + pedidosPorLoja.value[l.code].filter(p => p.status === 'pago' && p.dataPagamento && p.dataPagamento.slice(0,7) === thisMonth)
    .reduce((a, p) => a + (Number(p.valorPago ?? p.valorDae) || 0), 0), 0))

const proximosVencimentos = computed(() => {
  let all = []
  LOJAS.forEach(l => pedidosPorLoja.value[l.code].filter(p => p.status !== 'pago' && p.vencimento).forEach(p => all.push({ ...p, _loja: l.code, _lojaNome: l.nome })))
  all.sort((a, b) => a.vencimento.localeCompare(b.vencimento))
  return all.slice(0, 10)
})

function diferenca(p) { return (Number(p.valorDae) || 0) - (Number(p.valorPago) || 0) }
</script>

<template>
  <AppShell>
    <div v-if="carregando" class="muted">Carregando…</div>
    <template v-else>
      <p class="section-title">DAE — Visão Geral</p>
      <div class="store-grid">
        <div v-for="c in cards" :key="c.code" class="card store-card" :style="{borderLeft: '4px solid ' + c.color}" @click="router.push({name:'dae-loja', params:{loja:c.code}})">
          <p class="store-name">{{ c.nome }}</p>
          <div class="store-loja-code">{{ c.code }} &middot; {{ c.countPendente }} pendente(s){{ c.comErro.length ? ' · '+c.comErro.length+' p/ selagem' : '' }}</div>
          <div class="store-total">{{ fmtBRL(c.totalPendente) }}</div>
          <div class="store-total-label">total de DAE pendente</div>
          <div class="store-next">
            Próximo vencimento:
            <template v-if="c.proximo"><b>{{ fmtDateBR(c.proximo.vencimento) }}</b> &middot; NF {{ c.proximo.nfNumero || '—' }}
              <span class="stamp" :class="'stamp-'+statusVencimento(c.proximo)">{{ statusLabel(statusVencimento(c.proximo)) }}</span>
            </template>
            <template v-else>Nenhum vencimento pendente</template>
          </div>
        </div>
      </div>

      <div class="summary-bar">
        <div class="summary-chip">
          <div class="summary-chip-label">Total geral pendente (3 lojas)</div>
          <div class="summary-chip-value" style="color:var(--red);">{{ fmtBRL(globalPendente) }}</div>
        </div>
        <div class="summary-chip">
          <div class="summary-chip-label">Pago neste mês (3 lojas)</div>
          <div class="summary-chip-value" style="color:var(--green);">{{ fmtBRL(globalPagoMes) }}</div>
        </div>
        <div class="summary-chip">
          <div class="summary-chip-label">Notas que precisam de selagem</div>
          <div class="summary-chip-value" :style="{color: globalErro.length ? 'var(--red)' : ''}">{{ globalErro.length }}</div>
        </div>
      </div>

      <template v-if="globalErro.length">
        <p class="section-title">Notas com diferença no DAE (conferir selagem)</p>
        <div class="table-wrap" style="margin-bottom:22px;">
          <table>
            <thead><tr><th>Loja</th><th>NF</th><th>Pedido</th><th>DAE esperado</th><th>DAE pago</th><th>Diferença</th><th>Status</th></tr></thead>
            <tbody>
              <tr v-for="p in globalErro" :key="p.id">
                <td>{{ p._lojaNome }}</td><td>{{ p.nfNumero || '—' }}</td><td>{{ p.pedido || '—' }}</td>
                <td class="num">{{ fmtBRL(p.valorDae) }}</td><td class="num">{{ fmtBRL(p.valorPago) }}</td>
                <td class="num">{{ fmtBRL(diferenca(p)) }}</td>
                <td><span class="stamp stamp-vencido">Precisa selagem</span></td>
              </tr>
            </tbody>
          </table>
        </div>
      </template>

      <p class="section-title">Próximos vencimentos</p>
      <div class="table-wrap">
        <table>
          <thead><tr><th>Loja</th><th>NF</th><th>Pedido</th><th>Valor DAE</th><th>Vencimento</th><th>Status</th></tr></thead>
          <tbody>
            <tr v-if="!proximosVencimentos.length"><td colspan="6" class="muted" style="padding:24px;text-align:center;">Tudo pago ou nada lançado ainda.</td></tr>
            <tr v-for="p in proximosVencimentos" :key="p.id">
              <td>{{ p._lojaNome }}</td><td>{{ p.nfNumero || '—' }}</td><td>{{ p.pedido || '—' }}</td>
              <td class="num">{{ fmtBRL(p.valorDae) }}</td><td>{{ fmtDateBR(p.vencimento) }}</td>
              <td><span class="stamp" :class="'stamp-'+statusVencimento(p)">{{ statusLabel(statusVencimento(p)) }}</span></td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>
  </AppShell>
</template>

<style scoped>
.store-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:16px;margin-bottom:22px;}
@media (max-width:820px){ .store-grid{grid-template-columns:1fr;} }
.store-card{cursor:pointer;}
.store-name{font-family:'Source Serif 4',serif;font-weight:600;font-size:19px;margin:0 0 2px;}
.store-loja-code{color:var(--ink-soft);font-size:12px;letter-spacing:0.6px;text-transform:uppercase;}
.store-total{font-family:'IBM Plex Mono',monospace;font-weight:600;font-size:26px;margin:14px 0 4px;}
.store-total-label{color:var(--ink-soft);font-size:12px;}
.store-next{margin-top:14px;padding-top:12px;border-top:1px dashed var(--line);font-size:12.5px;color:var(--ink-soft);}
.summary-bar{display:flex;gap:16px;flex-wrap:wrap;margin-bottom:22px;}
.summary-chip{background:var(--paper-card);border-radius:var(--radius);box-shadow:var(--shadow);padding:14px 20px;flex:1;min-width:180px;}
.summary-chip-label{font-size:12px;color:var(--ink-soft);margin-bottom:6px;}
.summary-chip-value{font-family:'IBM Plex Mono',monospace;font-weight:600;font-size:20px;}
</style>
