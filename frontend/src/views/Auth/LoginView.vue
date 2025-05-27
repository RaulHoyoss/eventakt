<template>
  <div class="login-view container">
    <div class="card">
      <h1 class="login-title">Eventakt</h1>
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="email">Email</label>
          <input type="email" id="email" v-model="email" required />
        </div>

        <div class="form-group">
          <label for="password">Password</label>
          <input type="password" id="password" v-model="password" required />
        </div>

        <p v-if="errorMessage" class="error">{{ errorMessage }}</p>

        <button type="submit" class="btn btn-primary">Login</button>
      </form>
      <p class="login-footer">Don't have an account? <router-link to="/register">Registration</router-link></p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const email = ref('')
const password = ref('')
const errorMessage = ref('')
const authStore = useAuthStore()
const router = useRouter()

const handleLogin = async () => {
  errorMessage.value = ''

  if (!email.value || !password.value) {
    errorMessage.value = 'Por favor, completa todos los campos.'
    return
  }

  try {
    await authStore.login({ email: email.value, password: password.value })
    router.push('/')
  } catch (error) {
    errorMessage.value = error
  }
}
</script>

<style scoped>
.login-view {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
}

.card {
  background: #fff;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 0 10px #ccc;
  width: 100%;
  max-width: 400px;
}

.login-title {
  text-align: center;
  margin-bottom: 1.5rem;
  color: var(--secondary, #2c3e50);
}

.form-group {
  margin-bottom: 1rem;
  display: flex;
  flex-direction: column;
}

.form-group label {
  margin-bottom: 0.5rem;
}

input {
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 5px;
}

input:invalid {
  border-color: red;
}

.error {
  color: red;
  margin-bottom: 1rem;
  text-align: center;
}

.btn {
  width: 100%;
  padding: 0.75rem;
  background-color: var(--primary, #3498db);
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.login-footer {
  text-align: center;
  margin-top: 1rem;
  color: var(--dark-gray, #7f8c8d);
}

.login-footer a {
  color: var(--primary, #3498db);
  text-decoration: none;
}
</style>
