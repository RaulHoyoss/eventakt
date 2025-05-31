<template>
  <div class="event-form">
    <h2>{{ isEdit ? 'Edit Event' : 'Create new event for the ' + formattedDate }}</h2>

    <form @submit.prevent="submitEvent">
      <div class="form-group">
        <label>Tittle</label>
        <input v-model="form.title" required />
      </div>

      <div class="form-group">
        <label>Start time</label>
        <input type="time" v-model="form.startTime" required />
      </div>

      <div class="form-group">
        <label>End time</label>
        <input type="time" v-model="form.endTime" required />
      </div>

      <div class="form-group">
        <label>Category</label>
        <input v-model="form.category" placeholder="Opcional" />
      </div>

      <div class="form-group">
        <label>Contacts</label>
        <input
          v-model="contactSearch"
          placeholder="Search contacts..."
          @input="filterContacts"
        />
        <ul class="suggestions" v-if="filteredContacts.length">
          <li
            v-for="contact in filteredContacts"
            :key="contact"
            @click="addContact(contact)"
          >
            {{ contact }}
          </li>
        </ul>

    <div class="selected-contacts" v-if="form.contacts.length">
        <span
          class="contact-chip"
          v-for="contact in form.contacts"
          :key="contact"
        >
          {{ contact }}
          <button @click="removeContact(contact)">✖</button>
        </span>
      </div>
    </div>


      <button type="submit">Save event</button>
    </form>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import dayjs from 'dayjs'
import axios from 'axios'
import { useToast } from 'vue-toastification'

// Router y params
const route = useRoute()
const router = useRouter()
const toast = useToast()

const id = route.params.id // <-- Si hay id, estamos editando
const isEdit = computed(() => !!id)

const date = route.query.date || dayjs().format('YYYY-MM-DD')
const formattedDate = dayjs(date).format('DD [de] MMMM [de] YYYY')

// Contactos (simulados, reemplázalo si usas backend real)
const contacts = ref([
  'raul@example.com',
  'tutor@example.com',
  'alumno@example.com',
  'admin@example.com'
])

const form = ref({
  title: '',
  startTime: '',
  endTime: '',
  category: '',
  contacts: []
})

const contactSearch = ref('')
const filteredContacts = ref([])

const filterContacts = () => {
  const search = contactSearch.value.toLowerCase().trim()
  if (!search) {
    filteredContacts.value = []
    return
  }

  filteredContacts.value = contacts.value.filter(
    c =>
      c.toLowerCase().includes(search) &&
      !form.value.contacts.includes(c)
  )
}

const addContact = (contact) => {
  form.value.contacts.push(contact)
  contactSearch.value = ''
  filteredContacts.value = []
}

const removeContact = (contact) => {
  form.value.contacts = form.value.contacts.filter(c => c !== contact)
}

// Cargar datos si estamos editando
onMounted(async () => {
  if (isEdit.value) {
    try {
      const { data } = await axios.get(`http://localhost:8080/api/events/${id}`)
      form.value = {
        title: data.title,
        startTime: dayjs(data.start).format('HH:mm'),
        endTime: dayjs(data.end).format('HH:mm'),
        category: data.category || '',
        contacts: data.contacts || []
      }
    } catch (err) {
      toast.error('Error charging events:', err)
      router.push({ name: 'DayView', query: { date } })
    }
  }
})

const submitEvent = async () => {
  
  const start = dayjs(`${date}T${form.value.startTime}`)
  const end =   dayjs(`${date}T${form.value.endTime}`)

  if (!start.isBefore(end)){
    toast.error('The start time must be before the end time.')
    return
  }
  
  
  const payload = {
    title: form.value.title,
    start: `${date}T${form.value.startTime}`,
    end: `${date}T${form.value.endTime}`,
    category: form.value.category,
    contacts: form.value.contacts
  }

  try {
    if (isEdit.value) {
      await axios.put(`http://localhost:8080/api/events/${id}`, payload)
      toast.success('Event updated successfully!')
    } else {
      await axios.post('http://localhost:8080/api/events', payload)
      toast.success('Event created successfully!')
    }

    router.push({ name: 'DayView', query: { date } })
  } catch (error) {
    console.error('Error saving the event:', error)
    toast.error('Error saving the event.')
  }
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

.suggestions {
  border: 1px solid #ccc;
  border-radius: 6px;
  margin-top: 0.5rem;
  max-height: 150px;
  overflow-y: auto;
  list-style: none;
  padding: 0;
}

.suggestions li {
  padding: 0.5rem;
  cursor: pointer;
  background: #f9f9f9;
}

.suggestions li:hover {
  background-color: #e0e0e0;
}

.selected-contacts {
  margin-top: 0.5rem;
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.contact-chip {
  background: #3498db;
  color: white;
  padding: 0.3rem 0.6rem;
  border-radius: 20px;
  display: flex;
  align-items: center;
  gap: 0.3rem;
}

.contact-chip button {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
}

</style>
