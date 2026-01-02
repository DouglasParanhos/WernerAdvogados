<template>
  <div class="calculations">
    <div class="calculations-container">
      <div class="calculations-header">
        <button @click="goToHome" class="btn-home" title="Voltar para Home">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <polyline points="9 22 9 12 15 12 15 22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
        <h1>Cálculos Judiciais</h1>
        <p class="subtitle">Selecione o tipo de cálculo que deseja realizar</p>
      </div>
      
      <div class="calculations-grid">
        <button @click="goToNovaEscola" class="calculation-card novaescola-card">
          <div class="card-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2L2 7l10 5 10-5-10-5z" stroke="url(#iconGradientNE)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 17l10 5 10-5M2 12l10 5 10-5" stroke="url(#iconGradientNE)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <defs>
                <linearGradient id="iconGradientNE" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" style="stop-color:#003d7a;stop-opacity:1" />
                  <stop offset="50%" style="stop-color:#5a7ba8;stop-opacity:1" />
                  <stop offset="100%" style="stop-color:#9db4d4;stop-opacity:1" />
                </linearGradient>
              </defs>
            </svg>
          </div>
          <h2>NOVAESCOLA</h2>
          <p>Cálculo judicial detalhado com correção monetária e juros de mora</p>
        </button>
        
        <button @click="goToPiso" class="calculation-card piso-card" disabled>
          <div class="card-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2L2 7l10 5 10-5-10-5z" stroke="url(#iconGradientPiso)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" opacity="0.5"/>
              <path d="M2 17l10 5 10-5M2 12l10 5 10-5" stroke="url(#iconGradientPiso)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" opacity="0.5"/>
              <defs>
                <linearGradient id="iconGradientPiso" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" style="stop-color:#003d7a;stop-opacity:1" />
                  <stop offset="50%" style="stop-color:#5a7ba8;stop-opacity:1" />
                  <stop offset="100%" style="stop-color:#9db4d4;stop-opacity:1" />
                </linearGradient>
              </defs>
            </svg>
          </div>
          <h2>PISO</h2>
          <p>Em breve</p>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Calculations',
  methods: {
    goToHome() {
      this.$router.push('/dashboard')
    },
    goToNovaEscola() {
      this.$router.push('/calculations/novaescola')
    },
    goToPiso() {
      // Implementar quando PISO estiver disponível
    }
  }
}
</script>

<style scoped>
.calculations {
  min-height: 100vh;
  background: linear-gradient(135deg, #d0d8e0 0%, #e8eef5 50%, #c0c8d0 100%);
  background-attachment: fixed;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  position: relative;
  overflow: hidden;
}

.calculations-container {
  max-width: 1200px;
  width: 100%;
  position: relative;
  z-index: 1;
}

.calculations-header {
  text-align: center;
  margin-bottom: 3rem;
  color: #1a1a1a;
  position: relative;
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

.btn-home svg {
  width: 20px;
  height: 20px;
}

.calculations-header h1 {
  font-size: 3rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
  color: #2d3748;
  text-shadow: 0 2px 4px rgba(255, 255, 255, 0.5);
  letter-spacing: -0.5px;
}

.subtitle {
  font-size: 1.25rem;
  color: #4a5568;
  font-weight: 400;
  letter-spacing: 0.5px;
}

.calculations-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  margin-top: 2rem;
}

.calculation-card {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border: 1px solid rgba(0, 61, 122, 0.1);
  border-radius: 16px;
  padding: 2.5rem 2rem;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08), 0 2px 4px rgba(0, 0, 0, 0.05);
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-height: 250px;
  position: relative;
  overflow: hidden;
}

.calculation-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #003d7a, #5a7ba8, #9db4d4);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.calculation-card:hover:not(:disabled)::before {
  transform: scaleX(1);
}

.calculation-card:hover:not(:disabled) {
  transform: translateY(-8px);
  box-shadow: 0 16px 32px rgba(0, 61, 122, 0.15), 0 4px 8px rgba(0, 0, 0, 0.1);
  border-color: rgba(0, 61, 122, 0.3);
  background: linear-gradient(135deg, #ffffff 0%, #f0f4f8 100%);
}

.calculation-card:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.card-icon {
  width: 64px;
  height: 64px;
  margin-bottom: 1rem;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-icon svg {
  width: 100%;
  height: 100%;
  filter: drop-shadow(0 2px 4px rgba(0, 61, 122, 0.2));
}

.calculation-card:hover:not(:disabled) .card-icon {
  transform: scale(1.15) translateY(-4px);
  filter: drop-shadow(0 4px 8px rgba(0, 61, 122, 0.3));
}

.calculation-card h2 {
  font-size: 1.75rem;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 0.75rem;
  transition: color 0.3s ease;
}

.calculation-card:hover:not(:disabled) h2 {
  color: #003d7a;
}

.calculation-card p {
  font-size: 1rem;
  color: #4a5568;
  line-height: 1.5;
  margin: 0;
  transition: color 0.3s ease;
}

.calculation-card:hover:not(:disabled) p {
  color: #2d3748;
}

@media (max-width: 768px) {
  .calculations-header h1 {
    font-size: 2rem;
  }
  
  .subtitle {
    font-size: 1rem;
  }
  
  .calculations-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .calculation-card {
    padding: 2rem 1.5rem;
    min-height: 200px;
  }
  
  .card-icon {
    width: 48px;
    height: 48px;
  }
  
  .calculation-card h2 {
    font-size: 1.5rem;
  }
}
</style>


