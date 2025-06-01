import axios from 'axios'

const instance = axios.create({
  baseURL: 'http://localhost:8081/api', // ✅ Ajusta a tu puerto
  headers: {
    'Content-Type': 'application/json'
  }
})

// Interceptor para añadir token en cada petición automáticamente
instance.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

instance.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  const isAuthRoute = config.url.includes('/auth/login') || config.url.includes('/auth/register')

  if (token && !isAuthRoute) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})



export default instance

