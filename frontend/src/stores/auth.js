
import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const isAuthenticated = ref(false)
  const token = ref(localStorage.getItem('token') || null)

  const login = async (credentials) => {
    try {
      const response = await axios.post('http://localhost:8080/api/auth/login', credentials)
      user.value = { email: credentials.email }
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

    const response = await axios.post('http://localhost:8080/api/auth/register', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    token.value = response.data.token
    localStorage.setItem('token', token.value)
    return response.data
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

  // Verifica autenticación al iniciar
  if (token.value) {
    isAuthenticated.value = true
    // Aquí podrías hacer una petición para obtener los datos del usuario
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