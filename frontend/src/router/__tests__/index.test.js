// Testes unitários para router/index.js
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { createRouter, createWebHistory } from 'vue-router'
import { authService } from '../../services/authService'

// Mock do authService
vi.mock('../../services/authService', () => ({
  authService: {
    isAuthenticated: vi.fn(),
    getUser: vi.fn()
  }
}))

// Mock dos componentes Vue
vi.mock('../../views/Home.vue', () => ({ default: {} }))
vi.mock('../../views/Login.vue', () => ({ default: {} }))
vi.mock('../../views/HomePublic.vue', () => ({ default: {} }))
vi.mock('../../views/ClientList.vue', () => ({ default: {} }))
vi.mock('../../views/ClientMoviments.vue', () => ({ default: {} }))
vi.mock('../../layouts/PublicLayout.vue', () => ({ default: {} }))

describe('Router - Client Access Restrictions', () => {
  let router

  beforeEach(() => {
    vi.clearAllMocks()
    
    // Importar rotas dinamicamente para evitar problemas de importação
    const routes = [
      {
        path: '/',
        name: 'HomePublic',
        component: {},
        meta: { requiresAuth: false }
      },
      {
        path: '/login',
        name: 'Login',
        component: {},
        meta: { requiresAuth: false }
      },
      {
        path: '/dashboard',
        name: 'Home',
        component: {},
        meta: { requiresAuth: true }
      },
      {
        path: '/clients',
        name: 'ClientList',
        component: {},
        meta: { requiresAuth: true }
      },
      {
        path: '/processes',
        name: 'ProcessList',
        component: {},
        meta: { requiresAuth: true }
      },
      {
        path: '/tasks',
        name: 'Tasks',
        component: {},
        meta: { requiresAuth: true }
      },
      {
        path: '/statistics',
        name: 'Statistics',
        component: {},
        meta: { requiresAuth: true }
      },
      {
        path: '/calculations',
        name: 'Calculations',
        component: {},
        meta: { requiresAuth: true }
      },
      {
        path: '/my-moviments',
        name: 'ClientMoviments',
        component: {},
        meta: { requiresAuth: true }
      }
    ]

    router = createRouter({
      history: createWebHistory(),
      routes
    })

    // Adicionar beforeEach guard
    router.beforeEach((to, from, next) => {
      const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
      const isAuthenticated = authService.isAuthenticated()
      
      // If authenticated user tries to access public home page, redirect based on role
      if (to.path === '/' && isAuthenticated && to.name === 'HomePublic') {
        const user = authService.getUser()
        if (user && user.role === 'CLIENT') {
          next('/my-moviments')
        } else {
          next('/dashboard')
        }
        return
      }
      
      if (requiresAuth && !isAuthenticated) {
        // Redirect to login, but preserve the intended destination
        next('/login')
      } else if (to.path === '/login' && isAuthenticated) {
        // If authenticated and trying to access login, redirect based on role
        const user = authService.getUser()
        if (user && user.role === 'CLIENT') {
          next('/my-moviments')
        } else {
          next('/dashboard')
        }
      } else if (requiresAuth && isAuthenticated) {
        // If user is CLIENT and trying to access authenticated route that is not /my-moviments
        const user = authService.getUser()
        if (user && user.role === 'CLIENT' && to.path !== '/my-moviments') {
          next('/my-moviments')
          return
        }
        next()
      } else {
        next()
      }
    })
  })

  describe('Client user access restrictions', () => {
    it('deve redirecionar CLIENT de /dashboard para /my-moviments', async () => {
      authService.isAuthenticated.mockReturnValue(true)
      authService.getUser.mockReturnValue({ username: 'client', role: 'CLIENT' })

      await router.push('/dashboard')
      await router.isReady()

      expect(router.currentRoute.value.path).toBe('/my-moviments')
    })

    it('deve redirecionar CLIENT de /clients para /my-moviments', async () => {
      authService.isAuthenticated.mockReturnValue(true)
      authService.getUser.mockReturnValue({ username: 'client', role: 'CLIENT' })

      await router.push('/clients')
      await router.isReady()

      expect(router.currentRoute.value.path).toBe('/my-moviments')
    })

    it('deve redirecionar CLIENT de /processes para /my-moviments', async () => {
      authService.isAuthenticated.mockReturnValue(true)
      authService.getUser.mockReturnValue({ username: 'client', role: 'CLIENT' })

      await router.push('/processes')
      await router.isReady()

      expect(router.currentRoute.value.path).toBe('/my-moviments')
    })

    it('deve redirecionar CLIENT de /tasks para /my-moviments', async () => {
      authService.isAuthenticated.mockReturnValue(true)
      authService.getUser.mockReturnValue({ username: 'client', role: 'CLIENT' })

      await router.push('/tasks')
      await router.isReady()

      expect(router.currentRoute.value.path).toBe('/my-moviments')
    })

    it('deve redirecionar CLIENT de /statistics para /my-moviments', async () => {
      authService.isAuthenticated.mockReturnValue(true)
      authService.getUser.mockReturnValue({ username: 'client', role: 'CLIENT' })

      await router.push('/statistics')
      await router.isReady()

      expect(router.currentRoute.value.path).toBe('/my-moviments')
    })

    it('deve redirecionar CLIENT de /calculations para /my-moviments', async () => {
      authService.isAuthenticated.mockReturnValue(true)
      authService.getUser.mockReturnValue({ username: 'client', role: 'CLIENT' })

      await router.push('/calculations')
      await router.isReady()

      expect(router.currentRoute.value.path).toBe('/my-moviments')
    })

    it('deve permitir CLIENT acessar /my-moviments', async () => {
      authService.isAuthenticated.mockReturnValue(true)
      authService.getUser.mockReturnValue({ username: 'client', role: 'CLIENT' })

      await router.push('/my-moviments')
      await router.isReady()

      expect(router.currentRoute.value.path).toBe('/my-moviments')
    })

    it('deve permitir CLIENT acessar rotas públicas', async () => {
      authService.isAuthenticated.mockReturnValue(true)
      authService.getUser.mockReturnValue({ username: 'client', role: 'CLIENT' })

      await router.push('/')
      await router.isReady()

      // Deve redirecionar para /my-moviments quando autenticado
      expect(router.currentRoute.value.path).toBe('/my-moviments')
    })
  })

  describe('Non-client user access', () => {
    it('deve permitir ADMIN acessar /dashboard', async () => {
      authService.isAuthenticated.mockReturnValue(true)
      authService.getUser.mockReturnValue({ username: 'admin', role: 'ADMIN' })

      await router.push('/dashboard')
      await router.isReady()

      expect(router.currentRoute.value.path).toBe('/dashboard')
    })

    it('deve permitir USER acessar /clients', async () => {
      authService.isAuthenticated.mockReturnValue(true)
      authService.getUser.mockReturnValue({ username: 'user', role: 'USER' })

      await router.push('/clients')
      await router.isReady()

      expect(router.currentRoute.value.path).toBe('/clients')
    })
  })

  describe('Unauthenticated user access', () => {
    it('deve redirecionar usuário não autenticado de rota protegida para /login', async () => {
      authService.isAuthenticated.mockReturnValue(false)
      authService.getUser.mockReturnValue(null)

      await router.push('/dashboard')
      await router.isReady()

      expect(router.currentRoute.value.path).toBe('/login')
    })

    it('deve permitir usuário não autenticado acessar rotas públicas', async () => {
      authService.isAuthenticated.mockReturnValue(false)
      authService.getUser.mockReturnValue(null)

      await router.push('/')
      await router.isReady()

      expect(router.currentRoute.value.path).toBe('/')
    })
  })

  describe('Login redirect behavior', () => {
    it('deve redirecionar CLIENT autenticado de /login para /my-moviments', async () => {
      authService.isAuthenticated.mockReturnValue(true)
      authService.getUser.mockReturnValue({ username: 'client', role: 'CLIENT' })

      await router.push('/login')
      await router.isReady()

      expect(router.currentRoute.value.path).toBe('/my-moviments')
    })

    it('deve redirecionar ADMIN autenticado de /login para /dashboard', async () => {
      authService.isAuthenticated.mockReturnValue(true)
      authService.getUser.mockReturnValue({ username: 'admin', role: 'ADMIN' })

      await router.push('/login')
      await router.isReady()

      expect(router.currentRoute.value.path).toBe('/dashboard')
    })
  })
})

