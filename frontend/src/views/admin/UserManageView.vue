<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
      <h2>用户管理</h2>
      <div style="display:flex;gap:8px">
        <el-input v-model="keyword" placeholder="搜索用户..." clearable @keyup.enter="load" style="width:220px" />
        <el-button @click="exportCSV">导出CSV</el-button>
      </div>
    </div>
    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="用户" min-width="160">
        <template #default="{ row }"><el-avatar :size="28" :src="row.avatar" style="margin-right:8px" />{{ row.username }}</template>
      </el-table-column>
      <el-table-column prop="email" label="邮箱" min-width="180" />
      <el-table-column label="角色" width="120">
        <template #default="{ row }">
          <el-select v-model="row.role" size="small" @change="changeRole(row)">
            <el-option label="用户" value="USER" /><el-option label="管理员" value="ADMIN" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }"><el-tag :type="row.banned ? 'danger' : 'success'" size="small">{{ row.banned ? '封禁' : '正常' }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="createdAt" label="注册时间" width="170" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button text :type="row.banned ? 'success' : 'danger'" size="small" @click="toggleBan(row)">{{ row.banned ? '解封' : '封禁' }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:center" layout="prev,pager,next" :total="total" :page-size="10" @current-change="p => { page = p - 1; load() }" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminUsers, updateUserRole, toggleBanUser } from '../../api/admin'
import { ElMessage } from 'element-plus'

const list = ref([])
const loading = ref(false)
const keyword = ref('')
const page = ref(0)
const total = ref(0)

async function load() {
  loading.value = true
  try {
    const res = await getAdminUsers(page.value, 10, keyword.value || undefined)
    list.value = res.data.content || []
    total.value = res.data.totalElements || 0
  } catch {} finally { loading.value = false }
}

async function changeRole(row) {
  try { await updateUserRole(row.id, row.role); ElMessage.success('角色已更新') } catch {}
}

async function toggleBan(row) {
  try { await toggleBanUser(row.id); row.banned = !row.banned; ElMessage.success('操作成功') } catch {}
}

function exportCSV() {
  window.open('/api/admin/export/users', '_blank')
}

onMounted(() => load())
</script>
