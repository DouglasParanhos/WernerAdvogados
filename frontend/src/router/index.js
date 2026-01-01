import { createRouter, createWebHistory } from 'vue-router'
import { authService } from '../services/authService'
import PublicLayout from '../layouts/PublicLayout.vue'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import HomePublic from '../views/HomePublic.vue'
import About from '../views/About.vue'
import Areas from '../views/Areas.vue'
import Privacy from '../views/Privacy.vue'
import Interniveis from '../views/Interniveis.vue'
import ClientList from '../views/ClientList.vue'
import ClientDetails from '../views/ClientDetails.vue'
import ClientForm from '../views/ClientForm.vue'
import ProcessList from '../views/ProcessList.vue'
import ProcessForm from '../views/ProcessForm.vue'
import ProcessDetails from '../views/ProcessDetails.vue'
import Tasks from '../views/Tasks.vue'
import Statistics from '../views/Statistics.vue'
import Calculations from '../views/Calculations.vue'
import NovaEscolaCalculation from '../views/NovaEscolaCalculation.vue'

const routes = [
  // Public routes with PublicLayout
  {
    path: '/',
    component: PublicLayout,
    children: [
      {
        path: '',
        name: 'HomePublic',
        component: HomePublic,
        meta: { requiresAuth: false }
      },
      {
        path: 'about',
        name: 'About',
        component: About,
        meta: { requiresAuth: false }
      },
      {
        path: 'areas',
        name: 'Areas',
        component: Areas,
        meta: { requiresAuth: false }
      },
      {
        path: 'privacidade',
        name: 'Privacy',
        component: Privacy,
        meta: { requiresAuth: false }
      },
      {
        path: 'interniveis',
        name: 'Interniveis',
        component: Interniveis,
        meta: { requiresAuth: false }
      }
    ]
  },
  // Login route (no layout)
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  // Authenticated routes (no layout, will use App.vue header)
  {
    path: '/dashboard',
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
  },
  {
    path: '/calculations',
    name: 'Calculations',
    component: Calculations,
    meta: { requiresAuth: true }
  },
  {
    path: '/calculations/novaescola',
    name: 'NovaEscolaCalculation',
    component: NovaEscolaCalculation,
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
  
  // If authenticated user tries to access public home page, redirect to dashboard
  if (to.path === '/' && isAuthenticated && to.name === 'HomePublic') {
    next('/dashboard')
    return
  }
  
  if (requiresAuth && !isAuthenticated) {
    // Redirect to login, but preserve the intended destination
    next('/login')
  } else if (to.path === '/login' && isAuthenticated) {
    // If authenticated and trying to access login, redirect to dashboard
    next('/dashboard')
  } else {
    next()
  }
})

export default router
