import api from './api'

export default {
  trocarSenha(senhaAtual, novaSenha) {
    return api.post('/auth/trocar-senha', { senhaAtual, novaSenha })
  }
}
