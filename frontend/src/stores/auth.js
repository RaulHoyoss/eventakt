// src/stores/auth.js
import { defineStore } from 'pinia'
import { ref } from 'vue'
import axiosInstance from '@/api/axios' // con interceptor
import axios from 'axios'               // limpio
import api from '../api/api'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const isAuthenticated = ref(false)
  const token = ref(localStorage.getItem('token') || null)

  const login = async (credentials) => {
    console.log('Intentando login con:', credentials)
    try {
      const response = await axios.post('http://localhost:8081/api/auth/login', {
        username: credentials.username,
        password: credentials.password
      })
      console.log('Respuesta login:', response.data)

      token.value = response.data.token
      isAuthenticated.value = true
      localStorage.setItem('token', token.value)

      await fetchUserProfile()
    } catch (error) {
      console.error('Error en login:', error)
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

      const response = await axios.post('http://localhost:8081/api/auth/register', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })

      console.log('Respuesta register:', response.data)

      token.value = response.data.token
      isAuthenticated.value = true
      localStorage.setItem('token', token.value)

      await fetchUserProfile()
    } catch (error) {
      console.error('Error en register:', error)
      throw error.response?.data || 'Error al registrar'
    }
  }

  const fetchUserProfile = async () => {
    try {
      const response = await axiosInstance.get('/auth/profile')
      console.log('Respuesta perfil:', response.data)
      user.value = response.data
    } catch (error) {
      console.error('Error cargando perfil:', error)
    }
  }


  async function updateProfile(data) {
  const formData = new FormData()
  formData.append('name', data.name)
  formData.append('email', data.email)
  formData.append('phone', data.phone)

  if (data.profileImage) {
    formData.append('profileImage', data.profileImage)
  }

  try {
    const response = await api.put('/auth/profile', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        // El token ya lo añade el interceptor
      }
    })

    console.log('✅ Perfil actualizado:', response.data)

    // 👇 Refresca el usuario desde el backend
    await fetchUserProfile()

    return response.data
  } catch (error) {
    console.error('❌ Error actualizando perfil:', error)
    throw error
  }
}




  const logout = () => {
    console.log('Cerrando sesión')
    user.value = null
    token.value = null
    isAuthenticated.value = false
    localStorage.removeItem('token')
  }


  if (token.value) {
    isAuthenticated.value = true
    fetchUserProfile()
      .then(() => {
        console.log('Usuario cargado al iniciar')
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
    register,
    fetchUserProfile,
     updateProfile
  }
})
