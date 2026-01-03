<template>
  <div class="client-moviments">
    <div class="container">
      <div class="header">
        <h1>Meus Processos e Movimentações</h1>
      </div>
      
      <div v-if="loading" class="loading">Carregando movimentações...</div>
      <div v-if="error" class="error inline">{{ error }}</div>
      
      <div v-if="!loading && !error" class="moviments-container">
        <div v-if="groupedMoviments.length === 0" class="empty-state">
          <p>Nenhuma movimentação encontrada.</p>
        </div>
        
        <div v-else class="accordion">
          <div 
            v-for="group in groupedMoviments" 
            :key="group.processId" 
            class="accordion-item"
          >
            <button 
              @click="toggleProcess(group.processId)"
              class="accordion-header"
              :class="{ 'active': expandedProcesses.includes(group.processId) }"
            >
              <div class="accordion-header-content">
                <div class="process-info">
                  <div class="process-header-row">
                    <h3 class="process-number">{{ group.processNumero || `Processo #${group.processId}` }}</h3>
                    <span 
                      v-if="group.processTipoProcesso" 
                      class="process-type-badge"
                      :class="getBadgeClass(group.processTipoProcesso)"
                    >
                      {{ group.processTipoProcesso }}
                    </span>
                  </div>
                  <div class="process-details">
                    <span v-if="group.processMatriculationNumero" class="process-detail matriculation-info">
                      <strong>Matrícula:</strong> {{ group.processMatriculationNumero }}
                    </span>
                    <span v-if="group.processComarca" class="process-detail">{{ group.processComarca }}</span>
                    <span v-if="group.processVara" class="process-detail">{{ group.processVara }}</span>
                    <span v-if="group.processStatus" class="process-detail status-info">
                      <strong>Status:</strong> {{ group.processStatus }}
                    </span>
                  </div>
                </div>
                <div class="accordion-indicator">
                  <svg 
                    viewBox="0 0 24 24" 
                    fill="none" 
                    xmlns="http://www.w3.org/2000/svg"
                    :class="{ 'rotated': expandedProcesses.includes(group.processId) }"
                  >
                    <path d="M6 9l6 6 6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </div>
              </div>
              <div class="moviment-count">
                {{ group.moviments.length }} {{ group.moviments.length === 1 ? 'movimentação' : 'movimentações' }}
              </div>
            </button>
            
            <div 
              v-show="expandedProcesses.includes(group.processId)"
              class="accordion-content"
            >
              <div class="moviments-list">
                <div 
                  v-for="moviment in group.moviments" 
                  :key="moviment.id" 
                  class="moviment-card"
                >
                  <div class="moviment-header">
                    <div class="moviment-date">
                      <strong>{{ formatDate(moviment.date) }}</strong>
                    </div>
                  </div>
                  <div class="moviment-body">
                    <p class="moviment-description">{{ moviment.descricao }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { movimentService } from '../services/movimentService'

export default {
  name: 'ClientMoviments',
  data() {
    return {
      moviments: [],
      loading: false,
      error: null,
      expandedProcesses: []
    }
  },
  computed: {
    groupedMoviments() {
      // Agrupar movimentos por processo
      const groups = {}
      
      this.moviments.forEach(moviment => {
        const processId = moviment.processId
        
        if (!groups[processId]) {
          groups[processId] = {
            processId: processId,
            processNumero: moviment.processNumero,
            processComarca: moviment.processComarca,
            processVara: moviment.processVara,
            processTipoProcesso: moviment.processTipoProcesso,
            processMatriculationNumero: moviment.processMatriculationNumero,
            processStatus: moviment.processStatus,
            moviments: []
          }
        }
        
        groups[processId].moviments.push(moviment)
      })
      
      // Ordenar movimentos por data reversa dentro de cada grupo
      Object.values(groups).forEach(group => {
        group.moviments.sort((a, b) => {
          const dateA = new Date(a.date)
          const dateB = new Date(b.date)
          return dateB - dateA // Ordem reversa (mais recente primeiro)
        })
      })
      
      // Converter para array e ordenar por número do processo
      return Object.values(groups).sort((a, b) => {
        const numA = a.processNumero || ''
        const numB = b.processNumero || ''
        return numB.localeCompare(numA) // Ordem reversa alfabética
      })
    }
  },
  watch: {
    groupedMoviments: {
      handler(newVal) {
        // Expandir o primeiro processo por padrão quando os dados são carregados
        if (newVal.length > 0 && this.expandedProcesses.length === 0) {
          this.$nextTick(() => {
            this.expandedProcesses.push(newVal[0].processId)
          })
        }
      },
      immediate: true
    }
  },
  async mounted() {
    await this.loadMoviments()
  },
  methods: {
    async loadMoviments() {
      this.loading = true
      this.error = null
      try {
        this.moviments = await movimentService.getMyMoviments()
      } catch (err) {
        this.error = 'Erro ao carregar movimentações: ' + (err.response?.data?.message || err.message)
        console.error(err)
        if (err.response?.status === 403) {
          this.error = 'Acesso negado. Apenas clientes podem visualizar suas movimentações.'
        }
      } finally {
        this.loading = false
      }
    },
    toggleProcess(processId) {
      const index = this.expandedProcesses.indexOf(processId)
      if (index > -1) {
        this.expandedProcesses.splice(index, 1)
      } else {
        this.expandedProcesses.push(processId)
      }
    },
    formatDate(dateString) {
      if (!dateString) return '-'
      const date = new Date(dateString)
      return date.toLocaleDateString('pt-BR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    getBadgeClass(tipoProcesso) {
      if (!tipoProcesso) return ''
      const tipo = tipoProcesso.toUpperCase()
      if (tipo === 'PISO') return 'badge-piso'
      if (tipo === 'INTERNÍVEIS' || tipo === 'INTERNIVEIS') return 'badge-interniveis'
      if (tipo === 'NOVAESCOLA' || tipo === 'NOVA ESCOLA') return 'badge-novaescola'
      return 'badge-default'
    }
  }
}
</script>

<style scoped>
/* Estilos específicos do componente ClientMoviments */
/* Usa classes compartilhadas de utilities.css para loading, error e empty-state */

.client-moviments {
  padding: 2rem;
}

.header {
  margin-bottom: 2rem;
}

.header h1 {
  margin: 0;
  color: #333;
  font-size: 2rem;
}

.moviments-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  padding: 1.5rem;
}

/* Accordion Styles */
.accordion {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.accordion-item {
  border: 1px solid #dee2e6;
  border-radius: 8px;
  overflow: hidden;
  background: white;
  transition: box-shadow 0.2s;
}

.accordion-item:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.accordion-header {
  width: 100%;
  padding: 1.25rem 1.5rem;
  background: #f8f9fa;
  border: none;
  cursor: pointer;
  text-align: left;
  transition: background-color 0.2s;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.accordion-header:hover {
  background: #e9ecef;
}

.accordion-header.active {
  background: #e3f2fd;
  border-bottom: 2px solid #003d7a;
}

.accordion-header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.process-info {
  flex: 1;
}

.process-header-row {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.5rem;
  flex-wrap: wrap;
}

.process-number {
  margin: 0;
  color: #003d7a;
  font-size: 1.25rem;
  font-weight: 600;
}

.process-type-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.badge-piso {
  background-color: #e3f2fd;
  color: #1976d2;
  border: 1px solid #90caf9;
}

.badge-interniveis {
  background-color: #f3e5f5;
  color: #7b1fa2;
  border: 1px solid #ce93d8;
}

.badge-novaescola {
  background-color: #e8f5e9;
  color: #388e3c;
  border: 1px solid #a5d6a7;
}

.badge-default {
  background-color: #f5f5f5;
  color: #616161;
  border: 1px solid #bdbdbd;
}

.process-details {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.process-detail {
  color: #6c757d;
  font-size: 0.875rem;
}

.matriculation-info {
  font-weight: 500;
  color: #495057;
}

.matriculation-info strong {
  color: #003d7a;
  margin-right: 0.25rem;
}

.status-info {
  font-weight: 500;
  color: #495057;
}

.status-info strong {
  color: #003d7a;
  margin-right: 0.25rem;
}

.accordion-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  transition: transform 0.3s;
}

.accordion-indicator svg {
  width: 24px;
  height: 24px;
  color: #003d7a;
  transition: transform 0.3s;
}

.accordion-indicator svg.rotated {
  transform: rotate(180deg);
}

.moviment-count {
  font-size: 0.875rem;
  color: #6c757d;
  margin-top: 0.25rem;
}

.accordion-content {
  padding: 1rem 1.5rem;
  background: white;
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    max-height: 0;
  }
  to {
    opacity: 1;
    max-height: 2000px;
  }
}

.moviments-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.moviment-card {
  border: 1px solid #e9ecef;
  border-radius: 6px;
  padding: 1rem;
  background: #f8f9fa;
  transition: box-shadow 0.2s;
}

.moviment-card:hover {
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.moviment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #dee2e6;
}

.moviment-date {
  color: #003d7a;
  font-size: 0.9rem;
}

.moviment-body {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.moviment-description {
  margin: 0;
  color: #333;
  font-size: 0.95rem;
  line-height: 1.5;
}

/* Responsive */
@media (max-width: 768px) {
  .client-moviments {
    padding: 1rem;
  }
  
  .header h1 {
    font-size: 1.5rem;
  }
  
  .moviments-container {
    padding: 1rem;
  }
  
  .accordion-header {
    padding: 1rem;
  }
  
  .accordion-content {
    padding: 0.75rem 1rem;
  }
  
  .process-number {
    font-size: 1.1rem;
  }
  
  .process-header-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
  
  .process-type-badge {
    font-size: 0.7rem;
    padding: 0.2rem 0.6rem;
  }
  
  .process-details {
    flex-direction: column;
    gap: 0.25rem;
  }
  
  .moviment-card {
    padding: 0.75rem;
  }
}

@media (max-width: 480px) {
  .client-moviments {
    padding: 0.75rem;
  }
  
  .accordion-header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
  
  .accordion-indicator {
    align-self: flex-end;
  }
}
</style>

