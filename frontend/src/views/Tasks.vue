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
          <button type="button" @click="openNewTaskModal" class="toolbar-btn toolbar-btn--add" title="Nova Tarefa">
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
              <button
                v-if="column.status === 'COMPLETA' && getArchivedTasksCount() > 0"
                @click.stop="showArchivedCompleta = !showArchivedCompleta"
                class="btn-archive-toggle"
              >
                {{ showArchivedCompleta ? 'Ocultar arquivadas' : `Ver arquivadas (${getArchivedTasksCount()})` }}
              </button>
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
              :class="[getTaskColorClass(task.tipoTarefa), { 'task-card-archived': isArchivedTask(task) }]"
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
                  <span v-if="formatPrazoParaCard(task.prazoFinal)" class="task-prazo-inline">{{ formatPrazoParaCard(task.prazoFinal) }}</span>
                  <span v-if="isArchivedTask(task)" class="task-archived-badge">Arquivada</span>
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

      <TaskFormModal
        v-model="taskModalOpen"
        :task-for-edit="modalEditingTask"
        @saved="loadTasks"
      />
    </div>
  </div>
</template>

<script>
import { taskService } from '../services/taskService'
import TaskFormModal from '../components/TaskFormModal.vue'

export default {
  name: 'Tasks',
  components: { TaskFormModal },
  data() {
    return {
      tasks: [],
      loading: false,
      error: null,
      taskModalOpen: false,
      modalEditingTask: null,
      draggedTask: null,
      filterResponsavel: '',
      filterTipoTarefa: '',
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
      isMobile: false,
      showArchivedCompleta: false
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
  },
  watch: {
    taskModalOpen(val) {
      if (!val) this.modalEditingTask = null
    },
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
    isArchivedTask(task) {
      if (!task.completedOn) return false
      const completedDate = new Date(task.completedOn)
      const thirtyDaysAgo = new Date()
      thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30)
      return completedDate < thirtyDaysAgo
    },
    getArchivedTasksCount() {
      return this.tasks.filter(
        task => task.status === 'COMPLETA' && this.isArchivedTask(task)
      ).length
    },
    getTasksByStatus(status) {
      const TIPO_PRIORITY = {
        PRAZO: 0,
        ESCRITA_PECA: 1,
        PRESENCIAL: 2,
        COMUNICAR_CLIENTE: 3,
        ADMINISTRATIVO: 4
      }
      return this.tasks
        .filter(task => {
          const matchesStatus = task.status === status
          const matchesResponsavel = !this.filterResponsavel || task.responsavel === this.filterResponsavel
          const matchesTipoTarefa = !this.filterTipoTarefa || task.tipoTarefa === this.filterTipoTarefa
          if (!matchesStatus || !matchesResponsavel || !matchesTipoTarefa) return false
          if (status === 'COMPLETA' && !this.showArchivedCompleta && this.isArchivedTask(task)) return false
          return true
        })
        .sort((a, b) => {
          const pa = TIPO_PRIORITY[a.tipoTarefa] ?? 99
          const pb = TIPO_PRIORITY[b.tipoTarefa] ?? 99
          if (pa !== pb) return pa - pb
          const da = a.createdOn ? new Date(a.createdOn) : new Date(0)
          const db = b.createdOn ? new Date(b.createdOn) : new Date(0)
          return da - db
        })
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
    formatPrazoParaCard(isoDate) {
      if (isoDate == null || isoDate === '') return ''
      const s = String(isoDate).trim()
      const m = /^(\d{4})-(\d{2})-(\d{2})/.exec(s)
      if (m) {
        const [, y, mo, d] = m
        return `${d}/${mo}/${y}`
      }
      return s
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
      this.modalEditingTask = null
      this.taskModalOpen = true
    },
    openEditTaskModal(task) {
      this.modalEditingTask = task
      this.taskModalOpen = true
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

.task-prazo-inline {
  font-size: 0.75rem;
  font-weight: 600;
  color: #6b7280;
  white-space: nowrap;
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

.btn-archive-toggle {
  background: rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.15);
  border-radius: 6px;
  padding: 0.25rem 0.6rem;
  font-size: 0.75rem;
  font-weight: 600;
  color: #374151;
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s;
  white-space: nowrap;
}

.btn-archive-toggle:hover {
  background: rgba(0, 0, 0, 0.15);
  border-color: rgba(0, 0, 0, 0.25);
}

.task-card-archived {
  opacity: 0.55;
}

.task-archived-badge {
  padding: 0.2rem 0.45rem;
  border-radius: 4px;
  font-size: 0.7rem;
  font-weight: 600;
  color: #6b7280;
  background: #e5e7eb;
  border: 1px solid #d1d5db;
}
</style>

