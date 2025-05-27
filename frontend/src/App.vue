<script setup>
import { RouterView } from 'vue-router'
import NavBar from '@/components/NavBar.vue'
import { useAuthStore } from '@/stores/auth'
import { computed } from 'vue'

const authStore = useAuthStore()
const isAuthenticated = computed(() => authStore.isAuthenticated.value) // ¡Ojo con el .value!
</script>

<template>
  <div id="app">
    <!-- Barra de navegación -->
    <NavBar v-if="isAuthenticated" />

    <!-- Contenedor principal -->
    <main class="main-content">
      <RouterView />
    </main>

    <!-- Footer -->
    <footer v-if="isAuthenticated" class="app-footer">
      <p>Eventakt © 2023</p>
    </footer>
  </div>
</template>

<style>
/* Tus estilos actuales se mantienen igual */
#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  font-family: 'Arial', sans-serif;
}

.main-content {
  flex: 1;
  padding: 20px;
}

.app-footer {
  background-color: #42b983;
  color: white;
  text-align: center;
  padding: 10px;
}
</style>