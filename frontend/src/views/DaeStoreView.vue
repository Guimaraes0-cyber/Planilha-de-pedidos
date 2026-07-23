<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import AppShell from '../components/AppShell.vue'
import BoletoModal from '../components/BoletoModal.vue'
import boletosApi from '../services/boletos'
import pedidosApi from '../services/pedidos'
import { fmtBRL, fmtDateBR, statusSelagem } from '../services/utils'
import Chart from 'chart.js/auto'

const LOJA_META = {
  RM: { nome: 'Intimissimi Riomar' }, IGT: { nome: 'Intimissimi Iguatemi' }, CLZ: { nome: 'Calzedonia Via Bosque' }
}
const MONTH_NAMES = ['Janeiro','Fevereiro','Março','Abril','Maio','Junho','Julho','Agosto','Setembro','Outubro','Novembro','Dezembro']

const route = useRoute()
const loja = computed(() => route.params.loja)

const boletos = ref([])
const pedidos = ref([])
const carregando = ref(true)

const filtroAno = ref('todos')
const filtroMes = ref('todos')
const filtroStatus = ref('todos')
const busca = ref('')

const showModal = ref(false)
const editingBoleto = ref(null)

const chartCanvas = ref(null)
let chartInstance = null

async function carregar() {
  carregando.value = true
  boletos.value = await boletosApi.listar(loja.value)
  pedidos.value = await pedidosApi.listar(loja.value)
  carregando.value = false
  await nextTick()
  desenharGrafico()
}
onMounted(carregar)
watch(loja, carregar)

const anosDisponiveis = computed(() => [...new Set(boletos.value.map(b => b.data && b.data.slice(0, 4)).filter(Boolean))].sort())

function totalNotasDae(b) { return (b.notas || []).reduce((s, n) => s + (Number(n.valorDae) || 0), 0) }
function diferenca(b) { return (Number(b.valorPago) || 0) - totalNotasDae(b) }

const listaFiltrada = computed(() => {
  let list = [...boletos.value].sort((a, b) => (b.data || '').localeCompare(a.data || ''))
  if (filtroAno.value !== 'todos') list = list.filter(b => b.data && b.data.slice(0, 4) === filtroAno.value)
  if (filtroMes.value !== 'todos') list = list.filter(b => b.data && b.data.slice(5, 7) === filtroMes.value)
  if (busca.value) {
    const bx = busca.value.toLowerCase()
    list = list.filter(b => (b.notas || []).some(n => (n.nfNumero || '').toLowerCase().includes(bx)))
  }
  if (filtroStatus.value !== 'todos') {
    list = list.filter(b => {
      const temDif = Math.abs(diferenca(b)) >= 1
      return filtroStatus.value === 'diferenca' ? temDif : !temDif
    })
  }
  return list
})

function nfEncontrada(nfNumero) {
  return pedidos.value.some(p => p.nfNumero === nfNumero)
}

const selagemCounts = computed(() => {
  const c = { ok: 0, erro: 0, aguardando: 0 }
  pedidos.value.forEach(p => { c[statusSelagem(p)]++ })
  return c
})

function desenharGrafico() {
  if (chartInstance) chartInstance.destroy()
  if (!chartCanvas.value) return
  const c = selagemCounts.value
  if (!(c.ok + c.erro + c.aguardando)) return
  chartInstance = new Chart(chartCanvas.value, {
    type: 'doughnut',
    data: {
      labels: ['OK', 'Precisa selagem', 'Aguardando pagamento'],
      datasets: [{ data: [c.ok, c.erro, c.aguardando], backgroundColor: ['#2E6B4F', '#A63B2E', '#8B8FA3'], borderWidth: 2, borderColor: '#fff' }]
    },
    options: { plugins: { legend: { display: false } }, cutout: '65%' }
  })
}

function abrirNovo() { editingBoleto.value = null; showModal.value = true }
function abrirEditar(b) { editingBoleto.value = b; showModal.value = true }

async function salvarBoleto(payload) {
  let res
  if (editingBoleto.value) {
    res = await boletosApi.atualizar(loja.value, editingBoleto.value.id, payload)
  } else {
    res = await boletosApi.criar(loja.value, payload)
  }
  showModal.value = false
  await carregar()
  if (res && res.naoEncontradas && res.naoEncontradas.length) {
    alert(`Boleto salvo. As notas ${res.naoEncontradas.join(', ')} não foram encontradas como pedido lançado nesta loja ainda.`)
  }
}
async function excluirBoleto(b) {
  if (!confirm('Excluir este boleto? Isso não desfaz o pagamento já marcado nos pedidos vinculados.')) return
  await boletosApi.excluir(b.id)
  await carregar()
}
</script>

<template>
  <AppShell>
    <template #actions>
      <button class="btn btn-primary" @click="abrirNovo">+ Novo boleto de pagamento</button>
    </template>

    <div v-if="carregando" class="muted">Carregando…</div>
    <template v-else>
      <p class="section-title">DAE {{ LOJA_META[loja]?.nome }}</p>
      <p class="import-note">Um boleto pode cobrir várias notas, desde que sejam todas dessa mesma loja. Ao salvar, as notas listadas são marcadas como pagas automaticamente.</p>

      <div class="card" style="margin-bottom:22px;">
        <div class="summary-chip-label" style="margin-bottom:10px;">Notas que precisam de selagem — {{ LOJA_META[loja]?.nome }}</div>
        <div style="display:flex;gap:20px;align-items:center;flex-wrap:wrap;">
          <canvas ref="chartCanvas" width="150" height="150" style="max-width:150px;"></canvas>
          <div style="flex:1;min-width:160px;font-size:12.5px;">
            <div class="legend-row"><span class="dot" style="background:var(--green);"></span>OK <span class="num muted">{{ selagemCounts.ok }}</span></div>
            <div class="legend-row"><span class="dot" style="background:var(--red);"></span>Precisa selagem <span class="num muted">{{ selagemCounts.erro }}</span></div>
            <div class="legend-row"><span class="dot" style="background:#8B8FA3;"></span>Aguardando pagamento <span class="num muted">{{ selagemCounts.aguardando }}</span></div>
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
          <option value="diferenca">Com diferença</option>
          <option value="sem-diferenca">Sem diferença</option>
        </select>
        <input type="text" placeholder="Buscar por NF" v-model="busca">
      </div>

      <div class="table-wrap">
        <table>
          <thead><tr><th>Data do pagamento</th><th>Valor pago (boleto)</th><th>Total de notas</th><th>Notas cobertas</th><th>Diferença</th><th></th></tr></thead>
          <tbody>
            <tr v-if="!listaFiltrada.length"><td colspan="6" class="muted" style="padding:24px;text-align:center;">Nenhum boleto encontrado.</td></tr>
            <tr v-for="b in listaFiltrada" :key="b.id">
              <td>{{ fmtDateBR(b.data) }}</td>
              <td class="num">{{ fmtBRL(b.valorPago) }}</td>
              <td class="num">{{ (b.notas || []).length }}</td>
              <td style="font-size:12px;">
                <div v-for="n in b.notas" :key="n.nfNumero">
                  {{ n.nfNumero }} ({{ fmtBRL(n.valorDae) }})
                  <span :class="nfEncontrada(n.nfNumero) ? 'badge-auto' : 'badge-manual'">{{ nfEncontrada(n.nfNumero) ? 'vinculada' : 'NF não encontrada' }}</span>
                </div>
              </td>
              <td class="num">{{ fmtBRL(diferenca(b)) }}</td>
              <td class="row-actions">
                <button class="btn btn-ghost btn-sm" @click="abrirEditar(b)">Editar</button>
                <button class="btn btn-danger btn-sm" @click="excluirBoleto(b)">Excluir</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>

    <BoletoModal :show="showModal" :editing="editingBoleto" :loja="LOJA_META[loja]?.nome" @close="showModal=false" @save="salvarBoleto" />
  </AppShell>
</template>

<style scoped>
.import-note{font-size:11.5px;color:var(--ink-soft);margin:-6px 0 16px;}
.summary-chip-label{font-size:12px;color:var(--ink-soft);}
.legend-row{display:flex;align-items:center;gap:8px;margin-bottom:6px;}
.dot{width:10px;height:10px;border-radius:50%;display:inline-block;}
.toolbar{display:flex;gap:8px;flex-wrap:wrap;margin-bottom:14px;}
.row-actions{display:flex;gap:6px;}
.badge-auto{font-size:10px;color:var(--green);font-weight:600;display:block;}
.badge-manual{font-size:10px;color:var(--amber);font-weight:600;display:block;}
</style>
