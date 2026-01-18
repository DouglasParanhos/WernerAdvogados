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
              Edite o conteúdo do documento abaixo. Os dados {{ entityType === 'process' ? 'do processo' : 'do cliente' }} aparecem em <strong>negrito e maiúsculas</strong>.
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
      required: false
    },
    process: {
      type: Object,
      required: false
    },
    templateName: {
      type: String,
      required: true
    }
  },
  computed: {
    entityType() {
      return this.process ? 'process' : 'client'
    },
    entityId() {
      return this.process ? this.process.id : (this.client ? this.client.id : null)
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
      if (newVal && this.entityId && this.templateName) {
        this.loadDocumentContent()
      } else if (!newVal) {
        this.resetState()
      }
    }
  },
  methods: {
    async loadDocumentContent() {
      if (!this.entityId || !this.templateName) {
        this.error = `${this.entityType === 'process' ? 'Processo' : 'Cliente'} ou template não informado`
        return
      }
      
      this.loading = true
      this.error = null
      this.contentLoaded = false
      
      try {
        let response
        if (this.entityType === 'process') {
          response = await documentService.getProcessDocumentContent(
            this.process.id,
            this.templateName
          )
        } else {
          response = await documentService.getClientDocumentContent(
            this.client.id,
            this.templateName
          )
        }
        
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
            
            // Preservar marcadores de tabela como texto especial para processamento no backend
            // Eles serão processados e removidos durante a reconstrução do documento
            if (text === '[TABLE_START]' || text === '[TABLE_END]' || text === '[CELL_SEP]' || text === '[ROW_END]') {
              // Incluir marcador como operação especial para processamento no backend
              ops.push({ insert: text })
              return
            }
            
            // Preparar atributos de formatação
            const attributes = {}
            
            // Aplicar formatação se for dado do cliente/processo
            if (block.isClientData) {
              attributes.bold = true
            }
            
            // Adicionar formatação adicional se existir
            if (block.formatting) {
              // Converter valores booleanos do formato do backend
              if (block.formatting.bold === true) attributes.bold = true
              if (block.formatting.italic === true) attributes.italic = true
              if (block.formatting.underline === true) attributes.underline = true
            }
            
            // Processar texto preservando quebras de linha
            if (text.includes('\n')) {
              const lines = text.split('\n')
              lines.forEach((line, index) => {
                const op = {
                  insert: line
                }
                
                // Adicionar atributos apenas se houver formatação
                if (Object.keys(attributes).length > 0) {
                  op.attributes = { ...attributes }
                }
                
                ops.push(op)
                
                // Adicionar quebra de linha após cada linha (exceto a última se for vazia)
                if (index < lines.length - 1 || (index === lines.length - 1 && line === '' && lines.length > 1)) {
                  ops.push({ insert: '\n' })
                }
              })
            } else {
              // Texto sem quebras de linha
              const op = {
                insert: text
              }
              
              // Adicionar atributos apenas se houver formatação
              if (Object.keys(attributes).length > 0) {
                op.attributes = { ...attributes }
              }
              
              ops.push(op)
            }
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
      if (!this.entityId || !this.templateName || !this.editorContent) {
        alert('Erro: Dados incompletos para gerar documento')
        return
      }
      
      this.generating = true
      
      try {
        if (this.entityType === 'process') {
          await documentService.generateCustomProcessDocument(
            this.process.id,
            this.templateName,
            this.editorContent
          )
        } else {
          await documentService.generateCustomClientDocument(
            this.client.id,
            this.templateName,
            this.editorContent
          )
        }
        
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

