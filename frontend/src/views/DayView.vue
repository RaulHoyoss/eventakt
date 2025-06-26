<template>
  <div class="day-view">
    <div class="card">
      <h2 class="day-title">Events of {{ formattedDate }}</h2>

      <!-- Barra de 24 horas -->
      <div class="timeline-container">
        <div class="timeline-bar">
              <div class="hour-marker" v-for="hour in 24" :key="hour">
              {{ hour - 1 }}h
            </div>
        </div>

        <!-- Eventos superpuestos -->
        <div class="events-overlay">
    <div
      class="event-block"
      v-for="event in events"
      :key="event.id"
      :style="{
        left: `${getStartPercent(event.start)}%`,
        width: `${getDurationPercent(event.start, event.end)}%`,
        top: `${40 + event.level * 35}px`,
        backgroundColor: getColorForEvent(event.id)
      }"
      :title="event.title"
    >
      <span class="event-label">{{ event.title }}</span>
    </div>
  </div>
</div>

      <button class="btn-create" @click="goToCreate">➕ New Event</button>

      <ul class="event-list" v-if="events.length">
        <li v-for="event in sortedEvents" :key="event.id" class="event-item">
          <div class="event-header">
            <strong>{{ event.title }}</strong>
            <span class="event-time">{{ formatTime(event.start) }} - {{ formatTime(event.end) }}</span>
          </div>
          <p class="event-details">
            Date: {{ formatDate(event.start) }}<br>
            Category: {{ event.category || 'No Category' }}<br>
            Contacts: {{ formatContacts(event.contacts) }}
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
import dayjs from 'dayjs'
import api from '../api/api'
import { useToast } from 'vue-toastification'


const toast = useToast()
const route = useRoute()
const router = useRouter()
const date = route.query.date
const formattedDate = dayjs(date).format('DD [de] MMMM [de] YYYY')

const events = ref([])

const formatDate = (datetime) => dayjs(datetime).format('DD/MM/YYYY')
const formatTime = (datetime) => dayjs(datetime).format('HH:mm')
const formatContacts = (contacts) => {
  if (!contacts || !contacts.length) return ''
  return contacts.map(c => c.name || c.email).join(', ')
}

const loadEvents = async () => {
  try {
    console.log('🔄 Cargando eventos...')

    const { data } = await api.get('/events/my-events')
    console.log('✅ Eventos recibidos:', data)

    // Filtrar eventos de ese día
    const filtered = data.filter(ev =>
      dayjs(ev.start).format('YYYY-MM-DD') === dayjs(date).format('YYYY-MM-DD')
    )
    console.log(`📅 Eventos para el día ${date}:`, filtered)

    //events.value = filtered
    events.value = assignLevelsToEvents(filtered)
  } catch (error) {
    console.error('❌ Error cargando eventos:', error)
  }
}


const deleteEvent = async (id) => {
  if (!confirm('Are you sure you want to delete this event?')) return

  try {
    await api.delete(`/events/${id}`)
    toast.success('Event deleted successfully!')
    await loadEvents()
  } catch (error) {
    console.error('Error deleting event:', error)
    alert('No se pudo eliminar el evento.')
  }
}

const editEvent = (id) => {
  router.push({ name: 'EditEvent', params: { id } })
}

const goToCreate = () => {
  router.push({ name: 'NewEvent', query: { date } })
}

const sortedEvents = computed(() =>
  [...events.value].sort((a, b) => new Date(a.start) - new Date(b.start))
)

onMounted(() => {
  loadEvents()
})

const getEventStyle = (event) => {
  const start = dayjs(event.start)
  const end = dayjs(event.end)

  const startHour = start.hour() + start.minute() / 60
  const endHour = end.hour() + end.minute() / 60

  const left = (startHour / 24) * 100
  const width = ((endHour - startHour) / 24) * 100

  return {
    left: `${left}%`,
    width: `${width}%`
  }

}

////////////////////////////////////////77
const getStartPercent = (startTime) => {
  const hour = dayjs(startTime).hour()
  const minutes = dayjs(startTime).minute()
  return ((hour * 60 + minutes) / (24 * 60)) * 100
}

const getDurationPercent = (start, end) => {
  const startDate = dayjs(start)
  const endDate = dayjs(end)
  const durationMinutes = endDate.diff(startDate, 'minute')
  return (durationMinutes / (24 * 60)) * 100
}

const getColorForEvent = (id) => {
  const colors = ['#3498db', '#e67e22', '#16a085', '#8e44ad', '#e74c3c', '#2ecc71']
  return colors[id % colors.length]
}

const assignLevelsToEvents = (events) => {
  const levels = []
  events.forEach(event => {
    let level = 0
    while (
      levels[level]?.some(
        (e) =>
          (new Date(event.start) < new Date(e.end)) &&
          (new Date(event.end) > new Date(e.start))
      )
    ) {
      level++
    }
    if (!levels[level]) levels[level] = []
    levels[level].push(event)
    event.level = level
  })
  return events
}


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







.events-overlay {
  position: relative;
  top: 0;
  left: 0;
  height: calc(100% - 40px);
  width: 100%;
  z-index: 2;
}



.timeline-container {
  position: relative;
  margin: 2rem 0;
  border: 1px solid #ddd;
  border-radius: 10px;
  background-color: #f4f4f4;
  height: 150px;
  overflow-y: auto; /* habilita scroll si hay muchos niveles */
}

.timeline-hours {
  display: flex;
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 40px;
  border-bottom: 1px solid #ccc;
}

.timeline-bar {
  display: flex;
  position: sticky; /* aquí el truco */
  top: 0;           /* se pega arriba del contenedor */
  background-color: #eaeaea;
  height: 40px;
  border-bottom: 1px solid #ccc;
  z-index: 10; /* para que quede encima de eventos */
}

.hour-marker {
  flex: 1;
  font-size: 0.75rem;
  text-align: center;
  color: #666;
  border-left: 1px solid #eee;
  padding-top: 5px;
}



.event-block {
  position: absolute;
  height: 30px;
  border-radius: 6px;
  padding: 2px 6px;
  color: white;
  font-size: 0.8rem;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  cursor: pointer;
  box-shadow: 0 2px 5px rgba(0,0,0,0.2);
}

.event-block:hover {
  filter: brightness(1.1);
}


.event-label {
  pointer-events: none;
}


</style>
