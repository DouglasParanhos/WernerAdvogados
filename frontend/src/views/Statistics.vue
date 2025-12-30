<template>
  <div class="statistics">
    <div class="container">
      <div class="header">
        <button @click="goBack" class="btn btn-secondary">← Voltar</button>
        <h1>Estatísticas</h1>
        <button @click="loadStatistics" class="btn btn-primary" :disabled="loading" style="margin-left: auto;">
          {{ loading ? 'Atualizando...' : '🔄 Atualizar' }}
        </button>
      </div>
      
      <div v-if="loading" class="loading">Carregando estatísticas...</div>
      <div v-if="error" class="error">{{ error }}</div>
      
      <div v-if="!loading && !error && statistics" class="content">
        <!-- Cards de Estatísticas Gerais com Cores Diferentes -->
        <div class="stats-grid">
          <div class="stat-card stat-card-blue">
            <div class="stat-icon">👥</div>
            <h2>Clientes</h2>
            <div class="stat-value">{{ statistics.totalClients || 0 }}</div>
          </div>
          
          <div class="stat-card stat-card-green">
            <div class="stat-icon">📋</div>
            <h2>Processos</h2>
            <div class="stat-value">{{ statistics.totalProcesses || 0 }}</div>
          </div>
          
          <div class="stat-card stat-card-orange">
            <div class="stat-icon">📑</div>
            <h2>Matrículas</h2>
            <div class="stat-value">{{ statistics.totalMatriculations || 0 }}</div>
          </div>
          
          <div class="stat-card stat-card-red">
            <div class="stat-icon">📝</div>
            <h2>Movimentações</h2>
            <div class="stat-value">{{ statistics.totalMoviments || 0 }}</div>
          </div>
        </div>
        
        <!-- Processos por Tipo -->
        <div v-if="statistics.processesByType && statistics.processesByType.length > 0" class="section">
          <h2 class="section-title">Processos por Tipo</h2>
          <div class="type-cards-grid">
            <div v-for="item in statistics.processesByType" :key="item.type" 
                 :class="['type-card', getTypeCardClass(item.type)]">
              <div class="type-card-icon">{{ getTypeIcon(item.type) }}</div>
              <div class="type-card-content">
                <h3>{{ item.type || 'Não especificado' }}</h3>
                <div class="type-card-value">{{ item.count }}</div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Processos por Comarca - Separado por Tipo -->
        <div v-if="statistics.processesByComarca && statistics.processesByComarca.length > 0" class="section">
          <h2 class="section-title">Processos por Comarca</h2>
          
          <!-- Gráfico PISO -->
          <div v-if="comarcaDataPISO.length > 0" class="chart-container chart-piso">
            <div class="chart-header">
              <h3 class="chart-title">📊 PISO</h3>
            </div>
            <div class="table-container">
              <table>
                <thead>
                  <tr>
                    <th>Comarca</th>
                    <th>Quantidade</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in comarcaDataPISO" :key="item.comarca" class="row-piso">
                    <td><strong>{{ item.comarca || 'Não especificado' }}</strong></td>
                    <td class="count-cell">{{ item.count }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          
          <!-- Gráfico NOVAESCOLA -->
          <div v-if="comarcaDataNOVAESCOLA.length > 0" class="chart-container chart-novaescola">
            <div class="chart-header">
              <h3 class="chart-title">📊 NOVAESCOLA</h3>
            </div>
            <div class="table-container">
              <table>
                <thead>
                  <tr>
                    <th>Comarca</th>
                    <th>Quantidade</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in comarcaDataNOVAESCOLA" :key="item.comarca" class="row-novaescola">
                    <td><strong>{{ item.comarca || 'Não especificado' }}</strong></td>
                    <td class="count-cell">{{ item.count }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          
          <!-- Gráfico INTERNIVEIS -->
          <div v-if="comarcaDataINTERNIVEIS.length > 0" class="chart-container chart-interniveis">
            <div class="chart-header">
              <h3 class="chart-title">📊 INTERNIVEIS</h3>
            </div>
            <div class="table-container">
              <table>
                <thead>
                  <tr>
                    <th>Comarca</th>
                    <th>Quantidade</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in comarcaDataINTERNIVEIS" :key="item.comarca" class="row-interniveis">
                    <td><strong>{{ item.comarca || 'Não especificado' }}</strong></td>
                    <td class="count-cell">{{ item.count }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
        
        <!-- Processos por Status - Separado por Tipo -->
        <div v-if="statistics.processesByStatus && statistics.processesByStatus.length > 0" class="section">
          <h2 class="section-title">Processos por Status</h2>
          
          <!-- Gráfico PISO -->
          <div v-if="statusDataPISO.length > 0" class="chart-container chart-piso">
            <div class="chart-header">
              <h3 class="chart-title">📊 PISO</h3>
            </div>
            <div class="table-container">
              <table>
                <thead>
                  <tr>
                    <th>Status</th>
                    <th>Quantidade</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in statusDataPISO" :key="item.status" class="row-piso">
                    <td><strong>{{ item.status || 'Sem status' }}</strong></td>
                    <td class="count-cell">{{ item.count }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          
          <!-- Gráfico NOVAESCOLA -->
          <div v-if="statusDataNOVAESCOLA.length > 0" class="chart-container chart-novaescola">
            <div class="chart-header">
              <h3 class="chart-title">📊 NOVAESCOLA</h3>
            </div>
            <div class="table-container">
              <table>
                <thead>
                  <tr>
                    <th>Status</th>
                    <th>Quantidade</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in statusDataNOVAESCOLA" :key="item.status" class="row-novaescola">
                    <td><strong>{{ item.status || 'Sem status' }}</strong></td>
                    <td class="count-cell">{{ item.count }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          
          <!-- Gráfico INTERNIVEIS -->
          <div v-if="statusDataINTERNIVEIS.length > 0" class="chart-container chart-interniveis">
            <div class="chart-header">
              <h3 class="chart-title">📊 INTERNIVEIS</h3>
            </div>
            <div class="table-container">
              <table>
                <thead>
                  <tr>
                    <th>Status</th>
                    <th>Quantidade</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in statusDataINTERNIVEIS" :key="item.status" class="row-interniveis">
                    <td><strong>{{ item.status || 'Sem status' }}</strong></td>
                    <td class="count-cell">{{ item.count }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
        
        <!-- Honorários por Tipo -->
        <div v-if="statistics.honorariosByType && statistics.honorariosByType.length > 0" class="section">
          <h2 class="section-title">Honorários por Tipo de Processo</h2>
          <div class="honorarios-grid">
            <div v-for="item in statistics.honorariosByType" :key="item.tipoProcesso" 
                 :class="['honorarios-card', getTypeCardClass(item.tipoProcesso)]">
              <div class="honorarios-header">
                <div class="honorarios-icon">{{ getTypeIcon(item.tipoProcesso) }}</div>
                <h3>{{ item.tipoProcesso || 'Não especificado' }}</h3>
              </div>
              <div class="honorarios-body">
                <div class="honorarios-stat">
                  <span class="honorarios-label">Quantidade de Processos:</span>
                  <span class="honorarios-value">{{ item.quantidadeProcessos }}</span>
                </div>
                <div class="honorarios-stat">
                  <span class="honorarios-label">Honorários Contratuais:</span>
                  <span class="honorarios-value currency">{{ formatCurrency(item.totalHonorariosContratuais) }}</span>
                </div>
                <div class="honorarios-stat">
                  <span class="honorarios-label">Honorários Sucumbenciais:</span>
                  <span class="honorarios-value currency">{{ formatCurrency(item.totalHonorariosSucumbenciais) }}</span>
                </div>
                <div class="honorarios-total">
                  <span class="honorarios-total-label">Total:</span>
                  <span class="honorarios-total-value">{{ formatCurrency(item.totalHonorarios) }}</span>
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
  computed: {
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
    }
  },
  watch: {
    // Recarregar estatísticas quando a rota mudar (usuário voltar para esta página)
    '$route'(to, from) {
      // Só recarregar se realmente mudou para a rota de estatísticas
      if (to.name === 'Statistics') {
        this.loadStatistics()
      }
    }
  },
  methods: {
    async loadStatistics() {
      this.loading = true
      this.error = null
      try {
        this.statistics = await statisticsService.getStatistics()
        // Forçar atualização reativa
        await this.$nextTick()
      } catch (err) {
        this.error = 'Erro ao carregar estatísticas: ' + (err.response?.data?.message || err.message)
        console.error(err)
      } finally {
        this.loading = false
      }
    },
    formatCurrency(value) {
      if (!value) return 'R$ 0,00'
      return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value)
    },
    getTypeRowClass(tipo) {
      if (!tipo) return ''
      const tipoUpper = tipo.toUpperCase()
      if (tipoUpper === 'PISO') return 'row-piso'
      if (tipoUpper === 'NOVAESCOLA') return 'row-novaescola'
      if (tipoUpper === 'INTERNIVEIS') return 'row-interniveis'
      return ''
    },
    getComarcaDataByType(tipo) {
      if (!this.statistics?.processesByComarca || !Array.isArray(this.statistics.processesByComarca)) {
        return []
      }
      const result = []
      const tipoUpper = (tipo || '').toUpperCase()
      
      this.statistics.processesByComarca.forEach(comarca => {
        if (comarca && comarca.byType && Array.isArray(comarca.byType)) {
          const tipoData = comarca.byType.find(t => {
            const tType = (t?.type || '').toUpperCase()
            return tType === tipoUpper
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
    getStatusDataByType(tipo) {
      if (!this.statistics?.processesByStatus || !Array.isArray(this.statistics.processesByStatus)) {
        return []
      }
      const result = []
      const tipoUpper = (tipo || '').toUpperCase()
      
      this.statistics.processesByStatus.forEach(status => {
        if (status && status.byType && Array.isArray(status.byType)) {
          const tipoData = status.byType.find(t => {
            const tType = (t?.type || '').toUpperCase()
            return tType === tipoUpper
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
    getTypeCardClass(tipo) {
      if (!tipo) return ''
      const tipoUpper = tipo.toUpperCase()
      if (tipoUpper === 'PISO') return 'type-card-piso'
      if (tipoUpper === 'NOVAESCOLA') return 'type-card-novaescola'
      if (tipoUpper === 'INTERNIVEIS') return 'type-card-interniveis'
      return ''
    },
    getTypeIcon(tipo) {
      if (!tipo) return '📋'
      const tipoUpper = tipo.toUpperCase()
      if (tipoUpper === 'PISO') return '🏢'
      if (tipoUpper === 'NOVAESCOLA') return '🏫'
      if (tipoUpper === 'INTERNIVEIS') return '📚'
      return '📋'
    },
    goBack() {
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
.statistics {
  padding: 2rem;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
}

.container {
  max-width: 1400px;
  margin: 0 auto;
}

.header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
}

.btn-primary {
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(0, 123, 255, 0.3);
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 600;
  transition: all 0.2s;
}

.btn-primary:hover:not(:disabled) {
  background: linear-gradient(135deg, #0056b3 0%, #004085 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.4);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.header h1 {
  font-size: 2.5rem;
  color: #1a1a1a;
  font-weight: 700;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 3rem;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 2.5rem 2rem;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  text-align: center;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  border-top: 5px solid;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 5px;
}

.stat-card-blue {
  border-top-color: #003d7a;
  background: linear-gradient(135deg, #ffffff 0%, #f0f7ff 100%);
}

.stat-card-green {
  border-top-color: #28a745;
  background: linear-gradient(135deg, #ffffff 0%, #f0f9f4 100%);
}

.stat-card-orange {
  border-top-color: #ff9800;
  background: linear-gradient(135deg, #ffffff 0%, #fff8f0 100%);
}

.stat-card-red {
  border-top-color: #dc3545;
  background: linear-gradient(135deg, #ffffff 0%, #fff5f5 100%);
}

.stat-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 30px rgba(0,0,0,0.15);
}

.stat-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  opacity: 0.8;
}

.stat-card h2 {
  font-size: 1.1rem;
  color: #6c757d;
  margin-bottom: 1rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stat-value {
  font-size: 3rem;
  font-weight: 700;
  line-height: 1;
}

.stat-card-blue .stat-value {
  color: #003d7a;
}

.stat-card-green .stat-value {
  color: #28a745;
}

.stat-card-orange .stat-value {
  color: #ff9800;
}

.stat-card-red .stat-value {
  color: #dc3545;
}

.section {
  background: white;
  border-radius: 12px;
  padding: 2.5rem;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  margin-bottom: 2.5rem;
}

.section-title {
  font-size: 2rem;
  color: #1a1a1a;
  margin-bottom: 2rem;
  font-weight: 700;
  padding-bottom: 1rem;
  border-bottom: 4px solid #003d7a;
  background: linear-gradient(135deg, #003d7a 0%, #0056b3 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.chart-container {
  margin-bottom: 2.5rem;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.chart-container:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0,0,0,0.15);
}

.chart-header {
  padding: 1.5rem 2rem;
  border-bottom: 3px solid;
}

.chart-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.chart-piso {
  background: linear-gradient(135deg, #fff8f0 0%, #ffffff 100%);
  border: 2px solid #ff9800;
}

.chart-piso .chart-header {
  background: linear-gradient(135deg, #ff9800 0%, #ff6f00 100%);
  border-bottom-color: #ff6f00;
}

.chart-piso .chart-title {
  color: white;
}

.chart-piso thead {
  background: linear-gradient(135deg, #ff9800 0%, #ff6f00 100%);
}

.chart-novaescola {
  background: linear-gradient(135deg, #f0f9ff 0%, #ffffff 100%);
  border: 2px solid #2196f3;
}

.chart-novaescola .chart-header {
  background: linear-gradient(135deg, #2196f3 0%, #1976d2 100%);
  border-bottom-color: #1976d2;
}

.chart-novaescola .chart-title {
  color: white;
}

.chart-novaescola thead {
  background: linear-gradient(135deg, #2196f3 0%, #1976d2 100%);
}

.chart-interniveis {
  background: linear-gradient(135deg, #f0f9f4 0%, #ffffff 100%);
  border: 2px solid #28a745;
}

.chart-interniveis .chart-header {
  background: linear-gradient(135deg, #28a745 0%, #1e7e34 100%);
  border-bottom-color: #1e7e34;
}

.chart-interniveis .chart-title {
  color: white;
}

.chart-interniveis thead {
  background: linear-gradient(135deg, #28a745 0%, #1e7e34 100%);
}

.section h2 {
  font-size: 1.75rem;
  color: #1a1a1a;
  margin-bottom: 2rem;
  font-weight: 700;
  padding-bottom: 1rem;
  border-bottom: 3px solid #e8ecf1;
}

.table-container {
  overflow-x: auto;
  border-radius: 8px;
}

table {
  width: 100%;
  border-collapse: collapse;
  background: white;
}

thead {
  background: linear-gradient(135deg, #003d7a 0%, #0056b3 100%);
}

thead th {
  padding: 1.25rem 1rem;
  text-align: left;
  font-weight: 600;
  color: white;
  font-size: 0.95rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

tbody tr {
  transition: all 0.2s ease;
}

tbody tr:hover {
  background: #f8f9fa;
}

td {
  padding: 1rem;
  border-bottom: 1px solid #e8ecf1;
  color: #333;
  font-size: 0.95rem;
  transition: all 0.2s ease;
}

.comarca-cell,
.status-cell {
  font-weight: 600;
  background: #f8f9fa;
  vertical-align: middle;
}

.row-piso {
  background: linear-gradient(90deg, #fff8f0 0%, #ffffff 100%);
  border-left: 5px solid #ff9800;
}

.row-piso:hover {
  background: linear-gradient(90deg, #fff0e0 0%, #fff8f0 100%);
  transform: translateX(4px);
  box-shadow: 2px 0 8px rgba(255, 152, 0, 0.2);
}

.row-piso td {
  font-weight: 500;
}

.row-piso .count-cell {
  font-weight: 700;
  color: #ff6f00;
  font-size: 1.1rem;
}

.row-novaescola {
  background: linear-gradient(90deg, #f0f9ff 0%, #ffffff 100%);
  border-left: 5px solid #2196f3;
}

.row-novaescola:hover {
  background: linear-gradient(90deg, #e0f2ff 0%, #f0f9ff 100%);
  transform: translateX(4px);
  box-shadow: 2px 0 8px rgba(33, 150, 243, 0.2);
}

.row-novaescola td {
  font-weight: 500;
}

.row-novaescola .count-cell {
  font-weight: 700;
  color: #1976d2;
  font-size: 1.1rem;
}

.row-interniveis {
  background: linear-gradient(90deg, #f0f9f4 0%, #ffffff 100%);
  border-left: 5px solid #28a745;
}

.row-interniveis:hover {
  background: linear-gradient(90deg, #e0f2e8 0%, #f0f9f4 100%);
  transform: translateX(4px);
  box-shadow: 2px 0 8px rgba(40, 167, 69, 0.2);
}

.row-interniveis td {
  font-weight: 500;
}

.row-interniveis .count-cell {
  font-weight: 700;
  color: #1e7e34;
  font-size: 1.1rem;
}

.count-cell {
  text-align: center;
  font-size: 1.2rem;
}

.loading, .error {
  text-align: center;
  padding: 3rem;
  font-size: 1.2rem;
}

.error {
  color: #dc3545;
  background: #fff5f5;
  border-radius: 8px;
  border: 2px solid #fecaca;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 600;
  transition: all 0.2s;
}

.btn-secondary {
  background: linear-gradient(135deg, #6c757d 0%, #5a6268 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(108, 117, 125, 0.3);
}

.btn-secondary:hover {
  background: linear-gradient(135deg, #5a6268 0%, #495057 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(108, 117, 125, 0.4);
}

.type-cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
}

.type-card {
  border-radius: 16px;
  padding: 2rem;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
  border: 3px solid;
}

.type-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 30px rgba(0,0,0,0.2);
}

.type-card-icon {
  font-size: 4rem;
  line-height: 1;
}

.type-card-content {
  flex: 1;
}

.type-card-content h3 {
  font-size: 1.25rem;
  font-weight: 700;
  margin: 0 0 0.5rem 0;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.type-card-value {
  font-size: 2.5rem;
  font-weight: 700;
  line-height: 1;
}

.type-card-piso {
  background: linear-gradient(135deg, #fff8f0 0%, #ffffff 100%);
  border-color: #ff9800;
}

.type-card-piso .type-card-content h3 {
  color: #ff6f00;
}

.type-card-piso .type-card-value {
  color: #ff9800;
}

.type-card-novaescola {
  background: linear-gradient(135deg, #f0f9ff 0%, #ffffff 100%);
  border-color: #2196f3;
}

.type-card-novaescola .type-card-content h3 {
  color: #1976d2;
}

.type-card-novaescola .type-card-value {
  color: #2196f3;
}

.type-card-interniveis {
  background: linear-gradient(135deg, #f0f9f4 0%, #ffffff 100%);
  border-color: #28a745;
}

.type-card-interniveis .type-card-content h3 {
  color: #1e7e34;
}

.type-card-interniveis .type-card-value {
  color: #28a745;
}

.honorarios-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 2rem;
}

.honorarios-card {
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
  border: 3px solid;
  background: white;
}

.honorarios-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 30px rgba(0,0,0,0.2);
}

.honorarios-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid;
}

.honorarios-icon {
  font-size: 3rem;
  line-height: 1;
}

.honorarios-header h3 {
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.honorarios-body {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.honorarios-stat {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 8px;
}

.honorarios-label {
  font-weight: 600;
  color: #555;
  font-size: 0.9rem;
}

.honorarios-value {
  font-weight: 700;
  font-size: 1.1rem;
}

.honorarios-value.currency {
  font-size: 1.2rem;
}

.honorarios-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  margin-top: 0.5rem;
  border-radius: 8px;
  border: 2px solid;
}

.honorarios-total-label {
  font-weight: 700;
  font-size: 1.1rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.honorarios-total-value {
  font-weight: 700;
  font-size: 1.5rem;
}

.honorarios-card-piso {
  border-color: #ff9800;
  background: linear-gradient(135deg, #fff8f0 0%, #ffffff 100%);
}

.honorarios-card-piso .honorarios-header {
  border-bottom-color: #ff9800;
}

.honorarios-card-piso .honorarios-header h3 {
  color: #ff6f00;
}

.honorarios-card-piso .honorarios-value {
  color: #ff9800;
}

.honorarios-card-piso .honorarios-total {
  background: linear-gradient(135deg, #ff9800 0%, #ff6f00 100%);
  border-color: #ff6f00;
  color: white;
}

.honorarios-card-piso .honorarios-total-label,
.honorarios-card-piso .honorarios-total-value {
  color: white;
}

.honorarios-card-novaescola {
  border-color: #2196f3;
  background: linear-gradient(135deg, #f0f9ff 0%, #ffffff 100%);
}

.honorarios-card-novaescola .honorarios-header {
  border-bottom-color: #2196f3;
}

.honorarios-card-novaescola .honorarios-header h3 {
  color: #1976d2;
}

.honorarios-card-novaescola .honorarios-value {
  color: #2196f3;
}

.honorarios-card-novaescola .honorarios-total {
  background: linear-gradient(135deg, #2196f3 0%, #1976d2 100%);
  border-color: #1976d2;
  color: white;
}

.honorarios-card-novaescola .honorarios-total-label,
.honorarios-card-novaescola .honorarios-total-value {
  color: white;
}

.honorarios-card-interniveis {
  border-color: #28a745;
  background: linear-gradient(135deg, #f0f9f4 0%, #ffffff 100%);
}

.honorarios-card-interniveis .honorarios-header {
  border-bottom-color: #28a745;
}

.honorarios-card-interniveis .honorarios-header h3 {
  color: #1e7e34;
}

.honorarios-card-interniveis .honorarios-value {
  color: #28a745;
}

.honorarios-card-interniveis .honorarios-total {
  background: linear-gradient(135deg, #28a745 0%, #1e7e34 100%);
  border-color: #1e7e34;
  color: white;
}

.honorarios-card-interniveis .honorarios-total-label,
.honorarios-card-interniveis .honorarios-total-value {
  color: white;
}
</style>
