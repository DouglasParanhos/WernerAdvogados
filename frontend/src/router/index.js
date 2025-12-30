import { createRouter, createWebHistory } from 'vue-router'
import { authService } from '../services/authService'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import ClientList from '../views/ClientList.vue'
import ClientDetails from '../views/ClientDetails.vue'
import ClientForm from '../views/ClientForm.vue'
import ProcessList from '../views/ProcessList.vue'
import ProcessForm from '../views/ProcessForm.vue'
import ProcessDetails from '../views/ProcessDetails.vue'
import Tasks from '../views/Tasks.vue'
import Statistics from '../views/Statistics.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true }
  },
  {
    path: '/clients',
    name: 'ClientList',
    component: ClientList,
    meta: { requiresAuth: true }
  },
  {
    path: '/clients/:id',
    name: 'ClientDetails',
    component: ClientDetails,
    meta: { requiresAuth: true }
  },
  {
    path: '/clients/new',
    name: 'ClientNew',
    component: ClientForm,
    meta: { requiresAuth: true }
  },
  {
    path: '/clients/:id/edit',
    name: 'ClientEdit',
    component: ClientForm,
    meta: { requiresAuth: true }
  },
  {
    path: '/processes',
    name: 'ProcessList',
    component: ProcessList,
    meta: { requiresAuth: true }
  },
  {
    path: '/processes/new',
    name: 'ProcessNew',
    component: ProcessForm,
    meta: { requiresAuth: true }
  },
  {
    path: '/processes/:id/edit',
    name: 'ProcessEdit',
    component: ProcessForm,
    meta: { requiresAuth: true }
  },
  {
    path: '/processes/:id',
    name: 'ProcessDetails',
    component: ProcessDetails,
    meta: { requiresAuth: true }
  },
  {
    path: '/tasks',
    name: 'Tasks',
    component: Tasks,
    meta: { requiresAuth: true }
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: Statistics,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const isAuthenticated = authService.isAuthenticated()
  
  if (requiresAuth && !isAuthenticated) {
    next('/login')
  } else if (to.path === '/login' && isAuthenticated) {
    next('/')
  } else {
    next()
  }
})

export default router

