<template>
  <div class="contacts-view">
    <h2>Mis contactos</h2>
    <form @submit.prevent="addContact">
      <input type="text" v-model="newContact.name" placeholder="Nombre" required />
      <input type="email" v-model="newContact.email" placeholder="Email" required />
      <input type="tel" v-model="newContact.phone" placeholder="Teléfono" required />
      <input type="file" @change="onFileChange" />
      <button type="submit">Agregar contacto</button>
    </form>

    <ul class="contact-list">
      <li v-for="contact in contacts" :key="contact.id">
        <img :src="contact.photo || defaultPhoto" alt="foto" class="photo" />
        <div>
          <strong>{{ contact.name }}</strong><br />
          {{ contact.phone }}<br />
          {{ contact.email }}
        </div>
        <button @click="deleteContact(contact.id)">Eliminar</button>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const contacts = ref([])
const newContact = ref({ name: '', email: '', phone: '', photo: null })
const defaultPhoto = '/default-profile.jpg'

const onFileChange = (e) => {
  newContact.value.photo = e.target.files[0]
}

const addContact = () => {
  const formData = new FormData()
  formData.append('name', newContact.value.name)
  formData.append('email', newContact.value.email)
  formData.append('phone', newContact.value.phone)
  if (newContact.value.photo) formData.append('photo', newContact.value.photo)

  // Aquí deberías hacer POST a tu backend y luego actualizar la lista de contactos
  // Por ahora simulamos con un push local:
  contacts.value.push({
    id: Date.now(),
    ...newContact.value,
    photo: URL.createObjectURL(newContact.value.photo || '')
  })
  newContact.value = { name: '', email: '', phone: '', photo: null }
}

const deleteContact = (id) => {
  contacts.value = contacts.value.filter(c => c.id !== id)
}

onMounted(() => {
  // Aquí deberías hacer GET a tu backend
})
</script>

<style scoped>
.contacts-view {
  padding: 1rem;
}
form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 1rem;
}
input[type="text"],
input[type="email"],
input[type="tel"] {
  flex: 1 1 200px;
}
.contact-list {
  list-style: none;
  padding: 0;
}
.contact-list li {
  display: flex;
  gap: 10px;
  margin-bottom: 1rem;
  align-items: center;
}
.photo {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 50%;
}
</style>