<template>
  <nav class="navbar">
    <router-link to="/">Calendar</router-link>
    <router-link to="/contacts">Contacts</router-link>
    <button @click="logout" class="logout-btn">Logout</button>
  </nav>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

const logout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.navbar {
  background-color: #35495e;
  padding: 15px;
  display: flex;
  gap: 20px;
}

.navbar a {
  color: white;
  text-decoration: none;
}

.logout-btn {
  margin-left: auto;
  background: none;
  border: none;
  color: white;
  cursor: pointer;
}
</style>