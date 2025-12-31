<template>
  <div class="article-header">
        <h2 class="article-title">Como saber se estou recebendo o Piso?</h2><h2 class="article-title"> </h2>
        <div>
    
      <p class="article-date">Niterói, 02 de fevereiro de 2024</p>
    </div>

    <div class="article-content justificado">
      <p>
        Para entender se você está recebendo o Piso Salarial, precisamos simplificar um pouco sobre o assunto e saber onde encontrar essa informação.
      </p> <br/>

      <p>
        A Constituição da República diz que o salário dos servidores públicos deve ser estabelecido por lei, 
        levando em consideração a natureza do trabalho e a complexidade do cargo. Além disso, ela determina 
        que os profissionais da educação pública têm direito a um Piso Salarial, conforme uma Lei Federal.
      </p> <br/>

      <p>
        Essa Lei Federal é a Lei nº 11.738/2008, que fixa o Piso Nacional dos profissionais do magistério público. 
        Basicamente, ela diz que o salário inicial não pode ser inferior a um determinado valor. Essa lei foi 
        considerada constitucional pelo Supremo Tribunal Federal - STF.
      </p> <br/>

      <p>
        De acordo com essa lei, a União determinará o valor do Piso Nacional, sendo este determinado a cada ano.
        O Piso para uma jornada de 40 horas semanais, previsto em 2024, é de R$ 4.580,57. Isso significa que o valor mínimo 
        por hora seria R$ 114,51. Então, se o profissional trabalha 16 horas, o mínimo seria R$ 1.832,16, e assim por diante.
      </p> <br/>

      <div class="text-center flex-col-reverse flex-wrap content-center">
    <img
      src="/valorhoraPiso.png"
      alt="Piso"
      class="mr-4 image-resol image_center inline"
    />
  </div> <br/>

      <p>
        No entanto, no Estado do Rio de Janeiro, existe uma Lei Estadual que trata do Plano de Cargos e Salários. 
        Essa lei determina que o salário base deve ter um intervalo de 12% entre as referências (1 a 9). 
        Ou seja, a proporção mencionada acima precisa ser ajustada conforme a Lei Estadual.
      </p> <br/>

      <p>
        Atualmente, a maior discussão está na aplicação da Lei Estadual. O Estado argumenta que as duas leis não podem ser aplicadas simultaneamente, enquanto defendemos que são complementares. Essa disputa pelos valores está em andamento, com sucesso nas primeiras instâncias e aguardando o julgamento do STF.
      </p> <br/>
      <div class="text-center flex-col-reverse flex-wrap content-center">
    <img
      src="/questHora.png"
      alt="Piso"
      class="mr-4 image-resol image_center inline"
    />
  </div> <br/>
      <p>
        Se você acha que está recebendo menos do que o Piso, a melhor opção é contratar um advogado especializado. Ele poderá analisar sua situação e fornecer orientações específicas.
      </p>
    </div>


    

      
    <div>
    <p class="signature">Liz Werner</p> 

 
</div>
<div class="article-content justificado">
        <h2 class="font-semibold">Tem interesse ou ficou alguma dúvida? </h2> <br/>

         <!-- Adicionando os botões -->
      <div class="button-container">
        <router-link to="/piso" class="custom-button">
          Saiba mais sobre a ação
        </router-link>

        <a href="https://www.youtube.com/watch?v=y1X7Ge0SRwM" target="_blank" class="custom-button">
         Veja o vídeo do Tema
        </a>

        <a href="https://api.whatsapp.com/send/?phone=5521995264562&text&type=phone_number&app_absent=0" target="_blank" class="custom-button">
          Fale conosco
        </a>
        
      </div>
      </div><br/><br/><br/>
    </div>
  </template>
  
 
  
  <style scoped>
  .article-header {
    text-align: center;
    margin-bottom: 20px;
  }
  
  .article-title {
    font-size: 28px;
    margin: 0;
  }
  
  .article-date {
    margin: 5px 0;
    color: #777;
    text-align: right;
    margin-right: 100px;
  }
  .signature {
    text-align: right;
    margin-top: 20px;
    font-weight: bold;
    margin-right: 100px;
  }
  
  .article-content {
    font-size: 18px;
    line-height: 1.6;
  }
  .button-container {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.custom-button {
  background-color: #ccc;
  padding: 10px 15px;
  border-radius: 5px;
  text-decoration: none;
  color: #333;
  cursor: pointer;
  transition: background-color 0.3s ease, border 0.3s ease, color 0.3s ease;
  border: 2px solid #ccc;
}

.custom-button:hover {
  background-color: #999;
  border-color: #999;
  color: #fff;

}
.font-semibold {
  font-weight: bold;
}

.justificado {
  text-align: justify;
  margin-left: 100px;
  margin-right: 100px;
}
.font-italico{
  font-style: italic;
}

.text-center {
  text-align: center;
}

.flex-col-reverse {
  flex-direction: column-reverse;
}

.flex-wrap {
  flex-wrap: wrap;
}

.content-center {
  align-content: center;
}

.mr-4 {
  margin-right: 1rem;
}

.image-resol {
  max-width: 100%;
  height: auto;
}

.image_center {
  display: block;
  margin: 0 auto;
}

.inline {
  display: inline;
}

  </style>

<script>
export default {
  name: 'Piso1'
}
</script>

