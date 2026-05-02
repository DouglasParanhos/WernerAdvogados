<template>
  <div class="page">
    <div class="page-container">
      <div class="page-header">
        <button type="button" @click="goHome" class="btn-home" title="Voltar para Home">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <polyline points="9 22 9 12 15 12 15 22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
        <h1>Movimentos DataJud — TJRJ</h1>
        <p class="subtitle">
          Consulta a API pública do CNJ para processos cadastrados com número TJRJ (segmento <code>.8.19.</code>).
          O intervalo é <strong>da data inicial (00:00, horário de Brasília) até o momento em que você clica em Pesquisar</strong> — não é “últimos N dias” sozinho; use o atalho “Últimos 7 dias” se quiser uma janela de uma semana até agora.
          Os andamentos vêm <strong>agrupados por processo</strong>, em ordem cronológica (mais recentes primeiro); o <strong>grau</strong> (G1, G2, …) aparece em cada linha.
          A data inicial padrão é há 365 dias.
        </p>
      </div>

      <div class="panel">
        <div class="form-row">
          <label class="label" for="data-inicio">Data inicial</label>
          <input
            id="data-inicio"
            v-model="dataInicio"
            type="date"
            class="date-input"
            :disabled="loading"
          >
          <button
            type="button"
            class="btn-search"
            :disabled="loading || !dataInicio"
            @click="pesquisar"
          >
            <span v-if="loading" class="btn-spinner" aria-hidden="true"></span>
            {{ loading ? 'Pesquisando…' : 'Pesquisar' }}
          </button>
          <button
            type="button"
            class="btn-preset"
            :disabled="loading"
            title="Define a data inicial como hoje menos 7 dias"
            @click="setUltimos7Dias"
          >
            Últimos 7 dias
          </button>
        </div>
        <p v-if="errorMsg" class="error-msg" role="alert">{{ errorMsg }}</p>
      </div>

      <div v-if="resultado && !loading" class="summary">
        <span><strong>{{ resultado.totalConsultados }}</strong> processos consultados</span>
        <span><strong>{{ resultado.totalEncontrados }}</strong> encontrados no DataJud</span>
        <span><strong>{{ resultadosVisiveis.length }}</strong> com movimentos no período</span>
        <span><strong>{{ resultado.totalNaoEncontrados }}</strong> não encontrados</span>
        <span><strong>{{ resultado.totalErros }}</strong> erros</span>
        <span v-if="resultado.intervalo" class="interval-hint">
          Intervalo: {{ resultado.intervalo.dataInicio }} → até {{ resultado.intervalo.ate }} ({{ resultado.intervalo.zona }})
        </span>
      </div>

      <div v-if="resultado && !loading" class="results">
        <p v-if="!resultadosVisiveis.length" class="empty-results">
          Nenhum processo teve movimentos no período selecionado.
        </p>
        <section
          v-for="(row, idx) in resultadosVisiveis"
          :key="row.numeroProcesso + '-' + idx"
          class="process-block"
        >
          <h2 class="process-title">
            <span class="npu">{{ row.numeroProcesso }}</span>
            <a
              v-if="row.processoId"
              :href="processHref(row.processoId)"
              target="_blank"
              rel="noopener"
              class="btn-open-proc"
              title="Abrir processo em nova aba"
              aria-label="Abrir processo em nova aba"
            >↗</a>
            <span :class="['badge', `badge-${row.status}`]">{{ labelStatus(row.status) }}</span>
            <span class="mov-count">{{ totalMovCount(row) }} mov.</span>
          </h2>
          <p v-if="row.erro" class="erro-linha">{{ row.erro }}</p>
          <div v-if="grauResumos(row).length" class="process-grau-strip">
            <p
              v-for="(sum, si) in grauResumos(row)"
              :key="'strip-' + idx + '-' + si"
              class="grau-meta-line"
            >
              <span class="grau-strip-label">Grau {{ sum.grau }}</span>
              <span v-if="sum.classe || sum.orgao" class="grau-strip-detail">
                <template v-if="sum.classe">{{ sum.classe }}</template>
                <template v-if="sum.classe && sum.orgao"> · </template>
                <template v-if="sum.orgao">{{ sum.orgao }}</template>
              </span>
            </p>
          </div>
          <ul v-if="mergedMovimentos(row).length" class="mov-list">
            <li v-for="(mv, mi) in mergedMovimentos(row)" :key="'m-' + idx + '-' + mi">
              <span class="mv-data">{{ formatMvDate(mv.data) }}</span>
              <button
                type="button"
                class="btn-copy"
                :class="{ 'is-copied': copiedKey === `d-${idx}-${mi}` }"
                @click="copyText(formatMvDate(mv.data), `d-${idx}-${mi}`)"
                :title="copiedKey === `d-${idx}-${mi}` ? 'Copiado!' : 'Copiar data'"
                :aria-label="copiedKey === `d-${idx}-${mi}` ? 'Data copiada' : 'Copiar data'"
              >{{ copiedKey === `d-${idx}-${mi}` ? '✓' : '⧉' }}</button>
              <span class="mv-grau" :title="'Grau no DataJud: ' + mv.grauLabel">{{ mv.grauLabel }}</span>
              <span class="mv-desc">{{ mv.descricao || '(sem descrição)' }}</span>
              <button
                type="button"
                class="btn-copy"
                :class="{ 'is-copied': copiedKey === `t-${idx}-${mi}` }"
                @click="copyText(mv.descricao || '', `t-${idx}-${mi}`)"
                :title="copiedKey === `t-${idx}-${mi}` ? 'Copiado!' : 'Copiar descrição'"
                :aria-label="copiedKey === `t-${idx}-${mi}` ? 'Descrição copiada' : 'Copiar descrição'"
              >{{ copiedKey === `t-${idx}-${mi}` ? '✓' : '⧉' }}</button>
              <span v-if="mv.codigo" class="mv-cod">cód. {{ mv.codigo }}</span>
            </li>
          </ul>
        </section>
      </div>
    </div>
  </div>
</template>

<script>
import { datajudService } from '../services/datajudService'

function yyyyMmDdDaysAgo(days) {
  const d = new Date()
  d.setDate(d.getDate() - days)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

export default {
  name: 'DatajudMovimentos',
  data() {
    return {
      dataInicio: yyyyMmDdDaysAgo(365),
      loading: false,
      errorMsg: '',
      resultado: null,
      copiedKey: '',
      copiedTimer: null
    }
  },
  beforeUnmount() {
    if (this.copiedTimer) {
      clearTimeout(this.copiedTimer)
    }
  },
  computed: {
    /**
     * Defesa no cliente: só exibe cartões com pelo menos um movimento no período
     * (evita cartões “0 mov.” / status estranho se a API divergir).
     */
    resultadosVisiveis() {
      if (!this.resultado || !Array.isArray(this.resultado.resultados)) {
        return []
      }
      return this.resultado.resultados.filter((row) => {
        if (!row || row.status === 'nao_encontrado' || row.status === 'erro') {
          return false
        }
        return this.totalMovCount(row) > 0
      })
    }
  },
  methods: {
    goHome() {
      this.$router.push('/dashboard')
    },
    processHref(id) {
      return this.$router.resolve({ name: 'ProcessDetails', params: { id } }).href
    },
    async copyText(text, key) {
      const value = text == null ? '' : String(text)
      try {
        if (navigator.clipboard && window.isSecureContext) {
          await navigator.clipboard.writeText(value)
        } else {
          const ta = document.createElement('textarea')
          ta.value = value
          ta.setAttribute('readonly', '')
          ta.style.position = 'fixed'
          ta.style.opacity = '0'
          document.body.appendChild(ta)
          ta.select()
          document.execCommand('copy')
          document.body.removeChild(ta)
        }
        this.copiedKey = key
        if (this.copiedTimer) clearTimeout(this.copiedTimer)
        this.copiedTimer = setTimeout(() => {
          this.copiedKey = ''
          this.copiedTimer = null
        }, 1200)
      } catch (e) {
        // silently ignore – clipboard pode falhar em ambientes restritos
      }
    },
    labelStatus(s) {
      if (s === 'encontrado') return 'Encontrado'
      if (s === 'nao_encontrado') return 'Não encontrado'
      if (s === 'erro') return 'Erro'
      return s
    },
    formatMvDate(raw) {
      if (!raw) return '—'
      try {
        const normalized = String(raw).replace('Z', '+00:00')
        const dt = new Date(normalized)
        if (Number.isNaN(dt.getTime())) return raw
        return dt.toLocaleString('pt-BR', {
          dateStyle: 'short',
          timeStyle: 'short'
        })
      } catch {
        return raw
      }
    },
    mvSortKey(raw) {
      if (!raw) return Number.NEGATIVE_INFINITY
      try {
        const normalized = String(raw).replace('Z', '+00:00')
        const t = new Date(normalized).getTime()
        return Number.isNaN(t) ? Number.NEGATIVE_INFINITY : t
      } catch {
        return Number.NEGATIVE_INFINITY
      }
    },
    grauResumos(row) {
      const graus = row.graus || []
      return graus
        .filter((g) => g.movimentos && g.movimentos.length)
        .map((g) => ({
          grau: this.formatGrauLabel(g.grau),
          classe: g.classeProcessual || '',
          orgao: g.orgaoJulgador || ''
        }))
    },
    mergedMovimentos(row) {
      const graus = row.graus || []
      const out = []
      for (const g of graus) {
        const grauLabel = this.formatGrauLabel(g.grau)
        for (const mv of g.movimentos || []) {
          out.push({
            data: mv.data,
            descricao: mv.descricao,
            codigo: mv.codigo,
            grauLabel
          })
        }
      }
      out.sort((a, b) => this.mvSortKey(b.data) - this.mvSortKey(a.data))
      return out
    },
    async pesquisar() {
      this.errorMsg = ''
      this.resultado = null
      this.loading = true
      try {
        this.resultado = await datajudService.consultarMovimentos(this.dataInicio)
      } catch (e) {
        const msg = e.response?.data?.message || e.message || 'Falha na consulta.'
        this.errorMsg = msg
      } finally {
        this.loading = false
      }
    },
    setUltimos7Dias() {
      this.dataInicio = yyyyMmDdDaysAgo(7)
    },
    totalMovCount(row) {
      const graus = row.graus
      if (!graus || !graus.length) return 0
      return graus.reduce((n, g) => n + (g.movimentos ? g.movimentos.length : 0), 0)
    },
    formatGrauLabel(grau) {
      if (grau == null || grau === '' || grau === 'desconhecido') return '(não informado)'
      return grau
    }
  }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: linear-gradient(135deg, #d0d8e0 0%, #e8eef5 50%, #c0c8d0 100%);
  background-attachment: fixed;
  padding: 2rem;
}

.page-container {
  max-width: 960px;
  margin: 0 auto;
  position: relative;
}

.page-header {
  text-align: center;
  margin-bottom: 2rem;
  color: #1a1a1a;
  position: relative;
  padding-top: 0.25rem;
}

.btn-home {
  position: absolute;
  left: 0;
  top: 0;
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

.page-header h1 {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 0.75rem;
  color: #2d3748;
}

.subtitle {
  font-size: 1rem;
  color: #4a5568;
  line-height: 1.55;
  max-width: 42rem;
  margin: 0 auto;
}

.subtitle code {
  font-size: 0.9em;
  background: rgba(0, 61, 122, 0.08);
  padding: 0.1rem 0.35rem;
  border-radius: 4px;
}

.panel {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border: 1px solid rgba(0, 61, 122, 0.12);
  border-radius: 14px;
  padding: 1.25rem 1.5rem;
  margin-bottom: 1.5rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.form-row {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  gap: 1rem;
}

.label {
  display: block;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 0.35rem;
  width: 100%;
  flex-basis: 100%;
}

@media (min-width: 600px) {
  .label {
    width: auto;
    flex-basis: auto;
    margin-bottom: 0;
    margin-right: 0.25rem;
  }
}

.date-input {
  padding: 0.55rem 0.75rem;
  border: 1px solid rgba(0, 61, 122, 0.25);
  border-radius: 8px;
  font-size: 1rem;
  color: #1a1a1a;
  background: #fff;
}

.btn-search {
  padding: 0.55rem 1.35rem;
  background: linear-gradient(135deg, #003d7a 0%, #5a7ba8 100%);
  color: #fff;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.btn-search:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 61, 122, 0.35);
}

.btn-search:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.btn-preset {
  padding: 0.55rem 1rem;
  background: #fff;
  color: #003d7a;
  border: 1.5px solid rgba(0, 61, 122, 0.35);
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.15s ease, border-color 0.15s ease;
}

.btn-preset:hover:not(:disabled) {
  background: rgba(0, 61, 122, 0.06);
  border-color: #003d7a;
}

.btn-preset:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.process-grau-strip {
  margin: 0 0 0.65rem 0;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #eef2f7;
}

.grau-meta-line {
  font-size: 0.82rem;
  color: #4a5568;
  margin: 0 0 0.35rem 0;
  line-height: 1.4;
}

.grau-meta-line:last-child {
  margin-bottom: 0;
}

.grau-strip-label {
  font-weight: 600;
  color: #2c5282;
  margin-right: 0.35rem;
}

.grau-strip-detail {
  font-weight: 400;
}

.btn-spinner {
  width: 1rem;
  height: 1rem;
  border: 2px solid rgba(255, 255, 255, 0.35);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.error-msg {
  margin-top: 1rem;
  color: #b83232;
  font-size: 0.95rem;
}

.summary {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem 1.25rem;
  margin-bottom: 1.5rem;
  font-size: 0.95rem;
  color: #2d3748;
}

.interval-hint {
  flex-basis: 100%;
  font-size: 0.85rem;
  color: #4a5568;
}

.results {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.process-block {
  background: #fff;
  border: 1px solid rgba(0, 61, 122, 0.12);
  border-radius: 12px;
  padding: 1rem 1.25rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.process-title {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.5rem 0.75rem;
  font-size: 1.1rem;
  font-weight: 600;
  margin: 0 0 0.75rem 0;
  color: #1a202c;
}

.npu {
  font-family: ui-monospace, monospace;
  font-size: 0.95rem;
}

.btn-open-proc {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 1.6rem;
  height: 1.6rem;
  border-radius: 6px;
  border: 1px solid rgba(0, 61, 122, 0.3);
  background: rgba(0, 61, 122, 0.06);
  color: #003d7a;
  font-size: 0.9rem;
  text-decoration: none;
  line-height: 1;
  transition: background 0.15s ease, transform 0.1s ease;
}

.btn-open-proc:hover {
  background: rgba(0, 61, 122, 0.15);
  transform: translateY(-1px);
}

.mov-count {
  font-size: 0.75rem;
  font-weight: 500;
  color: #4a5568;
  background: rgba(0, 61, 122, 0.06);
  padding: 0.15rem 0.45rem;
  border-radius: 6px;
}

.empty-results {
  margin: 0;
  font-size: 0.95rem;
  color: #4a5568;
  font-style: italic;
}

.badge {
  font-size: 0.75rem;
  font-weight: 600;
  padding: 0.2rem 0.5rem;
  border-radius: 6px;
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.badge-encontrado {
  background: rgba(34, 139, 34, 0.15);
  color: #1e6b1e;
}

.badge-nao_encontrado {
  background: rgba(108, 117, 125, 0.2);
  color: #495057;
}

.badge-erro {
  background: rgba(184, 50, 50, 0.15);
  color: #9b2c2c;
}

.erro-linha {
  font-size: 0.9rem;
  color: #9b2c2c;
  margin: 0 0 0.5rem 0;
}

.mov-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.mov-list li {
  display: grid;
  grid-template-columns: minmax(7rem, 9rem) auto minmax(5rem, 7.5rem) 1fr auto auto;
  gap: 0.4rem 0.6rem;
  padding: 0.45rem 0;
  border-bottom: 1px solid #eef2f7;
  font-size: 0.92rem;
  align-items: start;
}

.mov-list li:last-child {
  border-bottom: none;
}

.mv-data {
  color: #003d7a;
  font-weight: 500;
  white-space: nowrap;
}

.mv-grau {
  font-size: 0.78rem;
  font-weight: 600;
  color: #4a5568;
  white-space: nowrap;
  align-self: center;
}

.mv-desc {
  color: #2d3748;
  line-height: 1.45;
}

.mv-cod {
  font-size: 0.8rem;
  color: #718096;
  white-space: nowrap;
}

.btn-copy {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 1.5rem;
  height: 1.5rem;
  padding: 0;
  border-radius: 5px;
  border: 1px solid rgba(0, 61, 122, 0.18);
  background: rgba(0, 61, 122, 0.04);
  color: #003d7a;
  cursor: pointer;
  font-size: 0.85rem;
  line-height: 1;
  transition: background 0.15s ease, color 0.15s ease, border-color 0.15s ease;
}

.btn-copy:hover {
  background: rgba(0, 61, 122, 0.12);
}

.btn-copy.is-copied {
  background: rgba(34, 139, 34, 0.15);
  border-color: rgba(34, 139, 34, 0.45);
  color: #1e6b1e;
}

.empty-movs {
  margin: 0;
  font-size: 0.9rem;
  color: #718096;
  font-style: italic;
}

@media (max-width: 560px) {
  .mov-list li {
    grid-template-columns: 1fr auto;
    grid-template-areas:
      'data       copyData'
      'grau       grau'
      'descricao  copyDesc'
      'codigo     codigo';
  }

  .mv-data {
    grid-area: data;
    white-space: normal;
  }

  .mv-grau {
    grid-area: grau;
    white-space: normal;
  }

  .mv-desc {
    grid-area: descricao;
  }

  .mv-cod {
    grid-area: codigo;
  }

  .mov-list li > .btn-copy:nth-of-type(1) {
    grid-area: copyData;
  }

  .mov-list li > .btn-copy:nth-of-type(2) {
    grid-area: copyDesc;
  }
}
</style>
