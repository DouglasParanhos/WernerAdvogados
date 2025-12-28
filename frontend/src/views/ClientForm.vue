<template>
  <div class="client-form">
    <div class="container">
      <div class="header">
        <button @click="goBack" class="btn btn-secondary">← Voltar</button>
        <h1>{{ isEdit ? 'Editar Cliente' : 'Novo Cliente' }}</h1>
      </div>
      
      <div v-if="loading" class="loading">Carregando...</div>
      <div v-if="error" class="error">{{ error }}</div>
      
      <form v-if="!loading" @submit.prevent="save" class="form">
        <div class="section">
          <h2>Dados Pessoais</h2>
          <div class="form-grid">
            <div class="form-group">
              <label>Nome Completo *</label>
              <input v-model="form.fullname" type="text" required />
              <span v-if="errors.fullname" class="error-text">{{ errors.fullname }}</span>
            </div>
            
            <div class="form-group">
              <label>Email *</label>
              <input v-model="form.email" type="email" required />
              <span v-if="errors.email" class="error-text">{{ errors.email }}</span>
            </div>
            
            <div class="form-group">
              <label>CPF *</label>
              <input v-model="form.cpf" type="text" required />
              <span v-if="errors.cpf" class="error-text">{{ errors.cpf }}</span>
            </div>
            
            <div class="form-group">
              <label>RG *</label>
              <input v-model="form.rg" type="text" required />
              <span v-if="errors.rg" class="error-text">{{ errors.rg }}</span>
            </div>
            
            <div class="form-group">
              <label>Estado Civil *</label>
              <select v-model="form.estadoCivil" required>
                <option value="">Selecione</option>
                <option value="SOLTEIRO">Solteiro</option>
                <option value="CASADO">Casado</option>
                <option value="DIVORCIADO">Divorciado</option>
                <option value="VIUVO">Viúvo</option>
                <option value="UNIAO_ESTAVEL">União Estável</option>
              </select>
              <span v-if="errors.estadoCivil" class="error-text">{{ errors.estadoCivil }}</span>
            </div>
            
            <div class="form-group">
              <label>Data de Nascimento *</label>
              <input v-model="form.dataNascimento" type="date" required />
              <span v-if="errors.dataNascimento" class="error-text">{{ errors.dataNascimento }}</span>
            </div>
            
            <div class="form-group">
              <label>Profissão *</label>
              <input v-model="form.profissao" type="text" required />
              <span v-if="errors.profissao" class="error-text">{{ errors.profissao }}</span>
            </div>
            
            <div class="form-group">
              <label>Telefone *</label>
              <input v-model="form.telefone" type="text" required />
              <span v-if="errors.telefone" class="error-text">{{ errors.telefone }}</span>
            </div>
            
            <div class="form-group">
              <label>Vivo *</label>
              <select v-model="form.vivo" required>
                <option :value="null">Selecione</option>
                <option :value="true">Sim</option>
                <option :value="false">Não</option>
              </select>
              <span v-if="errors.vivo" class="error-text">{{ errors.vivo }}</span>
            </div>
            
            <div class="form-group">
              <label>Representante</label>
              <input v-model="form.representante" type="text" />
            </div>
            
            <div class="form-group">
              <label>ID Funcional</label>
              <input v-model="form.idFuncional" type="text" />
            </div>
            
            <div class="form-group">
              <label>Nacionalidade</label>
              <input v-model="form.nacionalidade" type="text" />
            </div>
          </div>
        </div>
        
        <div class="section">
          <h2>Endereço</h2>
          <div class="form-grid">
            <div class="form-group">
              <label>Logradouro</label>
              <input v-model="form.address.logradouro" type="text" />
            </div>
            
            <div class="form-group">
              <label>Cidade</label>
              <input v-model="form.address.cidade" type="text" />
            </div>
            
            <div class="form-group">
              <label>Estado</label>
              <input v-model="form.address.estado" type="text" />
            </div>
            
            <div class="form-group">
              <label>CEP</label>
              <input v-model="form.address.cep" type="text" />
            </div>
          </div>
        </div>
        
        <div class="form-actions">
          <button type="button" @click="goBack" class="btn btn-secondary">Cancelar</button>
          <button type="submit" class="btn btn-primary" :disabled="saving">
            {{ saving ? 'Salvando...' : 'Salvar' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { personService } from '../services/personService'

export default {
  name: 'ClientForm',
  data() {
    return {
      form: {
        fullname: '',
        email: '',
        cpf: '',
        rg: '',
        estadoCivil: '',
        dataNascimento: '',
        profissao: '',
        telefone: '',
        vivo: null,
        representante: '',
        idFuncional: '',
        nacionalidade: '',
        address: {
          logradouro: '',
          cidade: '',
          estado: '',
          cep: ''
        }
      },
      errors: {},
      loading: false,
      saving: false,
      error: null
    }
  },
  computed: {
    isEdit() {
      return this.$route.name === 'ClientEdit'
    },
    clientId() {
      return this.$route.params.id
    }
  },
  async mounted() {
    if (this.isEdit) {
      await this.loadClient()
    }
  },
  methods: {
    async loadClient() {
      this.loading = true
      this.error = null
      try {
        const client = await personService.getById(this.clientId)
        this.form = {
          fullname: client.fullname || '',
          email: client.email || '',
          cpf: client.cpf || '',
          rg: client.rg || '',
          estadoCivil: client.estadoCivil || '',
          dataNascimento: client.dataNascimento || '',
          profissao: client.profissao || '',
          telefone: client.telefone || '',
          vivo: client.vivo,
          representante: client.representante || '',
          idFuncional: client.idFuncional || '',
          nacionalidade: client.nacionalidade || '',
          address: client.address || {
            logradouro: '',
            cidade: '',
            estado: '',
            cep: ''
          }
        }
      } catch (err) {
        this.error = 'Erro ao carregar cliente: ' + (err.response?.data?.message || err.message)
        console.error(err)
      } finally {
        this.loading = false
      }
    },
    async save() {
      this.errors = {}
      this.saving = true
      this.error = null
      
      try {
        const data = {
          fullname: this.form.fullname,
          email: this.form.email,
          cpf: this.form.cpf,
          rg: this.form.rg,
          estadoCivil: this.form.estadoCivil,
          dataNascimento: this.form.dataNascimento,
          profissao: this.form.profissao,
          telefone: this.form.telefone,
          vivo: this.form.vivo,
          representante: this.form.representante || null,
          idFuncional: this.form.idFuncional || null,
          nacionalidade: this.form.nacionalidade || null,
          address: this.form.address.logradouro ? this.form.address : null
        }
        
        if (this.isEdit) {
          await personService.update(this.clientId, data)
        } else {
          await personService.create(data)
        }
        
        this.$router.push('/')
      } catch (err) {
        if (err.response?.data?.message) {
          this.error = err.response.data.message
        } else {
          this.error = 'Erro ao salvar cliente: ' + err.message
        }
        console.error(err)
      } finally {
        this.saving = false
      }
    },
    goBack() {
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
.client-form {
  padding: 2rem;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
}

.header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
}

.header h1 {
  font-size: 2rem;
  color: #333;
}

.form {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  padding: 2rem;
}

.section {
  margin-bottom: 2rem;
}

.section h2 {
  font-size: 1.5rem;
  color: #333;
  margin-bottom: 1.5rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid #dee2e6;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-weight: 600;
  color: #495057;
  font-size: 0.875rem;
}

.form-group input,
.form-group select {
  padding: 0.75rem;
  border: 1px solid #ced4da;
  border-radius: 4px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #007bff;
}

.error-text {
  color: #dc3545;
  font-size: 0.875rem;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 1px solid #dee2e6;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #0056b3;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background-color: #545b62;
}

.loading, .error {
  text-align: center;
  padding: 2rem;
  font-size: 1.1rem;
}

.error {
  color: #dc3545;
  background: #f8d7da;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
}
</style>

