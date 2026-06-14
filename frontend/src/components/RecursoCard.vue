<template>
  <div class="recurso-card" :class="cardClasses">
    <!-- Cabeçalho sempre visível -->
    <div class="recurso-card__header" @click="toggleExpanded">
      <div class="recurso-card__header-left">
        <span class="recurso-badge" :class="classeBadgeClass">
          {{ classeLabel }}
        </span>
        <span v-if="recurso.statusRecurso" class="recurso-status">{{ recurso.statusRecurso }}</span>
        <span v-if="recurso.baixado" class="recurso-baixado-badge">Baixado</span>
      </div>
      <div class="recurso-card__header-right">
        <button
          v-if="!recurso.baixado"
          type="button"
          class="recurso-btn recurso-btn--edit"
          title="Editar recurso"
          @click.stop="openEdit"
        >
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
        <button
          v-if="!recurso.baixado"
          type="button"
          class="recurso-btn recurso-btn--baixar"
          title="Marcar como baixado"
          @click.stop="confirmBaixar"
        >
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M20 6L9 17l-5-5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
        <button
          v-if="recurso.baixado"
          type="button"
          class="recurso-btn recurso-btn--reativar"
          title="Reativar recurso"
          @click.stop="confirmReativar"
        >
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M3 3v5h5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
        <button
          type="button"
          class="recurso-btn recurso-btn--delete"
          title="Excluir recurso"
          @click.stop="confirmDelete"
        >
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M3 6h18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
        <button
          type="button"
          class="recurso-btn recurso-btn--toggle"
          :title="expanded ? 'Recolher' : 'Expandir'"
          @click.stop="toggleExpanded"
        >
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" :class="{ 'rotated': expanded }">
            <path d="M6 9l6 6 6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
      </div>
    </div>

    <!-- Corpo expandido -->
    <div v-if="expanded" class="recurso-card__body">
      <div class="recurso-info-grid">
        <div v-if="recurso.numero" class="recurso-info-item">
          <label>Nº do Processo (2ª inst.):</label>
          <span>{{ recurso.numero }}</span>
        </div>
        <div v-if="recurso.desembargadorRelator" class="recurso-info-item">
          <label>Des. Relator:</label>
          <span>{{ recurso.desembargadorRelator }}</span>
        </div>
        <div v-if="recurso.camara" class="recurso-info-item">
          <label>Câmara:</label>
          <span>{{ recurso.camara }}</span>
        </div>
        <div v-if="recurso.sistema" class="recurso-info-item">
          <label>Sistema:</label>
          <span>{{ recurso.sistema }}</span>
        </div>
        <div class="recurso-info-item">
          <label>RESP:</label>
          <span>{{ recurso.resp ? 'S' : 'N' }}</span>
        </div>
        <div class="recurso-info-item">
          <label>REXT:</label>
          <span>{{ recurso.rext ? 'S' : 'N' }}</span>
        </div>
        <div class="recurso-info-item">
          <label>Histórico Relator:</label>
          <span class="historico-badge" :class="historicoBadgeClass(recurso.historicoRelator)">
            {{ historicoLabel(recurso.historicoRelator) }}
          </span>
        </div>
        <div class="recurso-info-item">
          <label>Histórico Câmara:</label>
          <span class="historico-badge" :class="historicoBadgeClass(recurso.historicoCamara)">
            {{ historicoLabel(recurso.historicoCamara) }}
          </span>
        </div>
      </div>
    </div>

    <!-- Modal de criação/edição -->
    <div v-if="showModal" class="recurso-modal-overlay" @click.self="closeModal">
      <div class="recurso-modal">
        <h3>{{ isNew ? 'Novo Recurso' : 'Editar Recurso' }}</h3>

        <div class="form-group">
          <label>Classe:</label>
          <select v-model="form.classe" class="form-control">
            <option value="">Selecione...</option>
            <option value="APELACAO">Apelação</option>
            <option value="AGRAVO_DE_INSTRUMENTO">Agravo de Instrumento</option>
          </select>
        </div>

        <div class="form-group" v-if="form.classe === 'AGRAVO_DE_INSTRUMENTO'">
          <label>Nº do Processo (2ª instância):</label>
          <input v-model="form.numero" type="text" class="form-control" placeholder="Número do agravo" />
        </div>

        <div class="form-group">
          <label>Des. Relator:</label>
          <input v-model="form.desembargadorRelator" type="text" class="form-control" placeholder="Nome do desembargador relator" />
        </div>

        <div class="form-group">
          <label>Câmara:</label>
          <input v-model="form.camara" type="text" class="form-control" placeholder="Ex: 5ª Câmara Cível" />
        </div>

        <div class="form-group">
          <label>Sistema:</label>
          <select v-model="form.sistema" class="form-control">
            <option value="">Selecione...</option>
            <option value="TJRJ">TJRJ</option>
            <option value="EPROC">EPROC</option>
          </select>
        </div>

        <div class="form-group">
          <label>Status do Recurso:</label>
          <input v-model="form.statusRecurso" type="text" class="form-control" placeholder="Ex: Aguardando julgamento" />
        </div>

        <div class="form-group">
          <label>RESP:</label>
          <select v-model="form.resp" class="form-control">
            <option :value="false">N</option>
            <option :value="true">S</option>
          </select>
        </div>

        <div class="form-group">
          <label>REXT:</label>
          <select v-model="form.rext" class="form-control">
            <option :value="false">N</option>
            <option :value="true">S</option>
          </select>
        </div>

        <div class="form-group">
          <label>Histórico Relator:</label>
          <select v-model="form.historicoRelator" class="form-control">
            <option value="NA">N/A</option>
            <option value="PRO_PROFESSOR">Pró professor</option>
            <option value="CONTRA_PROFESSOR">Contra professor</option>
          </select>
        </div>

        <div class="form-group">
          <label>Histórico Câmara:</label>
          <select v-model="form.historicoCamara" class="form-control">
            <option value="NA">N/A</option>
            <option value="PRO_PROFESSOR">Pró professor</option>
            <option value="CONTRA_PROFESSOR">Contra professor</option>
          </select>
        </div>

        <div class="form-actions">
          <button @click="saveForm" class="btn btn-primary" :disabled="saving">
            {{ saving ? 'Salvando...' : 'Salvar' }}
          </button>
          <button @click="closeModal" class="btn btn-secondary">Cancelar</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { recursoService } from '../services/recursoService'

export default {
  name: 'RecursoCard',
  props: {
    recurso: {
      type: Object,
      required: true
    },
    isNew: {
      type: Boolean,
      default: false
    },
    processId: {
      type: [Number, String],
      default: null
    }
  },
  emits: ['updated', 'deleted'],
  data() {
    return {
      expanded: !this.recurso.baixado,
      showModal: this.isNew,
      saving: false,
      form: this.buildForm(this.recurso)
    }
  },
  computed: {
    classeLabel() {
      if (this.recurso.classe === 'APELACAO') return 'Apelação'
      if (this.recurso.classe === 'AGRAVO_DE_INSTRUMENTO') return 'Agravo de Instrumento'
      return this.recurso.classe || '—'
    },
    classeBadgeClass() {
      return {
        'recurso-badge--apelacao': this.recurso.classe === 'APELACAO',
        'recurso-badge--agravo': this.recurso.classe === 'AGRAVO_DE_INSTRUMENTO'
      }
    },
    cardClasses() {
      return {
        'recurso-card--baixado': this.recurso.baixado,
        'recurso-card--apelacao': !this.recurso.baixado && this.recurso.classe === 'APELACAO',
        'recurso-card--agravo': !this.recurso.baixado && this.recurso.classe === 'AGRAVO_DE_INSTRUMENTO'
      }
    }
  },
  watch: {
    isNew(val) {
      if (val) {
        this.form = this.buildForm({})
        this.showModal = true
      }
    }
  },
  methods: {
    buildForm(recurso) {
      return {
        classe: recurso.classe || '',
        numero: recurso.numero || '',
        desembargadorRelator: recurso.desembargadorRelator || '',
        camara: recurso.camara || '',
        sistema: recurso.sistema || '',
        statusRecurso: recurso.statusRecurso || '',
        resp: recurso.resp ?? false,
        rext: recurso.rext ?? false,
        historicoRelator: recurso.historicoRelator || 'NA',
        historicoCamara: recurso.historicoCamara || 'NA'
      }
    },
    toggleExpanded() {
      this.expanded = !this.expanded
    },
    openEdit() {
      this.form = this.buildForm(this.recurso)
      this.showModal = true
    },
    closeModal() {
      this.showModal = false
      if (this.isNew) {
        this.$emit('deleted')
      }
    },
    async saveForm() {
      if (!this.form.classe) {
        alert('Selecione a classe do recurso.')
        return
      }
      this.saving = true
      try {
        const payload = {
          ...this.form,
          processId: this.recurso.processId || this.processId
        }
        let saved
        if (this.isNew || !this.recurso.id) {
          saved = await recursoService.create(payload)
        } else {
          saved = await recursoService.update(this.recurso.id, payload)
        }
        this.showModal = false
        this.$emit('updated', saved)
      } catch (err) {
        alert('Erro ao salvar recurso: ' + (err.response?.data?.message || err.message))
      } finally {
        this.saving = false
      }
    },
    async confirmBaixar() {
      if (!confirm('Deseja marcar este recurso como "baixado"?')) return
      try {
        const updated = await recursoService.baixar(this.recurso.id)
        this.$emit('updated', updated)
      } catch (err) {
        alert('Erro ao baixar recurso: ' + (err.response?.data?.message || err.message))
      }
    },
    async confirmReativar() {
      if (!confirm('Deseja reativar este recurso?')) return
      try {
        const payload = {
          ...this.buildForm(this.recurso),
          processId: this.recurso.processId || this.processId,
          baixado: false
        }
        const updated = await recursoService.update(this.recurso.id, payload)
        this.expanded = true
        this.$emit('updated', updated)
      } catch (err) {
        alert('Erro ao reativar recurso: ' + (err.response?.data?.message || err.message))
      }
    },
    async confirmDelete() {
      if (!confirm('Tem certeza que deseja excluir este recurso?')) return
      try {
        await recursoService.delete(this.recurso.id)
        this.$emit('deleted', this.recurso.id)
      } catch (err) {
        alert('Erro ao excluir recurso: ' + (err.response?.data?.message || err.message))
      }
    },
    historicoBadgeClass(value) {
      return {
        'historico-badge--pro': value === 'PRO_PROFESSOR',
        'historico-badge--contra': value === 'CONTRA_PROFESSOR',
        'historico-badge--na': value === 'NA' || !value
      }
    },
    historicoLabel(value) {
      if (value === 'PRO_PROFESSOR') return 'Pró professor'
      if (value === 'CONTRA_PROFESSOR') return 'Contra professor'
      return 'N/A'
    }
  }
}
</script>

<style scoped>
.recurso-card {
  border: 1px solid #dee2e6;
  border-radius: 8px;
  margin-bottom: 1rem;
  overflow: hidden;
  background: #fff;
  transition: box-shadow 0.2s;
}

.recurso-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.recurso-card--apelacao {
  border-left: 4px solid #2196f3;
}

.recurso-card--agravo {
  border-left: 4px solid #ff9800;
}

.recurso-card--baixado {
  background: #f5f5f5;
  border-color: #ccc;
  opacity: 0.8;
}

.recurso-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.875rem 1rem;
  cursor: pointer;
  user-select: none;
  gap: 0.75rem;
}

.recurso-card--baixado .recurso-card__header {
  background: #eeeeee;
}

.recurso-card__header-left {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  flex-wrap: wrap;
  flex: 1;
  min-width: 0;
}

.recurso-card__header-right {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  flex-shrink: 0;
}

.recurso-badge {
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.03em;
  text-transform: uppercase;
  padding: 0.25rem 0.6rem;
  border-radius: 4px;
  white-space: nowrap;
}

.recurso-badge--apelacao {
  background: #e3f2fd;
  color: #1565c0;
}

.recurso-badge--agravo {
  background: #fff3e0;
  color: #e65100;
}

.recurso-status {
  font-size: 0.875rem;
  color: #555;
  font-style: italic;
}

.recurso-baixado-badge {
  font-size: 0.75rem;
  font-weight: 600;
  background: #e0e0e0;
  color: #616161;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
}

.recurso-btn {
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 0.3rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: background 0.15s, color 0.15s;
  color: #666;
}

.recurso-btn svg {
  width: 16px;
  height: 16px;
}

.recurso-btn--edit:hover {
  background: #f0f0f0;
  color: #333;
}

.recurso-btn--baixar:hover {
  background: #e8f5e9;
  color: #2e7d32;
}

.recurso-btn--reativar:hover {
  background: #e3f2fd;
  color: #1565c0;
}

.recurso-btn--delete:hover {
  background: #fff5f5;
  color: #c62828;
}

.recurso-btn--toggle svg {
  transition: transform 0.2s;
}

.recurso-btn--toggle svg.rotated {
  transform: rotate(180deg);
}

.recurso-card__body {
  padding: 1rem;
  border-top: 1px solid #e9ecef;
}

.recurso-info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 0.75rem;
}

.recurso-info-item {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
}

.recurso-info-item label {
  font-size: 0.8rem;
  font-weight: 600;
  color: #6c757d;
  text-transform: uppercase;
  letter-spacing: 0.03em;
}

.recurso-info-item span {
  font-size: 0.925rem;
  color: #333;
}

.historico-badge {
  display: inline-block;
  font-size: 0.8rem;
  font-weight: 600;
  padding: 0.2rem 0.55rem;
  border-radius: 4px;
}

.historico-badge--pro {
  background: #e8f5e9;
  color: #2e7d32;
}

.historico-badge--contra {
  background: #ffebee;
  color: #c62828;
}

.historico-badge--na {
  background: #f5f5f5;
  color: #757575;
}

/* Modal */
.recurso-modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

.recurso-modal {
  background: #fff;
  border-radius: 10px;
  padding: 2rem;
  width: 100%;
  max-width: 540px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.18);
}

.recurso-modal h3 {
  margin: 0 0 1.5rem;
  font-size: 1.25rem;
  color: #333;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.4rem;
  font-weight: 600;
  color: #495057;
  font-size: 0.9rem;
}

.form-control {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 1px solid #ced4da;
  border-radius: 4px;
  font-size: 0.95rem;
  box-sizing: border-box;
}

.form-control:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.2);
}

.form-actions {
  display: flex;
  gap: 0.5rem;
  margin-top: 1.5rem;
}

.btn {
  padding: 0.625rem 1.25rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.95rem;
  font-weight: 500;
  transition: background 0.2s;
}

.btn-primary {
  background: #007bff;
  color: #fff;
}

.btn-primary:hover:not(:disabled) {
  background: #0056b3;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: #6c757d;
  color: #fff;
}

.btn-secondary:hover {
  background: #545b62;
}

@media (max-width: 480px) {
  .recurso-info-grid {
    grid-template-columns: 1fr;
  }

  .recurso-modal {
    padding: 1.25rem;
  }

  .form-actions {
    flex-direction: column;
  }

  .form-actions .btn {
    width: 100%;
  }
}
</style>
