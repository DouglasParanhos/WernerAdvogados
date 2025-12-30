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
    // Se já estiver autenticado, redirecionar para home
    if (authService.isAuthenticated()) {
      router.push('/')
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
        await authService.login(this.username, this.password)
        router.push('/')
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.login-title {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  text-align: center;
}

.login-subtitle {
  font-size: 16px;
  color: #666;
  margin-bottom: 32px;
  text-align: center;
  font-weight: 400;
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
  color: #333;
}

.form-input {
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.2s;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
}

.form-input.error {
  border-color: #e74c3c;
}

.error-message {
  color: #e74c3c;
  font-size: 12px;
}

.error-alert {
  background-color: #fee;
  border: 1px solid #fcc;
  color: #c33;
  padding: 12px;
  border-radius: 8px;
  font-size: 14px;
  text-align: center;
}

.login-button {
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.2s, transform 0.2s;
  margin-top: 8px;
}

.login-button:hover:not(:disabled) {
  opacity: 0.9;
  transform: translateY(-1px);
}

.login-button:active:not(:disabled) {
  transform: translateY(0);
}

.login-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>

