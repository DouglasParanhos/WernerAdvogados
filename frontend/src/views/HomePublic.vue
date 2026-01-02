<template>
  <div class="home-public">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-content">
        <div class="logo-container">
          <img 
            src="/logo-w.png" 
            alt="Werner Advogados Logo" 
            class="logo-img"
            @error="handleImageError"
            :style="{ display: imageError ? 'none' : 'block' }"
          />
          <div v-if="imageError" class="logo-placeholder">
            <svg viewBox="0 0 200 200" class="logo-svg-placeholder">
              <!-- Left V (darker blue) -->
              <path d="M 20 180 L 20 40 L 60 40 L 60 100 L 100 100 L 100 40 L 140 40 L 140 180 L 100 180 L 100 120 L 60 120 L 60 180 Z" 
                    fill="url(#metallicGradientLeft)" 
                    stroke="rgba(255,255,255,0.3)" 
                    stroke-width="1"/>
              <!-- Right V (lighter silver) -->
              <path d="M 100 100 L 140 40 L 180 40 L 180 180 L 140 180 L 140 120 L 100 120 Z" 
                    fill="url(#metallicGradientRight)" 
                    stroke="rgba(255,255,255,0.4)" 
                    stroke-width="1"/>
              <!-- Light source effect at center -->
              <circle cx="100" cy="100" r="15" fill="url(#lightSource)" opacity="0.8"/>
              <!-- Gradients -->
              <defs>
                <linearGradient id="metallicGradientLeft" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" style="stop-color:#1e3a5f;stop-opacity:1" />
                  <stop offset="50%" style="stop-color:#2d4a6b;stop-opacity:1" />
                  <stop offset="100%" style="stop-color:#003d7a;stop-opacity:1" />
                </linearGradient>
                <linearGradient id="metallicGradientRight" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" style="stop-color:#c0c8d0;stop-opacity:1" />
                  <stop offset="50%" style="stop-color:#e8eef5;stop-opacity:1" />
                  <stop offset="100%" style="stop-color:#d0d8e0;stop-opacity:1" />
                </linearGradient>
                <radialGradient id="lightSource" cx="50%" cy="50%">
                  <stop offset="0%" style="stop-color:#ffffff;stop-opacity:1" />
                  <stop offset="100%" style="stop-color:#ffffff;stop-opacity:0" />
                </radialGradient>
              </defs>
            </svg>
          </div>
        </div>
        <h1 class="hero-title">Werner Advogados</h1>
        <p class="hero-subtitle">Excelência jurídica com compromisso e transparência</p>
      </div>
    </section>

    <!-- Values Section -->
    <section class="values-section">
      <div class="content-container">
        <h2 class="section-title">Trabalho Baseado em Valores</h2>
        <div class="values-grid">
          <div class="value-card">
            <div class="value-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <h3 class="value-title">Transparência</h3>
            <p class="value-description">Comprometemo-nos a fornecer informações claras e compreensíveis</p>
          </div>
          
          <div class="value-card">
            <div class="value-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M8 12L11 15L16 9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <h3 class="value-title">Confiança</h3>
            <p class="value-description">Construímos relacionamentos sólidos, pautados na confiança mútua</p>
          </div>
          
          <div class="value-card">
            <div class="value-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <h3 class="value-title">Credibilidade</h3>
            <p class="value-description">Com uma reputação sólida, oferecemos serviços jurídicos de alta qualidade</p>
          </div>
          
          <div class="value-card">
            <div class="value-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M12 8V12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M12 16H12.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <h3 class="value-title">Segurança</h3>
            <p class="value-description">Priorizamos a segurança jurídica para nossos clientes</p>
          </div>
        </div>
      </div>
    </section>

    <!-- About Section -->
    <section class="about-section">
      <div class="content-container">
        <div class="about-content">
          <div class="about-text">
            <h2 class="section-title">Dez Anos Trazendo um Novo Conceito de Trabalho na Advocacia</h2>
            <p>
              Com uma trajetória sólida e dedicada de uma década, orgulhamo-nos de ser
              agentes transformadores no cenário jurídico. Durante esse período, não
              apenas acumulamos experiência, mas também nos destacamos por introduzir
              inovação e excelência em todos os aspectos do campo jurídico.
            </p>
            <p>
              Ao longo dos anos, construímos um legado robusto, fundamentado em
              valores sólidos e impulsionado por uma incessante busca por
              aprimoramento. Essa abordagem nos permite enfrentar os desafios
              complexos do mundo jurídico com confiança e competência, garantindo
              serviços de alta qualidade aos nossos clientes.
            </p>
            <p>
              Nosso compromisso vai além das expectativas convencionais. Buscamos
              constantemente oferecer um atendimento diferenciado, onde a
              transparência, a eficiência e uma abordagem individualizada para cada
              cliente são pilares fundamentais. Acreditamos firmemente que a prática
              da advocacia transcende as meras interpretações de leis e regulamentos;
              trata-se, essencialmente, de construir relacionamentos sólidos e
              confiáveis com nossos clientes.
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- Commitment Section -->
    <section class="commitment-section">
      <div class="content-container">
        <div class="commitment-content">
          <h2 class="section-title">Nosso Compromisso</h2>
          <p>
            No Werner Advogados, não somos apenas defensores legais, mas parceiros
            comprometidos em proporcionar uma experiência jurídica excepcional. Cada
            cliente é único, e nossa dedicação em compreender suas necessidades
            específicas reflete-se em soluções personalizadas e resultados eficazes.
          </p>
          <p>
            Ao celebrar uma década de sucesso, renovamos nosso compromisso em moldar
            um futuro jurídico onde a excelência, a ética e a empatia são as pedras
            angulares de nossa prática. Estamos ansiosos para continuar essa
            jornada, contribuindo para o sucesso e a segurança jurídica de nossos
            clientes, hoje e nas décadas que virão.
          </p>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: "HomePublic",
  data() {
    return {
      imageError: false
    }
  },
  methods: {
    handleImageError() {
      this.imageError = true
    }
  }
}
</script>

<style scoped>
.home-public {
  min-height: 100vh;
  background: #f8f9fa;
}

/* Hero Section */
.hero-section {
  background: linear-gradient(135deg, #003d7a 0%, #1e5a9e 50%, #003d7a 100%);
  color: white;
  padding: 20px 20px 30px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 50%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(255, 255, 255, 0.05) 0%, transparent 50%);
  pointer-events: none;
}

.hero-content {
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.logo-container {
  display: flex;
  justify-content: center;
  margin-top: 0.3cm;
  margin-bottom: 0.3cm;
}

.logo-img {
  width: 300px;
  height: auto;
  max-height: 300px;
  object-fit: contain;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.3));
  animation: logoFloat 3s ease-in-out infinite;
}

.logo-placeholder {
  width: 300px;
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: logoFloat 3s ease-in-out infinite;
}

.logo-svg-placeholder {
  width: 100%;
  height: 100%;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
}

@keyframes logoFloat {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
  letter-spacing: 0.05em;
  font-family: "Libre Baskerville", serif;
}

.hero-subtitle {
  font-size: 1.5rem;
  font-weight: 300;
  opacity: 0.95;
  letter-spacing: 0.02em;
}

/* Values Section */
.values-section {
  padding: 80px 20px;
  background: white;
}

.content-container {
  max-width: 1200px;
  margin: 0 auto;
}

.section-title {
  font-size: 2.5rem;
  font-weight: 600;
  text-align: center;
  margin-bottom: 3rem;
  color: #1a1a1a;
  font-family: "Libre Baskerville", serif;
  letter-spacing: 0.02em;
}

.values-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-top: 2rem;
}

.value-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 2.5rem 2rem;
  text-align: center;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.value-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 61, 122, 0.15);
  border-color: #003d7a;
}

.value-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 1.5rem;
  color: #003d7a;
  display: flex;
  align-items: center;
  justify-content: center;
}

.value-icon svg {
  width: 100%;
  height: 100%;
}

.value-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: #1a1a1a;
  font-family: "Libre Baskerville", serif;
}

.value-description {
  font-size: 1rem;
  line-height: 1.6;
  color: #4a5568;
  margin: 0;
}

/* About Section */
.about-section {
  padding: 80px 20px;
  background: #f8f9fa;
}

.about-content {
  max-width: 900px;
  margin: 0 auto;
}

.about-text p {
  font-size: 1.125rem;
  line-height: 1.8;
  color: #2d3748;
  margin-bottom: 1.5rem;
  text-align: justify;
}

.about-text p:last-child {
  margin-bottom: 0;
}

/* Commitment Section */
.commitment-section {
  padding: 80px 20px;
  background: white;
}

.commitment-content {
  max-width: 900px;
  margin: 0 auto;
}

.commitment-content p {
  font-size: 1.125rem;
  line-height: 1.8;
  color: #2d3748;
  margin-bottom: 1.5rem;
  text-align: justify;
}

.commitment-content p:last-child {
  margin-bottom: 0;
}

/* Responsive */
@media (max-width: 768px) {
  .logo-img {
    width: 220px;
    max-height: 220px;
  }
  
  .logo-placeholder {
    width: 220px;
    height: 220px;
  }

  .hero-title {
    font-size: 2.5rem;
  }

  .hero-subtitle {
    font-size: 1.25rem;
  }

  .section-title {
    font-size: 2rem;
  }

  .values-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }

  .value-card {
    padding: 2rem 1.5rem;
  }

  .about-text p,
  .commitment-content p {
    font-size: 1rem;
  }

  .hero-section {
    padding: 80px 20px 60px;
  }

  .values-section,
  .about-section,
  .commitment-section {
    padding: 60px 20px;
  }
}
</style>
