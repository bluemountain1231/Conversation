<template>
  <div>
    <h2 style="margin-bottom:16px">帖子管理</h2>
    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
      <el-table-column prop="userId" label="作者ID" width="80" />
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="row.status === 'APPROVED' ? 'success' : row.status === 'REJECTED' ? 'danger' : 'warning'" size="small">
            {{ { APPROVED: '已通过', REJECTED: '已拒绝', PENDING: '待审核' }[row.status] || row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="likeCount" label="点赞" width="70" />
      <el-table-column prop="commentCount" label="评论" width="70" />
      <el-table-column prop="createdAt" label="发布时间" width="170" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button text type="success" size="small" @click="setStatus(row, 'APPROVED')">通过</el-button>
          <el-button text type="danger" size="small" @click="setStatus(row, 'REJECTED')">拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:center" layout="prev,pager,next" :total="total" :page-size="10" @current-change="p => { page = p - 1; load() }" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminPosts, updatePostStatus } from '../../api/admin'
import { ElMessage } from 'element-plus'

const list = ref([])
const loading = ref(false)
const page = ref(0)
const total = ref(0)

async function load() {
  loading.value = true
  try {
    const res = await getAdminPosts(page.value, 10)
    list.value = res.data.content || []
    total.value = res.data.totalElements || 0
  } catch {} finally { loading.value = false }
}

async function setStatus(row, status) {
  try { await updatePostStatus(row.id, status); row.status = status; ElMessage.success('状态已更新') } catch {}
}

onMounted(() => load())
</script>
