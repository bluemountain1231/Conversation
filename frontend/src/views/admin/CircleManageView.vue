<template>
  <div>
    <h2 style="margin-bottom:16px">圈子管理</h2>
    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="名称" min-width="160" />
      <el-table-column prop="memberCount" label="成员" width="80" />
      <el-table-column prop="postCount" label="帖子" width="80" />
      <el-table-column prop="creatorId" label="创建者ID" width="90" />
      <el-table-column prop="createdAt" label="创建时间" width="170" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-popconfirm title="确定删除该圈子?" @confirm="delCircle(row)">
            <template #reference><el-button text type="danger" size="small">删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:center" layout="prev,pager,next" :total="total" :page-size="10" @current-change="p => { page = p - 1; load() }" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminCircles, deleteAdminCircle } from '../../api/admin'
import { ElMessage } from 'element-plus'

const list = ref([])
const loading = ref(false)
const page = ref(0)
const total = ref(0)

async function load() {
  loading.value = true
  try {
    const res = await getAdminCircles(page.value, 10)
    list.value = res.data.content || []
    total.value = res.data.totalElements || 0
  } catch {} finally { loading.value = false }
}

async function delCircle(row) {
  try { await deleteAdminCircle(row.id); list.value = list.value.filter(c => c.id !== row.id); ElMessage.success('已删除') } catch {}
}

onMounted(() => load())
</script>
