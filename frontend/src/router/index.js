import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import ClientList from '../views/ClientList.vue'
import ClientDetails from '../views/ClientDetails.vue'
import ClientForm from '../views/ClientForm.vue'
import ProcessList from '../views/ProcessList.vue'
import ProcessForm from '../views/ProcessForm.vue'
import ProcessDetails from '../views/ProcessDetails.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/clients',
    name: 'ClientList',
    component: ClientList
  },
  {
    path: '/clients/:id',
    name: 'ClientDetails',
    component: ClientDetails
  },
  {
    path: '/clients/new',
    name: 'ClientNew',
    component: ClientForm
  },
  {
    path: '/clients/:id/edit',
    name: 'ClientEdit',
    component: ClientForm
  },
  {
    path: '/processes',
    name: 'ProcessList',
    component: ProcessList
  },
  {
    path: '/processes/new',
    name: 'ProcessNew',
    component: ProcessForm
  },
  {
    path: '/processes/:id/edit',
    name: 'ProcessEdit',
    component: ProcessForm
  },
  {
    path: '/processes/:id',
    name: 'ProcessDetails',
    component: ProcessDetails
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router

