import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/auth/LoginView.vue'
import RegisterView from '@/views/auth/RegisterView.vue'
import CalendarView from '@/views/CalendarView.vue'
import EventForm from '@/views/EventForm.vue'
import ContactsView from '@/views/ContactsView.vue'

const routes = [
  { path: '/',
    redirect: '/login' },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView,
    meta: { requiresAuth: false }
  },
  {
    path: '/calendar',
    name: 'calendar',
    component: CalendarView,
    //meta: { requiresAuth: true }
  },

  { path: '/new', 
    name: 'NewEvent', 
    component: EventForm 
  },
  { path: '/event/:id',
     name: 'EventForm', 
     component: EventForm,
      props: true 
  },
  { path: '/contacts',
     name: 'Contacts', 
     component: ContactsView 
  },
  {
  path: '/contacts/new',
  name: 'NewContact',
  component: () => import('@/views/NewContactView.vue')
},
{
  path: '/contacts/:id',
  name: 'EditContact',
  component: () => import('@/views/NewContactView.vue'),
  props: true
},
{
  path: '/day',
  name: 'DayView',
  component: () => import('@/views/DayView.vue'),
  //meta: { requiresAuth: true }
}

]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Mueve la lógica de autenticación a un archivo aparte o usa esto:
router.beforeEach(async (to) => {
  const authStore = (await import('@/stores/auth')).useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return '/login'
  }
})

export default router