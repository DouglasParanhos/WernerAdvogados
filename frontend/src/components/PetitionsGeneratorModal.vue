<template>
  <div v-if="show" class="modal-overlay" @click.self="close">
    <div class="modal-content">
      <div class="modal-header">
        <h3>Gerar Petições Iniciais</h3>
        <button @click="close" class="close-btn">&times;</button>
      </div>
      
      <div class="modal-body">
        <div v-if="loading" class="loading">
          Carregando petições disponíveis...
        </div>
        
        <div v-if="error" class="error">
          {{ error }}
        </div>
        
        <div v-if="!loading && !error" class="templates-list">
          <p class="info-text">
            Selecione uma petição inicial para gerar:
          </p>
          <div class="template-items">
            <button
              @click="generateDocument('iniciais/peticao_inicial_nova_escola.docx')"
              :disabled="generating"
              class="template-btn"
            >
              <span v-if="generating === 'iniciais/peticao_inicial_nova_escola.docx'" class="spinner"></span>
              <span v-else>Petição Inicial - Nova Escola</span>
            </button>
            <button
              @click="generateDocument('iniciais/peticao_inicial_piso.docx')"
              :disabled="generating"
              class="template-btn"
            >
              <span v-if="generating === 'iniciais/peticao_inicial_piso.docx'" class="spinner"></span>
              <span v-else>Petição Inicial - Piso</span>
            </button>
          </div>
        </div>
      </div>
      
      <div class="modal-footer">
        <button @click="close" class="btn btn-secondary">Fechar</button>
      </div>
    </div>
  </div>
</template>

<script>
import { documentService } from '../services/documentService'

export default {
  name: 'PetitionsGeneratorModal',
  props: {
    show: {
      type: Boolean,
      default: false
    },
    client: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      loading: false,
      error: null,
      generating: null
    }
  },
  methods: {
    async generateDocument(templatePath) {
      if (!this.client?.id) {
        alert('Erro: Cliente não informado')
        return
      }
      
      this.generating = templatePath
      
      try {
        await documentService.generateClientDocument(this.client.id, templatePath)
        // Fechar modal após gerar documento
        this.$emit('close')
      } catch (err) {
        alert('Erro ao gerar petição: ' + 
              (err.response?.data?.message || err.message))
        console.error(err)
      } finally {
        this.generating = null
      }
    },
    close() {
      this.$emit('close')
      this.error = null
      this.generating = null
    }
  }
}
</script>

<style scoped>
/* Estilos de modal inline (seguindo padrão master) */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  padding: 2rem;
  max-width: 600px;
  width: 90%;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 1001;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #dee2e6;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.5rem;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 2rem;
  cursor: pointer;
  color: #6c757d;
  line-height: 1;
  padding: 0;
  width: 2rem;
  height: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s;
}

.close-btn:hover {
  color: #333;
}

.modal-body {
  padding: 1.5rem;
  overflow-y: auto;
  flex: 1;
}

.modal-footer {
  padding: 1rem 1.5rem;
  border-top: 1px solid #dee2e6;
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
}

/* Estilos específicos do componente PetitionsGeneratorModal */

.template-items {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.template-btn {
  padding: 0.75rem 1rem;
  background: #003d7a;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  text-align: left;
  font-size: 1rem;
  transition: background-color 0.2s;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.template-btn:hover:not(:disabled) {
  background: #002d5c;
}

.template-btn:disabled {
  background: #6c757d;
  cursor: not-allowed;
}

.spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>

