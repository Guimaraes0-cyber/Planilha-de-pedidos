<script setup>
import { ref, watch } from 'vue'
import { todayISO } from '../services/utils'

const props = defineProps({ show: Boolean, itens: Array, itemPreset: String })
const emit = defineEmits(['close', 'save'])

const form = ref(blank())
function blank() {
  return { item: props.itemPreset || (props.itens && props.itens[0]) || '', tipo: 'entrada', quantidade: 1, data: todayISO(), destino: '', observacoes: '' }
}
watch(() => props.show, (val) => { if (val) form.value = blank() })

function submit() { emit('save', { ...form.value }) }
</script>

<template>
  <div v-if="show" class="overlay open" @click.self="$emit('close')">
    <div class="modal">
      <p class="modal-title">Registrar movimentação de brinde</p>
      <p class="modal-sub">Lance uma entrada (chegada de estoque) ou saída (envio pra loja/campanha).</p>
      <form @submit.prevent="submit">
        <div class="form-grid">
          <div class="field span-2">
            <label>Item</label>
            <select v-model="form.item">
              <option v-for="i in itens" :key="i" :value="i">{{ i }}</option>
            </select>
          </div>
          <div class="field">
            <label>Tipo</label>
            <select v-model="form.tipo">
              <option value="entrada">Entrada (chegou estoque)</option>
              <option value="saida">Saída (enviado)</option>
            </select>
          </div>
          <div class="field"><label>Quantidade</label><input type="number" min="1" v-model.number="form.quantidade" required></div>
          <div class="field"><label>Data</label><input type="date" v-model="form.data" required></div>
          <div class="field"><label>Destino (loja ou campanha)</label><input type="text" v-model="form.destino" placeholder="Ex: Shopping RioMar, Campanha Dia das Mães..."></div>
          <div class="field span-2"><label>Observações</label><input type="text" v-model="form.observacoes"></div>
        </div>
        <div class="modal-actions">
          <button type="button" class="btn btn-ghost" @click="$emit('close')">Cancelar</button>
          <button type="submit" class="btn btn-primary">Salvar movimentação</button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.overlay{position:fixed;inset:0;background:rgba(27,36,54,0.45);display:flex;align-items:flex-start;justify-content:center;padding:40px 16px;overflow-y:auto;z-index:50;}
.modal{background:#fff;border-radius:14px;max-width:480px;width:100%;padding:26px 28px 24px;box-shadow:0 20px 60px rgba(0,0,0,0.25);}
.modal-title{font-family:'Source Serif 4',serif;font-weight:600;font-size:19px;margin:0 0 4px;}
.modal-sub{color:var(--ink-soft);font-size:13px;margin:0 0 18px;}
.form-grid{display:grid;grid-template-columns:1fr 1fr;gap:12px 14px;}
.field{display:flex;flex-direction:column;gap:4px;}
.field.span-2{grid-column:1/-1;}
.field label{font-size:12px;font-weight:600;color:var(--ink-soft);}
.modal-actions{display:flex;justify-content:flex-end;gap:10px;margin-top:20px;}
</style>
