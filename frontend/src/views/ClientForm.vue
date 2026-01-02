<template>
  <div class="client-form">
    <div class="container">
      <div class="header">
        <button @click="goBack" class="btn btn-secondary">← Voltar</button>
        <h1>{{ isEdit ? 'Editar Cliente' : 'Novo Cliente' }}</h1>
      </div>
      
      <div v-if="loading" class="loading">Carregando...</div>
      <div v-if="error" class="error">{{ error }}</div>
      
      <!-- Modal de Sucesso -->
      <Teleport to="body">
        <div v-if="showSuccessModal" class="modal-overlay" @click="closeSuccessModal">
          <div class="modal-content" @click.stop>
            <h2>Sucesso!</h2>
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
          <h2>Matrículas</h2>
          <div class="matriculation-section">
            <div class="matriculation-form">
              <h3>Primeira Matrícula</h3>
              <div class="form-grid">
                <div class="form-group">
                  <label>Número *</label>
                  <input v-model="form.matriculation1.numero" type="text" />
                </div>
                
                <div class="form-group">
                  <label>Cargo *</label>
                  <input v-model="form.matriculation1.cargo" type="text" />
                </div>
                
                <div class="form-group">
                  <label>Início ERJ *</label>
                  <input 
                    v-model="form.matriculation1.inicioErj" 
                    type="text" 
                    placeholder="dd/mm/aaaa"
                    @input="formatDateInput($event, 'matriculation1', 'inicioErj')"
                    maxlength="10"
                  />
                </div>
                
                <div class="form-group">
                  <label>Data de Aposentadoria *</label>
                  <input 
                    v-model="form.matriculation1.dataAposentadoria" 
                    type="text" 
                    placeholder="dd/mm/aaaa"
                    @input="formatDateInput($event, 'matriculation1', 'dataAposentadoria')"
                    maxlength="10"
                  />
                </div>
                
                <div class="form-group">
                  <label>Nível Atual *</label>
                  <input v-model="form.matriculation1.nivelAtual" type="text" />
                </div>
                
                <div class="form-group">
                  <label>Triênio Atual *</label>
                  <input v-model="form.matriculation1.trienioAtual" type="text" />
                </div>
                
                <div class="form-group">
                  <label>Referência *</label>
                  <input v-model="form.matriculation1.referencia" type="text" />
                </div>
              </div>
            </div>
            
            <div class="checkbox-group">
              <label>
                <input 
                  type="checkbox" 
                  v-model="form.hasSecondMatriculation"
                  @change="toggleSecondMatriculation"
                />
                Adicionar Segunda Matrícula
              </label>
            </div>
            
            <div v-if="form.hasSecondMatriculation" class="matriculation-form">
              <h3>Segunda Matrícula</h3>
              <div class="form-grid">
                <div class="form-group">
                  <label>Número *</label>
                  <input v-model="form.matriculation2.numero" type="text" />
                </div>
                
                <div class="form-group">
                  <label>Cargo *</label>
                  <input v-model="form.matriculation2.cargo" type="text" />
                </div>
                
                <div class="form-group">
                  <label>Início ERJ *</label>
                  <input 
                    v-model="form.matriculation2.inicioErj" 
                    type="text" 
                    placeholder="dd/mm/aaaa"
                    @input="formatDateInput($event, 'matriculation2', 'inicioErj')"
                    maxlength="10"
                  />
                </div>
                
                <div class="form-group">
                  <label>Data de Aposentadoria *</label>
                  <input 
                    v-model="form.matriculation2.dataAposentadoria" 
                    type="text" 
                    placeholder="dd/mm/aaaa"
                    @input="formatDateInput($event, 'matriculation2', 'dataAposentadoria')"
                    maxlength="10"
                  />
                </div>
                
                <div class="form-group">
                  <label>Nível Atual *</label>
                  <input v-model="form.matriculation2.nivelAtual" type="text" />
                </div>
                
                <div class="form-group">
                  <label>Triênio Atual *</label>
                  <input v-model="form.matriculation2.trienioAtual" type="text" />
                </div>
                
                <div class="form-group">
                  <label>Referência *</label>
                  <input v-model="form.matriculation2.referencia" type="text" />
                </div>
              </div>
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
        },
        matriculation1: {
          numero: '',
          cargo: '',
          inicioErj: '',
          dataAposentadoria: '',
          nivelAtual: '',
          trienioAtual: '',
          referencia: ''
        },
        matriculation2: {
          numero: '',
          cargo: '',
          inicioErj: '',
          dataAposentadoria: '',
          nivelAtual: '',
          trienioAtual: '',
          referencia: ''
        },
        hasSecondMatriculation: false
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
        
        // Carregar matrículas
        const matriculations = client.matriculations || []
        const mat1 = matriculations[0] || {}
        const mat2 = matriculations[1] || {}
        
        this.form = {
          fullname: client.fullname || '',
          email: client.email || '',
          cpf: client.cpf || '',
          rg: client.rg || '',
          estadoCivil: client.estadoCivil || '',
          dataNascimento: this.formatDateToDDMMYYYY(client.dataNascimento),
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
          matriculation1: {
            numero: mat1.numero || '',
            cargo: mat1.cargo || '',
            inicioErj: this.formatDateToDDMMYYYY(mat1.inicioErj),
            dataAposentadoria: this.formatDateToDDMMYYYY(mat1.dataAposentadoria),
            nivelAtual: mat1.nivelAtual || '',
            trienioAtual: mat1.trienioAtual || '',
            referencia: mat1.referencia || ''
          },
          matriculation2: {
            numero: mat2.numero || '',
            cargo: mat2.cargo || '',
            inicioErj: this.formatDateToDDMMYYYY(mat2.inicioErj),
            dataAposentadoria: this.formatDateToDDMMYYYY(mat2.dataAposentadoria),
            nivelAtual: mat2.nivelAtual || '',
            trienioAtual: mat2.trienioAtual || '',
            referencia: mat2.referencia || ''
          },
          hasSecondMatriculation: !!mat2.numero
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
        // Preparar dados das matrículas
        // Validar e formatar primeira matrícula
        let matriculation1 = null
        if (this.isMatriculationValid(this.form.matriculation1)) {
          const inicioErj = this.formatDDMMYYYYToISO(this.form.matriculation1.inicioErj)
          const dataAposentadoria = this.formatDDMMYYYYToISO(this.form.matriculation1.dataAposentadoria)
          
          if (!inicioErj || !dataAposentadoria) {
            this.error = 'Por favor, verifique as datas da primeira matrícula'
            this.saving = false
            return
          }
          
          matriculation1 = {
            numero: this.form.matriculation1.numero.trim(),
            cargo: this.form.matriculation1.cargo.trim(),
            inicioErj: inicioErj,
            dataAposentadoria: dataAposentadoria,
            nivelAtual: this.form.matriculation1.nivelAtual.trim(),
            trienioAtual: this.form.matriculation1.trienioAtual.trim(),
            referencia: this.form.matriculation1.referencia.trim()
          }
          
          // personId só é necessário quando está editando
          // Na criação, o backend preenche automaticamente
          if (this.isEdit) {
            matriculation1.personId = parseInt(this.clientId)
          }
          // Não incluir personId na criação (null causaria erro de validação)
        }
        
        // Validar e formatar segunda matrícula
        let matriculation2 = null
        if (this.form.hasSecondMatriculation && this.isMatriculationValid(this.form.matriculation2)) {
          const inicioErj = this.formatDDMMYYYYToISO(this.form.matriculation2.inicioErj)
          const dataAposentadoria = this.formatDDMMYYYYToISO(this.form.matriculation2.dataAposentadoria)
          
          if (!inicioErj || !dataAposentadoria) {
            this.error = 'Por favor, verifique as datas da segunda matrícula'
            this.saving = false
            return
          }
          
          matriculation2 = {
            numero: this.form.matriculation2.numero.trim(),
            cargo: this.form.matriculation2.cargo.trim(),
            inicioErj: inicioErj,
            dataAposentadoria: dataAposentadoria,
            nivelAtual: this.form.matriculation2.nivelAtual.trim(),
            trienioAtual: this.form.matriculation2.trienioAtual.trim(),
            referencia: this.form.matriculation2.referencia.trim()
          }
          
          if (this.isEdit) {
            matriculation2.personId = parseInt(this.clientId)
          }
          // Não incluir personId na criação
        }
        
        // Validar data de nascimento
        const dataNascimento = this.formatDDMMYYYYToISO(this.form.dataNascimento)
        if (!dataNascimento) {
          this.error = 'Por favor, verifique a data de nascimento'
          this.saving = false
          return
        }
        
        const data = {
          fullname: this.form.fullname.trim(),
          email: this.form.email ? this.form.email.trim() : null,
          cpf: this.form.cpf.trim(),
          rg: this.form.rg.trim(),
          estadoCivil: this.form.estadoCivil,
          dataNascimento: dataNascimento,
          profissao: this.form.profissao.trim(),
          telefone: this.form.telefone.trim(),
          vivo: this.form.vivo,
          representante: this.form.representante ? this.form.representante.trim() : null,
          idFuncional: this.form.idFuncional ? this.form.idFuncional.trim() : null,
          nacionalidade: this.form.nacionalidade ? this.form.nacionalidade.trim() : null,
          address: this.form.address.logradouro ? {
            logradouro: this.form.address.logradouro.trim(),
            cidade: this.form.address.cidade ? this.form.address.cidade.trim() : '',
            estado: this.form.address.estado ? this.form.address.estado.trim() : '',
            cep: this.form.address.cep ? this.form.address.cep.trim() : ''
          } : null,
          matriculation1: matriculation1,
          matriculation2: matriculation2
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
    formatDateInput(event, field, subField = null) {
      let value = event.target.value.replace(/\D/g, '')
      
      if (value.length > 2) {
        value = value.substring(0, 2) + '/' + value.substring(2)
      }
      if (value.length > 5) {
        value = value.substring(0, 5) + '/' + value.substring(5, 9)
      }
      
      if (subField) {
        this.form[field][subField] = value
      } else {
        this.form[field] = value
      }
      
      event.target.value = value
    },
    formatDateToDDMMYYYY(dateString) {
      if (!dateString) return ''
      try {
        const date = new Date(dateString)
        if (isNaN(date.getTime())) return ''
        const day = String(date.getDate()).padStart(2, '0')
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const year = date.getFullYear()
        return `${day}/${month}/${year}`
      } catch (e) {
        return ''
      }
    },
    formatDDMMYYYYToISO(dateString) {
      if (!dateString || dateString.trim() === '') return null
      // Validar formato dd/mm/yyyy
      const parts = dateString.split('/')
      if (parts.length !== 3) return null
      const [day, month, year] = parts
      if (!day || !month || !year) return null
      
      // Validar se a data é válida
      const dayNum = parseInt(day, 10)
      const monthNum = parseInt(month, 10)
      const yearNum = parseInt(year, 10)
      
      if (isNaN(dayNum) || isNaN(monthNum) || isNaN(yearNum)) return null
      if (dayNum < 1 || dayNum > 31) return null
      if (monthNum < 1 || monthNum > 12) return null
      if (yearNum < 1900 || yearNum > 2100) return null
      
      // O backend aceita formato dd/MM/yyyy diretamente, então retornamos como está
      // Mas também podemos enviar no formato ISO para garantir compatibilidade
      // Vamos usar o formato ISO que é mais padrão
      const date = new Date(yearNum, monthNum - 1, dayNum)
      if (isNaN(date.getTime())) return null
      
      // Retornar no formato ISO (yyyy-MM-ddTHH:mm:ss)
      const isoString = `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}T00:00:00`
      return isoString
    },
    isMatriculationValid(mat) {
      return mat && 
             mat.numero && mat.numero.trim() !== '' &&
             mat.cargo && mat.cargo.trim() !== '' &&
             mat.inicioErj && mat.inicioErj.trim() !== '' &&
             mat.dataAposentadoria && mat.dataAposentadoria.trim() !== '' &&
             mat.nivelAtual && mat.nivelAtual.trim() !== '' &&
             mat.trienioAtual && mat.trienioAtual.trim() !== '' &&
             mat.referencia && mat.referencia.trim() !== ''
    },
    toggleSecondMatriculation() {
      if (!this.form.hasSecondMatriculation) {
        // Limpar segunda matrícula quando desmarcar
        this.form.matriculation2 = {
          numero: '',
          cargo: '',
          inicioErj: '',
          dataAposentadoria: '',
          nivelAtual: '',
          trienioAtual: '',
          referencia: ''
        }
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

.modal-overlay {
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  background: rgba(0, 0, 0, 0.5) !important;
  display: flex !important;
  align-items: center;
  justify-content: center;
  z-index: 99999 !important;
  animation: fadeIn 0.2s ease;
  pointer-events: auto !important;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal-content {
  background: white;
  border-radius: 8px;
  padding: 2rem;
  max-width: 400px;
  width: 90%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  animation: slideIn 0.3s ease;
  position: relative;
  z-index: 10000;
}

@keyframes slideIn {
  from {
    transform: translateY(-20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-content h2 {
  margin-top: 0;
  margin-bottom: 1rem;
  color: #28a745;
}

.modal-content p {
  margin-bottom: 1.5rem;
  color: #333;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

.matriculation-section {
  margin-top: 1rem;
}

.matriculation-form {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
  margin-bottom: 1.5rem;
  border: 1px solid #dee2e6;
}

.matriculation-form h3 {
  font-size: 1.25rem;
  color: #333;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #dee2e6;
}

.checkbox-group {
  margin: 1.5rem 0;
  padding: 1rem;
  background: #e9ecef;
  border-radius: 4px;
}

.checkbox-group label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  font-weight: 500;
}

.checkbox-group input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
}
</style>

