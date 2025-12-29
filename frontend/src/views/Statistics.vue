<template>
  <div class="statistics">
    <div class="container">
      <div class="header">
        <div class="header-left">
          <button @click="goToHome" class="btn-home" title="Voltar para Home">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="9 22 9 12 15 12 15 22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
          <h1>Estatísticas</h1>
        </div>
        <button @click="loadStatistics" class="btn btn-secondary btn-icon-only" :disabled="loading" title="Atualizar">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" width="20" height="20">
            <path d="M23 4v6h-6M1 20v-6h6M3.51 9a9 9 0 0 1 14.85-3.48L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
      </div>

      <div v-if="loading" class="loading">Carregando estatísticas...</div>
      <div v-if="error" class="error">{{ error }}</div>

      <div v-if="!loading && !error && statistics" class="statistics-content">
        <!-- Resumo em uma linha -->
        <div class="summary-row">
          <div class="summary-item">
            <div class="summary-icon processes-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <polyline points="14 2 14 8 20 8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <span class="summary-label">Processos</span>
            <span class="summary-value">{{ statistics.totalProcesses || 0 }}</span>
          </div>
          <div class="summary-item">
            <div class="summary-icon tasks-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <polyline points="22 4 12 14.01 9 11.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <span class="summary-label">Tarefas</span>
            <span class="summary-value">{{ statistics.totalTasks || 0 }}</span>
          </div>
          <div class="summary-item">
            <div class="summary-icon clients-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="9" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M23 21v-2a4 4 0 0 0-3-3.87" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M16 3.13a4 4 0 0 1 0 7.75" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <span class="summary-label">Clientes</span>
            <span class="summary-value">{{ statistics.totalClients || 0 }}</span>
          </div>
          <div class="summary-item">
            <div class="summary-icon money-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <line x1="12" y1="1" x2="12" y2="23" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <span class="summary-label">Honorários Esperados</span>
            <span class="summary-value">{{ formatCurrency(statistics.totalHonorariosEsperados || 0) }}</span>
          </div>
          <div class="summary-item">
            <div class="summary-icon value-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect x="1" y="4" width="22" height="16" rx="2" ry="2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <line x1="1" y1="10" x2="23" y2="10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <span class="summary-label">Valor Total dos Processos</span>
            <span class="summary-value">{{ formatCurrency(statistics.totalValorProcessos || 0) }}</span>
          </div>
        </div>

        <!-- Bloco: Processos -->
        <div class="statistics-block">
          <h2 class="block-title">Processos</h2>
          <div class="charts-grid">
            <!-- Processos por Tipo - Gráfico Pizza -->
            <div class="chart-card">
              <h3>Processos por Tipo</h3>
              <div class="chart-container">
                <div v-if="statistics.processesByType && statistics.processesByType.length > 0" class="pie-chart-wrapper">
                  <svg class="pie-chart" viewBox="0 0 200 200">
                    <circle
                      cx="100"
                      cy="100"
                      r="80"
                      fill="none"
                      stroke="#e0e0e0"
                      stroke-width="40"
                    />
                    <g v-for="(item, index) in processesByTypeWithPercentages" :key="item.type">
                      <circle
                        :cx="100"
                        :cy="100"
                        :r="80"
                        fill="none"
                        :stroke="getPieColor(index)"
                        :stroke-width="40"
                        :stroke-dasharray="getPieDashArray(item.percentage)"
                        :stroke-dashoffset="getPieOffset(index)"
                        transform="rotate(-90 100 100)"
                        class="pie-segment"
                      />
                    </g>
                  </svg>
                  <div class="pie-legend">
                    <div 
                      v-for="(item, index) in statistics.processesByType" 
                      :key="item.type"
                      class="legend-item"
                    >
                      <span class="legend-color" :style="{ backgroundColor: getPieColor(index) }"></span>
                      <span class="legend-label">{{ item.type }}</span>
                      <span class="legend-value">{{ item.count }} ({{ getPercentage(item.count, statistics.totalProcesses).toFixed(1) }}%)</span>
                    </div>
                  </div>
                </div>
                <div v-else class="no-data">Nenhum dado disponível</div>
              </div>
            </div>

            <!-- Processos por Comarca -->
            <div class="chart-card">
              <h3>Processos por Comarca</h3>
              <div class="chart-container">
                <div v-if="statistics.processesByComarca && statistics.processesByComarca.length > 0" class="bar-chart">
                  <div 
                    v-for="(item, index) in statistics.processesByComarca" 
                    :key="item.type"
                    class="bar-item"
                  >
                    <div class="bar-label">{{ item.type }}</div>
                    <div class="bar-wrapper">
                      <div 
                        class="bar" 
                        :style="{ width: getPercentage(item.count, statistics.totalProcesses) + '%', backgroundColor: getBarColor(index) }"
                      ></div>
                      <span class="bar-value">{{ item.count }}</span>
                    </div>
                  </div>
                </div>
                <div v-else class="no-data">Nenhum dado disponível</div>
              </div>
            </div>

            <!-- Processos por Status -->
            <div class="chart-card">
              <h3>Processos por Status</h3>
              <div class="chart-container">
                <div v-if="statistics.processesByStatus && statistics.processesByStatus.length > 0" class="bar-chart">
                  <div 
                    v-for="(item, index) in statistics.processesByStatus" 
                    :key="item.type"
                    class="bar-item"
                  >
                    <div class="bar-label">{{ item.type }}</div>
                    <div class="bar-wrapper">
                      <div 
                        class="bar" 
                        :style="{ width: getPercentage(item.count, statistics.totalProcesses) + '%', backgroundColor: getBarColor(index) }"
                      ></div>
                      <span class="bar-value">{{ item.count }}</span>
                    </div>
                  </div>
                </div>
                <div v-else class="no-data">Nenhum dado disponível</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Bloco: Tarefas -->
        <div class="statistics-block">
          <h2 class="block-title">Tarefas</h2>
          <div class="charts-grid">
            <!-- Tarefas por Status -->
            <div class="chart-card">
              <h3>Tarefas por Status</h3>
              <div class="chart-container">
                <div v-if="statistics.tasksByStatus && statistics.tasksByStatus.length > 0" class="bar-chart">
                  <div 
                    v-for="(item, index) in statistics.tasksByStatus" 
                    :key="item.type"
                    class="bar-item"
                  >
                    <div class="bar-label">{{ item.type }}</div>
                    <div class="bar-wrapper">
                      <div 
                        class="bar" 
                        :style="{ width: getPercentage(item.count, statistics.totalTasks) + '%', backgroundColor: getBarColor(index) }"
                      ></div>
                      <span class="bar-value">{{ item.count }}</span>
                    </div>
                  </div>
                </div>
                <div v-else class="no-data">Nenhum dado disponível</div>
              </div>
            </div>

            <!-- Tarefas por Tipo -->
            <div class="chart-card">
              <h3>Tarefas por Tipo</h3>
              <div class="chart-container">
                <div v-if="statistics.tasksByType && statistics.tasksByType.length > 0" class="bar-chart">
                  <div 
                    v-for="(item, index) in statistics.tasksByType" 
                    :key="item.type"
                    class="bar-item"
                  >
                    <div class="bar-label">{{ item.type }}</div>
                    <div class="bar-wrapper">
                      <div 
                        class="bar" 
                        :style="{ width: getPercentage(item.count, statistics.totalTasks) + '%', backgroundColor: getBarColor(index) }"
                      ></div>
                      <span class="bar-value">{{ item.count }}</span>
                    </div>
                  </div>
                </div>
                <div v-else class="no-data">Nenhum dado disponível</div>
              </div>
            </div>

            <!-- Tarefas por Responsável -->
            <div class="chart-card">
              <h3>Tarefas por Responsável</h3>
              <div class="chart-container">
                <div v-if="statistics.tasksByResponsavel && statistics.tasksByResponsavel.length > 0" class="bar-chart">
                  <div 
                    v-for="(item, index) in statistics.tasksByResponsavel" 
                    :key="item.type"
                    class="bar-item"
                  >
                    <div class="bar-label">{{ item.type }}</div>
                    <div class="bar-wrapper">
                      <div 
                        class="bar" 
                        :style="{ width: getPercentage(item.count, statistics.totalTasks) + '%', backgroundColor: getBarColor(index) }"
                      ></div>
                      <span class="bar-value">{{ item.count }}</span>
                    </div>
                  </div>
                </div>
                <div v-else class="no-data">Nenhum dado disponível</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Bloco: Honorários -->
        <div class="statistics-block">
          <h2 class="block-title">Honorários</h2>
          <div class="charts-grid">
            <!-- Honorários por Tipo -->
            <div class="chart-card chart-card-full">
              <h3>Honorários Esperados por Tipo de Processo</h3>
              <div class="chart-container">
                <div v-if="statistics.honorariosByType && statistics.honorariosByType.length > 0" class="table-chart">
                  <table class="stats-table">
                    <thead>
                      <tr>
                        <th>Tipo de Processo</th>
                        <th>Quantidade</th>
                        <th>Honorários Contratuais</th>
                        <th>Honorários Sucumbenciais</th>
                        <th>Total</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="item in statistics.honorariosByType" :key="item.tipoProcesso">
                        <td>{{ item.tipoProcesso }}</td>
                        <td>{{ item.quantidadeProcessos }}</td>
                        <td>{{ formatCurrency(item.totalHonorariosContratuais || 0) }}</td>
                        <td>{{ formatCurrency(item.totalHonorariosSucumbenciais || 0) }}</td>
                        <td class="total-cell">{{ formatCurrency(item.totalHonorarios || 0) }}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                <div v-else class="no-data">Nenhum dado disponível</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { statisticsService } from '../services/statisticsService'

export default {
  name: 'Statistics',
  data() {
    return {
      statistics: null,
      loading: false,
      error: null
    }
  },
  computed: {
    processesByTypeWithPercentages() {
      if (!this.statistics || !this.statistics.processesByType || !this.statistics.totalProcesses) {
        return []
      }
      
      let cumulativePercentage = 0
      return this.statistics.processesByType.map(item => {
        const percentage = (item.count / this.statistics.totalProcesses) * 100
        const result = {
          type: item.type,
          count: item.count,
          percentage: percentage,
          cumulativePercentage: cumulativePercentage
        }
        cumulativePercentage += percentage
        return result
      })
    }
  },
  async mounted() {
    await this.loadStatistics()
  },
  methods: {
    async loadStatistics() {
      this.loading = true
      this.error = null
      try {
        this.statistics = await statisticsService.getStatistics()
      } catch (err) {
        this.error = 'Erro ao carregar estatísticas: ' + (err.response?.data?.message || err.message)
        console.error(err)
      } finally {
        this.loading = false
      }
    },
    goToHome() {
      this.$router.push('/')
    },
    formatCurrency(value) {
      if (!value && value !== 0) return 'R$ 0,00'
      return new Intl.NumberFormat('pt-BR', { 
        style: 'currency', 
        currency: 'BRL' 
      }).format(value)
    },
    getPercentage(value, total) {
      if (!total || total === 0) return 0
      return (value / total) * 100
    },
    getPieColor(index) {
      const colors = ['#5a6b7a', '#6b7d8f', '#7a8b9a']
      return colors[index % colors.length]
    },
    getPieDashArray(percentage) {
      const circumference = 2 * Math.PI * 80 // r = 80
      const dashLength = (percentage * circumference) / 100
      return `${dashLength} ${circumference}`
    },
    getPieOffset(index) {
      if (index === 0) return 0
      const circumference = 2 * Math.PI * 80 // r = 80
      let offset = 0
      for (let i = 0; i < index; i++) {
        offset += (this.processesByTypeWithPercentages[i].percentage * circumference) / 100
      }
      return -offset
    },
    getBarColor(index) {
      const colors = [
        '#5a6b7a', '#6b7d8f', '#7a8b9a', '#8a9aab', '#9aabbc',
        '#4a5a6a', '#5a6a7a', '#6a7a8a', '#7a8a9a', '#8a9aaa',
        '#3a4a5a', '#4a5a6a', '#5a6a7a', '#6a7a8a', '#7a8a9a'
      ]
      return colors[index % colors.length]
    }
  }
}
</script>

<style scoped>
.statistics {
  min-height: 100vh;
  background: linear-gradient(135deg, #e8eef5 0%, #d8e0e8 50%, #c8d0d8 100%);
  padding: 2rem;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  background: white;
  padding: 1.5rem 2rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.btn-home {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #4a5a6a;
  transition: all 0.2s;
  border-radius: 8px;
}

.btn-home:hover {
  background: #f0f4f8;
  transform: scale(1.1);
}

.btn-home svg {
  width: 24px;
  height: 24px;
}

h1 {
  font-size: 1.75rem;
  color: #2a3a4a;
  margin: 0;
  font-weight: 600;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-size: 0.95rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  transition: all 0.2s;
  font-weight: 500;
}

.btn-secondary {
  background: #5a6b7a;
  color: white;
}

.btn-icon-only {
  padding: 0.75rem;
  width: 40px;
  height: 40px;
  justify-content: center;
}

.btn-icon-only svg {
  width: 20px;
  height: 20px;
}

.btn-secondary:hover:not(:disabled) {
  background: #4a5a6a;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.loading, .error {
  text-align: center;
  padding: 2rem;
  font-size: 1.1rem;
  color: #4a5a6a;
}

.error {
  color: #c62828;
  background: #ffebee;
  border-radius: 8px;
  margin: 1rem 0;
}

.statistics-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

/* Resumo em uma linha */
.summary-row {
  display: flex;
  gap: 1rem;
  background: white;
  padding: 1.25rem 1.5rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  flex-wrap: wrap;
  justify-content: space-around;
}

.summary-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  min-width: 120px;
}

.summary-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.summary-icon svg {
  width: 24px;
  height: 24px;
}

.summary-icon.processes-icon {
  background: linear-gradient(135deg, #5a6b7a, #6b7d8f);
  color: white;
}

.summary-icon.tasks-icon {
  background: linear-gradient(135deg, #6b7d8f, #7a8b9a);
  color: white;
}

.summary-icon.clients-icon {
  background: linear-gradient(135deg, #7a8b9a, #8a9aab);
  color: white;
}

.summary-icon.money-icon {
  background: linear-gradient(135deg, #8a9aab, #9aabbc);
  color: white;
}

.summary-icon.value-icon {
  background: linear-gradient(135deg, #4a5a6a, #5a6b7a);
  color: white;
}

.summary-label {
  font-size: 0.85rem;
  color: #6b7d8f;
  font-weight: 500;
  text-align: center;
}

.summary-value {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2a3a4a;
  white-space: nowrap;
}

/* Blocos de estatísticas */
.statistics-block {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.block-title {
  font-size: 1.5rem;
  color: #2a3a4a;
  margin: 0 0 1.5rem 0;
  font-weight: 600;
  border-bottom: 2px solid #e0e8f0;
  padding-bottom: 0.75rem;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 2rem;
}

.chart-card {
  background: #f8fafc;
  border-radius: 10px;
  padding: 1.5rem;
  border: 1px solid #e0e8f0;
}

.chart-card-full {
  grid-column: 1 / -1;
}

.chart-card h3 {
  margin: 0 0 1.25rem 0;
  font-size: 1.15rem;
  color: #3a4a5a;
  font-weight: 600;
}

.chart-container {
  min-height: 200px;
}

/* Gráfico Pizza */
.pie-chart-wrapper {
  display: flex;
  gap: 2rem;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
}

.pie-chart {
  width: 200px;
  height: 200px;
  flex-shrink: 0;
}

.pie-segment {
  transition: all 0.3s ease;
}

.pie-legend {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  flex: 1;
  min-width: 200px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 0.9rem;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 4px;
  flex-shrink: 0;
}

.legend-label {
  flex: 1;
  color: #4a5a6a;
  font-weight: 500;
}

.legend-value {
  color: #6b7d8f;
  font-weight: 600;
}

/* Gráfico de Barras */
.bar-chart {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.bar-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.bar-label {
  font-size: 0.9rem;
  color: #5a6b7a;
  font-weight: 500;
}

.bar-wrapper {
  display: flex;
  align-items: center;
  gap: 1rem;
  position: relative;
}

.bar {
  height: 28px;
  border-radius: 4px;
  transition: width 0.5s ease;
  min-width: 4px;
}

.bar-value {
  font-weight: 600;
  color: #3a4a5a;
  min-width: 40px;
  text-align: right;
  font-size: 0.9rem;
}

/* Tabela */
.table-chart {
  overflow-x: auto;
}

.stats-table {
  width: 100%;
  border-collapse: collapse;
}

.stats-table th {
  background: #e8f0f8;
  padding: 1rem;
  text-align: left;
  font-weight: 600;
  color: #3a4a5a;
  border-bottom: 2px solid #d0d8e0;
  font-size: 0.9rem;
}

.stats-table td {
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #e0e8f0;
  color: #5a6b7a;
  font-size: 0.9rem;
}

.stats-table tr:hover {
  background: #f0f4f8;
}

.total-cell {
  font-weight: 600;
  color: #4a5a6a;
}

.no-data {
  text-align: center;
  padding: 3rem;
  color: #8a9aab;
  font-style: italic;
}

@media (max-width: 768px) {
  .statistics {
    padding: 1rem;
  }

  .header {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }

  .summary-row {
    flex-direction: column;
    gap: 1rem;
  }

  .summary-item {
    flex-direction: row;
    justify-content: space-between;
    width: 100%;
  }

  .charts-grid {
    grid-template-columns: 1fr;
  }

  .chart-card {
    padding: 1.25rem;
  }

  .pie-chart-wrapper {
    flex-direction: column;
  }

  .stats-table {
    font-size: 0.85rem;
  }

  .stats-table th,
  .stats-table td {
    padding: 0.5rem;
  }
}
</style>
