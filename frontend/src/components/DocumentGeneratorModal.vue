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
            Selecione um documento e escolha como deseja gerá-lo:
          </p>
          <div class="template-items">
            <div
              v-for="template in templates"
              :key="template.fileName"
              class="template-item"
            >
              <div class="template-name">{{ template.name }}</div>
              <div class="template-actions">
                <button
                  @click="generateDocument(template.relativePath || template.fileName)"
                  :disabled="generating"
                  class="template-btn btn-generate"
                  title="Gerar documento imediatamente"
                >
                  <span v-if="generating === (template.relativePath || template.fileName)" class="spinner"></span>
                  <span v-else>Gerar Agora</span>
                </button>
                <button
                  @click="openEditor(template.relativePath || template.fileName)"
                  :disabled="generating"
                  class="template-btn btn-edit"
                  title="Editar documento antes de gerar"
                >
                  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" width="16" height="16">
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  <span>Editar</span>
                </button>
              </div>
            </div>
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
      
      // Extrair apenas o nome do arquivo se for um caminho relativo
      let templateName = templatePath
      if (templatePath && templatePath.includes('/')) {
        templateName = templatePath.substring(templatePath.lastIndexOf('/') + 1)
      } else if (templatePath && templatePath.includes('\\')) {
        templateName = templatePath.substring(templatePath.lastIndexOf('\\') + 1)
      }
      
      this.generating = templatePath
      
      try {
        await documentService.generateDocument(this.process.id, templateName)
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
    
    openEditor(templatePath) {
      // Extrair apenas o nome do arquivo se for um caminho relativo
      let templateName = templatePath
      if (templatePath && templatePath.includes('/')) {
        templateName = templatePath.substring(templatePath.lastIndexOf('/') + 1)
      } else if (templatePath && templatePath.includes('\\')) {
        templateName = templatePath.substring(templatePath.lastIndexOf('\\') + 1)
      }
      
      // Emitir evento para abrir o editor com o nome do arquivo
      this.$emit('open-editor', templateName)
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
/* Estilos específicos do componente DocumentGeneratorModal */
.modal-content {
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
}

.template-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-bottom: 0.75rem;
}

.template-name {
  font-weight: 500;
  color: #333;
  margin-bottom: 0.25rem;
}

.template-actions {
  display: flex;
  gap: 0.5rem;
}

.template-btn {
  flex: 1;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  transition: all 0.2s;
}

.btn-generate {
  background-color: #4CAF50;
  color: white;
}

.btn-generate:hover:not(:disabled) {
  background-color: #45a049;
}

.btn-edit {
  background-color: #2196F3;
  color: white;
}

.btn-edit:hover:not(:disabled) {
  background-color: #0b7dda;
}

.template-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.spinner {
  display: inline-block;
  width: 14px;
  height: 14px;
  border: 2px solid #ffffff;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>

