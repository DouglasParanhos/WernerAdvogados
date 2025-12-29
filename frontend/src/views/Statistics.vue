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
        <button @click="loadStatistics" class="btn btn-secondary" :disabled="loading">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" width="16" height="16">
            <path d="M23 4v6h-6M1 20v-6h6M3.51 9a9 9 0 0 1 14.85-3.48L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          Atualizar
        </button>
      </div>

      <div v-if="loading" class="loading">Carregando estatísticas...</div>
      <div v-if="error" class="error">{{ error }}</div>

      <div v-if="!loading && !error && statistics" class="statistics-content">
        <!-- Cards de totais -->
        <div class="stats-cards">
          <div class="stat-card">
            <div class="stat-card-icon processes">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <polyline points="14 2 14 8 20 8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <div class="stat-card-content">
              <h3>Total de Processos</h3>
              <p class="stat-value">{{ statistics.totalProcesses || 0 }}</p>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-card-icon tasks">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <polyline points="22 4 12 14.01 9 11.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <div class="stat-card-content">
              <h3>Total de Tarefas</h3>
              <p class="stat-value">{{ statistics.totalTasks || 0 }}</p>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-card-icon clients">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="9" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M23 21v-2a4 4 0 0 0-3-3.87" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M16 3.13a4 4 0 0 1 0 7.75" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <div class="stat-card-content">
              <h3>Total de Clientes</h3>
              <p class="stat-value">{{ statistics.totalClients || 0 }}</p>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-card-icon money">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <line x1="12" y1="1" x2="12" y2="23" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <div class="stat-card-content">
              <h3>Honorários Esperados</h3>
              <p class="stat-value">{{ formatCurrency(statistics.totalHonorariosEsperados || 0) }}</p>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-card-icon value">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect x="1" y="4" width="22" height="16" rx="2" ry="2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <line x1="1" y1="10" x2="23" y2="10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <div class="stat-card-content">
              <h3>Valor Total dos Processos</h3>
              <p class="stat-value">{{ formatCurrency(statistics.totalValorProcessos || 0) }}</p>
            </div>
          </div>
        </div>

        <!-- Gráficos e tabelas -->
        <div class="charts-grid">
          <!-- Processos por Tipo -->
          <div class="chart-card">
            <h2>Processos por Tipo</h2>
            <div class="chart-container">
              <div v-if="statistics.processesByType && statistics.processesByType.length > 0" class="bar-chart">
                <div 
                  v-for="item in statistics.processesByType" 
                  :key="item.type"
                  class="bar-item"
                >
                  <div class="bar-label">{{ item.type }}</div>
                  <div class="bar-wrapper">
                    <div 
                      class="bar" 
                      :style="{ width: getPercentage(item.count, statistics.totalProcesses) + '%' }"
                    ></div>
                    <span class="bar-value">{{ item.count }}</span>
                  </div>
                </div>
              </div>
              <div v-else class="no-data">Nenhum dado disponível</div>
            </div>
          </div>

          <!-- Processos por Comarca -->
          <div class="chart-card">
            <h2>Processos por Comarca</h2>
            <div class="chart-container">
              <div v-if="statistics.processesByComarca && statistics.processesByComarca.length > 0" class="bar-chart">
                <div 
                  v-for="item in statistics.processesByComarca" 
                  :key="item.type"
                  class="bar-item"
                >
                  <div class="bar-label">{{ item.type }}</div>
                  <div class="bar-wrapper">
                    <div 
                      class="bar" 
                      :style="{ width: getPercentage(item.count, statistics.totalProcesses) + '%' }"
                    ></div>
                    <span class="bar-value">{{ item.count }}</span>
                  </div>
                </div>
              </div>
              <div v-else class="no-data">Nenhum dado disponível</div>
            </div>
          </div>

          <!-- Processos por Vara -->
          <div class="chart-card">
            <h2>Processos por Vara</h2>
            <div class="chart-container">
              <div v-if="statistics.processesByVara && statistics.processesByVara.length > 0" class="bar-chart">
                <div 
                  v-for="item in statistics.processesByVara" 
                  :key="item.type"
                  class="bar-item"
                >
                  <div class="bar-label">{{ item.type }}</div>
                  <div class="bar-wrapper">
                    <div 
                      class="bar" 
                      :style="{ width: getPercentage(item.count, statistics.totalProcesses) + '%' }"
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
            <h2>Processos por Status</h2>
            <div class="chart-container">
              <div v-if="statistics.processesByStatus && statistics.processesByStatus.length > 0" class="bar-chart">
                <div 
                  v-for="item in statistics.processesByStatus" 
                  :key="item.type"
                  class="bar-item"
                >
                  <div class="bar-label">{{ item.type }}</div>
                  <div class="bar-wrapper">
                    <div 
                      class="bar" 
                      :style="{ width: getPercentage(item.count, statistics.totalProcesses) + '%' }"
                    ></div>
                    <span class="bar-value">{{ item.count }}</span>
                  </div>
                </div>
              </div>
              <div v-else class="no-data">Nenhum dado disponível</div>
            </div>
          </div>

          <!-- Honorários por Tipo -->
          <div class="chart-card">
            <h2>Honorários Esperados por Tipo de Processo</h2>
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

          <!-- Tarefas por Status -->
          <div class="chart-card">
            <h2>Tarefas por Status</h2>
            <div class="chart-container">
              <div v-if="statistics.tasksByStatus && statistics.tasksByStatus.length > 0" class="bar-chart">
                <div 
                  v-for="item in statistics.tasksByStatus" 
                  :key="item.type"
                  class="bar-item"
                >
                  <div class="bar-label">{{ item.type }}</div>
                  <div class="bar-wrapper">
                    <div 
                      class="bar" 
                      :style="{ width: getPercentage(item.count, statistics.totalTasks) + '%' }"
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
            <h2>Tarefas por Tipo</h2>
            <div class="chart-container">
              <div v-if="statistics.tasksByType && statistics.tasksByType.length > 0" class="bar-chart">
                <div 
                  v-for="item in statistics.tasksByType" 
                  :key="item.type"
                  class="bar-item"
                >
                  <div class="bar-label">{{ item.type }}</div>
                  <div class="bar-wrapper">
                    <div 
                      class="bar" 
                      :style="{ width: getPercentage(item.count, statistics.totalTasks) + '%' }"
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
            <h2>Tarefas por Responsável</h2>
            <div class="chart-container">
              <div v-if="statistics.tasksByResponsavel && statistics.tasksByResponsavel.length > 0" class="bar-chart">
                <div 
                  v-for="item in statistics.tasksByResponsavel" 
                  :key="item.type"
                  class="bar-item"
                >
                  <div class="bar-label">{{ item.type }}</div>
                  <div class="bar-wrapper">
                    <div 
                      class="bar" 
                      :style="{ width: getPercentage(item.count, statistics.totalTasks) + '%' }"
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
    }
  }
}
</script>

<style scoped>
.statistics {
  min-height: 100vh;
  background: linear-gradient(135deg, #d0d8e0 0%, #e8eef5 50%, #c0c8d0 100%);
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
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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
  color: #003d7a;
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
  font-size: 2rem;
  color: #1a1a1a;
  margin: 0;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  transition: all 0.2s;
  font-weight: 500;
}

.btn-secondary {
  background: #5a7ba8;
  color: white;
}

.btn-secondary:hover:not(:disabled) {
  background: #4a6b98;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.loading, .error {
  text-align: center;
  padding: 2rem;
  font-size: 1.2rem;
}

.error {
  color: #d32f2f;
  background: #ffebee;
  border-radius: 8px;
  margin: 1rem 0;
}

.statistics-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 1.5rem;
  transition: all 0.3s;
  min-width: 0; /* Permite que o card encolha */
  overflow: hidden; /* Previne overflow */
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.stat-card-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-card-icon svg {
  width: 32px;
  height: 32px;
  color: white;
}

.stat-card-icon.processes {
  background: linear-gradient(135deg, #003d7a, #5a7ba8);
}

.stat-card-icon.tasks {
  background: linear-gradient(135deg, #2e7d32, #66bb6a);
}

.stat-card-icon.clients {
  background: linear-gradient(135deg, #7b1fa2, #ba68c8);
}

.stat-card-icon.money {
  background: linear-gradient(135deg, #f57c00, #ffb74d);
}

.stat-card-icon.value {
  background: linear-gradient(135deg, #1976d2, #64b5f6);
}

.stat-card-content {
  flex: 1;
  min-width: 0; /* Permite que o conteúdo encolha */
}

.stat-card-content h3 {
  margin: 0 0 0.5rem 0;
  font-size: 0.9rem;
  color: #666;
  font-weight: 500;
}

.stat-value {
  margin: 0;
  font-size: clamp(1.2rem, 2vw, 2rem);
  font-weight: 700;
  color: #1a1a1a;
  word-break: break-word;
  overflow-wrap: break-word;
  line-height: 1.2;
  max-width: 100%;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 2rem;
}

.chart-card {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chart-card h2 {
  margin: 0 0 1.5rem 0;
  font-size: 1.5rem;
  color: #1a1a1a;
  border-bottom: 2px solid #e0e0e0;
  padding-bottom: 0.75rem;
}

.chart-container {
  min-height: 200px;
}

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
  color: #666;
  font-weight: 500;
}

.bar-wrapper {
  display: flex;
  align-items: center;
  gap: 1rem;
  position: relative;
}

.bar {
  height: 32px;
  background: linear-gradient(90deg, #003d7a, #5a7ba8);
  border-radius: 4px;
  transition: width 0.5s ease;
  min-width: 4px;
}

.bar-value {
  font-weight: 600;
  color: #1a1a1a;
  min-width: 40px;
  text-align: right;
}

.table-chart {
  overflow-x: auto;
}

.stats-table {
  width: 100%;
  border-collapse: collapse;
}

.stats-table th {
  background: #f5f5f5;
  padding: 1rem;
  text-align: left;
  font-weight: 600;
  color: #1a1a1a;
  border-bottom: 2px solid #e0e0e0;
}

.stats-table td {
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #e0e0e0;
  color: #666;
}

.stats-table tr:hover {
  background: #f9f9f9;
}

.total-cell {
  font-weight: 600;
  color: #003d7a;
}

.no-data {
  text-align: center;
  padding: 3rem;
  color: #999;
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

  .stats-cards {
    grid-template-columns: 1fr;
  }

  .stat-value {
    font-size: 1.5rem;
  }

  .charts-grid {
    grid-template-columns: 1fr;
  }

  .chart-card {
    padding: 1.5rem;
  }

  .stats-table {
    font-size: 0.9rem;
  }

  .stats-table th,
  .stats-table td {
    padding: 0.5rem;
  }
}
</style>

