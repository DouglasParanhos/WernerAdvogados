<template>
  <div class="areas-page">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">Áreas de Atuação</h1>
      </div>
    </section>

    <!-- Introduction Section -->
    <section class="intro-section">
      <div class="content-container">
        <div class="intro-content">
          <p>
            Em resposta à crescente demanda e à complexidade inerente às causas
            jurídicas, nosso escritório concentra-se integralmente na defesa dos
            Professores do Estado do Rio de Janeiro. Dentre as diversas questões
            abordadas, destacam-se aquelas relacionadas à indenização por valores
            pagos incorretamente, litígios envolvendo falta de pagamentos de
            gratificações e disputas referentes ao Piso Nacional.
          </p>
          <p>
            Este compromisso exige um acompanhamento meticuloso dos julgados tanto do
            Tribunal de Justiça do Estado do Rio de Janeiro quanto do Superior
            Tribunal de Justiça (STJ) e do Supremo Tribunal Federal (STF). À medida
            que as ações evoluem, novas teses surgem, demandando uma constante
            preparação para rebater alegações, fundamentadas nas leis e nos
            entendimentos judiciais. As questões não se limitam ao âmbito do Direito
            Administrativo, estendendo-se a áreas como direito civil, direito
            processual e direito constitucional, entre outras.
          </p>
        </div>
      </div>
    </section>

    <!-- Areas Grid -->
    <section class="areas-section">
      <div class="content-container">
        <div class="areas-grid">
          <!-- Nova Escola -->
          <div class="area-card">
            <div class="area-header">
              <h3 class="area-title">Nova Escola</h3>
            </div>
            <div class="area-content">
              <p class="area-description">
                A Ação trata da Gratificação <strong>Nova Escola</strong> do ano de 2002 que não foi
                paga pelo Estado do Rio de Janeiro. Estava ativo(a) em sala de Aula
                nesse período?
              </p>
            </div>
          </div>

          <!-- Piso -->
          <div class="area-card">
            <div class="area-header">
              <h3 class="area-title">Piso</h3>
            </div>
            <div class="area-content">
              <p class="area-description">
                A ação trata de pedido de cumprimento de Lei Federal que instituiu o
                Piso Salarial para o Magistério. É professor(a) do Estado ativo(a) ou
                aposentado(a)?
              </p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Philosophy Section -->
    <section class="philosophy-section">
      <div class="content-container">
        <div class="philosophy-content">
          <h2 class="section-title">Nossa Abordagem</h2>
          <div class="text-block">
            <p>
              Nosso escritório está em constante aprendizado, sempre atualizando nossos
              conhecimentos, principalmente nos temas em que atuamos efetivamente e
              explorando assuntos que são de interesse vital para nossos clientes.
              Investimos em estudos contínuos não apenas para aprimorar nossa expertise,
              mas também para garantir maior segurança e redução de riscos, minimizando
              surpresas indesejadas.
            </p>
          </div>
          <div class="text-block">
            <p>
              Optamos por não utilizar termos como <em>"causa ganha"</em> ou
              <em>"100% de certeza"</em>, pois compreendemos que as
              decisões judiciais não são exatas e dependem de análises detalhadas,
              envolvendo mais de um "julgador". Uma única ação pode passar por decisões
              de múltiplos juízes em primeiro grau, e em casos de recursos, por um
              colegiado de desembargadores. Se chegar aos Tribunais Superiores (STJ e
              STF), será analisada por um colegiado de ministros.
            </p>
          </div>
          <div class="text-block">
            <p>
              Dessa forma, abraçamos a humildade e a dedicação aos estudos, nos
              dedicando a buscar sempre o melhor resultado possível em prol dos
              interesses de nossos clientes.
            </p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: 'Areas'
}
</script>

<style scoped>
.areas-page {
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

/* Introduction Section */
.intro-section {
  padding: 60px 20px;
  background: white;
}

.content-container {
  max-width: 1200px;
  margin: 0 auto;
}

.intro-content {
  max-width: 900px;
  margin: 0 auto;
}

.intro-content p {
  font-size: 1.125rem;
  line-height: 1.8;
  color: #2d3748;
  text-align: justify;
  margin-bottom: 1.5rem;
}

.intro-content p:last-child {
  margin-bottom: 0;
}

/* Areas Section */
.areas-section {
  padding: 60px 20px;
  background: #f8f9fa;
}

.areas-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 2rem;
  max-width: 1000px;
  margin: 0 auto;
}

.area-card {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 2.5rem;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
}

.area-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 61, 122, 0.15);
  border-color: #003d7a;
}

.area-header {
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid #e5e7eb;
}

.area-title {
  font-size: 1.75rem;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
  font-family: "Libre Baskerville", serif;
}

.area-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.area-description {
  font-size: 1.125rem;
  line-height: 1.7;
  color: #2d3748;
  text-align: justify;
  margin-bottom: 1.5rem;
  flex: 1;
}

.area-description strong {
  color: #003d7a;
  font-weight: 600;
}

/* Philosophy Section */
.philosophy-section {
  padding: 60px 20px;
  background: white;
}

.philosophy-content {
  max-width: 900px;
  margin: 0 auto;
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

.text-block em {
  font-style: italic;
  color: #4a5568;
}

/* Responsive */
@media (max-width: 768px) {
  .hero-title {
    font-size: 2.25rem;
  }

  .hero-section {
    padding: 80px 20px 50px;
  }

  .intro-section,
  .areas-section,
  .philosophy-section {
    padding: 50px 20px;
  }

  .areas-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }

  .area-card {
    padding: 2rem 1.5rem;
  }

  .section-title {
    font-size: 1.75rem;
    margin-bottom: 1.5rem;
  }

  .intro-content p,
  .area-description,
  .text-block p {
    font-size: 1rem;
  }
}
</style>
