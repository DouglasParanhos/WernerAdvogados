<template>
  <div class="process-form">
      <div class="container medium">
      <div class="header">
        <button @click="goBack" class="btn btn-secondary">← Voltar</button>
        <h1>{{ isEdit ? 'Editar Processo' : 'Novo Processo' }}</h1>
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
          <h2>Dados do Processo</h2>
          <div class="form-grid">
            <div class="form-group">
              <label>Matrícula *</label>
              <select v-model="form.matriculationId" required :disabled="isEdit">
                <option value="">Selecione uma matrícula</option>
                <option v-for="mat in matriculations" :key="mat.id" :value="mat.id">
                  {{ mat.numero }} - {{ mat.cargo }}
                </option>
              </select>
              <span v-if="errors.matriculationId" class="error-text">{{ errors.matriculationId }}</span>
            </div>
            
            <div class="form-group">
              <label>Número do Processo *</label>
              <input v-model="form.numero" type="text" required />
              <span v-if="errors.numero" class="error-text">{{ errors.numero }}</span>
            </div>
            
            <div class="form-group">
              <label>Comarca *</label>
              <input v-model="form.comarca" type="text" required />
              <span v-if="errors.comarca" class="error-text">{{ errors.comarca }}</span>
            </div>
            
            <div class="form-group">
              <label>Vara *</label>
              <input v-model="form.vara" type="text" required />
              <span v-if="errors.vara" class="error-text">{{ errors.vara }}</span>
            </div>
            
            <div class="form-group">
              <label>Sistema *</label>
              <input v-model="form.sistema" type="text" required />
              <span v-if="errors.sistema" class="error-text">{{ errors.sistema }}</span>
            </div>
            
            <div class="form-group">
              <label>Tipo de Processo *</label>
              <select v-model="form.tipoProcesso" required>
                <option value="">Selecione o tipo</option>
                <option value="PISO">PISO</option>
                <option value="NOVAESCOLA">NOVAESCOLA</option>
                <option value="INTERNIVEIS">INTERNIVEIS</option>
              </select>
              <span v-if="errors.tipoProcesso" class="error-text">{{ errors.tipoProcesso }}</span>
            </div>
            
            <div class="form-group">
              <label>Status</label>
              <input v-model="form.status" type="text" />
            </div>
            
            <div class="form-group">
              <label>Valor Original</label>
              <input v-model="form.valorOriginal" type="number" step="0.01" @input="calculateHonorariosContratuais" />
            </div>
            
            <div class="form-group">
              <label>Valor Corrigido</label>
              <input v-model="form.valorCorrigido" type="number" step="0.01" @input="calculateHonorariosContratuais" />
            </div>
            
            <div class="form-group">
              <label>Previsão Honorários Contratuais</label>
              <input v-model="form.previsaoHonorariosContratuais" type="number" step="0.01" />
            </div>
            
            <div class="form-group">
              <label>Previsão Honorários Sucumbenciais</label>
              <input v-model="form.previsaoHonorariosSucumbenciais" type="number" step="0.01" />
            </div>
            
            <div class="form-group">
              <label>Distribuído em</label>
              <input v-model="form.distribuidoEm" type="date" />
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
import { processService } from '../services/processService'
import { matriculationService } from '../services/matriculationService'
import { personService } from '../services/personService'

export default {
  name: 'ProcessForm',
  data() {
    return {
      form: {
        matriculationId: null,
        numero: '',
        comarca: '',
        vara: '',
        sistema: '',
        tipoProcesso: '',
        status: '',
        valorOriginal: null,
        valorCorrigido: null,
        previsaoHonorariosContratuais: null,
        previsaoHonorariosSucumbenciais: null,
        distribuidoEm: ''
      },
      matriculations: [],
      errors: {},
      loading: false,
      saving: false,
      error: null,
      showSuccessModal: false,
      successMessage: '',
      processPersonId: null
    }
  },
  computed: {
    isEdit() {
      return this.$route.name === 'ProcessEdit'
    },
    processId() {
      return this.$route.params.id
    },
    personId() {
      return this.$route.query.personId
    }
  },
  async mounted() {
    await this.loadMatriculations()
    if (this.isEdit) {
      await this.loadProcess()
    } else if (this.personId) {
      // Se há personId na query, filtrar matrículas apenas desse cliente
      await this.loadMatriculations(this.personId)
    }
  },
  methods: {
    async loadMatriculations(personId = null) {
      try {
        if (personId) {
          this.matriculations = await matriculationService.getAll(personId)
        } else {
          this.matriculations = await matriculationService.getAll()
        }
      } catch (err) {
        console.error('Erro ao carregar matrículas:', err)
      }
    },
    async loadProcess() {
      this.loading = true
      this.error = null
      try {
        const process = await processService.getById(this.processId)
        this.form = {
          matriculationId: process.matriculationId,
          numero: process.numero || '',
          comarca: process.comarca || '',
          vara: process.vara || '',
          sistema: process.sistema || '',
          tipoProcesso: process.tipoProcesso || '',
          status: process.status || '',
          valorOriginal: process.valorOriginal || null,
          valorCorrigido: process.valorCorrigido || null,
          previsaoHonorariosContratuais: process.previsaoHonorariosContratuais || null,
          previsaoHonorariosSucumbenciais: process.previsaoHonorariosSucumbenciais || null,
          distribuidoEm: process.distribuidoEm || ''
        }
        // Se não tiver personId na query, tentar buscar da matrícula do processo
        if (!this.personId && process.matriculationId) {
          try {
            const matriculation = await matriculationService.getById(process.matriculationId)
            if (matriculation && matriculation.personId) {
              // Armazenar o personId para usar depois
              this.processPersonId = matriculation.personId
              console.log('processPersonId carregado:', this.processPersonId)
            }
          } catch (err) {
            console.error('Erro ao buscar matrícula:', err)
          }
        }
      } catch (err) {
        this.error = 'Erro ao carregar processo: ' + (err.response?.data?.message || err.message)
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
        // Obter valor efetivo (valorCorrigido se disponível, senão valorOriginal)
        const valorEfetivo = this.form.valorCorrigido !== null && this.form.valorCorrigido !== undefined 
          ? this.form.valorCorrigido 
          : this.form.valorOriginal
        
        // Calcular honorários contratuais se estiver vazio
        let honorariosContratuais = this.form.previsaoHonorariosContratuais
        if (!honorariosContratuais && valorEfetivo && this.form.tipoProcesso) {
          if (this.form.tipoProcesso === 'PISO') {
            honorariosContratuais = valorEfetivo * 0.30
          } else if (this.form.tipoProcesso === 'NOVAESCOLA' || this.form.tipoProcesso === 'INTERNIVEIS') {
            honorariosContratuais = valorEfetivo * 0.20
          }
        }
        
        const data = {
          matriculationId: this.form.matriculationId,
          numero: this.form.numero,
          comarca: this.form.comarca,
          vara: this.form.vara,
          sistema: this.form.sistema,
          tipoProcesso: this.form.tipoProcesso || null,
          status: this.form.status || null,
          valorOriginal: this.form.valorOriginal || null,
          valorCorrigido: this.form.valorCorrigido || null,
          previsaoHonorariosContratuais: honorariosContratuais || null,
          previsaoHonorariosSucumbenciais: this.form.previsaoHonorariosSucumbenciais || null,
          distribuidoEm: this.form.distribuidoEm || null
        }
        
        if (this.isEdit) {
          await processService.update(this.processId, data)
          this.successMessage = 'Processo atualizado com sucesso!'
        } else {
          await processService.create(data)
          this.successMessage = 'Processo cadastrado com sucesso!'
        }
        
        // Garantir que o modal apareça - NÃO redirecionar automaticamente
        this.saving = false // Parar o loading primeiro
        await this.$nextTick() // Aguardar atualização do DOM
        this.showSuccessModal = true
        // NÃO fazer router.push aqui - apenas mostrar o modal
      } catch (err) {
        if (err.response?.data?.message) {
          this.error = err.response.data.message
        } else {
          this.error = 'Erro ao salvar processo: ' + err.message
        }
        console.error(err)
        this.saving = false
      }
    },
    calculateHonorariosContratuais() {
      // Obter valor efetivo (valorCorrigido se disponível, senão valorOriginal)
      const valorEfetivo = this.form.valorCorrigido !== null && this.form.valorCorrigido !== undefined 
        ? this.form.valorCorrigido 
        : this.form.valorOriginal
      
      // Calcular automaticamente se honorários contratuais estiver vazio
      if (!this.form.previsaoHonorariosContratuais && valorEfetivo && this.form.tipoProcesso) {
        if (this.form.tipoProcesso === 'PISO') {
          this.form.previsaoHonorariosContratuais = valorEfetivo * 0.30
        } else if (this.form.tipoProcesso === 'NOVAESCOLA' || this.form.tipoProcesso === 'INTERNIVEIS') {
          this.form.previsaoHonorariosContratuais = valorEfetivo * 0.20
        }
      }
    },
    closeSuccessModal() {
      console.log('Fechando modal, processPersonId:', this.processPersonId, 'personId:', this.personId)
      this.showSuccessModal = false
      // Após fechar o modal, voltar para a página do cliente se tiver personId
      const personId = this.personId || this.processPersonId
      if (personId) {
        this.$router.push(`/clients/${personId}`)
      } else {
        // Se não tiver personId, tentar pegar da matrícula selecionada
        const selectedMat = this.matriculations.find(m => m.id === this.form.matriculationId)
        if (selectedMat && selectedMat.personId) {
          this.$router.push(`/clients/${selectedMat.personId}`)
        } else {
          this.$router.push('/processes')
        }
      }
    },
    goBack() {
      if (this.personId) {
        this.$router.push(`/clients/${this.personId}`)
      } else {
        this.$router.push('/')
      }
    }
  }
}
</script>

<style scoped>
/* Estilos específicos do componente ProcessForm */
.process-form {
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

