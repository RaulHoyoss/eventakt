<template>
  <div class="contact-form">
    <h2>{{ isEditMode ? 'Edit Contat' : 'New Contact' }}</h2>

    <form @submit.prevent="submitForm">
      <div class="form-group">
        <label>Name</label>
        <input v-model="form.name" required />
      </div>

      <div class="form-group">
        <label>Email</label>
        <input v-model="form.email" type="email" required />
      </div>

      <div class="form-group">
        <label>Phone</label>
        <input v-model="form.phone" type="tel" required />
      </div>

      <div class="form-group">
        <label>Photo (optional)</label>
        <input type="file" @change="handleFileChange" accept="image/*" />
        <div v-if="previewUrl" class="preview">
          <img :src="previewUrl" alt="Foto de contacto" />
        </div>
      </div>

      <button type="submit">
        {{ isEditMode ? 'Update' : 'Create' }} Contact
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { useToast } from 'vue-toastification'

const route = useRoute()
const router = useRouter()
const toast = useToast()

const isEditMode = ref(false)
const form = ref({
  name: '',
  email: '',
  phone: ''
})
const selectedFile = ref(null)
const previewUrl = ref(null)

const handleFileChange = (event) => {
  const file = event.target.files[0]
  selectedFile.value = file
  if (file) {
    previewUrl.value = URL.createObjectURL(file)
  }
}

const loadContact = async (id) => {
  try {
    const { data } = await axios.get(`http://localhost:8080/api/contacts/${id}`)
    form.value = {
      name: data.name,
      email: data.email,
      phone: data.phone
    }
    if (data.photoUrl) {
      previewUrl.value = data.photoUrl
    }
  } catch (err) {
    console.error('Error al cargar contacto:', err)
    toast.alert('The contact couldnt be charged.')
  }
}

const submitForm = async () => {
  const formData = new FormData()
  formData.append('name', form.value.name)
  formData.append('email', form.value.email)
  formData.append('phone', form.value.phone)
  if (selectedFile.value) {
    formData.append('photo', selectedFile.value)
  }

  try {
    if (isEditMode.value) {
      await axios.put(
        `http://localhost:8080/api/contacts/${route.params.id}`,
        formData,
        { headers: { 'Content-Type': 'multipart/form-data' } }
      )
    } else {
      await axios.post(
        `http://localhost:8080/api/contacts`,
        formData,
        { headers: { 'Content-Type': 'multipart/form-data' } }
      )
    }
    router.push({ name: 'Contacts' })
  } catch (err) {
    console.error('Error al guardar contacto:', err)
    alert('No se pudo guardar el contacto.')
  }
}

onMounted(() => {
  if (route.params.id) {
    isEditMode.value = true
    loadContact(route.params.id)
  }
})
</script>

<style scoped>
.contact-form {
  max-width: 500px;
  margin: 2rem auto;
  background: #fff;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 0 10px #ccc;
  text-align: center;
}

h2 {
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1rem;
  text-align: left;
}

label {
  display: block;
  font-weight: bold;
  margin-bottom: 0.3rem;
}

input {
  width: 100%;
  padding: 0.6rem;
  border: 1px solid #ccc;
  border-radius: 6px;
}

button {
  margin-top: 1rem;
  padding: 0.75rem 1.2rem;
  background-color: #2ecc71;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  width: 100%;
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
