import { createRouter, createWebHistory } from 'vue-router'
import ClientList from '../views/ClientList.vue'
import ClientDetails from '../views/ClientDetails.vue'
import ClientForm from '../views/ClientForm.vue'
import ProcessForm from '../views/ProcessForm.vue'

const routes = [
  {
    path: '/',
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
    path: '/processes/new',
    name: 'ProcessNew',
    component: ProcessForm
  },
  {
    path: '/processes/:id/edit',
    name: 'ProcessEdit',
    component: ProcessForm
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router

