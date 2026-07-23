<script setup>
import { ref, watch } from 'vue'
import { todayISO } from '../services/utils'

const props = defineProps({ show: Boolean, pedido: Object })
const emit = defineEmits(['close', 'save'])

const dataPagamento = ref(todayISO())
const valorPago = ref(0)

watch(() => props.show, (val) => {
  if (val && props.pedido) {
    dataPagamento.value = todayISO()
    valorPago.value = props.pedido.valorDae
  }
})

function submit() {
  emit('save', { dataPagamento: dataPagamento.value, valorPago: valorPago.value })
}
</script>

<template>
  <div v-if="show" class="overlay open" @click.self="$emit('close')">
    <div class="modal">
      <p class="modal-title">Marcar DAE como pago</p>
      <p class="modal-sub">NF {{ pedido?.nfNumero || '—' }} &middot; {{ pedido?.pedido }}</p>
      <form @submit.prevent="submit">
        <div class="form-grid">
          <div class="field"><label>Data do pagamento</label><input type="date" v-model="dataPagamento" required></div>
          <div class="field"><label>Valor pago (R$)</label><input type="number" step="0.01" v-model.number="valorPago" required></div>
        </div>
        <div class="modal-actions">
          <button type="button" class="btn btn-ghost" @click="$emit('close')">Cancelar</button>
          <button type="submit" class="btn btn-primary">Confirmar pagamento</button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.overlay{position:fixed;inset:0;background:rgba(27,36,54,0.45);display:flex;align-items:flex-start;justify-content:center;padding:40px 16px;overflow-y:auto;z-index:50;}
.modal{background:#fff;border-radius:14px;max-width:420px;width:100%;padding:26px 28px 24px;box-shadow:0 20px 60px rgba(0,0,0,0.25);}
.modal-title{font-family:'Source Serif 4',serif;font-weight:600;font-size:19px;margin:0 0 4px;}
.modal-sub{color:var(--ink-soft);font-size:13px;margin:0 0 18px;}
.form-grid{display:grid;grid-template-columns:1fr 1fr;gap:12px 14px;}
.field{display:flex;flex-direction:column;gap:4px;}
.field label{font-size:12px;font-weight:600;color:var(--ink-soft);}
.modal-actions{display:flex;justify-content:flex-end;gap:10px;margin-top:20px;}
</style>
