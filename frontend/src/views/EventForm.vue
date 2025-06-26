<template>
  <div class="event-form">
    <h2>{{ isEdit ? 'Edit Event' : 'Create new event for the ' + formattedDate }}</h2>

    <form @submit.prevent="submitEvent">
      <div class="form-group">
        <label>Title</label>
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

      <div class="form-group contacts-group">
        <label>Contacts</label>
        <div class="contacts-controls">
          <select v-model="selectedCategoryToBatchAdd" @change="addContactsByCategory">
            <option value="">-- Select Category to Add --</option>
            <option value="NONE">-- NONE (Clear All) --</option>
            <option
              v-for="cat in availableCategories"
              :key="cat.id"
              :value="cat.name"
            >
              {{ cat.name }}
            </option>
          </select>

          <input
            v-model="contactSearch"
            placeholder="Search contacts..."
            @input="filterContacts"
          />
        </div>
        <ul class="suggestions" v-if="filteredContacts.length">
          <li
            v-for="contact in filteredContacts"
            :key="contact.email"
            @click="addContact(contact)"
          >
            {{ contact.name }} <span class="contact-category-suggestion">({{ contact.category }})</span>
          </li>
        </ul>

        <div class="selected-contacts" v-if="form.contacts.length">
          <span
            class="contact-chip"
            v-for="contact in form.contacts"
            :key="contact.email"
          >
            {{ contact.name }} <span class="contact-category-chip">({{ contact.category }})</span>
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
import axios from 'axios' // Import axios (though you're using `api` which probably wraps it)
import { useToast } from 'vue-toastification'
import api from '../api/api' // Assuming `api` is your axiosInstance

// Router y params
const route = useRoute()
const router = useRouter()
const toast = useToast()

const id = route.params.id // <-- Si hay id, estamos editando
const isEdit = computed(() => !!id)

const date = route.query.date || dayjs().format('YYYY-MM-DD')
const formattedDate = dayjs(date).format('DD [de] MMMM [de] YYYY')

const allContacts = ref([]) // Renombrado a allContacts para almacenar todos los contactos cargados
const availableCategories = ref([]) // Para almacenar las categorías disponibles
const selectedCategoryToBatchAdd = ref('') // Para el valor seleccionado en el nuevo desplegable

const form = ref({
  title: '',
  startTime: '',
  endTime: '',
  category: '', // Esta es la categoría del EVENTO, no de los contactos
  contacts: []
})

const contactSearch = ref('')
const filteredContacts = ref([])

// Función para filtrar contactos en la búsqueda individual
const filterContacts = () => {
  const search = contactSearch.value.toLowerCase().trim()
  if (!search) {
    filteredContacts.value = []
    return
  }

  // Filtrar de `allContacts` y asegurarse de que no estén ya en `form.contacts`
  filteredContacts.value = allContacts.value.filter(
    c =>
      c.name.toLowerCase().includes(search) &&
      !form.value.contacts.some(fc => fc.email === c.email)
  )
}

// Función para añadir un contacto individual
const addContact = (contact) => {
  // Asegurarse de no añadir duplicados
  if (!form.value.contacts.some(c => c.email === contact.email)) {
    form.value.contacts.push(contact)
  }
  contactSearch.value = ''
  filteredContacts.value = []
}

// Función para eliminar un contacto individual
const removeContact = (contact) => {
  form.value.contacts = form.value.contacts.filter(c => c.email !== contact.email)
}

// NUEVA FUNCIÓN: Añadir contactos por categoría
const addContactsByCategory = () => {
  const selectedCatName = selectedCategoryToBatchAdd.value;

  if (selectedCatName === 'NONE') {
    form.value.contacts = []; // Eliminar todos los contactos
  } else if (selectedCatName) {
    // Filtrar todos los contactos que pertenecen a la categoría seleccionada
    const contactsToAdd = allContacts.value.filter(
      contact => contact.category === selectedCatName
    );

    // Añadir solo los contactos que no estén ya en la lista `form.contacts`
    contactsToAdd.forEach(contact => {
      if (!form.value.contacts.some(c => c.email === contact.email)) {
        form.value.contacts.push(contact);
      }
    });
  }
  // Resetear el desplegable después de la acción (opcional, pero buena UX)
  selectedCategoryToBatchAdd.value = '';
};

onMounted(async () => {
  try {
    // 1. Cargar TODOS los contactos del usuario (con sus categorías)
    const { data: contactsData } = await api.get('/contacts')
    allContacts.value = contactsData.map(contact => ({
      name: contact.name,
      email: contact.email,
      // Asegurarse de que category aquí es el nombre de la categoría
      category: contact.category ? contact.category.name : 'No Category'
    }))
    console.log('Todos los contactos cargados:', allContacts.value)

    // 2. Cargar las categorías disponibles para el desplegable
    const { data: categoriesData } = await api.get('events/categories')
    availableCategories.value = categoriesData.filter(cat => cat.name !== 'No Category'); // No Category no es una categoría real que puedas seleccionar

    console.log('Categorías disponibles:', availableCategories.value)

  } catch (err) {
    toast.error('Could not load contacts or categories.')
    console.error('Error cargando contactos o categorías:', err)
  }

  // Si estamos editando un evento, cargar sus datos
  if (isEdit.value) {
    try {
      const { data } = await api.get(`/events/${id}`)
      form.value = {
        title: data.title,
        startTime: dayjs(data.start).format('HH:mm'),
        endTime: dayjs(data.end).format('HH:mm'),
        category: data.category || '',
        // Aquí debes cargar los contactos existentes del evento
        // Asegúrate de que `data.contacts` contenga el `name` y `email`
        // y también la `category` si quieres que se muestre en el chip
        contacts: data.contacts ? data.contacts.map(contact => {
          // Busca el contacto completo en allContacts para obtener su categoría
          const fullContact = allContacts.value.find(c => c.email === contact.email);
          return {
            name: contact.name,
            email: contact.email,
            category: fullContact ? fullContact.category : 'No Category'
          };
        }) : []
      }
    } catch (err) {
      toast.error('Error cargando el evento.')
      router.push({ name: 'DayView', query: { date } })
    }
  }
})

const submitEvent = async () => {
  const start = dayjs(`${date}T${form.value.startTime}`)
  const end = dayjs(`${date}T${form.value.endTime}`)

  if (!start.isBefore(end)) {
    toast.error('The start time must be before the end time.')
    return
  }

  const payload = {
    title: form.value.title,
    description: '', // Puedes agregar un campo en el formulario si lo quieres usar
    start: `${date}T${form.value.startTime}`,
    end: `${date}T${form.value.endTime}`,
    category: form.value.category,
    contacts: form.value.contacts.map(contact => contact.email)
  }

  try {
    if (isEdit.value) {
      await api.put(`/events/${id}`, payload)
      toast.success('Event updated successfully!')
    } else {
      await api.post('/events', payload)
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

.contacts-group .contacts-controls {
  display: flex;
  gap: 10px; /* Espacio entre el select y el input */
  margin-bottom: 5px;
}

.contacts-group .contacts-controls select,
.contacts-group .contacts-controls input {
  flex: 1; /* Para que ocupen el espacio disponible */
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
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.suggestions li:hover {
  background-color: #e0e0e0;
}

.contact-category-suggestion {
  font-size: 0.8em;
  color: #666;
  margin-left: 5px;
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
  font-size: 0.9em;
}

.contact-chip button {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  padding: 0; /* Eliminar padding extra del botón dentro del chip */
  margin: 0; /* Eliminar margin extra del botón dentro del chip */
  line-height: 1; /* Ajustar altura de línea */
}

.contact-category-chip {
  font-size: 0.9em;
  color: rgba(255, 255, 255, 0.8);
  margin-left: 3px;
  font-weight: normal;
}
</style>