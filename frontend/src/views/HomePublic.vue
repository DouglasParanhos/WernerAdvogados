<template>
  <div class="background-container">
    <h2
      class="text-center tracking-widest Libre-Baskerville seu-texto-metalizado"
    >
      Werner Advogados
    </h2>
    <p class="text-center Libre-Baskerville"></p>

    <ul class="text-center Libre-Baskerville seu-texto-metalizado">
      <li>Trabalho baseado em valores</li>
      <li class="tracking-widest seu-texto-metalizado">
        <strong>Transparência</strong>
      </li>
      <li class="italico">
        "Comprometemo-nos a fornecer informações claras e compreensíveis"
      </li>
      <li class="tracking-widest seu-texto-metalizado">
        <strong>Confiança</strong>
      </li>
      <li class="italico">
        "Construímos relacionamentos sólidos, pautados na confiança mútua"
      </li>
      <li class="tracking-widest seu-texto-metalizado">
        <strong>Credibilidade</strong>
      </li>
      <li class="italico">
        "Com uma reputação sólida, oferecemos serviços jurídicos de alta
        qualidade"
      </li>
      <li class="tracking-widest seu-texto-metalizado">
        <strong>Segurança</strong>
      </li>
      <li class="italico">
        "Priorizamos a segurança jurídica para nossos clientes"
      </li>
      <li class=""><strong></strong></li>
    </ul>
    <p class="text-center Libre-Baskerville"></p>
  </div> <br />

  <div class = "columns">
    <!-- Coluna 1 -->
    <div class="column2 with-border">
      <div class=" Libre-Baskerville">
    <h3 class=" tracking-widest Libre-Baskerville">
      Dez anos trazendo um novo conceito de trabalho na advocacia
    </h3>
    
      <div class=" Libre-Baskerville">
      
      <p class="justificado1">
        Com uma trajetória sólida e dedicada de uma década, orgulhamo-nos de ser
        agentes transformadores no cenário jurídico. Durante esse período, não
        apenas acumulamos experiência, mas também nos destacamos por introduzir
        inovação e excelência em todos os aspectos do campo jurídico.
      </p>
      <p class="justificado1">
        Ao longo dos anos, construímos um legado robusto, fundamentado em
        valores sólidos e impulsionado por uma incessante busca por
        aprimoramento. Essa abordagem nos permite enfrentar os desafios
        complexos do mundo jurídico com confiança e competência, garantindo
        serviços de alta qualidade aos nossos clientes.
      </p>
      <p class="justificado1">
        Nosso compromisso vai além das expectativas convencionais. Buscamos
        constantemente oferecer um atendimento diferenciado, onde a
        transparência, a eficiência e uma abordagem individualizada para cada
        cliente são pilares fundamentais. Acreditamos firmemente que a prática
        da advocacia transcende as meras interpretações de leis e regulamentos;
        trata-se, essencialmente, de construir relacionamentos sólidos e
        confiáveis com nossos clientes.
      </p>
      <p class="justificado1">
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
   <!-- Coluna 2 -->
   <div class="column2">
    <img
          src="/tjs.png"
          alt="Imagem Tribunais"
          class="article-image"
        />
    </div> <br/>
</div> <br/> <br/>
    <div class = "columns">
    <!-- Coluna 1 -->
    <div class="column2 ">
      <img
          src="/table.jpeg"
          alt="Logo"
          class="text-center centered-image"
        />
      </div>
      <!-- Coluna 2 -->
   <div class="column2">
      <p class="justificado">
        No Werner Advogados, não somos apenas defensores legais, mas parceiros
        comprometidos em proporcionar uma experiência jurídica excepcional. Cada
        cliente é único, e nossa dedicação em compreender suas necessidades
        específicas reflete-se em soluções personalizadas e resultados eficazes.
      </p>
      <p class="justificado">
        Ao celebrar uma década de sucesso, renovamos nosso compromisso em moldar
        um futuro jurídico onde a excelência, a ética e a empatia são as pedras
        angulares de nossa prática. Estamos ansiosos para continuar essa
        jornada, contribuindo para o sucesso e a segurança jurídica de nossos
        clientes, hoje e nas décadas que virão.
      </p>
    </div>
    </div>
  
 
</template>

<script>
export default {
  name: "HomePublic",
};
</script>

<style scoped>

.Libre-Baskerville {
  font-family: "Libre Baskerville", serif;
}

h2 {
  margin-bottom: 95px;
  font-size: 65px;
  margin-top: 90px;
}
h3 {
  font-size: 30px;
  margin-top: 20px;
  margin: 10px;
}
li {
  font-size: 20px;
}
p {
  margin: 40px 0;
  font-size: 18px;
}

.justificado {
  text-align: justify;
  margin-left: 0px;
  margin-right: 0px;
}
.background-container {
  background-image: url("/Teste2.png");
  background-size: cover;
  background-position: center;
  color: #2d2d57;
  padding: 15px;
}
.background-container-logo {
  background-image: url("/LogoW.png");
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
  opacity: 0.5;
  padding: 20px;
}

.efeito-metalizado {
  position: relative;
  color: #17344e;
  background-image: linear-gradient(45deg, #0d1831, #cbd2d6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 1px 1px 1px rgba(3, 13, 27, 0.795);
}

.seu-texto-metalizado {
  position: relative;
  color: #17344e;
  background-image: linear-gradient(45deg, #0d1831, #cbd2d6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 1px 1px 1px rgba(3, 13, 27, 0.795);
}
.article-image {
  height: 600px;
  width: auto;
  margin-top: 70px;
  text-align: center;
  margin-left: 50px;
}


.columns {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  flex-wrap: wrap;
}
.column2 {
  width: calc(50% - 1rem);
  box-sizing: border-box;
}
.justificado1 {
  text-align: justify;
  margin-left: 0px;
  margin-right: 20px;
  font-size: 14px;
}
.with-border {
  border-right: 1px solid #6d6767ec;
}
.centered-image {
  display: block;
  margin: 0 auto;
  width: 70%;
}

.tracking-widest {
  letter-spacing: 0.1em;
}

.italico {
  font-style: italic;
}

.text-center {
  text-align: center;
}

/* Estilos específicos para telas menores (dispositivos móveis) */
@media only screen and (max-width: 600px) {
  .columns {
    flex-direction: column;
  }

  .column2 {
    width: 100%;
    margin-bottom: 20px;
  }

  .justificado1 {
    margin-left: 10px;
    margin-right: 10px;
  }

  .background-container {
    padding: 10px;
    margin-bottom: 20px;
  }

  .background-container-logo {
    padding: 10px;
    background-repeat: repeat;
    background-size: contain;
  }

  .efeito-metalizado {
    font-size: 24px;
    font-weight: 600;
  }

  .centered-image {
    display: block;
    margin: 0 auto;
    width: 70%;
  }
}
</style>

