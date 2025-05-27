<!-- src/views/EventForm.vue -->
<template>
  <div class="event-form">
    <h2>{{ isEdit ? 'Editar Evento' : 'Crear Evento' }}</h2>
    <form @submit.prevent="handleSubmit">
      <input v-model="event.title" placeholder="Título" required />
      <textarea v-model="event.description" placeholder="Descripción"></textarea>
      <input type="datetime-local" v-model="event.start" required />
      <input type="datetime-local" v-model="event.end" required />
      <select v-model="event.category">
        <option value="meeting">Reunión</option>
        <option value="work">Trabajo</option>
        <option value="personal">Personal</option>
      </select>
      <label>Contactos asociados:</label>
      <div v-for="contact in contacts" :key="contact.id">
        <input
          type="checkbox"
          :value="contact.id"
          v-model="event.contacts"
        />
        {{ contact.name }}
      </div>
      <button type="submit">{{ isEdit ? 'Actualizar' : 'Crear' }}</button>
      <button v-if="isEdit" @click.prevent="handleDelete">Eliminar</button>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)

const event = ref({
  title: '',
  description: '',
  start: route.params.date ? `${route.params.date}T09:00` : '',
  end: route.params.date ? `${route.params.date}T10:00` : '',
  category: 'meeting',
  contacts: []
})

const contacts = ref([])

const fetchContacts = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/contacts')
    contacts.value = response.data
  } catch (error) {
    console.error('Error al obtener contactos:', error)
  }
}

const fetchEvent = async () => {
  try {
    const response = await axios.get(`http://localhost:8080/api/events/${route.params.id}`)
    const data = response.data
    event.value = {
      title: data.title,
      description: data.description,
      start: data.start,
      end: data.end,
      category: data.category,
      contacts: data.contacts.map(c => c.id)
    }
  } catch (error) {
    console.error('Error al obtener el evento:', error)
  }
}

const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      await axios.put(`http://localhost:8080/api/events/${route.params.id}`, event.value)
    } else {
      await axios.post('http://localhost:8080/api/events', event.value)
    }
    router.push('/calendar')
  } catch (error) {
    console.error('Error al guardar el evento:', error)
  }
}

const handleDelete = async () => {
  try {
    await axios.delete(`http://localhost:8080/api/events/${route.params.id}`)
    router.push('/calendar')
  } catch (error) {
    console.error('Error al eliminar el evento:', error)
  }
}

onMounted(() => {
  fetchContacts()
  if (isEdit.value) {
    fetchEvent()
  }
})
</script>

<style scoped>
.event-form {
  padding: 20px;
}
form {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
</style>
