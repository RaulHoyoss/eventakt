<template>
  <div class="contacts-view">
    <h2>My contacts</h2>

    <button class="btn-create" @click="goToCreate">➕ New Contact</button>

    <div v-if="contacts.length" class="contact-grid">
      <div class="contact-card" v-for="contact in contacts" :key="contact.id">
        <img :src="contact.photo || placeholderImage" alt="Foto" class="contact-photo" />
        <h3>{{ contact.name }}</h3>
        <p><strong>Email:</strong> {{ contact.email }}</p>
        <p><strong>Phone:</strong> {{ contact.phone }}</p>
        <div class="actions">
          <button @click="editContact(contact.id)">✏️ Edit</button>
          <button @click="deleteContact(contact.id)">🗑️ Delete</button>
        </div>
      </div>
    </div>

    <p v-else class="no-contacts">You have no contacts added yet.</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { useToast } from 'vue-toastification'

const contacts = ref([])
const router = useRouter()
const toast = useToast()


const defaultImage = '/images/default-avatar.png'

const placeholderImage = '/images/default-avatar.png'


/*
const loadContacts = async () => {
  try {
    const { data } = await axios.get('http://localhost:8080/api/contacts')
    contacts.value = data
  } catch (error) {
    console.error('Error cargando contactos:', error)
  }
}

*/

const loadContacts = async () => {
  try {
    const { data } = await axios.get('http://localhost:8080/api/contacts')
    contacts.value = data.length ? data : [
      {
        id: 999,
        name: 'Ana García',
        email: 'ana@example.com',
        phone: '+34 612 345 678',
        photo: null // no tiene foto, usará placeholderImage
      }
    ]
  } catch (error) {
    console.error('Error cargando contactos:', error)
    // Si falla la API, mostramos un ejemplo por defecto
    contacts.value = [
      {
        id: 999,
        name: 'Ana García',
        email: 'ana@example.com',
        phone: '+34 612 345 678',
        photo: null
      }
    ]
  }
}

const goToCreate = () => {
  router.push({ name: 'NewContact' })
}

const editContact = (id) => {
  router.push({ name: 'EditContact', params: { id } })
}

const deleteContact = async (id) => {
  if (!confirm('Are you sure you want to delete this contact?')) return
  try {
    await axios.delete(`http://localhost:8080/api/contacts/${id}`)
    await loadContacts()
  } catch (error) {
    console.error('Error deleting contact:', error)
    toast.alert('The contact could not be deleted.')
  }
}

onMounted(loadContacts)
</script>

<style scoped>
.contacts-view {
  max-width: 1000px;
  margin: 2rem auto;
  padding: 1rem;
  text-align: center;
}

.btn-create {
  margin-bottom: 1rem;
  padding: 0.6rem 1rem;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.contact-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1.5rem;
  margin-top: 1rem;
}

.contact-card {
  background: #f7f7f7;
  padding: 1rem;
  border-radius: 10px;
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.contact-photo {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 0.5rem;
}

.actions {
  margin-top: 0.8rem;
  display: flex;
  justify-content: center;
  gap: 0.5rem;
}

.actions button {
  padding: 0.4rem 0.8rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.actions button:first-child {
  background-color: #2ecc71;
  color: white;
}

.actions button:last-child {
  background-color: #e74c3c;
  color: white;
}

.no-contacts {
  font-style: italic;
  color: #7f8c8d;
}
</style>
