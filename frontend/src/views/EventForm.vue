<template>
  <div class="event-form">
    <h2>Crear nuevo evento para el {{ formattedDate }}</h2>

    <form @submit.prevent="submitEvent">
      <div class="form-group">
        <label>Título</label>
        <input v-model="form.title" required />
      </div>

      <div class="form-group">
        <label>Hora de inicio</label>
        <input type="time" v-model="form.startTime" required />
      </div>

      <div class="form-group">
        <label>Hora de fin</label>
        <input type="time" v-model="form.endTime" required />
      </div>

      <div class="form-group">
        <label>Categoría</label>
        <input v-model="form.category" placeholder="Opcional" />
      </div>

      <div class="form-group">
        <label>Contactos</label>
        <select multiple v-model="form.contacts">
          <option v-for="contact in contacts" :key="contact" :value="contact">
            {{ contact }}
          </option>
        </select>
      </div>

      <button type="submit">Guardar evento</button>
    </form>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()

const date = route.query.date || dayjs().format('YYYY-MM-DD')
const formattedDate = dayjs(date).format('DD [de] MMMM [de] YYYY')

// Formulario reactivo
const form = ref({
  title: '',
  startTime: '',
  endTime: '',
  category: '',
  contacts: []
})

// Contactos disponibles (simulados por ahora)
const contacts = ref([
  'raul@example.com',
  'tutor@example.com',
  'alumno@example.com',
  'admin@example.com'
])

const submitEvent = () => {
  const event = {
    title: form.value.title,
    start: `${date}T${form.value.startTime}`,
    end: `${date}T${form.value.endTime}`,
    category: form.value.category,
    contacts: form.value.contacts
  }

  // Aquí puedes hacer axios.post(...) si tienes backend
  console.log('Evento creado:', event)

  // Redirigir a la vista del día
  router.push({ name: 'DayView', query: { date } })
}
</script>

<style scoped>
.event-form {
  max-width: 600px;
  margin: 2rem auto;
  background: #fff;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 0 10px #ccc;
}

h2 {
  text-align: center;
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1rem;
}

label {
  display: block;
  font-weight: bold;
  margin-bottom: 0.25rem;
}

input,
select {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 6px;
}

button {
  display: block;
  width: 100%;
  padding: 0.75rem;
  background-color: #2ecc71;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  margin-top: 1rem;
}
</style>
