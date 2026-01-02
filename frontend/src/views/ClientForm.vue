<template>
  <div class="client-form">
      <div class="container medium">
      <div class="header">
        <button @click="goBack" class="btn btn-secondary">← Voltar</button>
        <h1>{{ isEdit ? 'Editar Cliente' : 'Novo Cliente' }}</h1>
      </div>
      
      <div v-if="loading" class="loading">Carregando...</div>
      <div v-if="error" class="error inline">{{ error }}</div>
      
      <!-- Modal de Sucesso -->
      <Teleport to="body">
        <div v-if="showSuccessModal" class="modal-overlay" @click="closeSuccessModal">
          <div class="modal-content small" @click.stop>
            <h2 class="success">Sucesso!</h2>
            <p>{{ successMessage }}</p>
            <div class="modal-actions">
              <button @click="closeSuccessModal" class="btn btn-primary">OK</button>
            </div>
          </div>
        </div>
      </Teleport>
      
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
      error: null,
      showSuccessModal: false,
      successMessage: ''
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
          this.successMessage = 'Cliente atualizado com sucesso!'
        } else {
          await personService.create(data)
          this.successMessage = 'Cliente cadastrado com sucesso!'
        }
        
        // Garantir que o modal apareça
        this.saving = false // Parar o loading primeiro
        await this.$nextTick() // Aguardar atualização do DOM
        this.showSuccessModal = true
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
    closeSuccessModal() {
      this.showSuccessModal = false
      // Após fechar o modal, voltar para a página anterior
      if (this.isEdit && this.clientId) {
        this.$router.push(`/clients/${this.clientId}`)
      } else {
        this.$router.push('/clients')
      }
    },
    goBack() {
      // Voltar para a lista de clientes se veio de lá, senão para home
      const from = this.$route.query.from
      if (from === 'list' || this.isEdit) {
        if (this.isEdit && this.clientId) {
          this.$router.push(`/clients/${this.clientId}`)
        } else {
          this.$router.push('/clients')
        }
      } else {
        this.$router.push('/')
      }
    }
  }
}
</script>

<style scoped>
/* Estilos específicos do componente ClientForm */
.client-form {
  padding: 2rem;
}

.header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
}

.error {
  background: #f8d7da;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
}
</style>

