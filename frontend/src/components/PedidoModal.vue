<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  show: Boolean,
  editing: Object // null for new, or existing pedido object
})
const emit = defineEmits(['close', 'save'])

const form = ref(blankForm())

function blankForm() {
  return {
    data: new Date().toISOString().slice(0, 10),
    pedido: '', nfNumero: '', qt: null, valor: null, qtFaturado: null,
    valorFaturado: 0, previsaoEntrega: null, recebimento: null,
    vencimento: null, vencimentoFornecedor: null, valorDae: null,
    observacoes: ''
  }
}

watch(() => props.show, (val) => {
  if (val) {
    form.value = props.editing ? { ...props.editing } : blankForm()
    daeTocado.value = !!props.editing
  }
})

const daeTocado = ref(false)
function onValorFaturadoInput() {
  if (!daeTocado.value) {
    form.value.valorDae = Number((Number(form.value.valorFaturado || 0) * 0.16).toFixed(2))
  }
}
function onValorDaeInput() {
  daeTocado.value = true
}

function submit() {
  emit('save', { ...form.value })
}
</script>

<template>
  <div v-if="show" class="overlay open" @click.self="$emit('close')">
    <div class="modal">
      <p class="modal-title">{{ editing ? 'Editar pedido' : 'Novo pedido / NF' }}</p>
      <p class="modal-sub">Preencha com os dados da nota fiscal.</p>
      <form @submit.prevent="submit">
        <div class="form-grid">
          <div class="field"><label>Data</label><input type="date" v-model="form.data" required></div>
          <div class="field span-2"><label>Pedido / descrição</label>
            <input type="text" v-model="form.pedido" placeholder="Ex: RESSUPRIMENTO SEMANAL/215738, BRINDE, SEM PEDIDO DE VENDA..." required>
          </div>
          <div class="field"><label>Nº NF</label><input type="text" v-model="form.nfNumero"></div>
          <div class="field"></div>
          <div class="field"><label>QT pedida</label><input type="number" v-model.number="form.qt"></div>
          <div class="field"><label>Valor pedido (R$)</label><input type="number" step="0.01" v-model.number="form.valor"></div>
          <div class="field"><label>QT faturada</label><input type="number" v-model.number="form.qtFaturado"></div>
          <div class="field"><label>Valor faturado (R$)</label>
            <input type="number" step="0.01" v-model.number="form.valorFaturado" @input="onValorFaturadoInput" required>
          </div>
          <div class="field"><label>Previsão de entrega</label><input type="date" v-model="form.previsaoEntrega"></div>
          <div class="field"><label>Recebimento</label><input type="date" v-model="form.recebimento"></div>
          <div class="field"><label>Vencimento do DAE</label><input type="date" v-model="form.vencimento"></div>
          <div class="field"><label>Vencimento (pagamento ao fornecedor)</label><input type="date" v-model="form.vencimentoFornecedor"></div>
          <div class="field"><label>Valor do DAE esperado (R$)</label>
            <input type="number" step="0.01" v-model.number="form.valorDae" @input="onValorDaeInput">
            <span class="hint">Sugestão automática: 16% do valor faturado.</span>
          </div>
          <div class="field span-2"><label>Observações</label><input type="text" v-model="form.observacoes"></div>
        </div>
        <div class="modal-actions">
          <button type="button" class="btn btn-ghost" @click="$emit('close')">Cancelar</button>
          <button type="submit" class="btn btn-primary">{{ editing ? 'Salvar alterações' : 'Adicionar pedido' }}</button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.overlay{position:fixed;inset:0;background:rgba(27,36,54,0.45);display:flex;align-items:flex-start;justify-content:center;padding:40px 16px;overflow-y:auto;z-index:50;}
.modal{background:#fff;border-radius:14px;max-width:560px;width:100%;padding:26px 28px 24px;box-shadow:0 20px 60px rgba(0,0,0,0.25);}
.modal-title{font-family:'Source Serif 4',serif;font-weight:600;font-size:19px;margin:0 0 4px;}
.modal-sub{color:var(--ink-soft);font-size:13px;margin:0 0 18px;}
.form-grid{display:grid;grid-template-columns:1fr 1fr;gap:12px 14px;}
.field{display:flex;flex-direction:column;gap:4px;}
.field.span-2{grid-column:1/-1;}
.field label{font-size:12px;font-weight:600;color:var(--ink-soft);}
.hint{font-size:11px;color:var(--ink-soft);font-weight:400;}
.modal-actions{display:flex;justify-content:flex-end;gap:10px;margin-top:20px;}
</style>
