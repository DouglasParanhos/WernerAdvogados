<template>
  <div class="tasks-board">
    <div class="container">
      <div class="header">
        <div class="header-left">
          <button @click="goToHome" class="btn-home" title="Voltar para Home">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="9 22 9 12 15 12 15 22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
          <h1>Tarefas</h1>
        </div>
        <div class="header-actions">
          <div class="filter-group">
            <label for="filter-responsavel">Responsável:</label>
            <select id="filter-responsavel" v-model="filterResponsavel" class="filter-select">
              <option value="">Todos</option>
              <option value="Liz">Liz</option>
              <option value="Angelo">Angelo</option>
              <option value="Thiago">Thiago</option>
            </select>
          </div>
          <div class="filter-group">
            <label for="filter-tipo-tarefa">Tipo de Tarefa:</label>
            <select id="filter-tipo-tarefa" v-model="filterTipoTarefa" class="filter-select">
              <option value="">Todos</option>
              <option value="PRESENCIAL">Presencial / Diligência</option>
              <option value="COMUNICAR_CLIENTE">Comunicar com Cliente</option>
              <option value="ESCRITA_PECA">Escrita de Peça</option>
              <option value="PRAZO">Prazo</option>
              <option value="ADMINISTRATIVO">Administrativo</option>
            </select>
          </div>
          <button @click="openNewTaskModal" class="btn-icon-add" title="Nova Tarefa">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
              <path d="M12 8v8M8 12h8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>
        </div>
      </div>
      
      <div v-if="loading" class="loading">Carregando...</div>
      <div v-if="error" class="error">{{ error }}</div>
      
      <div v-if="!loading && !error" class="board-container">
        <div 
          v-for="column in columns" 
          :key="column.status" 
          class="board-column"
          :class="{ 'collapsed': isColumnCollapsed(column.status) }"
          @drop="onDrop($event, column.status)"
          @dragover.prevent
          @dragenter.prevent
        >
          <div class="column-header" :style="{ backgroundColor: column.color }">
            <h2>{{ column.title }}</h2>
            <div class="column-header-right">
              <span class="task-count">{{ getTasksByStatus(column.status).length }}</span>
              <button 
                v-if="isMobile" 
                @click.stop="toggleColumn(column.status)" 
                class="column-toggle-btn"
                :title="isColumnCollapsed(column.status) ? 'Expandir' : 'Minimizar'"
              >
                <svg v-if="isColumnCollapsed(column.status)" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M9 18l6-6-6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <svg v-else viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M6 9l6 6 6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>
            </div>
          </div>
          
          <div class="tasks-list" :class="{ 'collapsed': isColumnCollapsed(column.status) }">
            <div
              v-for="task in getVisibleTasksByStatus(column.status)"
              :key="task.id"
              class="task-card"
              :class="getTaskColorClass(task.tipoTarefa)"
              :draggable="true"
              @dragstart="onDragStart($event, task)"
              @click="openEditTaskModal(task)"
            >
              <div class="task-header">
                <div class="task-header-left">
                  <span class="task-type-badge" :style="{ backgroundColor: getTaskTypeColor(task.tipoTarefa) }">
                    {{ getTaskTypeLabel(task.tipoTarefa) }}
                  </span>
                  <span
                    class="task-type-badge"
                    :style="{ backgroundColor: getResponsavelColor(task.responsavel) }"
                  >{{ task.responsavel }}</span>
                </div>
                <button @click.stop="deleteTask(task.id)" class="task-delete-btn" title="Excluir tarefa">
                  ×
                </button>
              </div>
              <h3 class="task-title">{{ task.titulo }}</h3>
              <p v-if="task.processNumero" class="task-process-numero">{{ task.processNumero }}</p>
              <p v-if="task.descricao" class="task-description">{{ task.descricao }}</p>
            </div>
          </div>

          <div
            v-show="!isColumnCollapsed(column.status)"
            class="column-pagination"
          >
            <div class="filter-group column-page-size">
              <label :for="'exibir-' + column.status">Exibir:</label>
              <select
                :id="'exibir-' + column.status"
                v-model="columnDisplayLimit[column.status]"
                class="filter-select column-page-select"
                @change="onColumnPageSizeChange(column.status)"
              >
                <option value="10">10</option>
                <option value="25">25</option>
                <option value="50">50</option>
                <option value="all">Todas</option>
              </select>
            </div>
            <div
              v-if="getColumnTotalPages(column.status) > 1"
              class="column-page-nav"
            >
              <button
                type="button"
                class="column-page-btn"
                :disabled="isColumnPaginationFirst(column.status)"
                @click="columnPagePrev(column.status)"
              >
                Anterior
              </button>
              <span class="column-page-indicator">
                {{ getColumnPaginationLabel(column.status) }}
              </span>
              <button
                type="button"
                class="column-page-btn"
                :disabled="isColumnPaginationLast(column.status)"
                @click="columnPageNext(column.status)"
              >
                Seguinte
              </button>
            </div>
            <p
              v-if="getColumnPaginationHint(column.status)"
              class="column-pagination-hint"
            >
              {{ getColumnPaginationHint(column.status) }}
            </p>
          </div>
        </div>
      </div>
      
      <!-- Modal Nova/Editar Tarefa -->
      <div v-if="showNewTaskModal || showEditTaskModal" class="modal-overlay" @click="closeModal">
        <div class="modal-content" @click.stop>
          <h2>{{ showEditTaskModal ? 'Editar Tarefa' : 'Nova Tarefa' }}</h2>
          <form @submit.prevent="saveTask">
            <div class="form-group">
              <label>Título *</label>
              <input v-model="taskForm.titulo" type="text" required />
            </div>
            
            <div class="form-group">
              <label>Descrição</label>
              <textarea v-model="taskForm.descricao" rows="3"></textarea>
            </div>
            
            <div class="form-group">
              <label>Tipo de Tarefa *</label>
              <select v-model="taskForm.tipoTarefa" required>
                <option value="">Selecione...</option>
                <option value="PRESENCIAL">Presencial / Diligência</option>
                <option value="COMUNICAR_CLIENTE">Comunicar com Cliente</option>
                <option value="ESCRITA_PECA">Escrita de Peça</option>
                <option value="PRAZO">Prazo</option>
                <option value="ADMINISTRATIVO">Administrativo</option>
              </select>
            </div>
            
            <div class="form-group">
              <label>Status *</label>
              <select v-model="taskForm.status" required>
                <option value="PARA_INICIAR">Para Iniciar</option>
                <option value="EM_ANDAMENTO">Em Andamento</option>
                <option value="COMPLETA">Completa</option>
              </select>
            </div>
            
            <div class="form-group">
              <label>Responsável *</label>
              <select v-model="taskForm.responsavel" required>
                <option value="">Selecione...</option>
                <option value="Liz">Liz</option>
                <option value="Angelo">Angelo</option>
                <option value="Thiago">Thiago</option>
              </select>
            </div>

            <div class="form-group form-group-processo">
              <label for="processo-autocomplete-input">Processo</label>
              <div class="processo-row">
                <div
                  class="processo-autocomplete"
                  @keydown.escape.stop="closeProcessoSuggestions"
                >
                  <input
                    id="processo-autocomplete-input"
                    v-model="processoInputDisplay"
                    type="text"
                    autocomplete="off"
                    class="processo-autocomplete-input"
                    :disabled="semProcessoRelacionado"
                    placeholder="Digite para buscar pelo número..."
                    @input="onProcessoInput"
                    @focus="onProcessoFocus"
                    @blur="onProcessoBlur"
                  />
                  <ul
                    v-show="processoSuggestionsOpen && processoSuggestions.length && !semProcessoRelacionado"
                    class="processo-suggestions"
                    role="listbox"
                  >
                    <li
                      v-for="p in processoSuggestions"
                      :key="p.id"
                      class="processo-suggestion-item"
                      role="option"
                      @mousedown.prevent="selectProcessoSuggestion(p)"
                    >
                      {{ p.numero }}
                    </li>
                  </ul>
                  <p
                    v-if="processoSuggestionsLoading && !semProcessoRelacionado"
                    class="processo-suggestions-hint"
                  >
                    Buscando...
                  </p>
                </div>
                <label class="checkbox-sem-processo">
                  <input
                    type="checkbox"
                    v-model="semProcessoRelacionado"
                    @change="onSemProcessoRelacionadoChange"
                  />
                  <span>Sem processo relacionado</span>
                </label>
              </div>
            </div>
            
            <div class="modal-actions">
              <button type="button" @click="closeModal" class="btn btn-secondary">Cancelar</button>
              <button type="submit" class="btn btn-primary">Salvar</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { taskService } from '../services/taskService'
import { processService } from '../services/processService'

export default {
  name: 'Tasks',
  data() {
    return {
      tasks: [],
      loading: false,
      error: null,
      showNewTaskModal: false,
      showEditTaskModal: false,
      draggedTask: null,
      filterResponsavel: '',
      filterTipoTarefa: '',
      processoInputDisplay: '',
      processoConfirmadoNumero: null,
      processoSuggestions: [],
      processoSuggestionsOpen: false,
      processoSuggestionsLoading: false,
      processoSearchDebounceId: null,
      processoBlurTimeoutId: null,
      semProcessoRelacionado: false,
      taskForm: {
        id: null,
        titulo: '',
        descricao: '',
        tipoTarefa: '',
        status: 'PARA_INICIAR',
        responsavel: '',
        processId: null
      },
      columns: [
        { status: 'PARA_INICIAR', title: 'Para Iniciar', color: '#e2e8f0' },
        { status: 'EM_ANDAMENTO', title: 'Em Andamento', color: '#dbeafe' },
        { status: 'COMPLETA', title: 'Completa', color: '#d1fae5' }
      ],
      columnDisplayLimit: {
        PARA_INICIAR: '10',
        EM_ANDAMENTO: '10',
        COMPLETA: '10'
      },
      columnPageIndex: {
        PARA_INICIAR: 0,
        EM_ANDAMENTO: 0,
        COMPLETA: 0
      },
      collapsedColumns: new Set(),
      isMobile: false
    }
  },
  async mounted() {
    await this.loadTasks()
    this.checkMobile()
    window.addEventListener('resize', this.checkMobile)
    // No mobile, todas as colunas começam minimizadas
    if (this.isMobile) {
      this.columns.forEach(col => {
        this.collapsedColumns.add(col.status)
      })
    }
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.checkMobile)
    if (this.processoSearchDebounceId) {
      clearTimeout(this.processoSearchDebounceId)
    }
    if (this.processoBlurTimeoutId) {
      clearTimeout(this.processoBlurTimeoutId)
    }
  },
  watch: {
    filterResponsavel() {
      this.resetAllColumnPages()
    },
    filterTipoTarefa() {
      this.resetAllColumnPages()
    },
    tasks() {
      this.$nextTick(() => this.clampAllColumnPages())
    }
  },
  methods: {
    async loadTasks() {
      this.loading = true
      this.error = null
      try {
        this.tasks = await taskService.getAll()
      } catch (err) {
        this.error = 'Erro ao carregar tarefas: ' + (err.response?.data?.message || err.message)
        console.error(err)
      } finally {
        this.loading = false
        this.$nextTick(() => this.clampAllColumnPages())
      }
    },
    resetProcessoAutocomplete() {
      this.processoInputDisplay = ''
      this.processoConfirmadoNumero = null
      this.processoSuggestions = []
      this.processoSuggestionsOpen = false
      this.processoSuggestionsLoading = false
    },
    onProcessoInput() {
      if (this.semProcessoRelacionado) return
      const t = this.processoInputDisplay.trim()
      const c = (this.processoConfirmadoNumero || '').trim()
      if (t !== c) {
        this.taskForm.processId = null
        this.processoConfirmadoNumero = null
      }
      if (this.processoSearchDebounceId) {
        clearTimeout(this.processoSearchDebounceId)
      }
      this.processoSearchDebounceId = setTimeout(() => {
        this.processoSearchDebounceId = null
        this.fetchProcessoSuggestions()
      }, 300)
    },
    onProcessoFocus() {
      if (this.semProcessoRelacionado) return
      this.processoSuggestionsOpen = true
      if (this.processoInputDisplay.trim()) {
        this.fetchProcessoSuggestions()
      }
    },
    onProcessoBlur() {
      if (this.processoBlurTimeoutId) {
        clearTimeout(this.processoBlurTimeoutId)
      }
      this.processoBlurTimeoutId = setTimeout(() => {
        this.processoSuggestionsOpen = false
        this.processoBlurTimeoutId = null
      }, 200)
    },
    closeProcessoSuggestions() {
      this.processoSuggestionsOpen = false
    },
    async fetchProcessoSuggestions() {
      if (this.semProcessoRelacionado) return
      const q = this.processoInputDisplay.trim()
      if (!q) {
        this.processoSuggestions = []
        return
      }
      this.processoSuggestionsLoading = true
      try {
        const response = await processService.getAll(null, 0, 20, { numero: q })
        this.processoSuggestions = response.content ? response.content : []
      } catch (err) {
        console.error('Busca de processos:', err)
        this.processoSuggestions = []
      } finally {
        this.processoSuggestionsLoading = false
      }
    },
    selectProcessoSuggestion(p) {
      this.taskForm.processId = p.id
      this.processoConfirmadoNumero = p.numero
      this.processoInputDisplay = p.numero
      this.processoSuggestions = []
      this.processoSuggestionsOpen = false
    },
    getTasksByStatus(status) {
      return this.tasks
        .filter(task => {
          const matchesStatus = task.status === status
          const matchesResponsavel = !this.filterResponsavel || task.responsavel === this.filterResponsavel
          const matchesTipoTarefa = !this.filterTipoTarefa || task.tipoTarefa === this.filterTipoTarefa
          return matchesStatus && matchesResponsavel && matchesTipoTarefa
        })
        .sort((a, b) => (a.ordem || 0) - (b.ordem || 0))
    },
    getDisplayLimitForStatus(status) {
      const raw = this.columnDisplayLimit[status]
      if (raw === 'all' || raw == null || raw === '') return null
      const n = parseInt(String(raw), 10)
      return Number.isFinite(n) && n > 0 ? n : null
    },
    resetAllColumnPages() {
      this.columnPageIndex = {
        PARA_INICIAR: 0,
        EM_ANDAMENTO: 0,
        COMPLETA: 0
      }
    },
    getClampedColumnPage(status) {
      const full = this.getTasksByStatus(status)
      const limit = this.getDisplayLimitForStatus(status)
      if (limit == null) return 0
      const totalPages = Math.max(1, Math.ceil(full.length / limit))
      let idx = this.columnPageIndex[status]
      if (idx == null || idx < 0) idx = 0
      return Math.min(idx, totalPages - 1)
    },
    clampColumnPage(status) {
      const limit = this.getDisplayLimitForStatus(status)
      if (limit == null) {
        this.columnPageIndex[status] = 0
        return
      }
      this.columnPageIndex[status] = this.getClampedColumnPage(status)
    },
    clampAllColumnPages() {
      this.columns.forEach(col => this.clampColumnPage(col.status))
    },
    getColumnTotalPages(status) {
      const full = this.getTasksByStatus(status)
      const limit = this.getDisplayLimitForStatus(status)
      if (limit == null) return 1
      return Math.max(1, Math.ceil(full.length / limit))
    },
    isColumnPaginationFirst(status) {
      return this.getClampedColumnPage(status) <= 0
    },
    isColumnPaginationLast(status) {
      const last = this.getColumnTotalPages(status) - 1
      return this.getClampedColumnPage(status) >= last
    },
    columnPagePrev(status) {
      const clamped = this.getClampedColumnPage(status)
      if (clamped > 0) this.columnPageIndex[status] = clamped - 1
    },
    columnPageNext(status) {
      const clamped = this.getClampedColumnPage(status)
      const last = this.getColumnTotalPages(status) - 1
      if (clamped < last) this.columnPageIndex[status] = clamped + 1
    },
    getColumnPaginationLabel(status) {
      const p = this.getClampedColumnPage(status) + 1
      const total = this.getColumnTotalPages(status)
      return `${p} / ${total}`
    },
    onColumnPageSizeChange(status) {
      this.columnPageIndex[status] = 0
    },
    getVisibleTasksByStatus(status) {
      const full = this.getTasksByStatus(status)
      const limit = this.getDisplayLimitForStatus(status)
      if (limit == null) return full
      const page = this.getClampedColumnPage(status)
      const start = page * limit
      return full.slice(start, start + limit)
    },
    getColumnPaginationHint(status) {
      const full = this.getTasksByStatus(status)
      const total = full.length
      if (total === 0) return ''
      const limit = this.getDisplayLimitForStatus(status)
      if (limit == null) {
        return ''
      }
      const visible = this.getVisibleTasksByStatus(status).length
      const page = this.getClampedColumnPage(status)
      const start = page * limit + 1
      const end = page * limit + visible
      const totalPages = this.getColumnTotalPages(status)
      if (totalPages <= 1) {
        return ''
      }
      return `Mostrando ${start}–${end} de ${total}`
    },
    getTaskTypeColor(tipo) {
      const colors = {
        PRESENCIAL: '#3b82f6', // Azul
        COMUNICAR_CLIENTE: '#f97316', // Laranja
        ESCRITA_PECA: '#ef4444', // Vermelho
        PRAZO: '#ef4444', // Vermelho
        ADMINISTRATIVO: '#10b981' // Verde
      }
      return colors[tipo] || '#6b7280'
    },
    getTaskTypeLabel(tipo) {
      const labels = {
        PRESENCIAL: 'Presencial',
        COMUNICAR_CLIENTE: 'Comunicar',
        ESCRITA_PECA: 'Escrita',
        PRAZO: 'Prazo',
        ADMINISTRATIVO: 'Administrativo'
      }
      return labels[tipo] || tipo
    },
    getTaskColorClass(tipo) {
      return `task-${tipo.toLowerCase()}`
    },
    getResponsavelColor(nome) {
      if (!nome) return '#6b7280'
      const colors = {
        Liz: '#db2777',
        Angelo: '#a16207',
        Thiago: '#0a0a0a'
      }
      return colors[nome] || '#6b7280'
    },
    onDragStart(event, task) {
      this.draggedTask = task
      event.dataTransfer.effectAllowed = 'move'
    },
    async onDrop(event, newStatus) {
      event.preventDefault()
      if (!this.draggedTask) return
      
      if (this.draggedTask.status === newStatus) {
        this.draggedTask = null
        return
      }
      
      try {
        const tasksInNewStatus = this.getTasksByStatus(newStatus)
        const newOrder = tasksInNewStatus.length + 1
        
        await taskService.update(this.draggedTask.id, {
          ...this.draggedTask,
          status: newStatus,
          ordem: newOrder,
          processId: this.draggedTask.processId ?? null
        })
        
        await this.loadTasks()
      } catch (err) {
        alert('Erro ao mover tarefa: ' + (err.response?.data?.message || err.message))
      } finally {
        this.draggedTask = null
      }
    },
    openNewTaskModal() {
      this.showEditTaskModal = false
      this.semProcessoRelacionado = false
      this.resetProcessoAutocomplete()
      this.taskForm = {
        id: null,
        titulo: '',
        descricao: '',
        tipoTarefa: '',
        status: 'PARA_INICIAR',
        responsavel: '',
        processId: null
      }
      this.showNewTaskModal = true
    },
    openEditTaskModal(task) {
      this.taskForm = {
        id: task.id,
        titulo: task.titulo,
        descricao: task.descricao || '',
        tipoTarefa: task.tipoTarefa,
        status: task.status,
        responsavel: task.responsavel,
        processId: task.processId ?? null
      }
      this.semProcessoRelacionado = !task.processId
      this.processoInputDisplay = task.processNumero || ''
      this.processoConfirmadoNumero = task.processNumero || null
      this.processoSuggestions = []
      this.processoSuggestionsOpen = false
      this.showNewTaskModal = false
      this.showEditTaskModal = true
    },
    onSemProcessoRelacionadoChange() {
      if (this.semProcessoRelacionado) {
        this.taskForm.processId = null
        this.resetProcessoAutocomplete()
      }
    },
    closeModal() {
      this.showNewTaskModal = false
      this.showEditTaskModal = false
      this.semProcessoRelacionado = false
      this.resetProcessoAutocomplete()
      this.taskForm = {
        id: null,
        titulo: '',
        descricao: '',
        tipoTarefa: '',
        status: 'PARA_INICIAR',
        responsavel: '',
        processId: null
      }
    },
    async saveTask() {
      if (this.semProcessoRelacionado) {
        this.taskForm.processId = null
      } else if (!this.taskForm.processId) {
        alert('Selecione um processo na lista de sugestões ou marque que não há processo relacionado.')
        return
      }
      try {
        const payload = { ...this.taskForm }
        if (this.taskForm.id) {
          await taskService.update(this.taskForm.id, payload)
        } else {
          await taskService.create(payload)
        }
        await this.loadTasks()
        this.closeModal()
      } catch (err) {
        let msg = err.response?.data?.message || err.message || ''
        if (typeof msg === 'string' && /process_id|task\.process|column.*does not exist/i.test(msg)) {
          msg +=
            ' Verifique se a migração Flyway V4 (coluna task.process_id) foi aplicada na base de dados.'
        }
        alert('Erro ao salvar tarefa: ' + msg)
      }
    },
    async deleteTask(id) {
      if (!confirm('Tem certeza que deseja excluir esta tarefa?')) {
        return
      }
      try {
        await taskService.delete(id)
        await this.loadTasks()
      } catch (err) {
        alert('Erro ao excluir tarefa: ' + (err.response?.data?.message || err.message))
      }
    },
    goToHome() {
      this.$router.push('/')
    },
    checkMobile() {
      this.isMobile = window.innerWidth <= 1024
    },
    isColumnCollapsed(status) {
      return this.collapsedColumns.has(status)
    },
    toggleColumn(status) {
      if (this.collapsedColumns.has(status)) {
        this.collapsedColumns.delete(status)
      } else {
        this.collapsedColumns.add(status)
      }
    }
  }
}
</script>

<style scoped>
.tasks-board {
  padding: 2rem;
  min-height: 100vh;
  background: #f5f5f5;
}

/* Estilos específicos do Tasks - btn-home sem borda (diferente do layout.css) */
.btn-home {
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #4a5568;
  transition: color 0.2s;
}

.btn-home:hover {
  color: #003d7a;
}

.btn-home svg {
  width: 24px;
  height: 24px;
}

.btn-icon-add {
  background: #003d7a;
  color: white;
  border: none;
  border-radius: 50%;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 4px rgba(0, 61, 122, 0.2);
}

.btn-icon-add:hover {
  background: #002d5a;
  transform: scale(1.05);
  box-shadow: 0 4px 8px rgba(0, 61, 122, 0.3);
}

.btn-icon-add:active {
  transform: scale(0.95);
}

.btn-icon-add svg {
  width: 24px;
  height: 24px;
}

.board-container {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.5rem;
  margin-top: 2rem;
}

.board-column {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  min-height: 500px;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
  overflow: hidden;
}

.board-column.collapsed {
  min-height: auto;
}

.column-header {
  padding: 1rem;
  border-radius: 8px 8px 0 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.column-header-right {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.column-toggle-btn {
  background: rgba(0, 0, 0, 0.1);
  border: none;
  border-radius: 4px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  color: #1a1a1a;
  padding: 0;
}

.column-toggle-btn:hover {
  background: rgba(0, 0, 0, 0.2);
}

.column-toggle-btn svg {
  width: 20px;
  height: 20px;
}

.column-header h2 {
  margin: 0;
  font-size: 1.25rem;
  color: #1a1a1a;
  font-weight: 600;
}

.task-count {
  background: rgba(0,0,0,0.1);
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.875rem;
  font-weight: 600;
}

.tasks-list {
  flex: 1;
  padding: 1rem;
  overflow-y: auto;
  min-height: 400px;
  transition: all 0.3s ease;
}

.tasks-list.collapsed {
  max-height: 0;
  padding: 0 1rem;
  min-height: 0;
  overflow: hidden;
}

.column-pagination {
  padding: 0.75rem 1rem 1rem;
  border-top: 1px solid #e5e7eb;
  flex-shrink: 0;
}

.column-pagination .column-page-size {
  margin: 0;
  flex-direction: row;
  align-items: center;
  flex-wrap: wrap;
  gap: 0.5rem 0.75rem;
}

.column-pagination .column-page-size label {
  margin: 0;
  font-size: 0.8125rem;
  font-weight: 600;
  color: #4a5568;
}

.column-page-select {
  width: auto;
  min-width: 5rem;
  padding: 0.4rem 0.6rem;
  font-size: 0.875rem;
}

.column-page-nav {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  flex-wrap: wrap;
  margin-top: 0.75rem;
}

.column-page-btn {
  padding: 0.35rem 0.65rem;
  font-size: 0.8125rem;
  font-weight: 600;
  color: #003d7a;
  background: #eff6ff;
  border: 1px solid #bfdbfe;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s, color 0.15s;
}

.column-page-btn:hover:not(:disabled) {
  background: #dbeafe;
  border-color: #93c5fd;
}

.column-page-btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.column-page-indicator {
  font-size: 0.8125rem;
  font-weight: 600;
  color: #4a5568;
  min-width: 3rem;
  text-align: center;
}

.column-pagination-hint {
  margin: 0.5rem 0 0;
  font-size: 0.8125rem;
  color: #6b7280;
  line-height: 1.4;
}

.task-card {
  background: white;
  border-radius: 6px;
  padding: 1rem;
  margin-bottom: 0.75rem;
  cursor: pointer;
  transition: all 0.2s;
  border-left: 4px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.task-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0,0,0,0.15);
}

.task-card.task-presencial {
  border-left-color: #3b82f6;
}

.task-card.task-comunicar_cliente {
  border-left-color: #f97316;
}

.task-card.task-escrita_peca,
.task-card.task-prazo {
  border-left-color: #ef4444;
}

.task-card.task-administrativo {
  border-left-color: #10b981;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.task-header-left {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
  min-width: 0;
}

.task-type-badge {
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
  color: white;
}

.task-delete-btn {
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 1.5rem;
  color: #9ca3af;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s;
}

.task-delete-btn:hover {
  color: #ef4444;
}

.task-title {
  font-size: 1rem;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 0.5rem 0;
}

.task-process-numero {
  font-size: 0.8125rem;
  color: #003d7a;
  font-weight: 600;
  margin: 0 0 0.5rem 0;
  line-height: 1.3;
  word-break: break-word;
}

.task-description {
  font-size: 0.875rem;
  color: #6b7280;
  margin: 0 0 0.75rem 0;
  line-height: 1.4;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  padding: 2rem;
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-content h2 {
  margin: 0 0 1.5rem 0;
  color: #1a1a1a;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #4a5568;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

.form-group-processo .processo-row {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
  gap: 0.75rem 1rem;
}

.form-group-processo .processo-autocomplete {
  flex: 1;
  min-width: 12rem;
  position: relative;
}

.form-group-processo .processo-autocomplete-input {
  width: 100%;
}

.processo-suggestions {
  list-style: none;
  margin: 0;
  padding: 0;
  position: absolute;
  left: 0;
  right: 0;
  top: 100%;
  z-index: 20;
  max-height: 220px;
  overflow-y: auto;
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  margin-top: 4px;
}

.processo-suggestion-item {
  padding: 0.5rem 0.75rem;
  cursor: pointer;
  font-size: 0.9rem;
  color: #1a1a1a;
}

.processo-suggestion-item:hover {
  background: #f1f5f9;
}

.processo-suggestions-hint {
  margin: 0.35rem 0 0;
  font-size: 0.8rem;
  color: #6b7280;
}

.checkbox-sem-processo {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.9rem;
  color: #4a5568;
  margin: 0;
  white-space: nowrap;
}

.checkbox-sem-processo input {
  width: auto;
  margin: 0;
  cursor: pointer;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: #003d7a;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 2rem;
}

@media (max-width: 1024px) {
  .board-container {
    grid-template-columns: 1fr;
  }
  
  .column-toggle-btn {
    display: flex;
  }
  
  .header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .header-left {
    width: 100%;
  }
  
  .header-actions {
    width: 100%;
    flex-direction: column;
    align-items: flex-start;
    gap: 0.75rem;
  }
  
  .filter-group {
    width: 100%;
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
  
  .filter-select {
    width: 100%;
  }
}

@media (min-width: 1025px) {
  .column-toggle-btn {
    display: none;
  }
}
</style>

