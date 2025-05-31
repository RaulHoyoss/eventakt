<template>
  <div class="calendar-view">
    <div class="card">
      <h1 class="calendar-title">Your Calendar</h1>
      <FullCalendar class="calendar" :options="calendarOptions" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()

const calendarOptions = ref({
  plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
  initialView: 'dayGridMonth',
  selectable: true,
  height: 'auto',
  events: async (fetchInfo, successCallback, failureCallback) => {
    try {
      const res = await axios.get('http://localhost:8080/api/events')
      successCallback(res.data)
    } catch (err) {
      failureCallback(err)
    }
  },
  dateClick: (info) => {
    router.push({ name: 'DayView', query: { date: info.dateStr } })
  },
  eventClick: (info) => {
    router.push({ name: 'EditEvent', params: { id: info.event.id } })
  },
  eventDrop: async (info) => {
    await axios.put(`http://localhost:8080/api/events/${info.event.id}`, {
      start: info.event.start,
      end: info.event.end,
    })
  },
  eventResize: async (info) => {
    await axios.put(`http://localhost:8080/api/events/${info.event.id}`, {
      start: info.event.start,
      end: info.event.end,
    })
  },
})
</script>

<style scoped>
.calendar-view {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 2rem 1rem;
  background-color: #f9f9f9;
}

.card {
  background: #fff;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 0 10px #ccc;
  width: 100%;
  max-width: 1000px;
  max-height: 90vh;
  overflow: auto;
}

.calendar-title {
  text-align: center;
  margin-bottom: 1.5rem;
  color: #2c3e50;
}

.calendar :deep(.fc) {
  font-family: 'Segoe UI', sans-serif;
  font-size: 14px;
}

.calendar :deep(.fc-daygrid-event) {
  background-color: #3498db;
  border: none;
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
}
</style>
