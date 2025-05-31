<template>
  <nav class="navbar" v-if="isAuthenticated">
    <router-link to="/calendar" class="nav-link">Calendar</router-link>
    <router-link to="/contacts" class="nav-link">Contacts</router-link>

    <div class="navbar-right">
      <img
        :src="user?.photo || defaultAvatar"
        alt="User Photo"
        class="user-avatar"
      />
      <button @click="logout" class="logout-btn">Logout</button>
    </div>
  </nav>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { computed } from 'vue'

const authStore = useAuthStore()
const router = useRouter()

const user = computed(() => authStore.user)
const isAuthenticated = computed(() => authStore.isAuthenticated)
const defaultAvatar = '/images/default-avatar.png'  // Asegúrate que exista en public/images

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
