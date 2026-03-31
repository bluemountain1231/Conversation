<template>
  <div class="create-post-page">
    <el-card class="editor-card" shadow="never">
      <h2 style="margin-bottom: 24px">发布帖子</h2>

      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="给你的帖子起个标题" maxlength="100" show-word-limit size="large" />
        </el-form-item>

        <el-form-item label="发布到圈子（可选）">
          <el-select v-model="form.circleId" placeholder="选择圈子" clearable style="width: 100%">
            <el-option v-for="c in myCircles" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <div style="border: 1px solid #dcdfe6; border-radius: 4px; width: 100%; z-index: 10;">
            <Toolbar
              :editor="editorRef"
              :defaultConfig="toolbarConfig"
              style="border-bottom: 1px solid #e8e8e8"
            />
            <Editor
              v-model="form.content"
              :defaultConfig="editorConfig"
              style="height: 320px; overflow-y: hidden"
              @onCreated="handleCreated"
            />
          </div>
        </el-form-item>

        <el-form-item label="图片（可选）">
          <el-upload
            v-model:file-list="fileList"
            action="/api/upload/image"
            list-type="picture-card"
            :headers="uploadHeaders"
            :on-success="handleUploadSuccess"
            :on-remove="handleUploadRemove"
            :limit="9"
            accept="image/*"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <div class="form-actions">
            <el-button @click="$router.back()">取消</el-button>
            <el-button type="primary" size="large" @click="handleSubmit" :loading="submitting">
              发布帖子
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, shallowRef, onBeforeUnmount, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { createPost } from '../api/post'
import { getMyCircles } from '../api/circle'
import { ElMessage } from 'element-plus'
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const submitting = ref(false)
const fileList = ref([])
const uploadedImages = ref([])

const editorRef = shallowRef()
const toolbarConfig = {}
const editorConfig = {
  placeholder: '分享你的想法...',
  MENU_CONF: {
    uploadImage: {
      server: '/api/upload/image',
      fieldName: 'file',
      maxFileSize: 10 * 1024 * 1024,
      allowedFileTypes: ['image/*'],
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      },
      customInsert(res, insertFn) {
        if (res.code === 200) {
          insertFn(res.data.url, '', '')
        }
      }
    }
  }
}

function handleCreated(editor) {
  editorRef.value = editor
}

onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor) editor.destroy()
})

const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token')}`
}))

const myCircles = ref([])
const form = reactive({ title: '', content: '', circleId: route.query.circleId ? Number(route.query.circleId) : null })

onMounted(async () => {
  try { myCircles.value = (await getMyCircles()).data || [] } catch {}
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

function handleUploadSuccess(response) {
  if (response.code === 200) {
    uploadedImages.value.push(response.data.url)
  }
}

function handleUploadRemove(file) {
  const url = file.response?.data?.url
  if (url) {
    uploadedImages.value = uploadedImages.value.filter(u => u !== url)
  }
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const data = {
      title: form.title,
      content: form.content,
      images: uploadedImages.value,
      circleId: form.circleId || null
    }
    const res = await createPost(data)
    ElMessage.success('发布成功')
    router.push(`/post/${res.data.id}`)
  } catch {} finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.create-post-page {
  max-width: 780px;
  margin: 0 auto;
}

.editor-card {
  border-radius: 16px;
  padding: 8px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  width: 100%;
}
</style>
