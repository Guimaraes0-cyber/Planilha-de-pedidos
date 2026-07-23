<script setup>
import { ref, watch } from 'vue'
import { todayISO } from '../services/utils'

const props = defineProps({ show: Boolean, editing: Object, loja: String })
const emit = defineEmits(['close', 'save'])

let uidCounter = 0
function uid() { return 'n' + (uidCounter++) }

const data = ref(todayISO())
const valorPago = ref(0)
const notas = ref([{ _uid: uid(), nfNumero: '', valorDae: 0 }])

watch(() => props.show, (val) => {
  if (val) {
    if (props.editing) {
      data.value = props.editing.data
      valorPago.value = props.editing.valorPago
      notas.value = (props.editing.notas || []).map(n => ({ _uid: uid(), nfNumero: n.nfNumero, valorDae: n.valorDae }))
      if (!notas.value.length) notas.value = [{ _uid: uid(), nfNumero: '', valorDae: 0 }]
    } else {
      data.value = todayISO()
      valorPago.value = 0
      notas.value = [{ _uid: uid(), nfNumero: '', valorDae: 0 }]
    }
  }
})

function addNota() { notas.value.push({ _uid: uid(), nfNumero: '', valorDae: 0 }) }
function removeNota(u) {
  notas.value = notas.value.filter(n => n._uid !== u)
  if (!notas.value.length) addNota()
}

function submit() {
  const limpo = notas.value
    .map(n => ({ nfNumero: (n.nfNumero || '').trim(), valorDae: Number(n.valorDae) || 0 }))
    .filter(n => n.nfNumero)
  if (!limpo.length) { alert('Adicione pelo menos uma nota com o número da NF.'); return }
  emit('save', { data: data.value, valorPago: Number(valorPago.value) || 0, notas: limpo })
}
</script>

<template>
  <div v-if="show" class="overlay open" @click.self="$emit('close')">
    <div class="modal modal-lg">
      <p class="modal-title">{{ editing ? 'Editar boleto de DAE' : 'Novo boleto de pagamento' }} — {{ loja }}</p>
      <p class="modal-sub">Todas as notas devem ser dessa mesma loja.</p>
      <form @submit.prevent="submit">
        <div class="form-grid">
          <div class="field"><label>Data do pagamento</label><input type="date" v-model="data" required></div>
          <div class="field"><label>Valor pago no boleto (R$)</label><input type="number" step="0.01" v-model.number="valorPago" required></div>
        </div>
        <p class="section-title" style="font-size:13px;margin:18px 0 8px;">Notas cobertas por este boleto</p>
        <div v-for="n in notas" :key="n._uid" class="nota-row">
          <div class="field" style="flex:1;">
            <label>Nº NF</label>
            <input type="text" v-model="n.nfNumero">
          </div>
          <div class="field" style="flex:1;">
            <label>Valor DAE dessa nota (R$)</label>
            <input type="number" step="0.01" v-model.number="n.valorDae">
          </div>
          <button type="button" class="btn btn-danger btn-sm" @click="removeNota(n._uid)">Remover</button>
        </div>
        <button type="button" class="btn btn-ghost btn-sm" @click="addNota">+ Adicionar outra nota</button>
        <div class="modal-actions">
          <button type="button" class="btn btn-ghost" @click="$emit('close')">Cancelar</button>
          <button type="submit" class="btn btn-primary">Salvar boleto</button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.overlay{position:fixed;inset:0;background:rgba(27,36,54,0.45);display:flex;align-items:flex-start;justify-content:center;padding:40px 16px;overflow-y:auto;z-index:50;}
.modal{background:#fff;border-radius:14px;max-width:560px;width:100%;padding:26px 28px 24px;box-shadow:0 20px 60px rgba(0,0,0,0.25);}
.modal-lg{max-width:640px;}
.modal-title{font-family:'Source Serif 4',serif;font-weight:600;font-size:19px;margin:0 0 4px;}
.modal-sub{color:var(--ink-soft);font-size:13px;margin:0 0 18px;}
.form-grid{display:grid;grid-template-columns:1fr 1fr;gap:12px 14px;}
.field{display:flex;flex-direction:column;gap:4px;}
.field label{font-size:12px;font-weight:600;color:var(--ink-soft);}
.nota-row{display:flex;gap:8px;align-items:flex-end;margin-bottom:8px;}
.modal-actions{display:flex;justify-content:flex-end;gap:10px;margin-top:20px;}
</style>
