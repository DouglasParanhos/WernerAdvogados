<template>
  <div v-if="show" class="modal-overlay" @click.self="close">
    <div class="modal-content">
      <div class="modal-header">
        <h3>Gerar Documento</h3>
        <button @click="close" class="close-btn">&times;</button>
      </div>
      
      <div class="modal-body">
        <div v-if="loading" class="loading">
          Carregando documentos disponíveis...
        </div>
        
        <div v-if="error" class="error">
          {{ error }}
        </div>
        
        <div v-if="!loading && !error && templates.length > 0" class="templates-list">
          <p class="info-text">
            Selecione um documento para gerar:
          </p>
          <div class="template-items">
            <button
              v-for="template in templates"
              :key="template.fileName"
              @click="generateDocument(template.relativePath || template.fileName)"
              :disabled="generating"
              class="template-btn"
            >
              <span v-if="generating === (template.relativePath || template.fileName)" class="spinner"></span>
              <span v-else>{{ template.name }}</span>
            </button>
          </div>
        </div>
        
        <div v-if="!loading && !error && templates.length === 0" class="empty-state">
          Nenhum documento disponível para este tipo de processo.
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
  name: 'DocumentGeneratorModal',
  props: {
    show: {
      type: Boolean,
      default: false
    },
    process: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      templates: [],
      loading: false,
      error: null,
      generating: null
    }
  },
  watch: {
    show(newVal) {
      if (newVal && this.process?.id) {
        this.loadTemplates()
      }
    }
  },
  methods: {
    async loadTemplates() {
      if (!this.process?.id) {
        this.error = 'Processo não informado'
        return
      }
      
      this.loading = true
      this.error = null
      
      try {
        this.templates = await documentService.getTemplates(this.process.id)
      } catch (err) {
        this.error = 'Erro ao carregar documentos disponíveis: ' + 
                     (err.response?.data?.message || err.message)
        console.error(err)
      } finally {
        this.loading = false
      }
    },
    
    async generateDocument(templatePath) {
      if (!this.process?.id) {
        alert('Erro: Processo não informado')
        return
      }
      
      this.generating = templatePath
      
      try {
        await documentService.generateDocument(this.process.id, templatePath)
        // Fechar modal após gerar documento
        this.$emit('close')
      } catch (err) {
        alert('Erro ao gerar documento: ' + 
              (err.response?.data?.message || err.message))
        console.error(err)
      } finally {
        this.generating = null
      }
    },
    
    close() {
      this.$emit('close')
      // Limpar estado ao fechar
      this.templates = []
      this.error = null
      this.generating = null
    }
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
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
}

.loading, .error {
  text-align: center;
  padding: 2rem;
}

.error {
  color: #dc3545;
}

.info-text {
  margin-bottom: 1rem;
  color: #6c757d;
  font-size: 0.9rem;
}

.templates-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.template-items {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

/* Estilos de template-btn importados de styles/buttons.css */

.spinner {
  width: 1rem;
  height: 1rem;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.empty-state {
  text-align: center;
  padding: 2rem;
  color: #6c757d;
}

/* Estilos de botões importados de styles/buttons.css */

.btn {
  padding: 0.5rem 1rem;
}
</style>

