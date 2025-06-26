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

      <div class="form-group category-group">
        <label for="category">Category</label>
        <select id="category" v-model="form.category" required>
          <option value="" disabled>Select a category</option>
          <option v-for="category in categories" :key="category.id" :value="category.name">
            {{ category.name }}
          </option>
        </select>
        <button type="button" class="add-category-btn" @click="showAddCategoryModal = true">+</button>
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

    <div v-if="showAddCategoryModal" class="modal-overlay" @click.self="showAddCategoryModal = false">
      <div class="modal-content">
        <h3>Add new Category</h3>
        <input v-model="newCategoryName" placeholder="Name of category" @keyup.enter="addNewCategory" />
        <div class="modal-actions">
          <button @click="addNewCategory">Add</button>
          <button @click="showAddCategoryModal = false" class="cancel-btn">Cancel</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { useToast } from 'vue-toastification'
import axiosInstance from '@/api/axios'


const route = useRoute()
const router = useRouter()
const toast = useToast()

const isEditMode = ref(false)
const form = ref({
  name: '',
  email: '',
  phone: '',
  category: ''
})
const selectedFile = ref(null)
const previewUrl = ref(null)


const categories = ref([])
const showAddCategoryModal = ref(false)
const newCategoryName = ref([''])

// Función para obtener las categorías desde el backend
const fetchCategories = async () => {
  try {
    const { data } = await axiosInstance.get('/contacts/categories')
    categories.value = data
  } catch (error) {
    console.error('Error al obtener categorías:', error)
    toast.error('Error al cargar las categorías.')
  }
}



const handleFileChange = (event) => {
  const file = event.target.files[0]
  selectedFile.value = file
  if (file) {
    previewUrl.value = URL.createObjectURL(file)
  }
}

const loadContact = async (id) => {
  try {
  const { data } = await axiosInstance.get(`/contacts/${id}`)
    form.value = {
      name: data.name,
      email: data.email,
      phone: data.phone,
      category: data.category ? data.category.name : ''
    }
    if (data.photoUrl) {
      //previewUrl.value = data.photoUrl
      previewUrl.value = `http://localhost:8081${data.photoUrl}`;
    }
  } catch (err) {
    console.error('Error al cargar contacto:', err)
    toast.error('The contact couldnt be charged.')
  }
}


const addNewCategory = async () => {
  const trimmedCategoryName = newCategoryName.value.trim();
  if (!trimmedCategoryName) {
    toast.error('El nombre de la categoría no puede estar vacío.');
    return;
  }

  // Verifica si la categoría ya existe (ignorando mayúsculas/minúsculas)
  const categoryExists = categories.value.some(
    (cat) => cat.name.toUpperCase() === trimmedCategoryName.toUpperCase()
  );

  if (categoryExists) {
    toast.info(`Category "${trimmedCategoryName}" already exist.`);
  } else {
    // Añade la nueva categoría a la lista local.
    // El ID es un marcador de posición temporal para Vue; el backend asignará el real.
    categories.value.push({ id: Date.now(), name: trimmedCategoryName });
    toast.success(`Category "${trimmedCategoryName}" add to list. It will be saved when creating/updating a contact.`);
  }

  // Selecciona la categoría recién añadida o existente en el formulario
  form.value.category = trimmedCategoryName;
  newCategoryName.value = ''; // Limpia el input del modal
  showAddCategoryModal.value = false; // Cierra el modal
};


const submitForm = async () => {
  const formData = new FormData()
  formData.append('name', form.value.name)
  formData.append('email', form.value.email)
  formData.append('phone', form.value.phone)
  formData.append('category', form.value.category)
  if (selectedFile.value) {
    formData.append('photo', selectedFile.value)
  }

  try {
    if (isEditMode.value) {
      await axiosInstance.put(
        `http://localhost:8081/api/contacts/${route.params.id}`,
        formData,
        { headers: { 'Content-Type': 'multipart/form-data' } }
      )
      toast.success('Contact updated successfully!')
    } else {
      await axiosInstance.post(
        `http://localhost:8081/api/contacts`,
        formData,
        { headers: { 'Content-Type': 'multipart/form-data' } }
      )
      toast.success('Contact created successfully!')
    }
    router.push({ name: 'Contacts' })
  } catch (err) {
    console.error('Error al guardar contacto:', err)
    alert('No se pudo guardar el contacto.')
  }
}

onMounted(async () => {
  await fetchCategories(); // Primero carga las categorías disponibles
  if (route.params.id) {
    isEditMode.value = true
    loadContact(route.params.id) // Luego carga los datos del contacto si estamos en modo edición
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

/* Estilos para el grupo de categoría con el botón "+" */
.category-group {
  display: flex;
  align-items: flex-end; /* Alinea el select y el botón al final */
  gap: 10px; /* Espacio entre el select y el botón */
}

.category-group label {
  flex-basis: 100%; /* La etiqueta ocupa todo el ancho encima del select */
  margin-bottom: 0.3rem;
}

.category-group select {
  flex-grow: 1; /* Permite que el select ocupe el espacio disponible */
}

.add-category-btn {
  padding: 0.6rem 1rem;
  background-color: #007bff; /* Color azul para el botón de añadir */
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1.2rem;
  line-height: 1;
  height: 38px; /* Ajusta la altura para que coincida con el select */
  display: flex; /* Para centrar el '+' */
  justify-content: center;
  align-items: center;
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


/* Estilos del Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: #fff;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  width: 300px;
  text-align: center;
}

.modal-content h3 {
  margin-top: 0;
  margin-bottom: 1rem;
}

.modal-content input {
  margin-bottom: 1rem;
}

.modal-actions button {
  margin: 0 0.5rem;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.modal-actions button:first-child {
  background-color: #2ecc71;
  color: white;
}

.modal-actions .cancel-btn {
  background-color: #dc3545;
  color: white;
}
</style>