import { defineStore } from 'pinia'
import { ref } from 'vue'
import axiosInstance from '@/api/axios' // instancia con interceptores
import axios from 'axios' // axios limpio, sin interceptores

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const isAuthenticated = ref(false)
  const token = ref(localStorage.getItem('token') || null)

  const login = async (credentials) => {
  try {
    // 🚀 Usa axios limpio para login
    const response = await axios.post('http://localhost:8081/api/auth/login', credentials)
    token.value = response.data // El backend solo devuelve el token como String plano
    isAuthenticated.value = true
    localStorage.setItem('token', token.value)

    // ✅ Opcional: cargar el usuario actual
    const meResponse = await axiosInstance.get('/auth/me', {
      headers: { Authorization: `Bearer ${token.value}` }
    })
    user.value = meResponse.data
  } catch (error) {
    throw error.response?.data?.message || 'Error en el login'
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

      // 🚀 Usa axios limpio para registro
      const response = await axios.post('http://localhost:8081/api/auth/register', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })

      user.value = response.data.user
      token.value = response.data.token
      isAuthenticated.value = true
      localStorage.setItem('token', token.value)
    } catch (error) {
      throw error.response?.data?.message || 'Error al registrar'
    }
  }

  const logout = () => {
    user.value = null
    token.value = null
    isAuthenticated.value = false
    localStorage.removeItem('token')
  }

  // ✅ Cargar el usuario si ya hay token guardado
  if (token.value) {
    isAuthenticated.value = true
    axiosInstance.get('/auth/me')
      .then(response => {
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
