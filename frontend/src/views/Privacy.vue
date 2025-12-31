<template>
    <div class="background-container-logo text-center Libre-Baskerville">
      <h2>Política de Privacidade</h2>
  
      <h3>Introdução</h3>
      <p>
        Este documento foi criado com o objetivo de informar você, visitante da página, de forma objetiva e transparente, sobre a segurança que temos com os seus dados, e de como prezamos por essa segurança.
      </p>
  
      <h3>Dados Tratados</h3>
      <p>
        Quando você entrar no nosso site não coletamos nenhum tipo de dado, apenas temos o botão para entrar em contato conosco via Whatsapp.
      </p>
  
      <h3>Finalidade de tratamento</h3>
      <p>
        Ao entrar em contato conosco via Whatsapp, salvamos o seu contato para entrar em contato com você, nada mais.
      </p>
  
      <h3>Compartilhamento de dados</h3>
      <p>
        Não compartilhamos seu contato salvo em nosso Whatsapp com nenhum terceiro.
      </p>
  
      <h3>Cookies</h3>
      <p>
        Sempre que você acessar nosso site, um cookie temporário será criado para determinar se seu navegador aceita cookies. Ele não contém nenhum dado pessoal e será descartado quando você fechar o navegador. Quando você acessa nosso site, haverá uma mensagem sobre nossos cookies onde você pode aceitar ou recusar nossos termos.
      </p>
  
      <h3>Por quanto tempo mantemos os seus dados</h3>
      <p>
        Após entrar em contato conosco via Whatsapp clicando no botão, o seu contato é salvo, conservado e protegido por tempo indeterminado.
      </p>
  
      <h3>Quais os seus direitos sobre seus dados</h3>
      <p>
        Se você tiver clicado no botão para entrar em contato conosco, nós iremos salvar o contato, porém você pode solicitar para que não seja salvo.
      </p>
  
      <h3>Alteração da Política de Privacidade</h3>
      <p>
        Esteja ciente de que a Política de Privacidade pode ser alterada a qualquer momento, mas que se houver qualquer tipo de alteração você receberá uma notificação sobre isso. Data da última atualização: 21/12/2023.
      </p>
    </div>
  </template>
    <style scoped>
    h2, h3 {
      margin-bottom: 20px;
      font-size: 36px;
    }
    p {
      margin: 20px 0;
      text-align: justify;
    }

    .imaginterniveis {
  height: 200px;
  width: auto;
    }

    .background-container-logo {
    background-image: url('/FundoW.png');
    background-size: cover;
    background-position: center;
    padding: 20px;
  }
  .espacoantes{
margin-top: 80px;

  }

  .espacoapos{
    margin-bottom: 80px;
  }
  .espacoentre{
    margin-top: 30px;
    margin-bottom: 30px;
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
    .mobile-desaparece{
      display: none;
    }
    .desktop-desaparece {
    display: flex;
  }
  .tamanho-imagem
  {
    height: 30px;
    width: auto;
  }
    }
  </style>

<script>
export default {
  name: 'Privacy'
}
</script>

