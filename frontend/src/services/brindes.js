import api from './api'

export default {
  itens() {
    return api.get('/brindes/itens').then(r => r.data)
  },
  estoque() {
    return api.get('/brindes/estoque').then(r => r.data)
  },
  movimentos() {
    return api.get('/brindes/movimentos').then(r => r.data)
  },
  historico(item) {
    return api.get(`/brindes/movimentos/${encodeURIComponent(item)}`).then(r => r.data)
  },
  registrar(mov) {
    return api.post('/brindes/movimentos', mov).then(r => r.data)
  },
  excluir(id) {
    return api.delete(`/brindes/movimentos/${id}`)
  }
}
