import axios from 'axios'

// Em desenvolvimento e no deploy via IIS, o proxy same-origin cuida de "/api".
// No Render, frontend e backend são serviços separados com URLs diferentes,
// então definimos VITE_API_BASE_URL como variável de build (ver RENDER.md).
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api'
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('dae-token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response && err.response.status === 401) {
      localStorage.removeItem('dae-token')
      localStorage.removeItem('dae-user')
      window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)

export default api
