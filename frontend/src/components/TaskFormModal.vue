<template>
  <div v-if="modelValue" class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <h2>{{ isEditMode ? 'Editar Tarefa' : 'Nova Tarefa' }}</h2>
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

        <div class="form-group">
          <label for="task-form-modal-prazo-final">
            Prazo Final
            <span v-if="taskForm.tipoTarefa === 'PRAZO'" class="label-required-marker"> (obrigatório)</span>
          </label>
          <input
            id="task-form-modal-prazo-final"
            v-model="taskForm.prazoFinal"
            type="date"
            :required="taskForm.tipoTarefa === 'PRAZO'"
          />
        </div>

        <div class="form-group form-group-processo">
          <label for="task-form-modal-processo-input">Processo</label>
          <div class="processo-row">
            <div
              class="processo-autocomplete"
              @keydown.escape.stop="closeProcessoSuggestions"
            >
              <input
                id="task-form-modal-processo-input"
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
</template>

<script>
import { taskService } from '../services/taskService'
import { processService } from '../services/processService'

const emptyTaskForm = () => ({
  id: null,
  titulo: '',
  descricao: '',
  tipoTarefa: '',
  status: 'PARA_INICIAR',
  responsavel: '',
  processId: null,
  prazoFinal: ''
})

export default {
  name: 'TaskFormModal',
  props: {
    modelValue: {
      type: Boolean,
      default: false
    },
    taskForEdit: {
      type: Object,
      default: null
    },
    presetProcess: {
      type: Object,
      default: null
    }
  },
  emits: ['update:modelValue', 'saved'],
  data() {
    return {
      processoInputDisplay: '',
      processoConfirmadoNumero: null,
      processoSuggestions: [],
      processoSuggestionsOpen: false,
      processoSuggestionsLoading: false,
      processoSearchDebounceId: null,
      processoBlurTimeoutId: null,
      semProcessoRelacionado: false,
      taskForm: emptyTaskForm()
    }
  },
  computed: {
    isEditMode() {
      return !!(this.taskForEdit && this.taskForEdit.id)
    }
  },
  watch: {
    modelValue(val) {
      if (val) {
        this.$nextTick(() => this.applyOpenState())
      }
    }
  },
  beforeUnmount() {
    if (this.processoSearchDebounceId) {
      clearTimeout(this.processoSearchDebounceId)
    }
    if (this.processoBlurTimeoutId) {
      clearTimeout(this.processoBlurTimeoutId)
    }
  },
  methods: {
    applyOpenState() {
      const task = this.taskForEdit
      if (task && task.id) {
        this.taskForm = {
          id: task.id,
          titulo: task.titulo,
          descricao: task.descricao || '',
          tipoTarefa: task.tipoTarefa,
          status: task.status,
          responsavel: task.responsavel,
          processId: task.processId ?? null,
          prazoFinal: task.prazoFinal || ''
        }
        this.semProcessoRelacionado = !task.processId
        this.processoInputDisplay = task.processNumero || ''
        this.processoConfirmadoNumero = task.processNumero || null
        this.processoSuggestions = []
        this.processoSuggestionsOpen = false
        return
      }

      this.semProcessoRelacionado = false
      this.resetProcessoAutocomplete()
      this.taskForm = emptyTaskForm()
      const pp = this.presetProcess
      if (pp && pp.id != null) {
        this.taskForm.processId = pp.id
        const num = pp.numero != null ? String(pp.numero) : ''
        this.processoInputDisplay = num
        this.processoConfirmadoNumero = num || null
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
    onSemProcessoRelacionadoChange() {
      if (this.semProcessoRelacionado) {
        this.taskForm.processId = null
        this.resetProcessoAutocomplete()
      }
    },
    closeModal() {
      this.$emit('update:modelValue', false)
      this.semProcessoRelacionado = false
      this.resetProcessoAutocomplete()
      this.taskForm = emptyTaskForm()
    },
    async saveTask() {
      if (this.taskForm.tipoTarefa === 'PRAZO' && !(this.taskForm.prazoFinal && String(this.taskForm.prazoFinal).trim())) {
        alert('Informe o Prazo Final para tarefas do tipo Prazo.')
        return
      }
      if (this.semProcessoRelacionado) {
        this.taskForm.processId = null
      } else if (!this.taskForm.processId) {
        alert('Selecione um processo na lista de sugestões ou marque que não há processo relacionado.')
        return
      }
      try {
        const payload = { ...this.taskForm }
        payload.prazoFinal =
          this.taskForm.prazoFinal && String(this.taskForm.prazoFinal).trim()
            ? String(this.taskForm.prazoFinal).trim()
            : null
        if (this.taskForm.id) {
          await taskService.update(this.taskForm.id, payload)
        } else {
          await taskService.create(payload)
        }
        this.$emit('saved')
        this.closeModal()
      } catch (err) {
        let msg = err.response?.data?.message || err.message || ''
        if (typeof msg === 'string' && /process_id|task\.process|column.*does not exist/i.test(msg)) {
          msg +=
            ' Verifique se a migração Flyway V4 (coluna task.process_id) foi aplicada na base de dados.'
        }
        if (typeof msg === 'string' && /prazo_final|column.*does not exist/i.test(msg)) {
          msg +=
            ' Verifique se a migração Flyway V5 (coluna task.prazo_final) foi aplicada na base de dados.'
        }
        alert('Erro ao salvar tarefa: ' + msg)
      }
    }
  }
}
</script>

<style scoped>
.label-required-marker {
  font-weight: 600;
  color: #64748b;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
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
</style>
