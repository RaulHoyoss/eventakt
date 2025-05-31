import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const isAuthenticated = ref(true)
  const token = ref(localStorage.getItem('token') || null)

  const login = async (credentials) => {
    try {
      const response = await axios.post('http://localhost:8081/api/auth/login', credentials)
      user.value = response.data.user         // ✅ guarda datos completos del usuario
      token.value = response.data.token
      isAuthenticated.value = true
      localStorage.setItem('token', token.value)
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

      const response = await axios.post('http://localhost:8081/api/auth/register', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })

      user.value = response.data.user         // ✅ guarda usuario tras registro
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
    axios.get('http://localhost:8081/api/auth/me', {
      headers: { Authorization: `Bearer ${token.value}` }
    })
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
