import api from './api'

export default {
  listar() {
    return api.get('/usuarios').then(r => r.data)
  },
  criar(usuario) {
    return api.post('/usuarios', usuario).then(r => r.data)
  },
  desativar(id) {
    return api.put(`/usuarios/${id}/desativar`)
  },
  redefinirSenha(id, novaSenha) {
    return api.put(`/usuarios/${id}/senha`, { novaSenha })
  }
}
