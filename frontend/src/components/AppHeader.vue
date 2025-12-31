<template>
  <div>
    <!-- Cabeçalho para mobile -->
    <header 
      :class="[
        'shadow-sm mobile-header text-silver sticky top-0 z-50',
        isAuthenticated ? 'bg-gradient-header' : 'bg-slate-800'
      ]"
    >
      <div class="container mx-auto p-4 text-center">
        <!-- Logo centralizada e texto "Werner Advogados" abaixo na versão mobile -->
        <div class="mb-4">
          <router-link to="/" class="flex flex-col items-center">
            <img src="/LogoW.png" alt="Logo" class="title-imageLogo" />
            <span
              :class="[
                'titulo-mobile Libre-Baskerville font-bold tracking-widest',
                isAuthenticated ? 'text-white' : 'seu-texto-metalizado'
              ]"
              >Werner Advogados</span
            >
          </router-link>
        </div>
        <br />

        <!-- Links abaixo do outro -->
        <div class="mb-4">
          <router-link 
            to="/" 
            :class="['block margemcima', isAuthenticated ? 'text-white' : '']"
            :style="!isAuthenticated ? 'color: silver' : ''"
          >
            • Início
          </router-link>
          <router-link 
            to="/about" 
            :class="['block margemcima', isAuthenticated ? 'text-white' : '']"
            :style="!isAuthenticated ? 'color: silver' : ''"
          >
            • Sobre o Escritório
          </router-link>
          <router-link 
            to="/areas" 
            :class="['block margemcima', isAuthenticated ? 'text-white' : '']"
            :style="!isAuthenticated ? 'color: silver' : ''"
          >
            • Áreas de Atuação
          </router-link>
          <router-link
            to="/publicacoes"
            :class="['block margemcima', isAuthenticated ? 'text-white' : '']"
            :style="!isAuthenticated ? 'color: silver' : ''"
          >
            • Nossas Publicações
          </router-link>
          <br />
          <!-- Quando autenticado, mostra usuário e botão sair -->
          <template v-if="isAuthenticated">
            <div class="flex flex-col items-center gap-2 mt-2">
              <div class="text-white">
                <span class="font-semibold">{{ user?.username }}</span>
                <span class="text-xs opacity-90 ml-2 uppercase">{{ user?.role }}</span>
              </div>
              <button 
                @click="handleLogout" 
                class="btn-mobile font-semibold text-white bg-white bg-opacity-20 hover:bg-opacity-30"
                style="color: white; border: 1px solid rgba(255,255,255,0.3);"
              >
                Sair
              </button>
            </div>
          </template>
          <!-- Quando não autenticado, mostra link de login -->
          <router-link
            v-else
            to="/login"
            class="block margemcima btn-mobile font-semibold"
            style="color: #2e2b2b"
          >
            Acesso Restrito
          </router-link>
        </div>
      </div>
    </header>

    <!-- Cabeçalho para desktop -->
    <header 
      :class="[
        'shadow-sm desktop-header sticky top-0 z-50',
        isAuthenticated ? 'bg-gradient-header' : 'bg-white'
      ]"
    >
      <nav class="container mx-auto p-4 flex justify-between">
        <!-- Logo e texto "Werner Advogados" lado a lado -->
        <router-link
          to="/"
          :class="[
            'large-title lexend-deca tracking-widest title-with-image hover:font-semibold',
            isAuthenticated ? 'text-white' : 'seu-texto-metalizado'
          ]"
        >
          <img src="/LogoW.png" alt="Logo" class="title-imageLogo" />
          Werner Advogados
        </router-link>

        <!-- Links para desktop -->
        <ul class="flex gap-4 items-center">
          <li>
            <router-link 
              :to="isAuthenticated ? '/dashboard' : '/'" 
              :class="['margemcima hover:font-bold', isAuthenticated ? 'text-white' : '']"
            >
              • Início
            </router-link>
          </li>
          <li>
            <router-link 
              to="/about" 
              :class="['margemcima hover:font-bold', isAuthenticated ? 'text-white' : '']"
            >
              • Sobre o Escritório
            </router-link>
          </li>
          <li>
            <router-link 
              to="/areas" 
              :class="['margemcima hover:font-bold', isAuthenticated ? 'text-white' : '']"
            >
              • Áreas de Atuação
            </router-link>
          </li>
          <li>
            <router-link 
              to="/publicacoes" 
              :class="['margemcima hover:font-bold', isAuthenticated ? 'text-white' : '']"
            >
              • Nossas Publicações
            </router-link>
          </li>
          <li>
            <!-- Quando autenticado, mostra usuário e botão sair -->
            <template v-if="isAuthenticated">
              <div class="flex items-center gap-3">
                <div class="flex flex-col items-end text-white">
                  <span class="font-semibold text-sm">{{ user?.username }}</span>
                  <span class="text-xs opacity-90 uppercase">{{ user?.role }}</span>
                </div>
                <button 
                  @click="handleLogout" 
                  class="logout-button-desktop"
                >
                  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="w-4 h-4">
                    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <polyline points="16 17 21 12 16 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <line x1="21" y1="12" x2="9" y2="12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  Sair
                </button>
              </div>
            </template>
            <!-- Quando não autenticado, mostra link de login -->
            <router-link 
              v-else
              to="/login" 
              :class="['btn hover:font-bold', isAuthenticated ? 'text-white' : '']"
            >
              Acesso Restrito
            </router-link>
          </li>
        </ul>
      </nav>
    </header>
  </div>
</template>

<script>
import { authService } from '../services/authService'
import router from '../router'

export default {
  name: 'AppHeader',
  data() {
    return {
      user: null,
      isAuthenticated: false
    }
  },
  watch: {
    '$route'() {
      this.updateAuthState()
    }
  },
  mounted() {
    this.updateAuthState()
    // Observar mudanças no localStorage para atualizar o header quando o usuário fizer login/logout
    window.addEventListener('storage', this.updateAuthState)
    // Também verificar periodicamente (para mudanças na mesma aba)
    this.authCheckInterval = setInterval(() => {
      this.updateAuthState()
    }, 300)
  },
  beforeUnmount() {
    window.removeEventListener('storage', this.updateAuthState)
    if (this.authCheckInterval) {
      clearInterval(this.authCheckInterval)
    }
  },
  methods: {
    updateAuthState() {
      this.isAuthenticated = authService.isAuthenticated()
      this.user = authService.getUser()
    },
    handleLogout() {
      authService.logout()
      this.updateAuthState()
      router.push('/login')
    }
  }
}
</script>

<style scoped>
.bg-gradient-header {
  background: linear-gradient(135deg, #003d7a 0%, #5a7ba8 100%);
}

.logout-button-desktop {
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

.logout-button-desktop:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-1px);
}

.logout-button-desktop:active {
  transform: translateY(0);
}

/* Mobile header/footer */
.mobile-header {
  display: block;
}
.desktop-header {
  display: none;
}

/* Desktop header/footer */
@media only screen and (min-width: 768px) {
  .mobile-header {
    display: none;
  }
  .desktop-header {
    display: block;
  }
}

.title-imageLogo {
  height: 5rem;
}

@media only screen and (min-width: 768px) {
  .title-imageLogo {
    height: 4rem;
  }
}

.margemcima {
  margin-top: 5px;
}

.titulo-mobile {
  font-size: 20px;
}

.Libre-Baskerville {
  font-family: "Libre Baskerville", serif;
}

.lexend-deca {
  font-family: "Lexend Deca", sans-serif;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
}

.flex {
  display: flex;
}

.flex-col {
  flex-direction: column;
}

.items-center {
  align-items: center;
}

.items-end {
  align-items: flex-end;
}

.justify-between {
  justify-content: space-between;
}

.gap-2 {
  gap: 0.5rem;
}

.gap-3 {
  gap: 0.75rem;
}

.gap-4 {
  gap: 1rem;
}

.text-center {
  text-align: center;
}

.text-white {
  color: white;
}

.text-xs {
  font-size: 0.75rem;
}

.text-sm {
  font-size: 0.875rem;
}

.font-semibold {
  font-weight: 600;
}

.font-bold {
  font-weight: 700;
}

.tracking-widest {
  letter-spacing: 0.1em;
}

.large-title {
  font-size: 1.5rem;
}

.title-with-image {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.mb-4 {
  margin-bottom: 1rem;
}

.mt-2 {
  margin-top: 0.5rem;
}

.ml-2 {
  margin-left: 0.5rem;
}

.p-4 {
  padding: 1rem;
}

.shadow-sm {
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.bg-slate-800 {
  background-color: #1e293b;
}

.text-silver {
  color: silver;
}

.opacity-90 {
  opacity: 0.9;
}

.uppercase {
  text-transform: uppercase;
}

.btn-mobile {
  background-color: #e6e6e6;
  padding: 0.5rem 1rem;
  border-radius: 0.375rem;
  font-size: 0.875rem;
  display: inline-block;
  color: #201f1f;
  position: relative;
}

.btn-mobile::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.1) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.1) 50%,
    rgba(255, 255, 255, 0.1) 75%,
    transparent 75%,
    transparent
  );
  background-size: 3px 3px;
  z-index: -1;
}

.seu-texto-metalizado {
  position: relative;
  color: #d9e0e6;
  background-image: linear-gradient(45deg, #b1c6c9, #cbd2d6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.2);
}

.hover\:font-bold:hover {
  font-weight: 700;
}

.hover\:bg-opacity-30:hover {
  background-opacity: 0.3;
}

.block {
  display: block;
}

.sticky {
  position: sticky;
}

.top-0 {
  top: 0;
}

.z-50 {
  z-index: 50;
}

.w-4 {
  width: 1rem;
}

.h-4 {
  height: 1rem;
}

a {
  text-decoration: none;
  color: inherit;
}

a:hover {
  opacity: 0.8;
}
</style>

