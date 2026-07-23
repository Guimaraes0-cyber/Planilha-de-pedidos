<script setup>
import { ref, onMounted, computed, nextTick } from 'vue'
import AppShell from '../components/AppShell.vue'
import pedidosApi from '../services/pedidos'
import { fmtBRL, fmtDateBR, daysUntil, categoria, CATEGORIA_CORES } from '../services/utils'
import Chart from 'chart.js/auto'

const LOJAS = [
  { code: 'RM', nome: 'Intimissimi Riomar', color: 'var(--store-rm)' },
  { code: 'IGT', nome: 'Intimissimi Iguatemi', color: 'var(--store-igt)' },
  { code: 'CLZ', nome: 'Calzedonia Via Bosque', color: 'var(--store-clz)' }
]

const pedidosPorLoja = ref({ RM: [], IGT: [], CLZ: [] })
const carregando = ref(true)
const chartCanvas = ref(null)
let chartInstance = null

onMounted(async () => {
  for (const l of LOJAS) {
    pedidosPorLoja.value[l.code] = await pedidosApi.listar(l.code)
  }
  carregando.value = false
  await nextTick()
  desenharGrafico()
})

const cards = computed(() => LOJAS.map(l => {
  const list = pedidosPorLoja.value[l.code]
  const valorRessuprimento = list.filter(p => categoria(p.pedido) === 'Ressuprimento').reduce((s, p) => s + (Number(p.valorFaturado) || 0), 0)
  const brindesRecebidos = list.filter(p => categoria(p.pedido) === 'Brinde' && p.recebimento).length
  return { ...l, totalPedidos: list.length, valorRessuprimento, brindesRecebidos }
}))

const totalPedidosGeral = computed(() => LOJAS.reduce((s, l) => s + pedidosPorLoja.value[l.code].length, 0))
const valorFaturadoGeral = computed(() => LOJAS.reduce((s, l) => s + pedidosPorLoja.value[l.code].reduce((a, p) => a + (Number(p.valorFaturado) || 0), 0), 0))
const brindesGeral = computed(() => LOJAS.reduce((s, l) => s + pedidosPorLoja.value[l.code].filter(p => categoria(p.pedido) === 'Brinde' && p.recebimento).length, 0))

const aguardandoEntrega = computed(() => {
  let all = []
  LOJAS.forEach(l => {
    pedidosPorLoja.value[l.code].forEach(p => {
      const cat = categoria(p.pedido)
      if (!p.recebimento && cat !== 'Sem pedido de venda') {
        all.push({ ...p, _loja: l.code, _lojaNome: l.nome })
      }
    })
  })
  all.sort((a, b) => (a.previsaoEntrega || 'zzzz').localeCompare(b.previsaoEntrega || 'zzzz'))
  return all
})

const breakdown = computed(() => {
  const counts = {}, valores = {}
  LOJAS.forEach(l => pedidosPorLoja.value[l.code].forEach(p => {
    const cat = categoria(p.pedido)
    counts[cat] = (counts[cat] || 0) + 1
    valores[cat] = (valores[cat] || 0) + (Number(p.valorFaturado) || 0)
  }))
  const labels = Object.keys(counts).sort((a, b) => valores[b] - valores[a])
  return { labels, counts, valores, colors: labels.map(l => CATEGORIA_CORES[l] || '#8B8FA3') }
})

function desenharGrafico() {
  if (chartInstance) chartInstance.destroy()
  if (!chartCanvas.value || !breakdown.value.labels.length) return
  chartInstance = new Chart(chartCanvas.value, {
    type: 'doughnut',
    data: {
      labels: breakdown.value.labels,
      datasets: [{ data: breakdown.value.labels.map(l => breakdown.value.counts[l]), backgroundColor: breakdown.value.colors, borderWidth: 2, borderColor: '#fff' }]
    },
    options: { plugins: { legend: { display: false } }, cutout: '65%' }
  })
}

async function marcarRecebido(pedido) {
  await pedidosApi.atualizar(pedido.id, { ...pedido, recebimento: new Date().toISOString().slice(0, 10) })
  pedidosPorLoja.value[pedido._loja] = await pedidosApi.listar(pedido._loja)
}
async function atualizarPrevisao(pedido, valor) {
  await pedidosApi.atualizar(pedido.id, { ...pedido, previsaoEntrega: valor || null })
  pedidosPorLoja.value[pedido._loja] = await pedidosApi.listar(pedido._loja)
}
</script>

<template>
  <AppShell>
    <div v-if="carregando" class="muted">Carregando…</div>
    <template v-else>
      <p class="section-title">Indicadores Gerais</p>
      <div class="store-grid">
        <div v-for="c in cards" :key="c.code" class="card store-card" :style="{borderLeft: '4px solid ' + c.color}">
          <p class="store-name">{{ c.nome }}</p>
          <div class="store-loja-code">{{ c.code }} &middot; {{ c.totalPedidos }} pedido(s) lançado(s)</div>
          <div class="store-total">{{ fmtBRL(c.valorRessuprimento) }}</div>
          <div class="store-total-label">em ressuprimentos</div>
          <div class="store-next">Brindes recebidos: <b>{{ c.brindesRecebidos }}</b></div>
        </div>
      </div>

      <div class="summary-bar">
        <div class="summary-chip">
          <div class="summary-chip-label">Total de pedidos (3 lojas)</div>
          <div class="summary-chip-value">{{ totalPedidosGeral }}</div>
        </div>
        <div class="summary-chip">
          <div class="summary-chip-label">Valor faturado (3 lojas)</div>
          <div class="summary-chip-value" style="color:var(--green);">{{ fmtBRL(valorFaturadoGeral) }}</div>
        </div>
        <div class="summary-chip">
          <div class="summary-chip-label">Brindes recebidos (3 lojas)</div>
          <div class="summary-chip-value">{{ brindesGeral }}</div>
        </div>
      </div>

      <p class="section-title">Ressuprimentos aguardando entrega</p>
      <div class="table-wrap" style="margin-bottom:26px;">
        <table>
          <thead><tr><th>Loja</th><th>Pedido</th><th>NF</th><th>Previsão de entrega</th><th></th></tr></thead>
          <tbody>
            <tr v-if="!aguardandoEntrega.length"><td colspan="5" class="muted" style="padding:24px;text-align:center;">Nada aguardando entrega.</td></tr>
            <tr v-for="p in aguardandoEntrega" :key="p.id">
              <td>{{ p._lojaNome }}</td>
              <td>{{ p.pedido || '—' }}</td>
              <td class="muted">{{ p.nfNumero || '—' }}</td>
              <td>
                <input type="date" :value="p.previsaoEntrega" @change="atualizarPrevisao(p, $event.target.value)" style="width:140px;">
                <span v-if="p.previsaoEntrega && daysUntil(p.previsaoEntrega) < 0" style="color:var(--amber);font-size:10px;display:block;">atrasado</span>
              </td>
              <td><button class="btn btn-primary btn-sm" @click="marcarRecebido(p)">Marcar recebido</button></td>
            </tr>
          </tbody>
        </table>
      </div>

      <p class="section-title">Evolução &amp; Distribuição</p>
      <div class="card">
        <div class="summary-chip-label" style="margin-bottom:10px;">Tipo de pedido</div>
        <div style="display:flex;gap:20px;align-items:center;flex-wrap:wrap;">
          <canvas ref="chartCanvas" width="170" height="170" style="max-width:170px;"></canvas>
          <div style="flex:1;min-width:200px;">
            <div v-for="l in breakdown.labels" :key="l" style="display:flex;align-items:center;gap:8px;font-size:12.5px;margin-bottom:8px;">
              <span style="width:10px;height:10px;border-radius:50%;display:inline-block;" :style="{background: CATEGORIA_CORES[l] || '#8B8FA3'}"></span>
              <span style="flex:1;">{{ l }} <span class="muted">({{ breakdown.counts[l] }})</span></span>
              <span class="num" style="font-weight:600;">{{ fmtBRL(breakdown.valores[l]) }}</span>
            </div>
          </div>
        </div>
      </div>
    </template>
  </AppShell>
</template>

<style scoped>
.store-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:16px;margin-bottom:22px;}
@media (max-width:820px){ .store-grid{grid-template-columns:1fr;} }
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
