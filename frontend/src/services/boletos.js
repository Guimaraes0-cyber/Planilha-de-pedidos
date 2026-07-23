import api from './api'

export default {
  listar(loja) {
    return api.get(`/boletos/${loja}`).then(r => r.data)
  },
  criar(loja, boleto) {
    return api.post(`/boletos/${loja}`, boleto).then(r => r.data)
  },
  atualizar(loja, id, boleto) {
    return api.put(`/boletos/${loja}/${id}`, boleto).then(r => r.data)
  },
  excluir(id) {
    return api.delete(`/boletos/${id}`)
  }
}
