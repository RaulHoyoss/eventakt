<template>
  <div class="day-view">
    <div class="card">
      <h2 class="day-title">Events of {{ formattedDate }}</h2>

      <button class="btn-create" @click="goToCreate">➕ New Event</button>

      <ul class="event-list" v-if="events.length">
  <li v-for="event in sortedEvents" :key="event.id" class="event-item">
    <div class="event-header">
      <strong>{{ event.title }}</strong>
      <span class="event-time">{{ formatTime(event.start) }} - {{ formatTime(event.end) }}</span>
    </div>
    <p class="event-details">
      Fecha: {{ formatDate(event.start) }}<br>
      Categoría: {{ event.category || 'Sin categoría' }}<br>
      Contactos: {{ formatContacts(event.contacts) }}
    </p>
    <div class="event-actions">
      <button @click="editEvent(event.id)">Edit</button>
      <button @click="deleteEvent(event.id)">Delete</button>
    </div>
  </li>
</ul>
<p v-else class="no-events">There are not events for this day.</p>

    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import dayjs from 'dayjs'


const formatDate = (datetime) => dayjs(datetime).format('DD/MM/YYYY')
const formatContacts = (contacts) => {
  if (!contacts || !contacts.length) return 'Ninguno'
  return contacts.join(', ')
}


const route = useRoute()
const router = useRouter()
const date = route.query.date
const formattedDate = dayjs(date).format('DD [de] MMMM [de] YYYY')

const events = ref([])

const loadEvents = async () => {
  const { data } = await axios.get(`http://localhost:8081/api/events`)
  // Filtrar eventos de ese día
  events.value = data.filter(ev =>
  dayjs(ev.start).format('YYYY-MM-DD') === dayjs(date).format('YYYY-MM-DD')
)

}

const sortedEvents = computed(() =>
  [...events.value].sort((a, b) => new Date(a.start) - new Date(b.start))
)

const formatTime = (datetime) => dayjs(datetime).format('HH:mm')

const editEvent = (id) => {
  router.push({ name: 'EditEvent', params: { id } })
}

const deleteEvent = async (id) => {
  if (!confirm('Are you sure you want to delete this event?')) return

  try {
    await axios.delete(`http://localhost:8081/api/events/${id}`)
    await loadEvents() // recarga los eventos para actualizar la lista
  } catch (error) {
    console.error('Error deleting event:', error)
    alert('The event could not be deleted. Please try again..')
  }
}


const goToCreate = () => {
  router.push({ name: 'NewEvent', query: { date } })
}


/*
onMounted(() => {
  loadEvents()
})

*/

onMounted(() => {
  events.value = [
    {
      id: 1,
      title: 'Clase de Vue 3',
      start: `${dayjs(date).format('YYYY-MM-DD')}T10:00:00`,
      end: `${dayjs(date).format('YYYY-MM-DD')}T11:30:00`,
      category: 'Educación',
      contacts: ['raul@example.com', 'tutor@example.com']
    },
    {
      id: 2,
      title: 'Clase Zumba',
      start: `${dayjs(date).format('YYYY-MM-DD')}T9:00:00`,
      end: `${dayjs(date).format('YYYY-MM-DD')}T11:30:00`,
      category: 'Deportes',
      contacts: ['raul@example.com']
    }
  ]
})



</script>

<style scoped>
.day-view {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f9f9f9;
  padding: 1rem;
}

.card {
  background: #fff;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 0 10px #ccc;
  width: 100%;
  max-width: 600px;
}

.day-title {
  text-align: center;
  margin-bottom: 1.5rem;
  color: #2c3e50;
}

.btn-create {
  display: block;
  margin: 0 auto 1.5rem auto;
  padding: 0.75rem 1.25rem;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.event-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.event-item {
  background-color: #ecf0f1;
  padding: 1rem;
  margin-bottom: 1rem;
  border-radius: 6px;
}

.event-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}

.event-time {
  font-size: 0.9rem;
  color: #555;
}

.event-actions {
  display: flex;
  gap: 0.5rem;
}

.event-actions button {
  background: #2980b9;
  color: white;
  border: none;
  padding: 0.5rem;
  border-radius: 4px;
  cursor: pointer;
}

.no-events {
  text-align: center;
  color: #7f8c8d;
  font-style: italic;
}
</style>
