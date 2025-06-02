import { defineStore } from 'pinia'
import { ref } from 'vue'
import axiosInstance from '@/api/axios'  // con interceptor
import axios from 'axios'                 // limpio

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const isAuthenticated = ref(false)
  const token = ref(localStorage.getItem('token') || null)

  const login = async (credentials) => {
    console.log('Intentando login con:', credentials)
    try {
      // axios limpio para login
      const response = await axios.post('http://localhost:8081/api/auth/login', {
        username: credentials.username, // 👈 usa "username" como espera el backend
        password: credentials.password
      })
      console.log('Respuesta login:', response.data)
      token.value = response.data.token
      isAuthenticated.value = true
      localStorage.setItem('token', token.value)

      // axiosInstance para llamadas protegidas (token agregado automáticamente)
      const meResponse = await axiosInstance.get('/auth/me')
      console.log('Respuesta me:', meResponse.data)
      user.value = meResponse.data
    } catch (error) {
      throw error.response?.data || 'Error en el login'
    }
  }

  const register = async (userData) => {
    try {
      const formData = new FormData()
      formData.append('name', userData.name)
      formData.append('email', userData.email)
      formData.append('password', userData.password)
      formData.append('phone', userData.phone)
      if (userData.profileImage) {
        formData.append('profileImage', userData.profileImage)
      }

      // axios limpio para registro
      const response = await axios.post('http://localhost:8081/api/auth/register', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })

      user.value = response.data.user
      token.value = response.data.token
      isAuthenticated.value = true
      localStorage.setItem('token', token.value)
    } catch (error) {
      throw error.response?.data || 'Error al registrar'
    }
  }

  const logout = () => {
    user.value = null
    token.value = null
    isAuthenticated.value = false
    localStorage.removeItem('token')
  }

  // Si hay token en localStorage, carga el usuario automáticamente
  if (token.value) {
    isAuthenticated.value = true
    axiosInstance.get('/auth/me')
      .then(response => {
        console.log('Respuesta me al iniciar:', response.data)
        user.value = response.data
      })
      .catch(() => {
        logout()
      })
  }

  return {
    user,
    token,
    isAuthenticated,
    login,
    logout,
    register
  }
})
