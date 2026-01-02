<template>
   <div class="background-container">
    <h2
      class="tracking-widest Libre-Baskerville titulopag"
    >
    Publicações
    </h2> 
      
  </div> <br/>
  <hr class="my-2" />
  <div class="Libre-Baskerville">
    <p class="justificado">
      Bem-vindo à nossa seção de Publicações, onde a nossa equipe jurídica
      prepara conteúdos envolventes e informativos abordando uma ampla variedade
      de temas no campo do Direito Administrativo. Concentramos nossos esforços
      na defesa incansável dos direitos dos Professores do Estado do Rio de
      Janeiro.
    </p>
    <h2 class="font-semibold justificado">Explore os Tópicos</h2>
    <p class="justificado">
      Descubra artigos especializados, análises jurídicas e atualizações
      relevantes que abrangem desde questões administrativas até detalhes
      específicos relacionados aos direitos dos professores. Estamos
      comprometidos em oferecer um conteúdo acessível e esclarecedor.
    </p>
    <h2 class="font-semibold justificado">Compromisso com a Defesa dos Direitos</h2>
    <p class="justificado">
      Nossa equipe está dedicada a manter você informado sobre as últimas
      desenvolturas legais que afetam a comunidade educacional. Acreditamos na
      importância de compartilhar conhecimento para fortalecer a compreensão dos
      seus direitos.
    </p>
  </div>

  <div class="columns">
    <!-- Coluna 1 -->
    <div class="column2">
      <router-link to="/publicacoes/primeiraacao" class="article-card">
        <img
          src="/Slide1.png"
          alt="Imagem do Artigo 1"
          class="article-image transparent-image hover-zoom"
        />
        <h4 class="article-title font-semibold">
          Por que atuamos nessas Ações?
        </h4>
      </router-link>

      <router-link to="/publicacoes/piso1" class="article-card">
        <img
          src="/Slide3.png"
          alt="Imagem do Artigo 2"
          class="article-image transparent-image hover-zoom"
        />
        <h4 class="article-title font-semibold">
          Como saber se estou recebendo o Piso?
        </h4>
      </router-link>
    </div>

    <!-- Coluna 2 -->
    <div class="column2">
      <router-link to="/publicacoes/piso2" class="article-card">
        <img
          src="/Slide3.png"
          alt="Imagem do Artigo 3"
          class="article-image transparent-image hover-zoom"
        />
        <h4 class="article-title font-semibold">
          O Estado não está obrigado a pagar o Piso? Liminar suspensa?
        </h4>
      </router-link>
    </div>

    <!-- Coluna 3 -->
    <div class="column2">
      <router-link to="/publicacoes/piso3" class="article-card">
        <img
          src="/Slide3.png"
          alt="Imagem do Artigo 5"
          class="article-image transparent-image hover-zoom"
        />
        <h4 class="article-title font-semibold">
          Valorização do Professor, como podemos ajudar?
        </h4>
      </router-link>
    </div>
  </div>
</template>

<script>
export default {
  name: "Publications",
};
</script>

<style scoped>

.background-container {
  background-image: url("/bannerpub.png");
  background-position: center;
  color: #f7f7f7;
  padding: 150px;
  background-repeat: no-repeat;
  -webkit-background-size: cover;
  -moz-background-size: cover;
  -o-background-size: cover;
  background-size: cover;
  
}
.titulopag {
  font-size: 46px;
  color: white;
  text-align: center;
  margin-top: 10px;
 text-shadow: 1px 1px 1px rgba(255, 255, 255, 0.795);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-image: linear-gradient(45deg, #b1c6c9, #cbd2d6);
}

.Libre-Baskerville {
  font-family: "Libre Baskerville", serif;
}

h2 {
  margin-bottom: 5px;
  font-size: 26px;
  margin-top: 5px;
}
h3 {
  font-size: 40px;
  margin-top: 10px;
  margin: 10px;
}

li {
  font-size: 20px;
}
p {
  margin: 60px 0;
  font-size: 26px;
}

.justificado {
  text-align: justify;
  margin-left: 100px;
  margin-right: 100px;
}

.background-container-logo {
  background-image: url("/FundoW.png");
  background-size: cover;
  background-position: center;
  padding: 20px;
}

.columns {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  flex-wrap: wrap;
}
.column2 {
  width: calc(25% - 1rem);
  box-sizing: border-box;
}

.article-card {
  border: 1px solid #ccc;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 20px;
  cursor: pointer;
  text-decoration: none;
  color: inherit;
}

.article-image {
  width: 100%;
  height: auto;
  object-fit: cover;
}
.transparent-image {
  opacity: 0.8;
  transition: opacity 0.3s ease;
}

.transparent-image:hover {
  opacity: 1;
}

.hover-zoom:hover {
  transform: scale(1.1);
}

.article-title {
  text-align: center;
  padding: 10px;
  margin: 0;
  font-size: 18px;
  color: #333;
}

.tracking-widest {
  letter-spacing: 0.1em;
}

.font-semibold {
  font-weight: 600;
}

/* Estilos específicos para telas menores (dispositivos móveis) */
@media only screen and (max-width: 600px) {
  .Libre {
    font-size: 24px;
  }

  h3 {
    font-size: 32px;
  }

  p {
    font-size: 16px;
    margin: 15px 0;
  }

  h2 {
    font-size: 22px;
  }

  .justificado {
    margin-left: 10px;
    margin-right: 10px;
  }

  .column2 {
    width: 100%;
  }

  .article-card {
    margin-bottom: 10px;
  }

  .article-title {
    font-size: 16px;
  }
  .background-container {
  background-image: url("/bannerpub.png");
  background-position: center;
  color: #f7f7f7;
  padding: 50px;
  background-repeat: no-repeat;
  -webkit-background-size: cover;
  -moz-background-size: cover;
  -o-background-size: cover;
  background-size: cover;
  
}
  .titulopag {
    font-size: 26px;
  }
}
</style>

