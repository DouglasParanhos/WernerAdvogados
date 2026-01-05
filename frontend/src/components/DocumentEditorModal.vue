<template>
  <div v-if="show" class="modal-overlay" @click.self="close">
    <div class="modal-content document-editor-modal">
      <div class="modal-header">
        <h3>Editor de Documento</h3>
        <button @click="close" class="close-btn">&times;</button>
      </div>
      
      <div class="modal-body">
        <div v-if="loading" class="loading">
          Carregando conteúdo do documento...
        </div>
        
        <div v-if="error" class="error">
          {{ error }}
        </div>
        
        <div v-if="!loading && !error && contentLoaded" class="editor-container">
          <div class="editor-toolbar">
            <p class="info-text">
              Edite o conteúdo do documento abaixo. Os dados do cliente aparecem em <strong>negrito e maiúsculas</strong>.
            </p>
          </div>
          
          <div class="quill-wrapper">
            <QuillEditor
              ref="editorRef"
              v-model:content="editorContent"
              :options="editorOptions"
              contentType="delta"
              @ready="onEditorReady"
            />
          </div>
        </div>
      </div>
      
      <div class="modal-footer">
        <button @click="close" class="btn btn-secondary">Cancelar</button>
        <button 
          @click="generateDocument" 
          :disabled="generating || !contentLoaded"
          class="btn btn-primary"
        >
          <span v-if="generating" class="spinner"></span>
          <span v-else>Gerar Documento</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import { documentService } from '../services/documentService'

export default {
  name: 'DocumentEditorModal',
  components: {
    QuillEditor
  },
  props: {
    show: {
      type: Boolean,
      default: false
    },
    client: {
      type: Object,
      required: true
    },
    templateName: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      editorContent: null,
      contentLoaded: false,
      loading: false,
      error: null,
      generating: false,
      editorRef: null,
      editorOptions: {
        theme: 'snow',
        modules: {
          toolbar: [
            [{ 'header': [1, 2, 3, false] }],
            ['bold', 'italic', 'underline', 'strike'],
            [{ 'list': 'ordered'}, { 'list': 'bullet' }],
            [{ 'align': [] }],
            ['clean']
          ]
        },
        placeholder: 'Carregando conteúdo...'
      }
    }
  },
  watch: {
    show(newVal) {
      if (newVal && this.client?.id && this.templateName) {
        this.loadDocumentContent()
      } else if (!newVal) {
        this.resetState()
      }
    }
  },
  methods: {
    async loadDocumentContent() {
      if (!this.client?.id || !this.templateName) {
        this.error = 'Cliente ou template não informado'
        return
      }
      
      this.loading = true
      this.error = null
      this.contentLoaded = false
      
      try {
        const response = await documentService.getClientDocumentContent(
          this.client.id,
          this.templateName
        )
        
        // Converter o conteúdo para formato Quill Delta
        this.editorContent = this.convertToQuillDelta(response)
        this.contentLoaded = true
      } catch (err) {
        this.error = 'Erro ao carregar conteúdo: ' + 
                     (err.response?.data?.message || err.message)
        console.error(err)
      } finally {
        this.loading = false
      }
    },
    
    convertToQuillDelta(response) {
      // Converter ContentBlock[] para Quill Delta format
      const ops = []
      
      if (response.content && Array.isArray(response.content)) {
        response.content.forEach(block => {
          if (block.text) {
            const text = block.text
            const lines = text.split('\n')
            
            lines.forEach((line, index) => {
              // Adicionar linha (mesmo que vazia, para preservar quebras de linha)
              const op = {
                insert: line
              }
              
              // Aplicar formatação se for dado do cliente
              if (block.isClientData) {
                op.attributes = {
                  bold: true
                }
              }
              
              // Adicionar formatação adicional se existir
              if (block.formatting) {
                op.attributes = {
                  ...op.attributes,
                  ...block.formatting
                }
              }
              
              ops.push(op)
              
              // Adicionar quebra de linha após cada linha (exceto a última do bloco)
              if (index < lines.length - 1) {
                ops.push({ insert: '\n' })
              }
            })
          }
        })
      }
      
      return { ops }
    },
    
    onEditorReady(quill) {
      // Editor está pronto
      this.editorRef = quill
    },
    
    async generateDocument() {
      if (!this.client?.id || !this.templateName || !this.editorContent) {
        alert('Erro: Dados incompletos para gerar documento')
        return
      }
      
      this.generating = true
      
      try {
        await documentService.generateCustomClientDocument(
          this.client.id,
          this.templateName,
          this.editorContent
        )
        
        // Fechar modal após gerar documento
        this.$emit('close')
      } catch (err) {
        alert('Erro ao gerar documento: ' + 
              (err.response?.data?.message || err.message))
        console.error(err)
      } finally {
        this.generating = false
      }
    },
    
    close() {
      this.$emit('close')
    },
    
    resetState() {
      this.editorContent = null
      this.contentLoaded = false
      this.error = null
      this.generating = false
    }
  }
}
</script>

<style scoped>
.document-editor-modal {
  width: 90%;
  max-width: 900px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
}

.modal-body {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.editor-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.editor-toolbar {
  margin-bottom: 1rem;
}

.info-text {
  color: #666;
  font-size: 0.9rem;
  margin: 0;
}

.quill-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
}

.quill-wrapper :deep(.ql-container) {
  flex: 1;
  overflow-y: auto;
  font-size: 14px;
}

.quill-wrapper :deep(.ql-editor) {
  min-height: 300px;
  padding: 1rem;
}

.quill-wrapper :deep(.ql-toolbar) {
  border-top: none;
  border-left: none;
  border-right: none;
  border-bottom: 1px solid #ddd;
}

.spinner {
  display: inline-block;
  width: 14px;
  height: 14px;
  border: 2px solid #ffffff;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
  margin-right: 8px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>

