<template>
  <div>
    <!-- Conteúdo da Página -->
    <div class="container mx-auto p-4 min-h-full">
      <router-view />
    </div>

    <!-- Rodapé para mobile -->
    <footer class="footer-mobile mobile-footer bg-slate-800 text-white">
      <div class="container mx-auto flex flex-col items-center">
        <!-- Redes Sociais -->
        <div class="footer-section">
          <h4 class="text-xl font-bold mb-4">Redes Sociais</h4>
          <ul class="flex gap-4">
            <li>
              <a
                href="https://www.instagram.com/lizwerner.adv/"
                target="_blank"
                rel="noopener noreferrer"
              >
                <img
                  src="/icone_ig.png"
                  alt="Instagram"
                  class="h-8 w-auto mb-2"
                />
                <span></span>
              </a>
            </li>
            <li>
              <a
                href="https://www.facebook.com/LizWernerAdv/"
                target="_blank"
                rel="noopener noreferrer"
              >
                <img
                  src="/icone_fb.png"
                  alt="Facebook"
                  class="h-8 w-auto mb-2"
                />
                <span></span>
              </a>
            </li>
            <li>
              <a
                href="https://www.youtube.com/channel/UCgj5AqZpC2Qp7tM3hNGMrrw"
                target="_blank"
                rel="noopener noreferrer"
              >
                <img
                  src="/icone_yt.png"
                  alt="YouTube"
                  class="h-8 w-auto mb-8"
                />
                <span></span>
              </a>
            </li>
          </ul>
        </div>

        <!-- Contatos -->
        <div class="footer-section text-center">
          <h4 class="text-xl font-bold mb-4">Contatos</h4>
          <p class="mb-2">
            <img
              src="/icone_email.png"
              alt="E-mail"
              class="h-6 w-auto inline-block mr-2"
            />
            E-mail: lizwerner.adv@gmail.com
          </p>
          <p class="mb-2">
            <img
              src="/icone_wa.png"
              alt="WhatsApp"
              class="h-7 w-auto inline-block mr-2"
            />
            WhatsApp:
            <a
              href="https://wa.me/5521995264562"
              target="_blank"
              rel="noopener noreferrer"
            >
              (21) 99526-4562
            </a>
          </p>
        </div>
      </div>

      <!-- Logo + Werner Advogados -->
      <div class="text-center py-4 container mx-auto">
        <router-link
          to="/"
          class="lexend-deca title-with-imagefoot"
          style="text-align: -webkit-center"
        >
          <img src="/LogoW.png" alt="Logo" class="title-imagefoot" />
          <span
            class="Libre-Baskerville seu-texto-metalizado font-bold tracking-widest"
            >Werner Advogados</span
          >
        </router-link>
      </div>
    </footer>

    <!-- Rodapé para desktop -->
    <footer class="footer-desktop desktop-footer">
      <div class="container mx-auto flex justify-between">
        <!-- Redes Sociais -->
        <div class="footer-section">
          <h4 class="text-xl font-bold mb-4">Redes Sociais</h4>
          <ul class="flex items-center gap-4">
            <li>
              <a
                href="https://www.instagram.com/lizwerner.adv/"
                target="_blank"
                rel="noopener noreferrer"
              >
                <img
                  src="/icone_ig.png"
                  alt="Instagram"
                  class="h-8 w-auto mb-2"
                />
                <span>Instagram</span>
              </a>
            </li>
            <li>
              <a
                href="https://www.facebook.com/LizWernerAdv/"
                target="_blank"
                rel="noopener noreferrer"
              >
                <img
                  src="/icone_fb.png"
                  alt="Facebook"
                  class="h-8 w-auto mb-2"
                />
                <span>Facebook</span>
              </a>
            </li>
            <li>
              <a
                href="https://www.youtube.com/channel/UCgj5AqZpC2Qp7tM3hNGMrrw"
                target="_blank"
                rel="noopener noreferrer"
              >
                <img
                  src="/icone_yt.png"
                  alt="YouTube"
                  class="h-8 w-auto mb-2"
                />
                <span>YouTube</span>
              </a>
            </li>
          </ul>
        </div>

        <!-- Contatos -->
        <div class="footer-section">
          <h4 class="text-xl font-bold mb-6">Contatos</h4>
          <p class="flex items-center">
            <img
              src="/icone_email.png"
              alt="E-mail"
              class="h-6 w-auto mr-2"
            />
            E-mail: lizwerner.adv@gmail.com
          </p>
          <p class="flex items-center mt-1.5">
            <img
              src="/icone_wa.png"
              alt="WhatsApp"
              class="h-7 w-auto mr-2"
            />
            WhatsApp:
            <a
              href="https://wa.me/5521995264562"
              target="_blank"
              rel="noopener noreferrer"
            >
              (21) 99526-4562
            </a>
          </p>
        </div>

        <!-- Navegação -->
        <div class="footer-section">
          <h4 class="text-xl font-bold mb-4">Navegação</h4>
          <ul class="flex flex-col gap-2">
            <li>
              <router-link to="/" class="underline hover:font-bold">
                Início
              </router-link>
            </li>
            <li>
              <router-link to="/about" class="underline hover:font-bold"
                >Sobre o Escritório</router-link
              >
            </li>
            <li>
              <router-link to="/areas" class="underline hover:font-bold"
                >Áreas de Atuação</router-link
              >
            </li>
            <li>
              <router-link to="/privacidade" class="underline hover:font-bold"
                >Política de Privacidade</router-link
              >
            </li>
          </ul>
        </div>
      </div>

      <!-- Logo + Werner Advogados -->
      <div class="text-center py-4 container mx-auto">
        <router-link
          to="/"
          class="lexend-deca title-with-imagefoot"
          style="text-align: -webkit-center"
        >
          <img src="/LogoW.png" alt="Logo" class="title-imagefoot" />
          <span
            class="large-title Libre-Baskerville seu-texto-metalizado font-bold tracking-widest font-tamanho"
          >
            Werner Advogados
          </span>
        </router-link>
      </div>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'PublicLayout'
}
</script>

<style scoped>
.header {
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  background-color: white;
  position: absolute;
  width: 100%;
  top: 0;
}
.footer {
  background-color: #1e293b;
  color: white;
  padding: 2rem;
}
.router-link-exact-active {
  color: rgb(187, 181, 181);
}
.title-image {
  height: 2rem;
}

.title-imagefoot {
  height: 3rem;
}

.title-imageLogo {
  height: 5rem;
}
.font-tamanho {
  font-size: 26px;
}
.margemcima {
  margin-top: 5px;
}

/* Mobile header/footer */
.mobile-header {
  display: block;
}
.desktop-header {
  display: none;
}
.mobile-footer {
  display: block;
}
.desktop-footer {
  display: none;
}

/* Desktop header/footer */
@media only screen and (min-width: 768px) {
  .mobile-header {
    display: none;
  }
  .desktop-header {
    display: block;
  }
  .mobile-footer {
    display: none;
  }
  .desktop-footer {
    display: block;
    background-color: #1e293b;
    color: white;
    padding: 2rem;
  }

  .desktop-footer .footer-section {
    margin-bottom: 2rem;
  }

  .desktop-footer .footer-section h4 {
    text-align: left;
  }

  .desktop-footer .footer-section ul {
    justify-content: flex-start;
  }

  .header nav {
    flex-direction: row;
    align-items: center;
  }

  .header .title-with-image {
    display: flex;
    flex-direction: row;
    align-items: center;
    text-align: left;
  }

  .footer {
    padding: 2rem;
  }

  .footer-section {
    margin-bottom: 2rem;
  }

  .footer-section h4 {
    text-align: left;
  }

  .footer-section ul {
    justify-content: flex-start;
  }

  .title-image {
    margin-bottom: 0;
  }

  .title-imageLogo {
    height: 4rem;
  }

  .title-imagefoot {
    height: 8rem;
  }
}

.efeito-metalizado {
  position: relative;
  color: #d9e0e6;
  background-image: linear-gradient(45deg, #b1c6c9, #cbd2d6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.2);
}

.seu-texto-metalizado {
  position: relative;
  color: #d9e0e6;
  background-image: linear-gradient(45deg, #b1c6c9, #cbd2d6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.2);
}

.btn-mobile {
  background-color: #e6e6e6;
  padding: 0.5rem 1rem;
  border-radius: 0.375rem;
  font-size: 0.875rem;
  display: inline-block;
  color: #201f1f;
  position: relative;
}

.btn-mobile::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.1) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.1) 50%,
    rgba(255, 255, 255, 0.1) 75%,
    transparent 75%,
    transparent
  );
  background-size: 3px 3px;
  z-index: -1;
}
.titulo-mobile {
  font-size: 20px;
}

.Libre-Baskerville {
  font-family: "Libre Baskerville", serif;
}

.lexend-deca {
  font-family: "Lexend Deca", sans-serif;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
}

.flex {
  display: flex;
}

.flex-col {
  flex-direction: column;
}

.items-center {
  align-items: center;
}

.justify-between {
  justify-content: space-between;
}

.gap-4 {
  gap: 1rem;
}

.gap-2 {
  gap: 0.5rem;
}

.text-center {
  text-align: center;
}

.text-xl {
  font-size: 1.25rem;
}

.font-bold {
  font-weight: 700;
}

.mb-4 {
  margin-bottom: 1rem;
}

.mb-2 {
  margin-bottom: 0.5rem;
}

.mb-8 {
  margin-bottom: 2rem;
}

.mt-1\.5 {
  margin-top: 0.375rem;
}

.mr-2 {
  margin-right: 0.5rem;
}

.h-8 {
  height: 2rem;
}

.h-6 {
  height: 1.5rem;
}

.h-7 {
  height: 1.75rem;
}

.w-auto {
  width: auto;
}

.inline-block {
  display: inline-block;
}

.underline {
  text-decoration: underline;
}

.hover\:font-bold:hover {
  font-weight: 700;
}

.tracking-widest {
  letter-spacing: 0.1em;
}

.large-title {
  font-size: 1.5rem;
}

.title-with-image {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.title-with-imagefoot {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.min-h-full {
  min-height: 100%;
}

.p-4 {
  padding: 1rem;
}

.py-4 {
  padding-top: 1rem;
  padding-bottom: 1rem;
}

.bg-slate-800 {
  background-color: #1e293b;
}

.text-white {
  color: white;
}

.text-silver {
  color: silver;
}

.shadow-sm {
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

a {
  text-decoration: none;
  color: inherit;
}

a:hover {
  opacity: 0.8;
}
</style>

