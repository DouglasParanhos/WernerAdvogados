<template>
  <div v-if="show" class="modal-overlay" @click.self="close">
    <div class="modal-content small">
      <div class="modal-header">
        <h3>Configurar Login do Cliente</h3>
        <button @click="close" class="close-btn">&times;</button>
      </div>
      
      <div class="modal-body">
        <div v-if="loading" class="loading">
          Carregando...
        </div>
        
        <div v-if="error" class="error">
          {{ error }}
        </div>
        
        <form v-if="!loading && !error" @submit.prevent="save">
          <div class="form-group">
            <label for="username">Login:</label>
            <input
              id="username"
              v-model="credentials.username"
              type="text"
              :placeholder="usernamePlaceholder"
              required
            />
          </div>
          
          <div class="form-group">
            <label for="password">Senha:</label>
            <div class="password-input-group">
              <div class="password-input-wrapper">
                <input
                  id="password"
                  v-model="credentials.password"
                  :type="showPassword ? 'text' : 'password'"
                  required
                  minlength="8"
                />
                <button
                  type="button"
                  @click="togglePasswordVisibility"
                  class="password-toggle-btn"
                  :title="showPassword ? 'Ocultar senha' : 'Mostrar senha'"
                >
                  <svg v-if="showPassword" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <line x1="1" y1="1" x2="23" y2="23" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  <svg v-else viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </button>
              </div>
              <button
                type="button"
                @click="generatePassword"
                class="btn btn-secondary btn-generate"
                title="Gerar nova senha"
              >
                🔄 Gerar
              </button>
            </div>
            <small class="form-help">Senha de 8 caracteres alfanuméricos com caracteres especiais</small>
          </div>
        </form>
      </div>
      
      <div class="modal-footer">
        <button @click="close" class="btn btn-secondary" :disabled="saving">Cancelar</button>
        <button @click="save" class="btn btn-primary" :disabled="saving">
          {{ saving ? 'Salvando...' : 'Salvar' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { personService } from '../services/personService'

export default {
  name: 'ClientLoginModal',
  props: {
    show: {
      type: Boolean,
      default: false
    },
    client: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      credentials: {
        username: '',
        password: ''
      },
      usernamePlaceholder: '',
      loading: false,
      saving: false,
      error: null,
      showPassword: false
    }
  },
  watch: {
    show(newVal) {
      if (newVal && this.client?.id) {
        this.loadUsernameSuggestion()
        this.generatePassword()
      } else {
        this.reset()
      }
    }
  },
  methods: {
    async loadUsernameSuggestion() {
      if (!this.client?.id) return
      
      this.loading = true
      this.error = null
      
      try {
        const response = await personService.getUsernameSuggestion(this.client.id)
        this.usernamePlaceholder = response.username || ''
        if (!this.credentials.username) {
          this.credentials.username = response.username || ''
        }
      } catch (err) {
        console.error('Erro ao carregar sugestão de username:', err)
        // Não mostrar erro, apenas usar placeholder vazio
        this.usernamePlaceholder = ''
      } finally {
        this.loading = false
      }
    },
    
    generatePassword() {
      const characters = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*'
      let password = ''
      for (let i = 0; i < 8; i++) {
        password += characters.charAt(Math.floor(Math.random() * characters.length))
      }
      this.credentials.password = password
      this.showPassword = true // Mostrar senha gerada automaticamente
    },
    
    togglePasswordVisibility() {
      this.showPassword = !this.showPassword
    },
    
    async save() {
      if (!this.credentials.username || !this.credentials.password) {
        this.error = 'Por favor, preencha todos os campos'
        return
      }
      
      if (this.credentials.password.length < 8) {
        this.error = 'A senha deve ter no mínimo 8 caracteres'
        return
      }
      
      this.saving = true
      this.error = null
      
      try {
        await personService.configureCredentials(this.client.id, this.credentials)
        this.$emit('saved')
        this.close()
      } catch (err) {
        this.error = 'Erro ao salvar credenciais: ' + (err.response?.data?.message || err.message)
        console.error(err)
      } finally {
        this.saving = false
      }
    },
    
    close() {
      this.$emit('close')
    },
    
    reset() {
      this.credentials = {
        username: '',
        password: ''
      }
      this.usernamePlaceholder = ''
      this.error = null
      this.saving = false
      this.showPassword = false
    }
  }
}
</script>

<style scoped>
/* Estilos específicos do componente ClientLoginModal */
/* Usa classes compartilhadas de forms.css, modals.css e utilities.css */

.form-group {
  width: 100%;
  box-sizing: border-box;
}

.password-input-group {
  display: flex;
  gap: 0.5rem;
  align-items: stretch;
  width: 100%;
  box-sizing: border-box;
}

/* Força layout em coluna em telas pequenas */
@media (max-width: 600px) {
  .password-input-group {
    flex-direction: column !important;
  }
}

.password-input-wrapper {
  position: relative;
  flex: 1;
  display: flex;
  align-items: center;
  min-width: 0;
  box-sizing: border-box;
}

.password-input-wrapper input {
  flex: 1;
  padding-right: 2.5rem;
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
}

.password-toggle-btn {
  position: absolute;
  right: 0.5rem;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.25rem;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6c757d;
  transition: color 0.2s;
}

.password-toggle-btn:hover {
  color: #495057;
}

.password-toggle-btn svg {
  width: 18px;
  height: 18px;
}

.btn-generate {
  white-space: nowrap;
  font-size: 0.9rem;
}

.form-help {
  color: #666;
  font-size: 0.875rem;
  margin-top: 0.25rem;
}

/* Mobile first - ajustes para telas pequenas */
@media (max-width: 600px) {
  .password-input-group {
    flex-direction: column !important;
    gap: 0.75rem;
  }
  
  .password-input-wrapper {
    width: 100%;
  }
  
  .btn-generate {
    width: 100% !important;
    min-height: 44px;
    font-size: 1rem;
    padding: 0.75rem;
    margin-top: 0;
  }

  .password-input-wrapper input {
    min-height: 44px;
    font-size: 16px; /* Previne zoom no iOS */
  }

  .form-group input {
    min-height: 44px;
    font-size: 16px; /* Previne zoom no iOS */
  }

  .form-help {
    font-size: 0.8125rem;
    line-height: 1.4;
    margin-top: 0.5rem;
  }
}

@media (max-width: 480px) {
  .password-input-group {
    gap: 0.5rem;
  }

  .password-toggle-btn {
    right: 0.75rem;
    padding: 0.5rem;
    min-width: 44px;
    min-height: 44px;
  }

  .password-toggle-btn svg {
    width: 20px;
    height: 20px;
  }
  
  .form-group {
    margin-bottom: 1rem;
  }
}
</style>

