<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import AppShell from '../components/AppShell.vue'
import PedidoModal from '../components/PedidoModal.vue'
import PagarModal from '../components/PagarModal.vue'
import pedidosApi from '../services/pedidos'
import { fmtBRL, fmtDateBR, categoria, CATEGORIA_CORES, statusVencimento, statusLabel, statusSelagem, selagemLabel, selagemClass } from '../services/utils'
import Chart from 'chart.js/auto'

const LOJA_META = {
  RM: { nome: 'Intimissimi Riomar' }, IGT: { nome: 'Intimissimi Iguatemi' }, CLZ: { nome: 'Calzedonia Via Bosque' }
}

const route = useRoute()
const loja = computed(() => route.params.loja)
const pedidos = ref([])
const carregando = ref(true)

const filtroStatus = ref('todos')
const filtroAno = ref('todos')
const filtroMes = ref('todos')
const busca = ref('')

const MONTH_NAMES = ['Janeiro','Fevereiro','Março','Abril','Maio','Junho','Julho','Agosto','Setembro','Outubro','Novembro','Dezembro']

const showPedidoModal = ref(false)
const editingPedido = ref(null)
const showPagarModal = ref(false)
const pagandoPedido = ref(null)

const chartCanvas = ref(null)
let chartInstance = null

async function carregar() {
  carregando.value = true
  pedidos.value = await pedidosApi.listar(loja.value)
  carregando.value = false
  await nextTick()
  desenharGrafico()
}
onMounted(carregar)
watch(loja, carregar)

const anosDisponiveis = computed(() => [...new Set(pedidos.value.map(p => p.data && p.data.slice(0, 4)).filter(Boolean))].sort())

const listaFiltrada = computed(() => {
  let list = [...pedidos.value].sort((a, b) => (b.data || '').localeCompare(a.data || ''))
  if (filtroStatus.value !== 'todos') {
    list = list.filter(p => {
      if (filtroStatus.value === 'pago') return p.status === 'pago'
      if (filtroStatus.value === 'selagem') return statusSelagem(p) === 'erro'
      return statusVencimento(p) === filtroStatus.value
    })
  }
  if (filtroAno.value !== 'todos') list = list.filter(p => p.data && p.data.slice(0, 4) === filtroAno.value)
  if (filtroMes.value !== 'todos') list = list.filter(p => p.data && p.data.slice(5, 7) === filtroMes.value)
  if (busca.value) {
    const b = busca.value.toLowerCase()
    list = list.filter(p => (p.pedido || '').toLowerCase().includes(b) || (p.nfNumero || '').toLowerCase().includes(b))
  }
  return list
})

const breakdown = computed(() => {
  const counts = {}, valores = {}
  pedidos.value.forEach(p => {
    const cat = categoria(p.pedido)
    counts[cat] = (counts[cat] || 0) + 1
    valores[cat] = (valores[cat] || 0) + (Number(p.valorFaturado) || 0)
  })
  const labels = Object.keys(counts).sort((a, b) => valores[b] - valores[a])
  return { labels, counts, valores, colors: labels.map(l => CATEGORIA_CORES[l] || '#8B8FA3') }
})

function desenharGrafico() {
  if (chartInstance) chartInstance.destroy()
  if (!chartCanvas.value || !breakdown.value.labels.length) return
  chartInstance = new Chart(chartCanvas.value, {
    type: 'doughnut',
    data: { labels: breakdown.value.labels, datasets: [{ data: breakdown.value.labels.map(l => breakdown.value.counts[l]), backgroundColor: breakdown.value.colors, borderWidth: 2, borderColor: '#fff' }] },
    options: { plugins: { legend: { display: false } }, cutout: '65%' }
  })
}
watch(breakdown, async () => { await nextTick(); desenharGrafico() })

const notasSelagem = computed(() => pedidos.value.filter(p => statusSelagem(p) === 'erro').length)

function abrirNovo() { editingPedido.value = null; showPedidoModal.value = true }
function abrirEditar(p) { editingPedido.value = p; showPedidoModal.value = true }

async function salvarPedido(form) {
  const payload = { ...form, loja: loja.value }
  if (editingPedido.value) {
    await pedidosApi.atualizar(editingPedido.value.id, { ...editingPedido.value, ...payload })
  } else {
    await pedidosApi.criar(payload)
  }
  showPedidoModal.value = false
  await carregar()
}
async function excluirPedido(p) {
  if (!confirm('Excluir este pedido?')) return
  await pedidosApi.excluir(p.id)
  await carregar()
}
function abrirPagar(p) { pagandoPedido.value = p; showPagarModal.value = true }
async function confirmarPagamento(dados) {
  await pedidosApi.marcarPago(pagandoPedido.value.id, dados.dataPagamento, dados.valorPago)
  showPagarModal.value = false
  await carregar()
}
async function atualizarVencimentoFornecedor(p, valor) {
  await pedidosApi.atualizar(p.id, { ...p, vencimentoFornecedor: valor || null })
  await carregar()
}
</script>

<template>
  <AppShell>
    <template #actions>
      <button class="btn btn-primary" @click="abrirNovo">+ Novo pedido / NF</button>
    </template>

    <div v-if="carregando" class="muted">Carregando…</div>
    <template v-else>
      <div class="summary-bar">
        <div class="summary-chip">
          <div class="summary-chip-label">Valor em Ressuprimento</div>
          <div class="summary-chip-value">{{ fmtBRL(breakdown.valores['Ressuprimento'] || 0) }}</div>
        </div>
        <div class="summary-chip">
          <div class="summary-chip-label">Valor em Brinde</div>
          <div class="summary-chip-value">{{ fmtBRL(breakdown.valores['Brinde'] || 0) }}</div>
        </div>
        <div class="summary-chip">
          <div class="summary-chip-label">Valor em Sem Pedido de Venda</div>
          <div class="summary-chip-value">{{ fmtBRL(breakdown.valores['Sem pedido de venda'] || 0) }}</div>
        </div>
        <div class="summary-chip">
          <div class="summary-chip-label">Notas que precisam de selagem</div>
          <div class="summary-chip-value" :style="{color: notasSelagem ? 'var(--red)' : ''}">{{ notasSelagem }}</div>
        </div>
      </div>

      <div class="card" style="margin-bottom:22px;">
        <div class="summary-chip-label" style="margin-bottom:10px;">Distribuição por tipo de pedido — {{ LOJA_META[loja]?.nome }}</div>
        <div style="display:flex;gap:20px;align-items:center;flex-wrap:wrap;">
          <canvas ref="chartCanvas" width="150" height="150" style="max-width:150px;"></canvas>
          <div style="flex:1;min-width:200px;">
            <div v-for="l in breakdown.labels" :key="l" style="display:flex;align-items:center;gap:8px;font-size:12.5px;margin-bottom:8px;">
              <span style="width:10px;height:10px;border-radius:50%;display:inline-block;" :style="{background: CATEGORIA_CORES[l] || '#8B8FA3'}"></span>
              <span style="flex:1;">{{ l }} <span class="muted">({{ breakdown.counts[l] }})</span></span>
              <span class="num" style="font-weight:600;">{{ fmtBRL(breakdown.valores[l]) }}</span>
            </div>
            <span v-if="!breakdown.labels.length" class="muted" style="font-size:12.5px;">Nenhum pedido lançado ainda.</span>
          </div>
        </div>
      </div>

      <div class="toolbar">
        <select v-model="filtroAno">
          <option value="todos">Todos os anos</option>
          <option v-for="a in anosDisponiveis" :key="a" :value="a">{{ a }}</option>
        </select>
        <select v-model="filtroMes">
          <option value="todos">Todos os meses</option>
          <option v-for="(nome, i) in MONTH_NAMES" :key="i" :value="String(i+1).padStart(2,'0')">{{ nome }}</option>
        </select>
        <select v-model="filtroStatus">
          <option value="todos">Todos os status</option>
          <option value="pendente">Pendente</option>
          <option value="urgente">Vencendo (7 dias)</option>
          <option value="vencido">Vencido</option>
          <option value="pago">Pago</option>
          <option value="selagem">Precisa selagem</option>
        </select>
        <input type="text" placeholder="Buscar por pedido ou NF" v-model="busca">
      </div>

      <div class="table-wrap">
        <table class="table-detailed">
          <thead>
            <tr>
              <th>Data</th><th>Pedido</th><th>NF</th><th>QT ped.</th><th>Valor ped.</th><th>QT fat.</th><th>Valor fat.</th>
              <th>Previsão</th><th>Recebim.</th><th>Venc. DAE</th><th>Venc. Fornecedor</th>
              <th>DAE esperado</th><th>DAE pago</th><th>Diferença</th><th>Selagem</th><th>Pagamento</th><th>Obs.</th><th></th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="!listaFiltrada.length"><td colspan="18" class="muted" style="padding:24px;text-align:center;">Nenhum pedido encontrado.</td></tr>
            <tr v-for="p in listaFiltrada" :key="p.id">
              <td>{{ fmtDateBR(p.data) }}</td>
              <td>{{ p.pedido || '—' }}</td>
              <td class="muted">{{ p.nfNumero || '—' }}</td>
              <td class="num">{{ p.qt ?? '—' }}</td>
              <td class="num">{{ p.valor != null ? fmtBRL(p.valor) : '—' }}</td>
              <td class="num">{{ p.qtFaturado ?? '—' }}</td>
              <td class="num">{{ fmtBRL(p.valorFaturado) }}</td>
              <td>{{ fmtDateBR(p.previsaoEntrega) }}</td>
              <td>{{ fmtDateBR(p.recebimento) }}</td>
              <td>{{ fmtDateBR(p.vencimento) }}</td>
              <td><input type="date" :value="p.vencimentoFornecedor" @change="atualizarVencimentoFornecedor(p, $event.target.value)" style="width:130px;"></td>
              <td class="num">{{ fmtBRL(p.valorDae) }}</td>
              <td class="num">{{ p.status === 'pago' ? fmtBRL(p.valorPago) : '—' }}</td>
              <td class="num">{{ p.status === 'pago' ? fmtBRL((p.valorDae||0) - (p.valorPago||0)) : '—' }}</td>
              <td><span class="stamp" :class="selagemClass(statusSelagem(p))">{{ selagemLabel(statusSelagem(p)) }}</span></td>
              <td><span class="stamp" :class="'stamp-' + statusVencimento(p)">{{ statusLabel(statusVencimento(p)) }}</span></td>
              <td class="muted">{{ p.observacoes || '—' }}</td>
              <td class="row-actions">
                <button v-if="p.status !== 'pago'" class="btn btn-ghost btn-sm" @click="abrirPagar(p)">Pagar</button>
                <button class="btn btn-ghost btn-sm" @click="abrirEditar(p)">Editar</button>
                <button class="btn btn-danger btn-sm" @click="excluirPedido(p)">Excluir</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>

    <PedidoModal :show="showPedidoModal" :editing="editingPedido" @close="showPedidoModal=false" @save="salvarPedido" />
    <PagarModal :show="showPagarModal" :pedido="pagandoPedido" @close="showPagarModal=false" @save="confirmarPagamento" />
  </AppShell>
</template>

<style scoped>
.summary-bar{display:flex;gap:16px;flex-wrap:wrap;margin-bottom:22px;}
.summary-chip{background:var(--paper-card);border-radius:var(--radius);box-shadow:var(--shadow);padding:14px 20px;flex:1;min-width:180px;}
.summary-chip-label{font-size:12px;color:var(--ink-soft);margin-bottom:6px;}
.summary-chip-value{font-family:'IBM Plex Mono',monospace;font-weight:600;font-size:20px;}
.toolbar{display:flex;gap:8px;flex-wrap:wrap;margin-bottom:14px;}
.table-detailed{min-width:1660px;}
.row-actions{display:flex;gap:6px;}
</style>
