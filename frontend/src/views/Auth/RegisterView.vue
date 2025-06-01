<template>
  <div class="register-view container">
    <div class="card">
      <h1 class="register-title">Create an account on Eventakt</h1>
      <form @submit.prevent="handleRegister" class="register-form" enctype="multipart/form-data">
        <div class="form-group">
          <label for="name">Full Name</label>
          <input type="text" id="name" v-model="user.name" required />
        </div>

        <div class="form-group">
          <label for="phone">Phone</label>
          <input type="tel" id="phone" v-model="user.phone" required pattern="^[0-9]{7,15}$" />
        </div>

        <div class="form-group">
          <label for="email">Email</label>
          <input type="email" id="email" v-model="user.email" required />
        </div>

        <div class="form-group">
          <label for="password">Password</label>
          <input type="password" id="password" v-model="user.password" required minlength="6" />
        </div>

        <div class="form-group">
          <label for="confirmPassword">Confirm Password</label>
          <input type="password" id="confirmPassword" v-model="user.confirmPassword" required />
        </div>

        <div class="form-group">
          <label for="photo">Profile photo (optional)</label>
          <input type="file" id="photo" @change="handleFileUpload" accept="image/*" />
          <div v-if="previewUrl" class="preview">
          <img :src="previewUrl" alt="Foto de contacto" />
        </div>
        </div>

        <p v-if="errorMessage" class="error">{{ errorMessage }}</p>

        <button type="submit" class="btn btn-primary">Register</button>
      </form>
      <p class="register-footer">
        ¿Already hava an account? <router-link to="/login">Login</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const user = ref({
  name: '',
  phone: '',
  email: '',
  password: '',
  confirmPassword: ''
})
const photo = ref(null)
const errorMessage = ref('')
const authStore = useAuthStore()
const router = useRouter()

const previewUrl = ref(null)


const handleFileUpload = (e) => {
  const file = e.target.files[0]
  photo.value = file
  if (file) {
    previewUrl.value = URL.createObjectURL(file)
  } else {
    previewUrl.value = null
  }
}


const handleRegister = async () => {
  errorMessage.value = ''

  if (!user.value.name || !user.value.email || !user.value.password || !user.value.phone) {
    errorMessage.value = 'Por favor, completa todos los campos obligatorios.'
    return
  }

  if (user.value.password !== user.value.confirmPassword) {
    errorMessage.value = 'Las contraseñas no coinciden.'
    return
  }


  try {
    await authStore.register({
  name: user.value.name,
  phone: user.value.phone,
  email: user.value.email,
  password: user.value.password,
  profileImage: photo.value
  })

    router.push('/')
    if (previewUrl.value) {
  URL.revokeObjectURL(previewUrl.value)
  }
  } catch (error) {
    errorMessage.value = error.message || 'Error al registrar'
  }
}
</script>

<style scoped>
.register-view {
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
  max-width: 500px;
}

.register-title {
  text-align: center;
  margin-bottom: 1.5rem;
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

.register-footer {
  margin-top: 1rem;
  text-align: center;
}

.preview {
  margin-top: 1rem;
  text-align: center;
}

.preview img {
  max-width: 120px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 0 5px rgba(0,0,0,0.2);
}

</style>
