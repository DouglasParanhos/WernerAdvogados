<template>
  <div class="process-details">
    <div class="container">
      <div class="header">
        <div class="header-left">
          <button @click="goToHome" class="btn-home" title="Voltar para Home">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="9 22 9 12 15 12 15 22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
          <button @click="goBack" class="btn btn-secondary">← Voltar</button>
        </div>
        <div class="header-actions">
          <button
            type="button"
            @click="refreshDatajudMovimentos"
            class="btn-icon-refresh"
            title="Atualizar movimentos DataJud"
            aria-label="Atualizar movimentos DataJud"
            :disabled="loading || !process || !isTjrjNumero || refreshingDatajud"
          >
            <svg
              viewBox="0 0 24 24"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
              :class="{ spinning: refreshingDatajud }"
            >
              <path d="M21 12a9 9 0 0 0-9-9 9.75 9.75 0 0 0-6.74 2.74L3 8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M3 3v5h5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M3 12a9 9 0 0 0 9 9 9.75 9.75 0 0 0 6.74-2.74L21 16" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M16 21h5v-5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
          <button
            type="button"
            @click="showTaskModal = true"
            class="btn-icon-add"
            title="Adicionar tarefa"
            aria-label="Adicionar tarefa"
            :disabled="loading || !process"
          >
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
              <path d="M12 8v8M8 12h8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>
          <button
            type="button"
            @click="openDocumentModal"
            class="btn-icon-document"
            title="Gerar Documento"
            aria-label="Gerar Documento"
            :disabled="loading || !process"
          >
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="14 2 14 8 20 8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="16" y1="13" x2="8" y2="13" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <line x1="16" y1="17" x2="8" y2="17" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>
          <button @click="goToEdit" class="btn-icon-edit" title="Editar Processo">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>
      </div>
      
      <div v-if="loading" class="loading">Carregando...</div>
      <div v-if="error" class="error">{{ error }}</div>
      
      <div v-if="!loading && !error && process" class="content">
        <!-- Dados do Processo -->
        <div class="section">
          <h2>Dados do Processo</h2>
          <div class="info-grid">
            <div class="info-item">
              <label>Cliente:</label>
              <span>{{ process.nomeCliente || '—' }}</span>
            </div>
            <div class="info-item">
              <label>Número:</label>
              <span>{{ process.numero }}</span>
            </div>
            <div class="info-item">
              <label>Comarca:</label>
              <span>{{ process.comarca }}</span>
            </div>
            <div class="info-item">
              <label>Vara:</label>
              <span>{{ process.vara }}</span>
            </div>
            <div class="info-item">
              <label>Sistema:</label>
              <span class="info-item-sistema-row">
                <span class="info-item-sistema-text">{{ process.sistema }}</span>
                <a
                  v-if="sistemaPortalLink"
                  :href="sistemaPortalLink.url"
                  class="sistema-portal-link"
                  target="_blank"
                  rel="noopener noreferrer"
                  :title="sistemaPortalLink.title"
                  :aria-label="sistemaPortalLink.ariaLabel"
                >
                  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                    <path d="M18 13v6a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M15 3h6v6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M10 14L21 3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </a>
              </span>
            </div>
            <div class="info-item" v-if="process.tipoProcesso">
              <label>Tipo:</label>
              <span>{{ process.tipoProcesso }}</span>
            </div>
            <div class="info-item" v-if="process.status">
              <label>Status:</label>
              <span>{{ process.status }}</span>
            </div>
            <div class="info-item" v-if="process.valorOriginal">
              <label>Valor Original:</label>
              <span>{{ formatCurrency(process.valorOriginal) }}</span>
            </div>
            <div class="info-item" v-if="process.valorCorrigido">
              <label>Valor Corrigido:</label>
              <span>{{ formatCurrency(process.valorCorrigido) }}</span>
            </div>
            <div class="info-item" v-if="process.previsaoHonorariosContratuais">
              <label>Previsão Honorários Contratuais:</label>
              <span>{{ formatCurrency(process.previsaoHonorariosContratuais) }}</span>
            </div>
            <div class="info-item" v-if="process.previsaoHonorariosSucumbenciais">
              <label>Previsão Honorários Sucumbenciais:</label>
              <span>{{ formatCurrency(process.previsaoHonorariosSucumbenciais) }}</span>
            </div>
            <div class="info-item" v-if="process.distribuidoEm">
              <label>Distribuído em:</label>
              <span>{{ formatDate(process.distribuidoEm) }}</span>
            </div>
          </div>
        </div>
        
        <!-- Movimentações -->
        <div class="section">
          <div class="section-header">
            <h2>Movimentações</h2>
            <button type="button" @click="openNewMovimentForm" class="btn-icon-add" title="Nova Movimentação">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
          </div>
          
          <!-- Formulário de Nova Movimentação -->
          <div v-if="showNewMovimentForm" class="moviment-form">
            <h3>Nova Movimentação</h3>
            <div class="form-group">
              <label for="new-moviment-datetime">Data:</label>
              <div class="datetime-local-row">
                <input
                  id="new-moviment-datetime"
                  ref="newMovimentDateInput"
                  type="datetime-local"
                  v-model="newMoviment.date"
                  class="form-control datetime-local-input"
                />
                <button
                  type="button"
                  class="btn-datetime-picker"
                  title="Abrir calendário"
                  aria-label="Abrir calendário para selecionar data e hora"
                  @click="openDatetimeLocalPicker('newMovimentDateInput')"
                >
                  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                    <rect x="3" y="4" width="18" height="18" rx="2" stroke="currentColor" stroke-width="2"/>
                    <path d="M3 10h18M8 2v4M16 2v4" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                  </svg>
                </button>
              </div>
            </div>
            <div class="form-group">
              <label>Descrição:</label>
              <textarea v-model="newMoviment.descricao" class="form-control" rows="3"></textarea>
            </div>
            <div class="form-group checkbox-group">
              <label class="checkbox-label">
                <input type="checkbox" v-model="newMoviment.visibleToClient" class="checkbox-input" />
                <span class="checkbox-custom"></span>
                <span class="checkbox-text">Visível para o cliente</span>
              </label>
            </div>
            <div class="form-actions">
              <button @click="saveNewMoviment" class="btn btn-primary" :disabled="saving">Salvar</button>
              <button @click="cancelNewMoviment" class="btn btn-secondary">Cancelar</button>
            </div>
          </div>
          
          <!-- Lista de Movimentações -->
          <div v-if="movimentsForDisplay.length > 0" class="moviments-list">
            <div
              v-for="moviment in movimentsForDisplay"
              :key="moviment.isPending ? moviment.tempId : moviment.id"
              class="moviment-card"
              :class="{ 'moviment-card--pending': moviment.isPending }"
            >
              <div v-if="!moviment.isPending && editingMovimentId === moviment.id" class="moviment-edit-form">
                <div class="form-group">
                  <label for="edit-moviment-datetime">Data:</label>
                  <div class="datetime-local-row">
                    <input
                      id="edit-moviment-datetime"
                      ref="editMovimentDateInput"
                      type="datetime-local"
                      v-model="editingMoviment.date"
                      class="form-control datetime-local-input"
                    />
                    <button
                      type="button"
                      class="btn-datetime-picker"
                      title="Abrir calendário"
                      aria-label="Abrir calendário para selecionar data e hora"
                      @click="openDatetimeLocalPicker('editMovimentDateInput')"
                    >
                      <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                        <rect x="3" y="4" width="18" height="18" rx="2" stroke="currentColor" stroke-width="2"/>
                        <path d="M3 10h18M8 2v4M16 2v4" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                      </svg>
                    </button>
                  </div>
                </div>
                <div class="form-group">
                  <label>Descrição:</label>
                  <textarea v-model="editingMoviment.descricao" class="form-control" rows="3"></textarea>
                </div>
                <div class="form-group checkbox-group">
                  <label class="checkbox-label">
                    <input type="checkbox" v-model="editingMoviment.visibleToClient" class="checkbox-input" />
                    <span class="checkbox-custom"></span>
                    <span class="checkbox-text">Visível para o cliente</span>
                  </label>
                </div>
                <div class="form-actions">
                  <button @click="saveEditMoviment" class="btn btn-primary" :disabled="saving">Salvar</button>
                  <button @click="cancelEditMoviment" class="btn btn-secondary">Cancelar</button>
                </div>
              </div>

              <div v-else-if="!moviment.isPending && editingMovimentId !== moviment.id" class="moviment-display">
                <div class="moviment-header">
                  <div class="moviment-date">{{ formatDate(moviment.date) }}</div>
                  <div class="moviment-actions">
                    <button @click="startEditMoviment(moviment)" class="icon-btn edit-btn" title="Editar movimentação">
                      <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      </svg>
                    </button>
                    <button @click="deleteMoviment(moviment.id)" class="icon-btn delete-btn" title="Excluir movimentação">
                      <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M3 6h18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      </svg>
                    </button>
                  </div>
                </div>
                <div class="moviment-description">{{ moviment.descricao }}</div>
              </div>

              <div v-else-if="moviment.isPending" class="moviment-display">
                <div class="moviment-header moviment-header--pending">
                  <div class="moviment-date-block">
                    <div class="moviment-date">{{ formatDatajudMovimentDate(moviment.date) }}</div>
                    <span v-if="moviment.grauLabel" class="grau-pill">{{ formatGrauLabel(moviment.grauLabel) }}</span>
                  </div>
                  <div class="moviment-actions moviment-actions--pending">
                    <span class="badge-datajud-new" title="Movimento retornado do DataJud, ainda não salvo">Novo (DataJud)</span>
                    <button
                      type="button"
                      class="btn btn-primary btn-sm"
                      :disabled="savingPendingId === moviment.tempId"
                      @click="savePendingMoviment(moviment.tempId)"
                    >
                      {{ savingPendingId === moviment.tempId ? 'Salvando…' : 'Salvar' }}
                    </button>
                    <button
                      type="button"
                      class="btn btn-secondary btn-sm"
                      :disabled="savingPendingId === moviment.tempId"
                      @click="ignorePendingMoviment(moviment.tempId)"
                    >
                      Ignorar
                    </button>
                  </div>
                </div>
                <div class="moviment-description">{{ moviment.descricao }}</div>
                <div v-if="moviment.isPending" class="pending-checkbox">
                  <label class="checkbox-label">
                    <input type="checkbox" v-model="moviment.pendingRef.visibleToClient" class="checkbox-input" />
                    <span class="checkbox-custom"></span>
                    <span class="checkbox-text">Visível para o cliente</span>
                  </label>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="no-moviments">
            Nenhuma movimentação cadastrada
          </div>
        </div>
      </div>
    </div>

    <TaskFormModal
      v-model="showTaskModal"
      :preset-process="process ? { id: process.id, numero: process.numero } : null"
    />

    <DocumentGeneratorModal
      v-if="process"
      :show="showDocumentModal"
      :process="process"
      @close="closeDocumentModal"
    />
  </div>
</template>

<script>
import { processService } from '../services/processService'
import { movimentService } from '../services/movimentService'
import { matriculationService } from '../services/matriculationService'
import { datajudService } from '../services/datajudService'
import TaskFormModal from '../components/TaskFormModal.vue'
import DocumentGeneratorModal from '../components/DocumentGeneratorModal.vue'

function yyyyMmDdDaysAgo(days) {
  const d = new Date()
  d.setDate(d.getDate() - days)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

/** Valor inicial para `datetime-local`: hoje às 00:01 no fuso local. */
function todayAt0001Local() {
  const d = new Date()
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}T00:01`
}

function newPendingTempId() {
  const c = globalThis.crypto
  if (c && typeof c.randomUUID === 'function') {
    return c.randomUUID()
  }
  return `p-${Date.now()}-${Math.random().toString(36).slice(2, 11)}`
}

const SISTEMA_PORTAL_URL = {
  PJE: 'https://tjrj.pje.jus.br/1g/login.seam',
  TJRJ: 'https://www3.tjrj.jus.br/consultaprocessual/#/consultapublica#porNumero',
  EPROC: 'https://eproc1g.tjrj.jus.br/eproc/'
}

/** @returns {{ url: string, title: string, ariaLabel: string } | null} */
function resolveSistemaPortalLink(sistema) {
  if (sistema == null || sistema === '') return null
  const key = String(sistema).trim().toUpperCase()
  const url = SISTEMA_PORTAL_URL[key]
  if (!url) return null
  const titleByKey = {
    PJE: 'Abrir portal PJE TJRJ',
    TJRJ: 'Abrir consulta processual TJRJ',
    EPROC: 'Abrir portal eproc TJRJ'
  }
  const title = titleByKey[key] || `Abrir portal ${key}`
  return {
    url,
    title,
    ariaLabel: `${title} (nova aba)`
  }
}

export default {
  name: 'ProcessDetails',
  components: { TaskFormModal, DocumentGeneratorModal },
  data() {
    return {
      process: null,
      moviments: [],
      pendingDatajud: [],
      loading: false,
      error: null,
      refreshingDatajud: false,
      savingPendingId: null,
      showTaskModal: false,
      showDocumentModal: false,
      showNewMovimentForm: false,
      editingMovimentId: null,
      editingMoviment: null,
      saving: false,
      newMoviment: {
        descricao: '',
        date: '',
        processId: null,
        visibleToClient: false
      }
    }
  },
  computed: {
    isTjrjNumero() {
      const n = this.process?.numero
      return typeof n === 'string' && n.toLowerCase().includes('.8.19.')
    },
    movimentsForDisplay() {
      const dbRows = (this.moviments || []).map((m) => ({
        ...m,
        isPending: false,
        sortKey: this.movementTimeMs(m.date) ?? 0
      }))
      const pendRows = (this.pendingDatajud || []).map((p) => ({
        isPending: true,
        pendingRef: p,
        tempId: p.tempId,
        date: p.dataRaw,
        descricao: p.descricao,
        grauLabel: p.grauLabel,
        sortKey: this.movementTimeMs(p.dataRaw) ?? Number.NEGATIVE_INFINITY
      }))
      return [...dbRows, ...pendRows].sort((a, b) => b.sortKey - a.sortKey)
    },
    sistemaPortalLink() {
      return resolveSistemaPortalLink(this.process?.sistema)
    }
  },
  async mounted() {
    await this.loadProcess()
    await this.loadMoviments()
  },
  methods: {
    async loadProcess() {
      this.loading = true
      this.error = null
      try {
        const id = this.$route.params.id
        this.process = await processService.getById(id)
        this.newMoviment.processId = id
      } catch (err) {
        this.error = 'Erro ao carregar processo: ' + (err.response?.data?.message || err.message)
        console.error(err)
      } finally {
        this.loading = false
      }
    },
    async loadMoviments() {
      try {
        const processId = this.$route.params.id
        this.moviments = await movimentService.getAll(processId)
      } catch (err) {
        console.error('Erro ao carregar movimentações:', err)
      }
    },
    normalizeMovementDesc(s) {
      if (s == null) return ''
      return String(s).trim().replace(/\s+/g, ' ').toLowerCase()
    },
    /** Instante em ms para comparação DataJud vs movimento salvo (parse compatível com ISO e strings do TJRJ). */
    movementTimeMs(value) {
      if (value == null) return null
      try {
        const normalized = String(value).replace('Z', '+00:00')
        const t = new Date(normalized).getTime()
        return Number.isNaN(t) ? null : t
      } catch {
        return null
      }
    },
    movementKey(dateValue, descricao) {
      const ms = this.movementTimeMs(dateValue)
      const timePart = ms != null ? String(ms) : `raw:${String(dateValue)}`
      return `${timePart}::${this.normalizeMovementDesc(descricao)}`
    },
    formatDatajudMovimentDate(raw) {
      if (!raw) return '—'
      try {
        const normalized = String(raw).replace('Z', '+00:00')
        const dt = new Date(normalized)
        if (Number.isNaN(dt.getTime())) return raw
        return dt.toLocaleString('pt-BR', {
          day: '2-digit',
          month: '2-digit',
          year: 'numeric',
          hour: '2-digit',
          minute: '2-digit'
        })
      } catch {
        return raw
      }
    },
    formatGrauLabel(grau) {
      if (grau == null || grau === '' || grau === 'desconhecido') return '(não informado)'
      return grau
    },
    flattenDatajudRow(row) {
      const graus = row.graus || []
      const out = []
      for (const g of graus) {
        const grauLabel = this.formatGrauLabel(g.grau)
        for (const mv of g.movimentos || []) {
          out.push({
            data: mv.data,
            descricao: mv.descricao,
            grauLabel
          })
        }
      }
      out.sort((a, b) => (this.movementTimeMs(b.data) ?? 0) - (this.movementTimeMs(a.data) ?? 0))
      return out
    },
    async refreshDatajudMovimentos() {
      if (!this.process?.id || !this.isTjrjNumero) {
        return
      }
      this.refreshingDatajud = true
      try {
        const dataInicio = yyyyMmDdDaysAgo(365)
        const row = await datajudService.consultarMovimentosProcesso(this.process.id, dataInicio)
        if (row.status === 'erro') {
          window.alert(
            'Erro ao consultar DataJud: ' + (row.erro || 'falha desconhecida')
          )
          return
        }
        if (row.status === 'nao_encontrado') {
          window.alert('Processo não encontrado no DataJud para o NPU informado.')
          return
        }
        const flat = this.flattenDatajudRow(row)
        const existingKeys = new Set(
          (this.moviments || []).map((m) => this.movementKey(m.date, m.descricao))
        )
        for (const p of this.pendingDatajud) {
          existingKeys.add(this.movementKey(p.dataRaw, p.descricao))
        }
        let added = 0
        for (const item of flat) {
          const key = this.movementKey(item.data, item.descricao)
          if (!existingKeys.has(key)) {
            existingKeys.add(key)
            this.pendingDatajud.push({
              tempId: newPendingTempId(),
              dataRaw: item.data,
              descricao: item.descricao,
              grauLabel: item.grauLabel,
              visibleToClient: true
            })
            added++
          }
        }
        if (!added) {
          window.alert(
            'Nenhum movimento novo do DataJud no período (últimos 365 dias) que não estivesse já na lista.'
          )
        }
      } catch (err) {
        const msg = err.response?.data?.message || err.message || 'Falha na consulta DataJud.'
        window.alert(msg)
        console.error(err)
      } finally {
        this.refreshingDatajud = false
      }
    },
    async savePendingMoviment(tempId) {
      const pending = this.pendingDatajud.find((p) => p.tempId === tempId)
      if (!pending) return
      const ms = this.movementTimeMs(pending.dataRaw)
      if (ms == null) {
        window.alert('Data do movimento inválida; não é possível salvar.')
        return
      }
      this.savingPendingId = tempId
      try {
        await movimentService.create({
          processId: Number(this.$route.params.id),
          descricao: pending.descricao,
          date: new Date(ms).toISOString(),
          visibleToClient: pending.visibleToClient !== false
        })
        this.pendingDatajud = this.pendingDatajud.filter((p) => p.tempId !== tempId)
        await this.loadMoviments()
      } catch (err) {
        window.alert('Erro ao salvar movimentação: ' + (err.response?.data?.message || err.message))
      } finally {
        this.savingPendingId = null
      }
    },
    ignorePendingMoviment(tempId) {
      this.pendingDatajud = this.pendingDatajud.filter((p) => p.tempId !== tempId)
    },
    formatDate(date) {
      if (!date) return '-'
      const d = new Date(date)
      return d.toLocaleString('pt-BR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    formatCurrency(value) {
      if (!value) return '-'
      return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value)
    },
    async goBack() {
      // Voltar para o cliente se o processo tem matrícula com personId, senão para lista de processos
      if (this.process?.matriculationId) {
        try {
          const matriculation = await matriculationService.getById(this.process.matriculationId)
          if (matriculation?.personId) {
            this.$router.push(`/clients/${matriculation.personId}`)
            return
          }
        } catch (err) {
          console.error('Erro ao buscar matrícula:', err)
        }
      }
      // Se não conseguir encontrar o cliente, voltar para lista de processos
      this.$router.push('/processes')
    },
    goToHome() {
      this.$router.push('/dashboard')
    },
    goToEdit() {
      this.$router.push(`/processes/${this.process.id}/edit`)
    },
    openDocumentModal() {
      this.showDocumentModal = true
    },
    closeDocumentModal() {
      this.showDocumentModal = false
    },
    openNewMovimentForm() {
      const pid = this.process?.id ?? this.$route.params.id
      this.newMoviment = {
        descricao: '',
        date: todayAt0001Local(),
        processId: pid,
        visibleToClient: false
      }
      this.showNewMovimentForm = true
    },
    openDatetimeLocalPicker(refKey) {
      this.$nextTick(() => {
        const raw = this.$refs[refKey]
        const input = raw && (Array.isArray(raw) ? raw[0] : raw)
        if (!input || input.tagName !== 'INPUT') return
        if (typeof input.showPicker === 'function') {
          try {
            input.showPicker()
            return
          } catch {
            /* Alguns browsers só permitem showPicker em gesto do usuário ou falham silenciosamente */
          }
        }
        input.focus()
        try {
          input.click()
        } catch {
          /* noop */
        }
      })
    },
    async saveNewMoviment() {
      if (!this.newMoviment.descricao || !this.newMoviment.date) {
        alert('Por favor, preencha todos os campos')
        return
      }
      
      this.saving = true
      try {
        // Converter para formato ISO
        const dateISO = new Date(this.newMoviment.date).toISOString()
        await movimentService.create({
          ...this.newMoviment,
          date: dateISO
        })
        await this.loadMoviments()
        this.cancelNewMoviment()
      } catch (err) {
        alert('Erro ao salvar movimentação: ' + (err.response?.data?.message || err.message))
      } finally {
        this.saving = false
      }
    },
    cancelNewMoviment() {
      this.showNewMovimentForm = false
      this.newMoviment = {
        descricao: '',
        date: '',
        processId: this.process.id,
        visibleToClient: false
      }
    },
    startEditMoviment(moviment) {
      this.editingMovimentId = moviment.id
      // Converter data para formato datetime-local
      const date = new Date(moviment.date)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      this.editingMoviment = {
        id: moviment.id,
        descricao: moviment.descricao,
        date: `${year}-${month}-${day}T${hours}:${minutes}`,
        processId: moviment.processId,
        visibleToClient: moviment.visibleToClient !== undefined ? moviment.visibleToClient : true
      }
    },
    async saveEditMoviment() {
      if (!this.editingMoviment.descricao || !this.editingMoviment.date) {
        alert('Por favor, preencha todos os campos')
        return
      }
      
      this.saving = true
      try {
        const dateISO = new Date(this.editingMoviment.date).toISOString()
        const { id, ...fields } = this.editingMoviment
        await movimentService.update(id, {
          ...fields,
          date: dateISO
        })
        await this.loadMoviments()
        this.cancelEditMoviment()
      } catch (err) {
        alert('Erro ao salvar movimentação: ' + (err.response?.data?.message || err.message))
      } finally {
        this.saving = false
      }
    },
    cancelEditMoviment() {
      this.editingMovimentId = null
      this.editingMoviment = null
    },
    async deleteMoviment(id) {
      if (!confirm('Tem certeza que deseja excluir esta movimentação?')) {
        return
      }
      try {
        await movimentService.delete(id)
        await this.loadMoviments()
      } catch (err) {
        alert('Erro ao excluir movimentação: ' + (err.response?.data?.message || err.message))
      }
    }
  }
}
</script>

<style scoped>
.process-details {
  padding: 2rem;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.btn-home {
  background: transparent;
  border: 1.5px solid #6c757d;
  border-radius: 8px;
  padding: 0.5rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6c757d;
  transition: all 0.2s;
  width: 40px;
  height: 40px;
}

.btn-home:hover {
  background-color: #f8f9fa;
  color: #003d7a;
  border-color: #003d7a;
}

.btn-home svg {
  width: 20px;
  height: 20px;
}

.header-actions {
  display: flex;
  gap: 0.75rem;
  align-items: center;
}

.btn-icon-refresh {
  background: #17a2b8;
  border: none;
  border-radius: 8px;
  padding: 0.75rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  transition: all 0.2s;
  width: 48px;
  height: 48px;
}

.btn-icon-refresh:hover:not(:disabled) {
  background-color: #138496;
}

.btn-icon-refresh:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.btn-icon-refresh svg {
  width: 24px;
  height: 24px;
}

.btn-icon-refresh svg.spinning {
  animation: spin-refresh 0.9s linear infinite;
}

@keyframes spin-refresh {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.btn-icon-edit,
.btn-icon-add,
.btn-icon-document {
  background: #6c757d;
  border: none;
  border-radius: 8px;
  padding: 0.75rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  transition: all 0.2s;
  width: 48px;
  height: 48px;
}

.btn-icon-edit:hover {
  background-color: #545b62;
}

.btn-icon-add {
  background: #007bff;
}

.btn-icon-add:hover {
  background-color: #0056b3;
}

.btn-icon-document {
  background: #003d7a;
}

.btn-icon-document:hover:not(:disabled) {
  background-color: #002d5c;
}

.btn-icon-document:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.btn-icon-edit svg,
.btn-icon-add svg,
.btn-icon-document svg {
  width: 24px;
  height: 24px;
}

.icon-btn {
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 0.375rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  border-radius: 4px;
}

.icon-btn svg {
  width: 18px;
  height: 18px;
}

.edit-btn {
  color: #6c757d;
}

.edit-btn:hover {
  background-color: #f8f9fa;
  color: #495057;
}

.delete-btn {
  color: #dc3545;
}

.delete-btn:hover {
  background-color: #fff5f5;
  color: #c82333;
}

.section {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  padding: 2rem;
  margin-bottom: 2rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.section h2 {
  font-size: 1.5rem;
  color: #333;
  margin: 0;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1rem;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.info-item label {
  font-weight: 600;
  color: #6c757d;
  font-size: 0.875rem;
}

.info-item span {
  color: #333;
  font-size: 1rem;
}

.info-item-sistema-row {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.info-item-sistema-text {
  min-width: 0;
}

.sistema-portal-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: #003d7a;
  line-height: 1;
  transition: color 0.2s;
}

.sistema-portal-link:hover {
  color: #002d5a;
}

.sistema-portal-link:focus {
  outline: 2px solid #003d7a;
  outline-offset: 2px;
  border-radius: 4px;
}

.sistema-portal-link svg {
  width: 1.125rem;
  height: 1.125rem;
}

.moviment-form {
  background: #f8f9fa;
  border-radius: 6px;
  padding: 1.5rem;
  margin-bottom: 1.5rem;
}

.moviment-form h3 {
  margin-top: 0;
  margin-bottom: 1rem;
  color: #333;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #495057;
}

.datetime-local-row {
  display: flex;
  align-items: stretch;
  gap: 0.5rem;
}

.datetime-local-input {
  flex: 1;
  min-width: 0;
}

.btn-datetime-picker {
  flex-shrink: 0;
  width: 42px;
  min-height: 42px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  border: 1px solid #ced4da;
  border-radius: 6px;
  color: #495057;
  cursor: pointer;
  transition: border-color 0.2s, background 0.2s, color 0.2s;
}

.btn-datetime-picker:hover {
  border-color: #007bff;
  background: #f0f7ff;
  color: #007bff;
}

.btn-datetime-picker:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.25);
}

.btn-datetime-picker svg {
  width: 22px;
  height: 22px;
}

/* Checkbox customizado */
.checkbox-group {
  margin-bottom: 1.5rem;
}

.checkbox-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  user-select: none;
  margin-bottom: 0 !important;
  font-weight: 500;
  color: #495057;
}

.checkbox-input {
  position: absolute;
  opacity: 0;
  cursor: pointer;
  height: 0;
  width: 0;
}

.checkbox-custom {
  position: relative;
  display: inline-block;
  width: 20px;
  height: 20px;
  min-width: 20px;
  min-height: 20px;
  background-color: #fff;
  border: 2px solid #ced4da;
  border-radius: 4px;
  margin-right: 0.75rem;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.checkbox-label:hover .checkbox-custom {
  border-color: #007bff;
  background-color: #f0f7ff;
}

.checkbox-input:checked ~ .checkbox-custom {
  background-color: #007bff;
  border-color: #007bff;
}

.checkbox-input:checked ~ .checkbox-custom::after {
  content: '';
  position: absolute;
  left: 6px;
  top: 2px;
  width: 5px;
  height: 10px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.checkbox-input:focus ~ .checkbox-custom {
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.25);
}

.checkbox-text {
  font-size: 0.95rem;
  line-height: 1.5;
  color: #495057;
}

.form-control {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ced4da;
  border-radius: 4px;
  font-size: 1rem;
}

.form-control:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 0.2rem rgba(0,123,255,0.25);
}

.form-actions {
  display: flex;
  gap: 0.5rem;
}

.moviments-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.moviment-card {
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  padding: 1rem;
}

.moviment-card--pending {
  border-color: #17a2b8;
  background: linear-gradient(135deg, #f0fbfc 0%, #f8f9fa 100%);
  box-shadow: 0 0 0 1px rgba(23, 162, 184, 0.15);
}

.moviment-header--pending {
  flex-wrap: wrap;
  gap: 0.5rem;
}

.moviment-date-block {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.5rem;
}

.grau-pill {
  font-size: 0.75rem;
  font-weight: 600;
  color: #0c5460;
  background: #d1ecf1;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
}

.badge-datajud-new {
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.03em;
  color: #0c5460;
  background: #bee5eb;
  padding: 0.35rem 0.55rem;
  border-radius: 4px;
  margin-right: 0.25rem;
}

.moviment-actions--pending {
  flex-wrap: wrap;
  align-items: center;
}

.pending-checkbox {
  margin-top: 0.75rem;
  padding-top: 0.5rem;
  border-top: 1px dashed #ced4da;
}

.moviment-display {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.moviment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.moviment-date {
  font-weight: 600;
  color: #495057;
  font-size: 0.9rem;
}

.moviment-description {
  color: #333;
  line-height: 1.5;
}

.moviment-actions {
  display: flex;
  gap: 0.5rem;
}

.moviment-edit-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.no-moviments {
  text-align: center;
  padding: 2rem;
  color: #6c757d;
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

.btn-danger {
  background-color: #dc3545;
  color: white;
}

.btn-danger:hover {
  background-color: #c82333;
}

.btn-sm {
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
}

.loading, .error {
  text-align: center;
  padding: 2rem;
  font-size: 1.1rem;
}

.error {
  color: #dc3545;
}

/* Responsividade para mobile */
@media (max-width: 768px) {
  .process-details {
    padding: 1rem;
  }

  .container {
    max-width: 100%;
  }

  .header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }

  .header-left {
    display: flex;
    gap: 0.5rem;
  }

  .header-actions {
    width: 100%;
    justify-content: flex-end;
  }

  .btn-home {
    width: 36px;
    height: 36px;
  }

  .btn-home svg {
    width: 18px;
    height: 18px;
  }

  .btn-icon-edit,
  .btn-icon-add,
  .btn-icon-document {
    width: 44px;
    height: 44px;
  }

  .btn-icon-refresh {
    width: 44px;
    height: 44px;
  }

  .btn-icon-edit svg,
  .btn-icon-add svg,
  .btn-icon-document svg {
    width: 20px;
    height: 20px;
  }

  .section {
    padding: 1rem;
    margin-bottom: 1rem;
  }

  .section-header {
    flex-direction: column;
    align-items: stretch;
    gap: 1rem;
  }

  .section-header h2 {
    font-size: 1.25rem;
  }

  .section-header button {
    width: 100%;
  }

  .section-header .btn-icon-add {
    width: 100%;
    justify-content: center;
  }

  .info-grid {
    grid-template-columns: 1fr;
    gap: 0.75rem;
  }

  .moviment-form {
    padding: 1rem;
  }

  .moviment-form h3 {
    font-size: 1.1rem;
  }

  .form-group {
    margin-bottom: 1rem;
  }

  .form-actions {
    flex-direction: column;
  }

  .form-actions button {
    width: 100%;
  }

  .moviment-card {
    padding: 0.75rem;
  }

  .moviment-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.75rem;
  }

  .moviment-actions {
    width: 100%;
    justify-content: flex-end;
    gap: 0.5rem;
  }

  .moviment-actions .icon-btn {
    min-width: 40px;
    min-height: 40px;
    padding: 0.5rem;
  }

  .moviment-actions .icon-btn svg {
    width: 20px;
    height: 20px;
  }

  .moviment-edit-form {
    gap: 0.75rem;
  }

  .btn {
    padding: 0.625rem 1rem;
    font-size: 0.9rem;
  }

  .btn-sm {
    padding: 0.5rem 0.75rem;
    font-size: 0.8rem;
  }

  .checkbox-label {
    font-size: 0.9rem;
  }

  .checkbox-custom {
    width: 18px;
    height: 18px;
    min-width: 18px;
    min-height: 18px;
    margin-right: 0.5rem;
  }

  .checkbox-input:checked ~ .checkbox-custom::after {
    left: 5px;
    top: 1px;
    width: 4px;
    height: 8px;
  }
}

@media (max-width: 480px) {
  .process-details {
    padding: 0.5rem;
  }

  .header {
    margin-bottom: 1rem;
  }

  .btn-home {
    width: 32px;
    height: 32px;
  }

  .btn-home svg {
    width: 16px;
    height: 16px;
  }

  .btn-icon-edit,
  .btn-icon-add,
  .btn-icon-document {
    width: 40px;
    height: 40px;
  }

  .btn-icon-refresh {
    width: 40px;
    height: 40px;
  }

  .btn-icon-edit svg,
  .btn-icon-add svg,
  .btn-icon-document svg {
    width: 18px;
    height: 18px;
  }

  .section {
    padding: 0.75rem;
  }

  .section h2 {
    font-size: 1.1rem;
  }

  .moviment-form h3 {
    font-size: 1rem;
  }

  .form-control {
    font-size: 0.9rem;
    padding: 0.625rem;
  }

  .moviment-actions .icon-btn {
    min-width: 36px;
    min-height: 36px;
    padding: 0.375rem;
  }

  .moviment-actions .icon-btn svg {
    width: 18px;
    height: 18px;
  }

  .checkbox-text {
    font-size: 0.85rem;
  }
}
</style>

