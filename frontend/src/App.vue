<template>
  <div id="app">
    <header v-if="isAuthenticated" class="app-header">
      <div class="header-content">
        <div class="header-left">
          <h2 class="app-title">Werner Advogados</h2>
        </div>
        <div class="header-right">
          <div class="user-info">
            <span class="username">{{ user?.username }}</span>
            <span class="user-role">{{ user?.role }}</span>
          </div>
          <button @click="handleLogout" class="logout-button">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="16 17 21 12 16 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="21" y1="12" x2="9" y2="12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            Sair
          </button>
        </div>
      </div>
    </header>
    <main :class="{ 'with-header': isAuthenticated }">
      <router-view />
    </main>
  </div>
</template>

<script>
import { authService } from './services/authService'
import router from './router'

export default {
  name: 'App',
  data() {
    return {
      user: null
    }
  },
  computed: {
    isAuthenticated() {
      return authService.isAuthenticated()
    }
  },
  watch: {
    '$route'() {
      this.updateUser()
    }
  },
  mounted() {
    this.updateUser()
  },
  methods: {
    updateUser() {
      this.user = authService.getUser()
    },
    handleLogout() {
      authService.logout()
      router.push('/login')
    }
  }
}
</script>

<style>
@import './styles/buttons.css';

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
  background-color: #f5f5f5;
  color: #333;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  background: linear-gradient(135deg, #003d7a 0%, #5a7ba8 100%);
  color: white;
  padding: 1rem 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
}

.app-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0;
  color: white;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.user-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 0.25rem;
}

.username {
  font-weight: 600;
  font-size: 0.95rem;
}

.user-role {
  font-size: 0.8rem;
  opacity: 0.9;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.logout-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 8px;
  color: white;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.2s;
}

.logout-button:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-1px);
}

.logout-button:active {
  transform: translateY(0);
}

.logout-button svg {
  width: 18px;
  height: 18px;
}

main {
  flex: 1;
}

main.with-header {
  /* Espaço já é gerenciado pelo layout interno das views */
}

@media (max-width: 768px) {
  .app-header {
    padding: 1rem;
  }
  
  .app-title {
    font-size: 1.2rem;
  }
  
  .user-info {
    display: none;
  }
  
  .logout-button {
    padding: 0.5rem;
  }
  
  .logout-button::after {
    content: '';
  }
  
  .logout-button {
    font-size: 0;
  }
}
</style>

