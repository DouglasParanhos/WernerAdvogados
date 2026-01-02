<template>
  <div class="about-page">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">Sobre o Escritório</h1>
      </div>
    </section>

    <!-- Main Content -->
    <section class="content-section">
      <div class="content-container">
        <div class="about-content">
          <div class="text-block">
            <p>
              Fundado por Liz Werner, natural de Niterói/RJ, o escritório é liderado
              por uma advogada focada em proporcionar um atendimento diferenciado,
              oferecendo informações claras e precisas. Liz é bacharel pela
              Universidade Federal Fluminense, onde iniciou sua jornada na advocacia.
            </p>
          </div>

          <div class="text-block">
            <p>
              Sua escolha pela carreira jurídica foi motivada pela percepção de uma
              lacuna no <strong>atendimento</strong>, especialmente
              em situações delicadas envolvendo pessoas próximas. Além da graduação,
              Liz possui pós-graduação em Direito Imobiliário e especializações em
              áreas como direito contratual, proteção de dados e direito
              administrativo. Essa busca constante pela excelência é uma marca
              registrada de sua trajetória profissional.
            </p>
          </div>

          <div class="text-block">
            <p>
              Liz consolidou sua expertise no campo do direito administrativo,
              destacando-se ao resolver questões específicas envolvendo professores da
              rede estadual do Estado do Rio de Janeiro. Sua dedicação a essa área tem
              sido evidenciada ao longo dos anos.
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- Partnership Section -->
    <section class="partnership-section">
      <div class="content-container">
        <div class="partnership-content">
          <h2 class="section-title">Parceria Estratégica</h2>
          <div class="text-block">
            <p>
              Com o crescimento do escritório e a evolução das demandas, uma parceria
              estratégica foi estabelecida com o <strong>Escritório Masullo e Aguiar</strong>, sob a
              liderança do Dr. Thiago Aguiar. Essa colaboração fortaleceu a atuação,
              proporcionando uma abordagem conjunta para enfrentar os desafios
              jurídicos.
            </p>
          </div>
          <div class="text-block">
            <p>
              Hoje, a parceria solidificada estende suas demandas por todo o Estado do
              Rio de Janeiro, refletindo o compromisso compartilhado com a excelência e
              a busca por soluções jurídicas eficazes. A união dessas mentes brilhantes
              não é apenas profissional; é uma aliança fundamentada em valores e
              expertise.
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- History Section -->
    <section class="history-section">
      <div class="content-container">
        <div class="history-content">
          <h2 class="section-title">Nossa História</h2>
          <div class="text-block">
            <p>
              A colaboração entre os escritórios não se resume apenas em serem colegas
              de profissão; suas trajetórias se entrelaçaram durante a faculdade de
              direito. Compartilharam aulas, congressos e experiências como parceiros
              em um mandato do Centro Acadêmico do Curso de Direito da Universidade
              Federal Fluminense. A pluralidade de pensamento e as diferentes formas
              de atuação se complementam, proporcionando um trabalho abrangente e
              completo.
            </p>
          </div>
          <div class="text-block">
            <p>
              No nosso escritório, não apenas defendemos causas; construímos relações
              sólidas e buscamos, incessantemente, alcançar a justiça de maneira ética
              e comprometida.
            </p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: 'About'
}
</script>

<style scoped>
.about-page {
  min-height: 100vh;
  background: #f8f9fa;
}

/* Hero Section */
.hero-section {
  background: linear-gradient(135deg, #003d7a 0%, #1e5a9e 50%, #003d7a 100%);
  color: white;
  padding: 100px 20px 60px;
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

.hero-title {
  font-size: 3rem;
  font-weight: 700;
  letter-spacing: 0.05em;
  font-family: "Libre Baskerville", serif;
  margin: 0;
}

/* Content Sections */
.content-section,
.partnership-section,
.history-section {
  padding: 60px 20px;
}

.content-section {
  background: white;
}

.partnership-section {
  background: #f8f9fa;
}

.history-section {
  background: white;
}

.content-container {
  max-width: 900px;
  margin: 0 auto;
}

.about-content,
.partnership-content,
.history-content {
  width: 100%;
}

.section-title {
  font-size: 2.25rem;
  font-weight: 600;
  margin-bottom: 2rem;
  color: #1a1a1a;
  font-family: "Libre Baskerville", serif;
  letter-spacing: 0.02em;
  text-align: center;
}

.text-block {
  margin-bottom: 1.5rem;
}

.text-block:last-child {
  margin-bottom: 0;
}

.text-block p {
  font-size: 1.125rem;
  line-height: 1.8;
  color: #2d3748;
  text-align: justify;
  margin: 0;
}

.text-block strong {
  color: #003d7a;
  font-weight: 600;
}

/* Responsive */
@media (max-width: 768px) {
  .hero-title {
    font-size: 2.25rem;
  }

  .hero-section {
    padding: 80px 20px 50px;
  }

  .content-section,
  .partnership-section,
  .history-section {
    padding: 50px 20px;
  }

  .section-title {
    font-size: 1.75rem;
    margin-bottom: 1.5rem;
  }

  .text-block p {
    font-size: 1rem;
  }
}
</style>
