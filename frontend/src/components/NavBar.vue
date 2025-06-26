<template>
  <nav class="navbar" v-if="showNavbar">
    <router-link to="/calendar" class="nav-link">Calendar</router-link>
    <router-link to="/contacts" class="nav-link">Contacts</router-link>

    <div class="navbar-right" v-if="user">
      <span class="user-name" @click="goToProfile">{{ user.name }}</span>
            <img
        :src="getProfileImageUrl(user.profileImagePath)"
        alt="User Photo"
        class="user-avatar"
      />
    
      <button @click="logout" class="logout-btn">Logout</button>
    </div>
  </nav>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth'
import { useRouter, useRoute } from 'vue-router'
import { computed } from 'vue'

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()

const allowedRoutes = ['calendar', 'DayView','UserProfile', 'NewEvent', 'EventForm', 'Contacts', 'NewContact', 'EditContact', 'EditEvent']
const showNavbar = computed(() => allowedRoutes.includes(route.name))

const user = computed(() => authStore.user)


const logout = () => {
  authStore.logout()
  router.push('/login')
}


const goToProfile = () => {
  router.push('/profile')
}

const defaultAvatar = '/images/default-avatar.png'

function getProfileImageUrl(path) {
  // Si tiene imagen, usa la completa con el backend
  if (path) {
    return `http://localhost:8081/${path.replace(/\\/g, '/')}`
  }
  // Si no tiene, usa la imagen por defecto
  return `http://localhost:8081${defaultAvatar}`
}
</script>

<style scoped>
.navbar {
  background-color: #35495e;
  padding: 15px;
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav-link {
  color: white;
  text-decoration: none;
}

.navbar-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-name {
  color: white;
  font-weight: bold;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid white;
}

.logout-btn {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
}
</style>
