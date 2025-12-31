<template>
 <div class="background-container">
    <h2
      class="tracking-widest Libre-Baskerville titulopag"
    >
    Áreas de Atuação
    </h2> 
      
  </div> <br/>
  <hr class="my-2" />


  <div class="justificado tamanho-fonte">
    <p>
      Em resposta à crescente demanda e à complexidade inerente às causas
      jurídicas, nosso escritório concentra-se integralmente na defesa dos
      Professores do Estado do Rio de Janeiro. Dentre as diversas questões
      abordadas, destacam-se aquelas relacionadas à indenização por valores
      pagos incorretamente, litígios envolvendo falta de pagamentos de
      gratificações e disputas referentes ao Piso Nacional.
    </p> <br/>
    <p>
      Este compromisso exige um acompanhamento meticuloso dos julgados tanto do
      Tribunal de Justiça do Estado do Rio de Janeiro quanto do Superior
      Tribunal de Justiça (STJ) e do Supremo Tribunal Federal (STF). À medida
      que as ações evoluem, novas teses surgem, demandando uma constante
      preparação para rebater alegações, fundamentadas nas leis e nos
      entendimentos judiciais. As questões não se limitam ao âmbito do Direito
      Administrativo, estendendo-se a áreas como direito civil, direito
      processual e direito constitucional, entre outras.
    </p> <br/>
  </div> <br/>

  <!-- Versão Desktop -->
  <div class="flex mobile-desaparece">
    <!-- Coluna 1: Interníveis -->
    <div class="coluna">
      <h3 class=""></h3>
      <div class="coluna-content">
        <img
          src="/interniveis.png"
          alt="quadro 1"
          class="image-resol-areas"
        />
        <p class="justificado">
          Trata-se de uma diferença de que o Estado teria que ter pago no
          período de agosto/1998 a março/2003, referente a mudança de nível. Estava ativo(a) ou aposentado(a)
          nesse período? Clique aqui para saber mais.
        </p>
        <button
          @click="redirecionarParaPagina('interniveis')"
          class="btn custom-info"
        >
          Saiba Mais
        </button>
      </div>
    </div>

    <!-- Coluna 2: Nova Escola -->
    <div class="coluna">
      <h3></h3>
      <div class="coluna-content">
        <img src="/NE.png" alt="quadro NE" class="image-resol-areas" />
        <p class="justificado">
          A Ação trata da Gratificação <span class = "font-bold"> Nova Escola</span> do ano de 2002 que não foi
          paga pelo Estado do Rio de Janeiro. Estava ativo(a) em sala de Aula
          nesse período? Clique aqui para saber mais.
        </p>
        <button
          @click="redirecionarParaPagina('novaescola')"
          class="btn custom-info"
        >
          Saiba Mais
        </button>
      </div>
    </div>

    <!-- Coluna 3: Piso -->
    <div class="coluna">
      <h3></h3>
      <div class="coluna-content">
        <img
          src="/piso.png"
          alt="quadro piso"
          class="image-resol-areas"
        />
        <p class="justificado">
          A ação trata de pedido de cumprimento de Lei Federal que instituiu o
          Piso Salarial para o Magistério. É professor(a) do Estado ativo(a) ou
          aposentado(a)? Clique aqui para saber mais.
        </p>
        <button @click="redirecionarParaPagina('piso')" class="btn custom-info">
          Saiba Mais
        </button>
      </div>
    </div> 
  </div><br/>

  <!--Versão Mobile-->
  <div class="flex desktop-desaparece" style="flex-direction: column">
    <!-- Coluna 1: Interníveis -->
    <div class="coluna">
      <h3 class="">Interníveis</h3>
      <div class="coluna-content">
        <img
          src="/interniveis.png"
          alt="quadro 1"
          class="tamanho-imagem"
        />
        <p class="justificado">
          Trata-se de uma diferença de que o Estado teria que ter pago no
          período de agosto/1998 a março/2003. Estava ativo(a) ou aposentado(a)
          nesse período? Clique aqui para saber mais.
        </p>
        <button @click="redirecionarParaPagina('interniveis')" class="btn">
          Saiba Mais
        </button>
      </div>
    </div>

    <!-- Coluna 2: Nova Escola -->
    <div class="coluna">
      <h3>Nova Escola</h3>
      <div class="coluna-content">
        <img src="/NE.png" alt="quadro NE" class="tamanho-imagem" />
        <p class="justificado">
          A Ação trata da Gratificação Nova Escola do ano de 2002 que não foi
          paga pelo Estado do Rio de Janeiro. Estava ativo(a) em sala de Aula
          nesse período? Clique aqui para saber mais.
        </p>
        <button @click="redirecionarParaPagina('novaescola')" class="btn">
          Saiba Mais
        </button>
      </div>
    </div>

    <!-- Coluna 3: Piso -->
    <div class="coluna">
      <h3>Piso</h3>
      <div class="coluna-content">
        <img src="/piso.png" alt="quadro piso" class="tamanho-imagem" />
        <p class="justificado">
          A ação trata de pedido de cumprimento de Lei Federal que instituiu o
          Piso Salarial para o Magistério. É professor(a) do Estado ativo(a) ou
          aposentado(a)? Clique aqui para saber mais.
        </p>
        <button @click="redirecionarParaPagina('piso')" class="btn">
          Saiba Mais
        </button>
      </div>
    </div>
  </div>

  <div class="justificado tamanho-fonte">
    <p>
      Nosso escritório está em constante aprendizado, sempre atualizando nossos
      conhecimentos, principalmente nos temas em que atuamos efetivamente e
      explorando assuntos que são de interesse vital para nossos clientes.
      Investimos em estudos contínuos não apenas para aprimorar nossa expertise,
      mas também para garantir maior segurança e redução de riscos, minimizando
      surpresas indesejadas.
    </p> <br/>
    <p>
      Optamos por não utilizar termos como
      <span class="italico">"causa ganha"</span> ou
      <span class="italico">"100% de certeza"</span>, pois compreendemos que as
      decisões judiciais não são exatas e dependem de análises detalhadas,
      envolvendo mais de um "julgador". Uma única ação pode passar por decisões
      de multiplos juízes em primeiro grau, e em casos de recursos, por um
      colegiado de desembargadores. Se chegar aos Tribunais Superiores (STJ e
      STF), será analisada por um colegiado de ministros.
    </p> <br/>
    <p>
      Dessa forma, abraçamos a humildade e a dedicação aos estudos, nos
      dedicando a buscar sempre o melhor resultado possível em prol dos
      interesses de nossos clientes.
    </p>
  </div>

  <img
    src="/martelo.png"
    alt="martelo"
    class="mx-auto image-resol mobile-image desktop-container tamanhofoto"
  />
  <div class="mobile-desaparece">
    <!-- Versão Desktop -->
    <div class="mobile-desaparece">
      <h3>Outras Ações nas quais atuamos</h3> <br/>
      <div class="flex">
        <!-- Imagem -->
        <div>
         
        </div>

        <!-- Título e Parágrafo -->
        <div class="text-container">
          <h1>• Férias ou Licença Prêmio não gozada - conversão em pecúnia</h1>
          <p class="justificado tamanho-fonte">
            Licença prêmio não gozada significa que uma pessoa tem direito a um
            período de licença, mas não utilizou essa licença, mesmo tendo
            preenchido os requisitos para fazê-lo. Isso é um direito adquirido
            por servidores públicos, e eles têm a possibilidade de receber esse
            período não utilizado em dinheiro. Qualquer interpretação diferente
            levaria a um enriquecimento injustificado por parte da Administração
            Pública, o que não é permitido pela lei.
          </p>
        </div>
      </div>
    </div>

    <!--Versão Mobile-->
    <div class="flex desktop-desaparece text-container-mobile">
      <h1>• Férias ou Licença Prêmio não gozada - conversão em pecúnia</h1>
      <p class="justificado tamanho-fonte-mobile">
        Licença prêmio não gozada significa que uma pessoa tem direito a um
        período de licença, mas não utilizou essa licença, mesmo tendo
        preenchido os requisitos para fazê-lo. Isso é um direito adquirido por
        servidores públicos, e eles têm a possibilidade de receber esse período
        não utilizado em dinheiro. Qualquer interpretação diferente levaria a um
        enriquecimento injustificado por parte da Administração Pública, o que
        não é permitido pela lei.
      </p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Areas',
  methods: {
    redirecionarParaPagina(rota) {
      this.$router.push(`/${rota}`)
    }
  }
}
</script>

<style scoped>
.background-container {
  background-image: url("/bannerAreas.png");
  background-size: cover;
  background-position: center;
  color: #f7f7f7;
  padding: 10px;
}
.titulopag {
  font-size: 46px;
  color: white;
  text-align: right;
  margin-top: 200px;
  margin-right: 20px;
  text-shadow: 1px 1px 1px rgba(255, 255, 255, 0.795);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-image: linear-gradient(45deg, #b1c6c9, #cbd2d6);
}

h2 {
  margin-bottom: 20px;
  font-size: 36px;
}
h3 {
  margin-bottom: 20px;
  font-size: 36px;
  text-align: center;
}
h1 {
  font-size: 30px;
}
p {
  margin: 22px 0;
}
.coluna {
  width: 28%;
  border: 1px solid #110c3f8c;
  padding: 20px;
  box-sizing: border-box;
  border-radius: 0.325rem;
}
.coluna:hover {
  border-color: #02102585;
  box-shadow: 0px 8px 8px 0px rgba(7, 7, 41, 0.4);
}

.coluna-content {
  text-align: center;
}
.flex {
  display: flex;
  justify-content: space-between;
}

.image-resol-areas {
  display: inline;
  height: 13rem;
  width: auto;
  margin: 0 auto;
}

.tamanho-fonte {
  font-size: 24px;
}

.flex.mobile-desaparece .custom-info {
  background-color: transparent;
  border-radius: 8px;
  margin: 10px;
  padding: 5px;
  cursor: pointer;
  display: inline-block;
  height: auto;
  width: auto;
  border: 0.5px solid #d9d9d9;
  box-shadow: 0px 4px 4px 0px rgba(0, 0, 0, 0.25);
}

.flex.mobile-desaparece .custom-info:hover {
  border-color: #02102585;
  box-shadow: 0px 8px 8px 0px rgba(7, 7, 41, 0.4);
}
@media only screen and (min-width: 601px) {
  .desktop-desaparece {
    display: none;
  }
}
@media (max-width: 600px) {
  .flex-mobile {
    flex-direction: column;
    align-items: center;
  }

  .coluna {
    width: 100%;
    margin-bottom: 20px;
  }
  h2 {
    font-size: 32px;
    margin-top: 40px;
    margin-bottom: 40px;
    font-weight: bold;
  }

  h3 {
    font-size: 22px;
    margin-top: 10px;
    margin-bottom: 10px;
  }

  li {
    font-size: 12px;
  }

  p {
    font-size: 14px;
    margin: 30px 0;
  }
  .mobile-desaparece {
    display: none;
  }
  .desktop-desaparece {
    display: flex;
  }
  .tamanho-imagem {
    height: 30px;
    width: auto;
  }
}
.image-resol {
  max-width: 100%;
  height: auto;
}
@media only screen and (max-width: 600px) {
  .desktop-container {
    display: none;
  }
  .tamanhofotomobile {
    width: 200px;
    height: auto;
  }
  .flex.desktop-desaparece .custom-info {
  background-color: transparent;
  border-radius: 8px;
  margin: 10px;
  padding: 5px;
  cursor: pointer;
  display: inline-block;
  height: auto;
  width: auto;
  border: 0.5px solid #d9d9d9;
  box-shadow: 0px 4px 4px 0px rgba(0, 0, 0, 0.25);
}

.flex.desktop-desaparece .custom-info:hover {
  border-color: #02102585;
  box-shadow: 0px 8px 8px 0px rgba(7, 7, 41, 0.4);
}
}
.tamanhofoto {
  width: 200px;
  height: auto;
}
.tamanhofoto2 {
  width: 750px;
  height: auto;
  margin-right: 40px;
}

.custom-info {
  background-color: transparent;
  border-radius: 8px;
  margin: 10px;
  padding: 5px;
  cursor: pointer;
  display: inline-block;
  height: auto;
  width: auto;
  border: 0.5px solid #d9d9d9;
  box-shadow: 0px 4px 4px 0px rgba(0, 0, 0, 0.25);
}
.custom-info:hover {
  border-color: #02102585;
  box-shadow: 0px 8px 8px 0px rgba(7, 7, 41, 0.4);
}

.justificado {
  text-align: justify;
}

.italico {
  font-style: italic;
}

.font-bold {
  font-weight: bold;
}

.Libre-Baskerville {
  font-family: "Libre Baskerville", serif;
}

.tracking-widest {
  letter-spacing: 0.1em;
}

.mx-auto {
  margin-left: auto;
  margin-right: auto;
}

.mobile-image {
  max-width: 80%;
  height: auto;
}
</style>

