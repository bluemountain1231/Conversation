<template>
  <div class="create-circle-page">
    <el-card class="form-card" shadow="never">
      <h2>创建圈子</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top" style="margin-top:24px">
        <el-form-item label="圈子名称" prop="name">
          <el-input v-model="form.name" placeholder="给圈子起个名字" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="圈子介绍" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="介绍一下你的圈子" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="圈子头像（可选）">
          <el-upload :action="'/api/upload/image'" :headers="uploadHeaders" :show-file-list="false"
            :on-success="handleAvatarUpload" accept="image/*">
            <el-avatar :size="64" :src="form.avatar || defaultAvatar" shape="square" />
            <el-button size="small" style="margin-left:12px">上传头像</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <div class="form-actions">
            <el-button @click="$router.back()">取消</el-button>
            <el-button type="primary" @click="handleSubmit" :loading="submitting">创建圈子</el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { createCircle } from '../api/circle'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)
const defaultAvatar = 'https://api.dicebear.com/7.x/shapes/svg?seed=circle'

const form = reactive({ name: '', description: '', avatar: '' })
const rules = { name: [{ required: true, message: '请输入圈子名称', trigger: 'blur' }] }

const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token')}`
}))

function handleAvatarUpload(res) {
  if (res.code === 200) form.avatar = res.data.url
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    const res = await createCircle(form)
    ElMessage.success('创建成功')
    router.push(`/circle/${res.data.id}`)
  } catch {} finally { submitting.value = false }
}
</script>

<style scoped>
.create-circle-page { max-width: 600px; margin: 0 auto; }
.form-card { border-radius: 16px; padding: 8px; }
.form-card h2 { font-size: 22px; }
.form-actions { display: flex; justify-content: flex-end; gap: 12px; width: 100%; }
</style>
