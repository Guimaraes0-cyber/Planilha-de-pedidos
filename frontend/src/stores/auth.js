import { defineStore } from 'pinia'
import api from '../services/api'

export const ROLE_TABS = {
  ADMIN:          { overview: true,  lojas: true,  dae: true,  brindes: true,  usuarios: true },
  DIRETORIA:      { overview: true,  lojas: true,  dae: true,  brindes: true,  usuarios: false },
  ADMINISTRATIVO: { overview: true,  lojas: true,  dae: true,  brindes: false, usuarios: false },
  MARKETING:      { overview: false, lojas: false, dae: false, brindes: true,  usuarios: false }
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('dae-token') || null,
    username: localStorage.getItem('dae-user') || null,
    role: localStorage.getItem('dae-role') || null
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    perms: (state) => ROLE_TABS[state.role] || ROLE_TABS.ADMINISTRATIVO
  },
  actions: {
    async login(username, senha) {
      const res = await api.post('/auth/login', { username, senha })
      this.token = res.data.token
      this.username = res.data.username
      this.role = res.data.role
      localStorage.setItem('dae-token', this.token)
      localStorage.setItem('dae-user', this.username)
      localStorage.setItem('dae-role', this.role)
    },
    logout() {
      this.token = null
      this.username = null
      this.role = null
      localStorage.removeItem('dae-token')
      localStorage.removeItem('dae-user')
      localStorage.removeItem('dae-role')
    }
  }
})
