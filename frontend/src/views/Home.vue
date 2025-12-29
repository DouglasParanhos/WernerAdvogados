<template>
  <div class="home">
    <div class="home-container">
      <div class="home-header">
        <div class="logo-container">
          <img 
            src="/logo-w.png" 
            alt="Werner Advogados Logo" 
            class="logo-img"
            @error="handleImageError"
            :style="{ display: imageError ? 'none' : 'block' }"
          />
          <div v-if="imageError" class="logo-placeholder">
            <svg viewBox="0 0 200 200" class="logo-svg-placeholder">
              <!-- Left V (darker blue) -->
              <path d="M 20 180 L 20 40 L 60 40 L 60 100 L 100 100 L 100 40 L 140 40 L 140 180 L 100 180 L 100 120 L 60 120 L 60 180 Z" 
                    fill="url(#metallicGradientLeft)" 
                    stroke="rgba(255,255,255,0.3)" 
                    stroke-width="1"/>
              <!-- Right V (lighter silver) -->
              <path d="M 100 100 L 140 40 L 180 40 L 180 180 L 140 180 L 140 120 L 100 120 Z" 
                    fill="url(#metallicGradientRight)" 
                    stroke="rgba(255,255,255,0.4)" 
                    stroke-width="1"/>
              <!-- Light source effect at center -->
              <circle cx="100" cy="100" r="15" fill="url(#lightSource)" opacity="0.8"/>
              <!-- Gradients -->
              <defs>
                <linearGradient id="metallicGradientLeft" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" style="stop-color:#1e3a5f;stop-opacity:1" />
                  <stop offset="50%" style="stop-color:#2d4a6b;stop-opacity:1" />
                  <stop offset="100%" style="stop-color:#003d7a;stop-opacity:1" />
                </linearGradient>
                <linearGradient id="metallicGradientRight" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" style="stop-color:#c0c8d0;stop-opacity:1" />
                  <stop offset="50%" style="stop-color:#e8eef5;stop-opacity:1" />
                  <stop offset="100%" style="stop-color:#d0d8e0;stop-opacity:1" />
                </linearGradient>
                <radialGradient id="lightSource" cx="50%" cy="50%">
                  <stop offset="0%" style="stop-color:#ffffff;stop-opacity:1" />
                  <stop offset="100%" style="stop-color:#ffffff;stop-opacity:0" />
                </radialGradient>
              </defs>
            </svg>
          </div>
        </div>
        <h1>Werner Advogados</h1>
        <p class="subtitle">Sistema de Gestão Processual</p>
      </div>
      
      <div class="menu-grid">
        <button @click="goToClients" class="menu-card clients-card">
          <div class="card-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" stroke="url(#iconGradient1)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <circle cx="9" cy="7" r="4" stroke="url(#iconGradient1)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M23 21v-2a4 4 0 0 0-3-3.87" stroke="url(#iconGradient1)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M16 3.13a4 4 0 0 1 0 7.75" stroke="url(#iconGradient1)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <defs>
                <linearGradient id="iconGradient1" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" style="stop-color:#003d7a;stop-opacity:1" />
                  <stop offset="50%" style="stop-color:#5a7ba8;stop-opacity:1" />
                  <stop offset="100%" style="stop-color:#9db4d4;stop-opacity:1" />
                </linearGradient>
              </defs>
            </svg>
          </div>
          <h2>Clientes</h2>
          <p>Gerenciar clientes e informações</p>
        </button>
        
        <button @click="goToProcesses" class="menu-card processes-card">
          <div class="card-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" stroke="url(#iconGradient2)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="14 2 14 8 20 8" stroke="url(#iconGradient2)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="16" y1="13" x2="8" y2="13" stroke="url(#iconGradient2)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="16" y1="17" x2="8" y2="17" stroke="url(#iconGradient2)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="10 9 9 9 8 9" stroke="url(#iconGradient2)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <defs>
                <linearGradient id="iconGradient2" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" style="stop-color:#003d7a;stop-opacity:1" />
                  <stop offset="50%" style="stop-color:#5a7ba8;stop-opacity:1" />
                  <stop offset="100%" style="stop-color:#9db4d4;stop-opacity:1" />
                </linearGradient>
              </defs>
            </svg>
          </div>
          <h2>Processos</h2>
          <p>Visualizar e gerenciar processos</p>
        </button>
        
        <button @click="goToTasks" class="menu-card tasks-card">
          <div class="card-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" stroke="url(#iconGradient3)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="22 4 12 14.01 9 11.01" stroke="url(#iconGradient3)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <defs>
                <linearGradient id="iconGradient3" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" style="stop-color:#003d7a;stop-opacity:1" />
                  <stop offset="50%" style="stop-color:#5a7ba8;stop-opacity:1" />
                  <stop offset="100%" style="stop-color:#9db4d4;stop-opacity:1" />
                </linearGradient>
              </defs>
            </svg>
          </div>
          <h2>Tarefas</h2>
          <p>Gerenciar tarefas e atividades</p>
        </button>
        
        <button @click="goToStatistics" class="menu-card statistics-card">
          <div class="card-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="3" y="12" width="4" height="8" rx="1" fill="url(#iconGradient4)" stroke="url(#iconGradient4)" stroke-width="1.5"/>
              <rect x="10" y="6" width="4" height="14" rx="1" fill="url(#iconGradient4)" stroke="url(#iconGradient4)" stroke-width="1.5"/>
              <rect x="17" y="10" width="4" height="10" rx="1" fill="url(#iconGradient4)" stroke="url(#iconGradient4)" stroke-width="1.5"/>
              <line x1="2" y1="20" x2="22" y2="20" stroke="url(#iconGradient4)" stroke-width="2" stroke-linecap="round"/>
              <defs>
                <linearGradient id="iconGradient4" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" style="stop-color:#003d7a;stop-opacity:1" />
                  <stop offset="50%" style="stop-color:#5a7ba8;stop-opacity:1" />
                  <stop offset="100%" style="stop-color:#9db4d4;stop-opacity:1" />
                </linearGradient>
              </defs>
            </svg>
          </div>
          <h2>Estatísticas</h2>
          <p>Visualizar relatórios e métricas</p>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {
      imageError: false
    }
  },
  methods: {
    handleImageError() {
      this.imageError = true
    },
    goToClients() {
      this.$router.push('/clients')
    },
    goToProcesses() {
      this.$router.push('/processes')
    },
    goToTasks() {
      // Placeholder - pode ser implementado futuramente
      alert('Funcionalidade de Tarefas em desenvolvimento')
    },
    goToStatistics() {
      // Placeholder - pode ser implementado futuramente
      alert('Funcionalidade de Estatísticas em desenvolvimento')
    }
  }
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: linear-gradient(135deg, #d0d8e0 0%, #e8eef5 50%, #c0c8d0 100%);
  background-attachment: fixed;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  position: relative;
  overflow: hidden;
}

.home::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 50%, rgba(255, 255, 255, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(255, 255, 255, 0.2) 0%, transparent 50%),
    linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, transparent 100%);
  pointer-events: none;
}

.home-container {
  max-width: 1200px;
  width: 100%;
  position: relative;
  z-index: 1;
}

.home-header {
  text-align: center;
  margin-bottom: 3rem;
  color: #1a1a1a;
}

.logo-container {
  display: flex;
  justify-content: center;
  margin-bottom: 1.5rem;
}

.logo-img {
  width: 250px;
  height: auto;
  max-height: 250px;
  object-fit: contain;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.3));
  animation: logoFloat 3s ease-in-out infinite;
}

.logo-placeholder {
  width: 250px;
  height: 250px;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: logoFloat 3s ease-in-out infinite;
}

.logo-svg-placeholder {
  width: 100%;
  height: 100%;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
}

@keyframes logoFloat {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
}

.home-header h1 {
  font-size: 3rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
  color: #1a1a1a;
  text-shadow: 0 2px 4px rgba(255, 255, 255, 0.5);
  letter-spacing: -0.5px;
}

.subtitle {
  font-size: 1.25rem;
  color: #4a5568;
  font-weight: 400;
  letter-spacing: 0.5px;
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
  margin-top: 2rem;
}

.menu-card {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border: 1px solid rgba(0, 61, 122, 0.1);
  border-radius: 16px;
  padding: 2.5rem 2rem;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08), 0 2px 4px rgba(0, 0, 0, 0.05);
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-height: 220px;
  position: relative;
  overflow: hidden;
}

.menu-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #003d7a, #5a7ba8, #9db4d4);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.menu-card:hover::before {
  transform: scaleX(1);
}

.menu-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 32px rgba(0, 61, 122, 0.15), 0 4px 8px rgba(0, 0, 0, 0.1);
  border-color: rgba(0, 61, 122, 0.3);
  background: linear-gradient(135deg, #ffffff 0%, #f0f4f8 100%);
}

.menu-card:active {
  transform: translateY(-4px);
}

.card-icon {
  width: 64px;
  height: 64px;
  margin-bottom: 1rem;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-icon svg {
  width: 100%;
  height: 100%;
  filter: drop-shadow(0 2px 4px rgba(0, 61, 122, 0.2));
}

.menu-card:hover .card-icon {
  transform: scale(1.15) translateY(-4px);
  filter: drop-shadow(0 4px 8px rgba(0, 61, 122, 0.3));
}

.menu-card h2 {
  font-size: 1.75rem;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 0.75rem;
  transition: color 0.3s ease;
}

.menu-card:hover h2 {
  color: #003d7a;
}

.menu-card p {
  font-size: 1rem;
  color: #4a5568;
  line-height: 1.5;
  margin: 0;
  transition: color 0.3s ease;
}

.menu-card:hover p {
  color: #2d3748;
}

/* Responsividade */
@media (max-width: 768px) {
  .home-header h1 {
    font-size: 2rem;
  }
  
  .subtitle {
    font-size: 1rem;
  }
  
  .logo-img {
    width: 150px;
    max-height: 150px;
  }
  
  .logo-placeholder {
    width: 150px;
    height: 150px;
  }
  
  .menu-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .menu-card {
    padding: 2rem 1.5rem;
    min-height: 180px;
  }
  
  .card-icon {
    width: 48px;
    height: 48px;
  }
  
  .menu-card h2 {
    font-size: 1.5rem;
  }
}
</style>

