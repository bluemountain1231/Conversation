<template>
  <div>
    <h2 style="margin-bottom:16px">操作日志</h2>
    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="adminName" label="管理员" width="120" />
      <el-table-column prop="action" label="操作" width="120" />
      <el-table-column prop="targetType" label="目标类型" width="100" />
      <el-table-column prop="targetId" label="目标ID" width="80" />
      <el-table-column prop="detail" label="详情" min-width="200" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="时间" width="170" />
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:center" layout="prev,pager,next" :total="total" :page-size="20" @current-change="p => { page = p - 1; load() }" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminLogs } from '../../api/admin'

const list = ref([])
const loading = ref(false)
const page = ref(0)
const total = ref(0)

async function load() {
  loading.value = true
  try {
    const res = await getAdminLogs(page.value, 20)
    list.value = res.data.content || []
    total.value = res.data.totalElements || 0
  } catch {} finally { loading.value = false }
}

onMounted(() => load())
</script>
