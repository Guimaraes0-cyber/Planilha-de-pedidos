import api from './api'

export default {
  listar(loja) {
    return api.get(`/pedidos/${loja}`).then(r => r.data)
  },
  criar(pedido) {
    return api.post('/pedidos', pedido).then(r => r.data)
  },
  atualizar(id, pedido) {
    return api.put(`/pedidos/${id}`, pedido).then(r => r.data)
  },
  excluir(id) {
    return api.delete(`/pedidos/${id}`)
  },
  marcarPago(id, dataPagamento, valorPago) {
    return api.put(`/pedidos/${id}/pagar`, { dataPagamento, valorPago })
  }
}
