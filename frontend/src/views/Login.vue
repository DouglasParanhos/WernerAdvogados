<template>
  <div class="login-container">
    <div class="login-card">
      <h1 class="login-title">Sistema de Gestão</h1>
      <h2 class="login-subtitle">Faça login para continuar</h2>
      
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="username">Usuário</label>
          <input
            id="username"
            v-model="username"
            type="text"
            class="form-input"
            :class="{ 'error': errors.username }"
            placeholder="Digite seu usuário"
            required
            autocomplete="username"
          />
          <span v-if="errors.username" class="error-message">{{ errors.username }}</span>
        </div>
        
        <div class="form-group">
          <label for="password">Senha</label>
          <input
            id="password"
            v-model="password"
            type="password"
            class="form-input"
            :class="{ 'error': errors.password }"
            placeholder="Digite sua senha"
            required
            autocomplete="current-password"
          />
          <span v-if="errors.password" class="error-message">{{ errors.password }}</span>
        </div>
        
        <div v-if="errorMessage" class="error-alert">
          {{ errorMessage }}
        </div>
        
        <button type="submit" class="login-button" :disabled="loading">
          <span v-if="loading">Entrando...</span>
          <span v-else>Entrar</span>
        </button>
      </form>
    </div>
  </div>
</template>

<script>
import { authService } from '../services/authService'
import router from '../router'

export default {
  name: 'Login',
  data() {
    return {
      username: '',
      password: '',
      errors: {},
      errorMessage: '',
      loading: false
    }
  },
  mounted() {
    // Se já estiver autenticado, redirecionar baseado na role
    if (authService.isAuthenticated()) {
      const user = authService.getUser()
      if (user && user.role === 'CLIENT') {
        router.push('/my-moviments')
      } else {
        router.push('/dashboard')
      }
    }
  },
  methods: {
    validateForm() {
      this.errors = {}
      
      if (!this.username.trim()) {
        this.errors.username = 'Usuário é obrigatório'
      }
      
      if (!this.password) {
        this.errors.password = 'Senha é obrigatória'
      }
      
      return Object.keys(this.errors).length === 0
    },
    
    async handleLogin() {
      this.errorMessage = ''
      
      if (!this.validateForm()) {
        return
      }
      
      this.loading = true
      
      try {
        const response = await authService.login(this.username, this.password)
        // Redirecionar baseado na role
        const user = authService.getUser()
        if (user && user.role === 'CLIENT') {
          router.push('/my-moviments')
        } else {
          router.push('/dashboard')
        }
      } catch (error) {
        if (error.response && error.response.status === 401) {
          this.errorMessage = 'Credenciais inválidas. Verifique seu usuário e senha.'
        } else {
          this.errorMessage = 'Erro ao fazer login. Tente novamente.'
        }
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #d0d8e0 0%, #e8eef5 50%, #c0c8d0 100%);
  background-attachment: fixed;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.login-container::before {
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

.login-card {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border: 1px solid rgba(0, 61, 122, 0.1);
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08), 0 2px 4px rgba(0, 0, 0, 0.05);
  width: 100%;
  max-width: 400px;
  position: relative;
  z-index: 1;
}

.login-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #003d7a, #5a7ba8, #9db4d4);
  border-radius: 16px 16px 0 0;
}

.login-title {
  font-size: 2rem;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 8px;
  text-align: center;
  text-shadow: 0 2px 4px rgba(255, 255, 255, 0.5);
  letter-spacing: -0.5px;
}

.login-subtitle {
  font-size: 1rem;
  color: #4a5568;
  margin-bottom: 32px;
  text-align: center;
  font-weight: 400;
  letter-spacing: 0.5px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #1a1a1a;
}

.form-input {
  padding: 12px 16px;
  border: 2px solid rgba(0, 61, 122, 0.1);
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.2s;
  background: white;
  color: #1a1a1a;
}

.form-input:focus {
  outline: none;
  border-color: #003d7a;
  box-shadow: 0 0 0 3px rgba(0, 61, 122, 0.1);
}

.form-input.error {
  border-color: #dc3545;
}

.error-message {
  color: #dc3545;
  font-size: 12px;
}

.error-alert {
  background-color: #fff5f5;
  border: 1px solid #fcc;
  color: #c82333;
  padding: 12px;
  border-radius: 8px;
  font-size: 14px;
  text-align: center;
}

.login-button {
  padding: 14px;
  background: linear-gradient(135deg, #003d7a 0%, #5a7ba8 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 8px;
  box-shadow: 0 4px 12px rgba(0, 61, 122, 0.15), 0 2px 4px rgba(0, 0, 0, 0.1);
}

.login-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #002d5c 0%, #4a6b9a 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(0, 61, 122, 0.2), 0 4px 8px rgba(0, 0, 0, 0.15);
}

.login-button:active:not(:disabled) {
  transform: translateY(0);
}

.login-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

@media (max-width: 768px) {
  .login-card {
    padding: 30px 20px;
  }
  
  .login-title {
    font-size: 1.75rem;
  }
  
  .login-subtitle {
    font-size: 0.9rem;
  }
}
</style>

