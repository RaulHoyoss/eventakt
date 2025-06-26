// src/api/api.js
import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8081/api',
  headers: {
    'Content-Type': 'application/json'
    
  }
})

// Interceptor que se ejecuta antes de cada petición
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  console.log('📦 Token dinámico enviado en request:', token)

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

export default api
