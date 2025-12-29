<template>
  <div class="client-form">
    <div class="container">
      <div class="header">
        <button @click="goBack" class="btn btn-secondary">← Voltar</button>
        <h1>{{ isEdit ? 'Editar Cliente' : 'Novo Cliente' }}</h1>
      </div>
      
      <div v-if="loading" class="loading">Carregando...</div>
      <div v-if="error" class="error">{{ error }}</div>
      
      <!-- Modal de Sucesso para Cadastro -->
      <div v-if="showSuccessModal && !isEdit" class="modal-overlay" @click.self="closeSuccessModal">
        <div class="modal-content success-modal">
          <div class="modal-header">
            <h3>Sucesso!</h3>
          </div>
          <div class="modal-body">
            <p>Cliente cadastrado com sucesso!</p>
            <p class="question-text">Deseja realizar novo cadastro?</p>
          </div>
          <div class="modal-footer">
            <button @click="handleNewRegistration" class="btn btn-primary">Sim</button>
            <button @click="closeSuccessModal" class="btn btn-secondary">Não</button>
          </div>
        </div>
      </div>
      
      <!-- Modal de Sucesso para Edição -->
      <div v-if="showSuccessModal && isEdit" class="modal-overlay" @click.self="closeEditSuccessModal">
        <div class="modal-content success-modal">
          <div class="modal-header">
            <h3>Sucesso!</h3>
          </div>
          <div class="modal-body">
            <p>Cliente atualizado com sucesso!</p>
          </div>
          <div class="modal-footer">
            <button @click="closeEditSuccessModal" class="btn btn-primary">OK</button>
          </div>
        </div>
      </div>
      
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
              <label>Email</label>
              <input v-model="form.email" type="email" />
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
              <input 
                v-model="form.dataNascimento" 
                type="text" 
                placeholder="dd/mm/aaaa"
                @input="formatDateInput($event, 'dataNascimento')"
                maxlength="10"
                required 
              />
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
        
        <div class="section">
          <h2>Matrícula 1</h2>
          <div class="form-grid">
            <div class="form-group">
              <label>Número *</label>
              <input v-model="form.matriculation1.numero" type="text" required />
              <span v-if="errors.matriculation1?.numero" class="error-text">{{ errors.matriculation1.numero }}</span>
            </div>
            
            <div class="form-group">
              <label>Início ERJ *</label>
              <input 
                v-model="form.matriculation1.inicioErj" 
                type="text" 
                placeholder="dd/mm/aaaa"
                @input="formatDateInput($event, 'matriculation1.inicioErj')"
                maxlength="10"
                required 
              />
              <span v-if="errors.matriculation1?.inicioErj" class="error-text">{{ errors.matriculation1.inicioErj }}</span>
            </div>
            
            <div class="form-group">
              <label>Cargo *</label>
              <input v-model="form.matriculation1.cargo" type="text" required />
              <span v-if="errors.matriculation1?.cargo" class="error-text">{{ errors.matriculation1.cargo }}</span>
            </div>
            
            <div class="form-group">
              <label>Data de Aposentadoria *</label>
              <input 
                v-model="form.matriculation1.dataAposentadoria" 
                type="text" 
                placeholder="dd/mm/aaaa"
                @input="formatDateInput($event, 'matriculation1.dataAposentadoria')"
                maxlength="10"
                required 
              />
              <span v-if="errors.matriculation1?.dataAposentadoria" class="error-text">{{ errors.matriculation1.dataAposentadoria }}</span>
            </div>
            
            <div class="form-group">
              <label>Nível Atual *</label>
              <input v-model="form.matriculation1.nivelAtual" type="text" required />
              <span v-if="errors.matriculation1?.nivelAtual" class="error-text">{{ errors.matriculation1.nivelAtual }}</span>
            </div>
            
            <div class="form-group">
              <label>Triênio Atual *</label>
              <input v-model="form.matriculation1.trienioAtual" type="text" required />
              <span v-if="errors.matriculation1?.trienioAtual" class="error-text">{{ errors.matriculation1.trienioAtual }}</span>
            </div>
            
            <div class="form-group">
              <label>Referência *</label>
              <input v-model="form.matriculation1.referencia" type="text" required />
              <span v-if="errors.matriculation1?.referencia" class="error-text">{{ errors.matriculation1.referencia }}</span>
            </div>
          </div>
        </div>
        
        <div class="section">
          <div class="form-group checkbox-group">
            <label>
              <input type="checkbox" v-model="hasSecondMatriculation" />
              Possui segunda matrícula
            </label>
          </div>
        </div>
        
        <div v-if="hasSecondMatriculation" class="section">
          <h2>Matrícula 2</h2>
          <div class="form-grid">
            <div class="form-group">
              <label>Número *</label>
              <input v-model="form.matriculation2.numero" type="text" required />
              <span v-if="errors.matriculation2?.numero" class="error-text">{{ errors.matriculation2.numero }}</span>
            </div>
            
            <div class="form-group">
              <label>Início ERJ *</label>
              <input 
                v-model="form.matriculation2.inicioErj" 
                type="text" 
                placeholder="dd/mm/aaaa"
                @input="formatDateInput($event, 'matriculation2.inicioErj')"
                maxlength="10"
                required 
              />
              <span v-if="errors.matriculation2?.inicioErj" class="error-text">{{ errors.matriculation2.inicioErj }}</span>
            </div>
            
            <div class="form-group">
              <label>Cargo *</label>
              <input v-model="form.matriculation2.cargo" type="text" required />
              <span v-if="errors.matriculation2?.cargo" class="error-text">{{ errors.matriculation2.cargo }}</span>
            </div>
            
            <div class="form-group">
              <label>Data de Aposentadoria *</label>
              <input 
                v-model="form.matriculation2.dataAposentadoria" 
                type="text" 
                placeholder="dd/mm/aaaa"
                @input="formatDateInput($event, 'matriculation2.dataAposentadoria')"
                maxlength="10"
                required 
              />
              <span v-if="errors.matriculation2?.dataAposentadoria" class="error-text">{{ errors.matriculation2.dataAposentadoria }}</span>
            </div>
            
            <div class="form-group">
              <label>Nível Atual *</label>
              <input v-model="form.matriculation2.nivelAtual" type="text" required />
              <span v-if="errors.matriculation2?.nivelAtual" class="error-text">{{ errors.matriculation2.nivelAtual }}</span>
            </div>
            
            <div class="form-group">
              <label>Triênio Atual *</label>
              <input v-model="form.matriculation2.trienioAtual" type="text" required />
              <span v-if="errors.matriculation2?.trienioAtual" class="error-text">{{ errors.matriculation2.trienioAtual }}</span>
            </div>
            
            <div class="form-group">
              <label>Referência *</label>
              <input v-model="form.matriculation2.referencia" type="text" required />
              <span v-if="errors.matriculation2?.referencia" class="error-text">{{ errors.matriculation2.referencia }}</span>
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
        },
        matriculation1: {
          numero: '',
          inicioErj: '',
          cargo: '',
          dataAposentadoria: '',
          nivelAtual: '',
          trienioAtual: '',
          referencia: ''
        },
        matriculation2: {
          numero: '',
          inicioErj: '',
          cargo: '',
          dataAposentadoria: '',
          nivelAtual: '',
          trienioAtual: '',
          referencia: ''
        }
      },
      hasSecondMatriculation: false,
      errors: {},
      loading: false,
      saving: false,
      error: null,
      showSuccessModal: false,
      savedFormData: null
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
          dataNascimento: client.dataNascimento ? this.formatDateForInput(client.dataNascimento) : '',
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
          },
          matriculation1: (() => {
            if (client.matriculations && client.matriculations.length > 0 && client.matriculations[0]) {
              const mat1 = client.matriculations[0]
              return {
                numero: mat1.numero || '',
                inicioErj: mat1.inicioErj ? this.formatDateForInput(mat1.inicioErj) : '',
                cargo: mat1.cargo || '',
                dataAposentadoria: mat1.dataAposentadoria ? this.formatDateForInput(mat1.dataAposentadoria) : '',
                nivelAtual: mat1.nivelAtual || '',
                trienioAtual: mat1.trienioAtual || '',
                referencia: mat1.referencia || ''
              }
            }
            return {
              numero: '',
              inicioErj: '',
              cargo: '',
              dataAposentadoria: '',
              nivelAtual: '',
              trienioAtual: '',
              referencia: ''
            }
          })(),
          matriculation2: (() => {
            if (client.matriculations && client.matriculations.length > 1 && client.matriculations[1]) {
              const mat2 = client.matriculations[1]
              return {
                numero: mat2.numero || '',
                inicioErj: mat2.inicioErj ? this.formatDateForInput(mat2.inicioErj) : '',
                cargo: mat2.cargo || '',
                dataAposentadoria: mat2.dataAposentadoria ? this.formatDateForInput(mat2.dataAposentadoria) : '',
                nivelAtual: mat2.nivelAtual || '',
                trienioAtual: mat2.trienioAtual || '',
                referencia: mat2.referencia || ''
              }
            }
            return {
              numero: '',
              inicioErj: '',
              cargo: '',
              dataAposentadoria: '',
              nivelAtual: '',
              trienioAtual: '',
              referencia: ''
            }
          })()
        }
        // Define se há segunda matrícula baseado nos dados carregados
        this.hasSecondMatriculation = client.matriculations && client.matriculations.length > 1 && 
                                      client.matriculations[1] && 
                                      client.matriculations[1].numero
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
          dataNascimento: this.convertDateToISO(this.form.dataNascimento),
          profissao: this.form.profissao,
          telefone: this.form.telefone,
          vivo: this.form.vivo,
          representante: this.form.representante || null,
          idFuncional: this.form.idFuncional || null,
          nacionalidade: this.form.nacionalidade || null,
          address: this.form.address.logradouro ? this.form.address : null,
          matriculation1: this.hasMatriculation1() ? {
            numero: this.form.matriculation1.numero,
            inicioErj: this.convertDateToISO(this.form.matriculation1.inicioErj),
            cargo: this.form.matriculation1.cargo,
            dataAposentadoria: this.convertDateToISO(this.form.matriculation1.dataAposentadoria),
            nivelAtual: this.form.matriculation1.nivelAtual,
            trienioAtual: this.form.matriculation1.trienioAtual,
            referencia: this.form.matriculation1.referencia
          } : null,
          matriculation2: this.hasSecondMatriculation && this.hasMatriculation2() ? {
            numero: this.form.matriculation2.numero,
            inicioErj: this.convertDateToISO(this.form.matriculation2.inicioErj),
            cargo: this.form.matriculation2.cargo,
            dataAposentadoria: this.convertDateToISO(this.form.matriculation2.dataAposentadoria),
            nivelAtual: this.form.matriculation2.nivelAtual,
            trienioAtual: this.form.matriculation2.trienioAtual,
            referencia: this.form.matriculation2.referencia
          } : null
        }
        
        if (this.isEdit) {
          await personService.update(this.clientId, data)
          // Em modo de edição, mostra modal de sucesso e mantém na página
          this.showSuccessModal = true
        } else {
          await personService.create(data)
          // Salva os dados do formulário antes de mostrar o modal
          this.savedFormData = JSON.parse(JSON.stringify(this.form))
          // Mostra o modal de sucesso
          this.showSuccessModal = true
        }
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
    resetForm() {
      this.form = {
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
        },
        matriculation1: {
          numero: '',
          inicioErj: '',
          cargo: '',
          dataAposentadoria: '',
          nivelAtual: '',
          trienioAtual: '',
          referencia: ''
        },
        matriculation2: {
          numero: '',
          inicioErj: '',
          cargo: '',
          dataAposentadoria: '',
          nivelAtual: '',
          trienioAtual: '',
          referencia: ''
        }
      }
      this.hasSecondMatriculation = false
      this.errors = {}
      this.error = null
      this.savedFormData = null
    },
    hasMatriculation1() {
      return this.form.matriculation1.numero && 
             this.form.matriculation1.cargo && 
             this.form.matriculation1.nivelAtual && 
             this.form.matriculation1.trienioAtual && 
             this.form.matriculation1.referencia
    },
    hasMatriculation2() {
      return this.form.matriculation2.numero && 
             this.form.matriculation2.cargo && 
             this.form.matriculation2.nivelAtual && 
             this.form.matriculation2.trienioAtual && 
             this.form.matriculation2.referencia
    },
    formatDateInput(event, fieldPath) {
      // Aplica máscara dd/mm/aaaa enquanto o usuário digita
      let value = event.target.value.replace(/\D/g, '') // Remove tudo que não é dígito
      
      if (value.length > 2) {
        value = value.substring(0, 2) + '/' + value.substring(2)
      }
      if (value.length > 5) {
        value = value.substring(0, 5) + '/' + value.substring(5, 9)
      }
      
      // Atualiza o modelo Vue baseado no caminho do campo
      const parts = fieldPath.split('.')
      if (parts.length === 1) {
        this.$set(this.form, parts[0], value)
      } else if (parts.length === 2) {
        this.$set(this.form[parts[0]], parts[1], value)
      } else if (parts.length === 3) {
        this.$set(this.form[parts[0]][parts[1]], parts[2], value)
      }
      
      // Atualiza o valor no campo
      event.target.value = value
    },
    formatDateForInput(dateString) {
      // Converte de formato ISO (yyyy-MM-dd ou yyyy-MM-ddTHH:mm:ss) para dd/mm/aaaa
      if (!dateString) return ''
      try {
        let date
        if (typeof dateString === 'string') {
          // Remove a parte de hora se existir
          const dateOnly = dateString.split('T')[0].split(' ')[0]
          const parts = dateOnly.split('-')
          if (parts.length === 3) {
            // Formato yyyy-MM-dd
            return `${parts[2]}/${parts[1]}/${parts[0]}`
          }
          // Tenta parsear como data ISO
          date = new Date(dateString)
        } else {
          date = new Date(dateString)
        }
        
        if (date && !isNaN(date.getTime())) {
          const day = String(date.getDate()).padStart(2, '0')
          const month = String(date.getMonth() + 1).padStart(2, '0')
          const year = date.getFullYear()
          return `${day}/${month}/${year}`
        }
        return ''
      } catch (error) {
        console.error('Erro ao formatar data:', dateString, error)
        return ''
      }
    },
    convertDateToISO(dateString) {
      // Converte de dd/mm/aaaa para ISO (yyyy-MM-dd)
      if (!dateString) return null
      
      // Remove espaços e caracteres especiais
      const cleanDate = dateString.trim()
      
      // Verifica se está no formato dd/mm/aaaa
      const datePattern = /^(\d{2})\/(\d{2})\/(\d{4})$/
      const match = cleanDate.match(datePattern)
      
      if (match) {
        const day = match[1]
        const month = match[2]
        const year = match[3]
        
        // Valida a data
        const date = new Date(`${year}-${month}-${day}`)
        if (!isNaN(date.getTime()) && 
            date.getDate() == day && 
            date.getMonth() + 1 == month && 
            date.getFullYear() == year) {
          // Retorna no formato yyyy-MM-dd (sem hora)
          return `${year}-${month}-${day}`
        }
      }
      
      // Se não estiver no formato esperado, tenta parsear como está
      console.warn('Data em formato inválido:', dateString)
      return null
    },
    handleNewRegistration() {
      // Limpa o formulário para novo cadastro
      this.resetForm()
      this.closeSuccessModal()
    },
    closeSuccessModal() {
      // Fecha o modal e mantém os dados anteriores (apenas para cadastro)
      this.showSuccessModal = false
      // Restaura os dados salvos se existirem
      if (this.savedFormData) {
        this.form = JSON.parse(JSON.stringify(this.savedFormData))
      }
    },
    closeEditSuccessModal() {
      // Fecha o modal de sucesso na edição e recarrega os dados atualizados
      this.showSuccessModal = false
      // Recarrega os dados do cliente para garantir que está tudo atualizado
      this.loadClient()
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

/* Estilos de botões importados de styles/buttons.css */

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

/* Modal de Sucesso */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content.success-modal {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.modal-header {
  padding: 1.5rem;
  border-bottom: 1px solid #dee2e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  color: #28a745;
  font-size: 1.5rem;
}

.modal-body {
  padding: 1.5rem;
  text-align: center;
}

.modal-body p {
  margin: 0.5rem 0;
  color: #495057;
  font-size: 1rem;
}

.question-text {
  font-weight: 600;
  margin-top: 1rem !important;
  color: #333 !important;
}

.modal-footer {
  padding: 1rem 1.5rem;
  border-top: 1px solid #dee2e6;
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

.checkbox-group {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.checkbox-group input[type="checkbox"] {
  width: auto;
  margin: 0;
  cursor: pointer;
}

.checkbox-group label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  font-weight: 500;
}
</style>

