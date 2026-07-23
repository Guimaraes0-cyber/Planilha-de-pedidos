import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  { path: '/login', name: 'login', component: () => import('../views/LoginView.vue') },
  { path: '/', name: 'overview', component: () => import('../views/OverviewView.vue'), meta: { perm: 'overview' } },
  { path: '/loja/:loja', name: 'loja', component: () => import('../views/StoreView.vue'), meta: { perm: 'lojas' } },
  { path: '/dae', name: 'dae-overview', component: () => import('../views/DaeOverviewView.vue'), meta: { perm: 'dae' } },
  { path: '/dae/:loja', name: 'dae-loja', component: () => import('../views/DaeStoreView.vue'), meta: { perm: 'dae' } },
  { path: '/brindes', name: 'brindes', component: () => import('../views/BrindesView.vue'), meta: { perm: 'brindes' } },
  { path: '/configuracoes', name: 'configuracoes', component: () => import('../views/ConfiguracoesView.vue'), meta: { perm: 'usuarios' } },
  { path: '/usuarios', name: 'usuarios', component: () => import('../views/UsuariosView.vue'), meta: { perm: 'usuarios' } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const auth = useAuthStore()

  if (to.name !== 'login' && !auth.isLoggedIn) {
    return { name: 'login' }
  }
  if (to.name === 'login' && auth.isLoggedIn) {
    return { name: 'overview' }
  }
  if (to.meta.perm && !auth.perms[to.meta.perm]) {
    // usuário sem permissão pra essa aba -> manda pra primeira aba permitida
    const perms = auth.perms
    if (perms.overview) return { name: 'overview' }
    if (perms.brindes) return { name: 'brindes' }
    if (perms.dae) return { name: 'dae-overview' }
    return { name: 'login' }
  }
  return true
})

export default router
