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
          <button @click="goBack" class="btn btn-secondary">← Voltar</button>
        </div>
        <h1>Estatísticas</h1>
        <div @click="!loading && !isProcessing && updateAllData()" class="refresh-icon" :class="{ 'disabled': loading || isProcessing }" title="Atualizar dados">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" :class="{ 'rotating': loading || isProcessing }">
            <path d="M21.5 2v6h-6M2.5 22v-6h6M2 11.5a10 10 0 0 1 18.8-4.3M22 12.5a10 10 0 0 1-18.8 4.2"/>
          </svg>
        </div>
      </div>
      
      <div v-if="loading" class="loading">Carregando estatísticas...</div>
      <div v-if="error" class="error">{{ error }}</div>
      
      <div v-if="!loading && !error && statistics" class="content">
        <!-- Cards de Estatísticas Gerais -->
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-label">Clientes</div>
            <div class="stat-value">{{ statistics.totalClients || 0 }}</div>
          </div>
          
          <div class="stat-card">
            <div class="stat-label">Processos</div>
            <div class="stat-value">{{ statistics.totalProcesses || 0 }}</div>
          </div>
          
          <div class="stat-card">
            <div class="stat-label">Matrículas</div>
            <div class="stat-value">{{ statistics.totalMatriculations || 0 }}</div>
          </div>
          
          <div class="stat-card">
            <div class="stat-label">Movimentações</div>
            <div class="stat-value">{{ statistics.totalMoviments || 0 }}</div>
          </div>
        </div>

        <!-- Cards Principais: Total das Ações, Total das Ações Corrigido e Total de Honorários Esperados -->
        <div class="main-stats-grid">
          <div class="main-stat-card">
            <div class="main-stat-label">Total das Ações</div>
            <div class="main-stat-value">{{ formatCurrency(statistics.totalAcoes || 0) }}</div>
          </div>
          
          <div class="main-stat-card">
            <div class="main-stat-label">
              Total das Ações Corrigido
              <span v-if="isProcessing" class="processing-indicator" title="Atualizando valores corrigidos...">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="spinning-icon">
                  <path d="M21.5 2v6h-6M2.5 22v-6h6M2 11.5a10 10 0 0 1 18.8-4.3M22 12.5a10 10 0 0 1-18.8 4.2"/>
                </svg>
              </span>
            </div>
            <div class="main-stat-value">{{ formatCurrency(statistics.totalAcoesCorrigido || 0) }}</div>
          </div>
          
          <div class="main-stat-card">
            <div class="main-stat-label">Total de Honorários Esperados</div>
            <div class="main-stat-value">{{ formatCurrency(statistics.totalHonorariosEsperados || 0) }}</div>
          </div>
        </div>
        
        <!-- Processos por Tipo - Gráfico de Pizza -->
        <div v-if="statistics.processesByType && statistics.processesByType.length > 0" class="section">
          <h2 class="section-title">Processos por Tipo</h2>
          <div class="chart-wrapper-small">
            <Pie :data="processesByTypeChartData" :options="chartOptions" />
          </div>
        </div>
        
        <!-- Processos por Comarca - Gráficos Compactos -->
        <div v-if="statistics.processesByComarca && statistics.processesByComarca.length > 0" class="section">
          <h2 class="section-title">Processos por Comarca</h2>
          <div class="charts-grid">
            <!-- Gráfico PISO -->
            <div v-if="comarcaDataPISO.length > 0" class="chart-item">
              <h3 class="chart-subtitle">PISO</h3>
              <div class="chart-wrapper-compact">
                <Bar :data="comarcaPISOChartData" :options="barChartOptions" />
              </div>
            </div>
            
            <!-- Gráfico NOVAESCOLA -->
            <div v-if="comarcaDataNOVAESCOLA.length > 0" class="chart-item">
              <h3 class="chart-subtitle">NOVAESCOLA</h3>
              <div class="chart-wrapper-compact">
                <Bar :data="comarcaNOVAESCOLAChartData" :options="barChartOptions" />
              </div>
            </div>
            
            <!-- Gráfico INTERNIVEIS -->
            <div v-if="comarcaDataINTERNIVEIS.length > 0" class="chart-item">
              <h3 class="chart-subtitle">INTERNIVEIS</h3>
              <div class="chart-wrapper-compact">
                <Bar :data="comarcaINTERNIVEISChartData" :options="barChartOptions" />
              </div>
            </div>
          </div>
        </div>
        
        <!-- Processos por Status - Gráficos Compactos -->
        <div v-if="statistics.processesByStatus && statistics.processesByStatus.length > 0" class="section">
          <h2 class="section-title">Processos por Status</h2>
          <div class="charts-grid">
            <!-- Gráfico PISO -->
            <div v-if="statusDataPISO.length > 0" class="chart-item">
              <h3 class="chart-subtitle">PISO</h3>
              <div class="chart-wrapper-compact">
                <Bar :data="statusPISOChartData" :options="columnChartOptions" />
              </div>
            </div>
            
            <!-- Gráfico NOVAESCOLA -->
            <div v-if="statusDataNOVAESCOLA.length > 0" class="chart-item">
              <h3 class="chart-subtitle">NOVAESCOLA</h3>
              <div class="chart-wrapper-compact">
                <Bar :data="statusNOVAESCOLAChartData" :options="columnChartOptions" />
              </div>
            </div>
            
            <!-- Gráfico INTERNIVEIS -->
            <div v-if="statusDataINTERNIVEIS.length > 0" class="chart-item">
              <h3 class="chart-subtitle">INTERNIVEIS</h3>
              <div class="chart-wrapper-compact">
                <Bar :data="statusINTERNIVEISChartData" :options="columnChartOptions" />
              </div>
            </div>
          </div>
        </div>
        
        <!-- Honorários por Tipo -->
        <div v-if="statistics.honorariosByType && statistics.honorariosByType.length > 0" class="section">
          <h2 class="section-title">Honorários por Tipo de Processo</h2>
          <div class="honorarios-grid">
            <div v-for="item in statistics.honorariosByType" :key="item.tipoProcesso" class="honorarios-card">
              <div class="honorarios-header">
                <h3>{{ item.tipoProcesso || 'Não especificado' }}</h3>
              </div>
              <div class="honorarios-body">
                <div class="honorarios-stat">
                  <span class="honorarios-label">Quantidade:</span>
                  <span class="honorarios-value">{{ item.quantidadeProcessos }}</span>
                </div>
                <div class="honorarios-stat">
                  <span class="honorarios-label">Contratuais:</span>
                  <span class="honorarios-value currency">{{ formatCurrency(item.totalHonorariosContratuais) }}</span>
                </div>
                <div class="honorarios-stat">
                  <span class="honorarios-label">Sucumbenciais:</span>
                  <span class="honorarios-value currency">{{ formatCurrency(item.totalHonorariosSucumbenciais) }}</span>
                </div>
                <div class="honorarios-total">
                  <span class="honorarios-total-label">Total Honorários:</span>
                  <span class="honorarios-total-value">{{ formatCurrency(item.totalHonorarios) }}</span>
                </div>
                <div class="honorarios-stat total-acoes">
                  <span class="honorarios-label">Total das Ações:</span>
                  <span class="honorarios-value currency">{{ formatCurrency(item.totalAcoes || 0) }}</span>
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
import { statisticsService } from '../services/statisticsService'
import { Chart as ChartJS, ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement } from 'chart.js'
import { Pie, Bar } from 'vue-chartjs'

ChartJS.register(ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement)

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'bottom',
      labels: {
        padding: 10,
        font: {
          size: 11
        }
      }
    },
    tooltip: {
      callbacks: {
        label: function(context) {
          const label = context.label || ''
          const value = context.parsed || 0
          const total = context.dataset.data.reduce((a, b) => a + b, 0)
          const percentage = total > 0 ? ((value / total) * 100).toFixed(1) : 0
          return `${label}: ${value} (${percentage}%)`
        }
      }
    }
  }
}

const barChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  indexAxis: 'y',
  plugins: {
    legend: {
      display: false
    },
    tooltip: {
      callbacks: {
        label: function(context) {
          return `${context.parsed.x} processos`
        }
      }
    }
  },
  scales: {
    x: {
      beginAtZero: true,
      ticks: {
        stepSize: 1
      }
    }
  }
}

const columnChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  indexAxis: 'x',
  plugins: {
    legend: {
      display: false
    },
    tooltip: {
      callbacks: {
        label: function(context) {
          return `${context.parsed.y} processos`
        }
      }
    }
  },
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        stepSize: 1
      }
    }
  }
}

const compactChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'bottom',
      labels: {
        padding: 8,
        font: {
          size: 10
        }
      }
    },
    tooltip: {
      callbacks: {
        label: function(context) {
          const label = context.label || ''
          const value = context.parsed || 0
          const total = context.dataset.data.reduce((a, b) => a + b, 0)
          const percentage = total > 0 ? ((value / total) * 100).toFixed(1) : 0
          return `${label}: ${value} (${percentage}%)`
        }
      }
    }
  }
}

// Paleta de cores elegante e sóbria
const colorPalette = [
  '#4a5f7a', // Dark Muted Blue
  '#6b7a8a', // Medium Blue-Gray
  '#8a9aab', // Medium Desaturated Blue
  '#8a9a8a', // Medium Desaturated Green-Gray
  '#c0c0b0', // Light Greige/Taupe
  '#e8e8e0'  // Off-White/Very Light Gray
]

export default {
  name: 'Statistics',
  components: {
    Pie,
    Bar
  },
  async mounted() {
    await this.checkProcessStatus()
    await this.loadStatistics()
  },
  beforeUnmount() {
    this.disconnectStatusStream()
  },
  computed: {
    processesByTypeChartData() {
      if (!this.statistics?.processesByType || !Array.isArray(this.statistics.processesByType)) {
        return { labels: [], datasets: [] }
      }
      
      const labels = this.statistics.processesByType.map(item => item.type || 'Não especificado')
      const data = this.statistics.processesByType.map(item => item.count || 0)
      
      return {
        labels,
        datasets: [{
          data,
          backgroundColor: colorPalette.slice(0, data.length),
          borderColor: '#ffffff',
          borderWidth: 1
        }]
      }
    },
    comarcaDataPISO() {
      if (!this.statistics?.processesByComarca || !Array.isArray(this.statistics.processesByComarca)) {
        return []
      }
      const result = []
      this.statistics.processesByComarca.forEach(comarca => {
        if (comarca && comarca.byType && Array.isArray(comarca.byType)) {
          const tipoData = comarca.byType.find(t => {
            const tType = (t?.type || '').toUpperCase()
            return tType === 'PISO'
          })
          if (tipoData && tipoData.count > 0) {
            result.push({
              comarca: comarca.comarca || 'Não especificado',
              count: Number(tipoData.count) || 0
            })
          }
        }
      })
      return result.sort((a, b) => (b.count || 0) - (a.count || 0))
    },
    comarcaPISOChartData() {
      if (this.comarcaDataPISO.length === 0) {
        return { labels: [], datasets: [] }
      }
      return {
        labels: this.comarcaDataPISO.map(item => item.comarca),
        datasets: [{
          label: 'Processos',
          data: this.comarcaDataPISO.map(item => item.count),
          backgroundColor: colorPalette[0],
          borderColor: colorPalette[0],
          borderWidth: 1
        }]
      }
    },
    comarcaDataNOVAESCOLA() {
      if (!this.statistics?.processesByComarca || !Array.isArray(this.statistics.processesByComarca)) {
        return []
      }
      const result = []
      this.statistics.processesByComarca.forEach(comarca => {
        if (comarca && comarca.byType && Array.isArray(comarca.byType)) {
          const tipoData = comarca.byType.find(t => {
            const tType = (t?.type || '').toUpperCase()
            return tType === 'NOVAESCOLA'
          })
          if (tipoData && tipoData.count > 0) {
            result.push({
              comarca: comarca.comarca || 'Não especificado',
              count: Number(tipoData.count) || 0
            })
          }
        }
      })
      return result.sort((a, b) => (b.count || 0) - (a.count || 0))
    },
    comarcaNOVAESCOLAChartData() {
      if (this.comarcaDataNOVAESCOLA.length === 0) {
        return { labels: [], datasets: [] }
      }
      return {
        labels: this.comarcaDataNOVAESCOLA.map(item => item.comarca),
        datasets: [{
          label: 'Processos',
          data: this.comarcaDataNOVAESCOLA.map(item => item.count),
          backgroundColor: colorPalette[1],
          borderColor: colorPalette[1],
          borderWidth: 1
        }]
      }
    },
    comarcaDataINTERNIVEIS() {
      if (!this.statistics?.processesByComarca || !Array.isArray(this.statistics.processesByComarca)) {
        return []
      }
      const result = []
      this.statistics.processesByComarca.forEach(comarca => {
        if (comarca && comarca.byType && Array.isArray(comarca.byType)) {
          const tipoData = comarca.byType.find(t => {
            const tType = (t?.type || '').toUpperCase()
            return tType === 'INTERNIVEIS'
          })
          if (tipoData && tipoData.count > 0) {
            result.push({
              comarca: comarca.comarca || 'Não especificado',
              count: Number(tipoData.count) || 0
            })
          }
        }
      })
      return result.sort((a, b) => (b.count || 0) - (a.count || 0))
    },
    comarcaINTERNIVEISChartData() {
      if (this.comarcaDataINTERNIVEIS.length === 0) {
        return { labels: [], datasets: [] }
      }
      return {
        labels: this.comarcaDataINTERNIVEIS.map(item => item.comarca),
        datasets: [{
          label: 'Processos',
          data: this.comarcaDataINTERNIVEIS.map(item => item.count),
          backgroundColor: colorPalette[2],
          borderColor: colorPalette[2],
          borderWidth: 1
        }]
      }
    },
    statusDataPISO() {
      if (!this.statistics?.processesByStatus || !Array.isArray(this.statistics.processesByStatus)) {
        return []
      }
      const result = []
      this.statistics.processesByStatus.forEach(status => {
        if (status && status.byType && Array.isArray(status.byType)) {
          const tipoData = status.byType.find(t => {
            const tType = (t?.type || '').toUpperCase()
            return tType === 'PISO'
          })
          if (tipoData && tipoData.count > 0) {
            result.push({
              status: status.status || 'Sem status',
              count: Number(tipoData.count) || 0
            })
          }
        }
      })
      return result.sort((a, b) => (b.count || 0) - (a.count || 0))
    },
    statusPISOChartData() {
      if (this.statusDataPISO.length === 0) {
        return { labels: [], datasets: [] }
      }
      const data = this.statusDataPISO.map(item => item.count)
      return {
        labels: this.statusDataPISO.map(item => item.status),
        datasets: [{
          label: 'Processos',
          data: data,
          backgroundColor: data.map((_, index) => colorPalette[index % colorPalette.length]),
          borderColor: data.map((_, index) => colorPalette[index % colorPalette.length]),
          borderWidth: 1
        }]
      }
    },
    statusDataNOVAESCOLA() {
      if (!this.statistics?.processesByStatus || !Array.isArray(this.statistics.processesByStatus)) {
        return []
      }
      const result = []
      this.statistics.processesByStatus.forEach(status => {
        if (status && status.byType && Array.isArray(status.byType)) {
          const tipoData = status.byType.find(t => {
            const tType = (t?.type || '').toUpperCase()
            return tType === 'NOVAESCOLA'
          })
          if (tipoData && tipoData.count > 0) {
            result.push({
              status: status.status || 'Sem status',
              count: Number(tipoData.count) || 0
            })
          }
        }
      })
      return result.sort((a, b) => (b.count || 0) - (a.count || 0))
    },
    statusNOVAESCOLAChartData() {
      if (this.statusDataNOVAESCOLA.length === 0) {
        return { labels: [], datasets: [] }
      }
      const data = this.statusDataNOVAESCOLA.map(item => item.count)
      return {
        labels: this.statusDataNOVAESCOLA.map(item => item.status),
        datasets: [{
          label: 'Processos',
          data: data,
          backgroundColor: data.map((_, index) => colorPalette[index % colorPalette.length]),
          borderColor: data.map((_, index) => colorPalette[index % colorPalette.length]),
          borderWidth: 1
        }]
      }
    },
    statusDataINTERNIVEIS() {
      if (!this.statistics?.processesByStatus || !Array.isArray(this.statistics.processesByStatus)) {
        return []
      }
      const result = []
      this.statistics.processesByStatus.forEach(status => {
        if (status && status.byType && Array.isArray(status.byType)) {
          const tipoData = status.byType.find(t => {
            const tType = (t?.type || '').toUpperCase()
            return tType === 'INTERNIVEIS'
          })
          if (tipoData && tipoData.count > 0) {
            result.push({
              status: status.status || 'Sem status',
              count: Number(tipoData.count) || 0
            })
          }
        }
      })
      return result.sort((a, b) => (b.count || 0) - (a.count || 0))
    },
    statusINTERNIVEISChartData() {
      if (this.statusDataINTERNIVEIS.length === 0) {
        return { labels: [], datasets: [] }
      }
      const data = this.statusDataINTERNIVEIS.map(item => item.count)
      return {
        labels: this.statusDataINTERNIVEIS.map(item => item.status),
        datasets: [{
          label: 'Processos',
          data: data,
          backgroundColor: data.map((_, index) => colorPalette[index % colorPalette.length]),
          borderColor: data.map((_, index) => colorPalette[index % colorPalette.length]),
          borderWidth: 1
        }]
      }
    },
    chartOptions() {
      return chartOptions
    },
    barChartOptions() {
      return barChartOptions
    },
    columnChartOptions() {
      return columnChartOptions
    }
  },
  watch: {
    '$route'(to, from) {
      if (to.name === 'Statistics') {
        this.loadStatistics()
      }
    }
  },
  data() {
    return {
      statistics: null,
      loading: false,
      error: null,
      isProcessing: false,
      eventSource: null
    }
  },
  methods: {
    async loadStatistics(showLoading = true) {
      if (showLoading) {
        this.loading = true
      }
      this.error = null
      try {
        const newStatistics = await statisticsService.getStatistics()
        // Só atualiza se receber dados válidos
        if (newStatistics) {
          this.statistics = newStatistics
          await this.$nextTick()
        }
      } catch (err) {
        this.error = 'Erro ao carregar estatísticas: ' + (err.response?.data?.message || err.message)
        console.error(err)
        // Mantém os dados existentes em caso de erro
      } finally {
        this.loading = false
      }
    },
    async updateAllData() {
      if (this.isProcessing) {
        return
      }
      
      this.error = null
      try {
        // Primeiro, aciona a atualização dos valores corrigidos
        await statisticsService.updateProcessValues()
        
        // Mostra o ícone indicador e conecta ao SSE
        this.isProcessing = true
        this.connectToStatusStream()
      } catch (err) {
        this.error = 'Erro ao atualizar dados: ' + (err.response?.data?.message || err.message)
        console.error(err)
        this.isProcessing = false
      }
    },
    async checkProcessStatus() {
      try {
        const status = await statisticsService.getProcessStatus()
        if (status.status === 'RUNNING') {
          this.isProcessing = true
          this.connectToStatusStream()
        }
      } catch (err) {
        console.error('Erro ao verificar status do processamento:', err)
      }
    },
    connectToStatusStream() {
      // Desconecta stream anterior se existir
      this.disconnectStatusStream()
      
      this.eventSource = statisticsService.connectToStatusStream(
        (data) => {
          if (data.status === 'COMPLETED' || data.status === 'ERROR') {
            this.isProcessing = false
            this.disconnectStatusStream()
            
            if (data.status === 'COMPLETED') {
              // Recarrega as estatísticas após conclusão sem mostrar loading
              this.loadStatistics(false)
            } else {
              this.error = 'Erro ao processar atualização: ' + (data.errorMessage || 'Erro desconhecido')
            }
          } else if (data.status === 'RUNNING') {
            this.isProcessing = true
          }
        },
        (error) => {
          console.error('Erro na conexão SSE:', error)
          // Tenta reconectar após 2 segundos
          setTimeout(() => {
            if (this.isProcessing) {
              this.connectToStatusStream()
            }
          }, 2000)
        }
      )
    },
    disconnectStatusStream() {
      if (this.eventSource) {
        this.eventSource.close()
        this.eventSource = null
      }
    },
    formatCurrency(value) {
      if (!value) return 'R$ 0,00'
      return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value)
    },
    goToHome() {
      this.$router.push('/dashboard')
    },
    goBack() {
      this.$router.push('/dashboard')
    }
  }
}
</script>

<style scoped>
.statistics {
  padding: 1.5rem;
  min-height: 100vh;
  background: #f5f7fa;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
}

.header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
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

.refresh-icon {
  margin-left: auto;
  cursor: pointer;
  color: #5a7ba8;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0.25rem;
  border-radius: 4px;
  transition: color 0.2s, transform 0.2s;
}

.refresh-icon:hover:not(.disabled) {
  color: #4a6b98;
  transform: scale(1.1);
}

.refresh-icon.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.refresh-icon svg {
  transition: transform 0.3s ease;
}

.refresh-icon:hover:not(.disabled) svg:not(.rotating) {
  transform: rotate(90deg);
}

.refresh-icon svg.rotating {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.header h1 {
  font-size: 1.75rem;
  color: #2d3748;
  font-weight: 600;
  margin: 0;
}

/* Cards de Estatísticas Gerais */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 1.25rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border-left: 3px solid #5a7ba8;
}

.stat-label {
  font-size: 0.85rem;
  color: #718096;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.stat-value {
  font-size: 1.75rem;
  font-weight: 600;
  color: #2d3748;
  line-height: 1.2;
}

/* Cards Principais */
.main-stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.main-stat-card {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border-left: 3px solid #5a7ba8;
}

.main-stat-label {
  font-size: 0.9rem;
  color: #718096;
  margin-bottom: 0.75rem;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.processing-indicator {
  display: inline-flex;
  align-items: center;
  color: #5a7ba8;
  cursor: help;
}

.spinning-icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.main-stat-value {
  font-size: 1.5rem;
  font-weight: 600;
  color: #2d3748;
  line-height: 1.2;
}

.section {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 1.5rem;
}

.section-title {
  font-size: 1.25rem;
  color: #2d3748;
  margin-bottom: 1rem;
  font-weight: 600;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #e2e8f0;
}

.chart-wrapper-small {
  min-height: 300px;
  position: relative;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
}

.chart-item {
  background: #f8f9fa;
  border-radius: 6px;
  padding: 1rem;
}

.chart-subtitle {
  font-size: 0.95rem;
  color: #4a5568;
  margin-bottom: 0.75rem;
  font-weight: 600;
  text-align: center;
}

.chart-wrapper-compact {
  min-height: 200px;
  position: relative;
}

.loading, .error {
  text-align: center;
  padding: 2rem;
  font-size: 1rem;
}

.error {
  color: #e53e3e;
  background: #fed7d7;
  border-radius: 6px;
  border: 1px solid #fc8181;
}

.btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-secondary {
  background: #718096;
  color: white;
}

.btn-secondary:hover {
  background: #5a6b7f;
}

.honorarios-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1rem;
}

.honorarios-card {
  border-radius: 8px;
  padding: 1.25rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #e2e8f0;
  background: white;
}

.honorarios-header {
  margin-bottom: 1rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #e2e8f0;
}

.honorarios-header h3 {
  font-size: 1rem;
  font-weight: 600;
  margin: 0;
  color: #2d3748;
}

.honorarios-body {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.honorarios-stat {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
}

.honorarios-label {
  font-weight: 500;
  color: #718096;
  font-size: 0.85rem;
}

.honorarios-value {
  font-weight: 600;
  font-size: 0.9rem;
  color: #2d3748;
}

.honorarios-value.currency {
  font-size: 0.95rem;
}

.honorarios-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
  margin-top: 0.25rem;
  border-radius: 4px;
  background: #5a7ba8;
  color: white;
}

.honorarios-total-label {
  font-weight: 600;
  font-size: 0.9rem;
}

.honorarios-total-value {
  font-weight: 700;
  font-size: 1.1rem;
}

.honorarios-stat.total-acoes {
  background: #f0f4f8;
  border: 1px solid #cbd5e0;
  margin-top: 0.5rem;
  font-weight: 600;
}

.honorarios-stat.total-acoes .honorarios-label {
  font-weight: 600;
  color: #4a5568;
}

.honorarios-stat.total-acoes .honorarios-value {
  font-weight: 700;
  color: #2d3748;
  font-size: 1rem;
}

@media (max-width: 768px) {
  .stats-grid,
  .main-stats-grid {
    grid-template-columns: 1fr;
  }
  
  .charts-grid {
    grid-template-columns: 1fr;
  }
  
  .chart-wrapper-small {
    min-height: 250px;
  }
  
  .chart-wrapper-compact {
    min-height: 180px;
  }
}
</style>
