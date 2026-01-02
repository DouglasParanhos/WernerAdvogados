<template>
  <div v-if="show" class="modal-overlay" @click.self="close">
    <div class="modal-content">
      <div class="modal-header">
        <h3>Gerar Documento do Cliente</h3>
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
          Nenhum documento disponível para este cliente.
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
  name: 'ClientDocumentGeneratorModal',
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
      templates: [],
      loading: false,
      error: null,
      generating: null
    }
  },
  watch: {
    show(newVal) {
      if (newVal && this.client?.id) {
        this.loadTemplates()
      }
    }
  },
  methods: {
    async loadTemplates() {
      if (!this.client?.id) {
        this.error = 'Cliente não informado'
        return
      }
      
      this.loading = true
      this.error = null
      
      try {
        this.templates = await documentService.getClientTemplates(this.client.id)
      } catch (err) {
        this.error = 'Erro ao carregar documentos disponíveis: ' + 
                     (err.response?.data?.message || err.message)
        console.error(err)
      } finally {
        this.loading = false
      }
    },
    
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
/* Estilos específicos do componente ClientDocumentGeneratorModal */
.modal-content {
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
}
</style>

