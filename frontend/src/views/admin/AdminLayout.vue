<template>
  <div class="admin-layout">
    <aside class="admin-sidebar">
      <div class="admin-logo">管理后台</div>
      <el-menu :default-active="route.path" router class="admin-menu">
        <el-menu-item index="/admin"><el-icon><DataAnalysis /></el-icon>仪表盘</el-menu-item>
        <el-menu-item index="/admin/users"><el-icon><User /></el-icon>用户管理</el-menu-item>
        <el-menu-item index="/admin/posts"><el-icon><Document /></el-icon>帖子管理</el-menu-item>
        <el-menu-item index="/admin/circles"><el-icon><Compass /></el-icon>圈子管理</el-menu-item>
        <el-menu-item index="/admin/logs"><el-icon><List /></el-icon>操作日志</el-menu-item>
      </el-menu>
      <div class="admin-back">
        <el-button text @click="$router.push('/')"><el-icon><Back /></el-icon>返回前台</el-button>
      </div>
    </aside>
    <main class="admin-main">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
const route = useRoute()
</script>

<style scoped>
.admin-layout { display: flex; min-height: 100vh; background: #f0f2f5; }
.admin-sidebar { width: 220px; background: #001529; display: flex; flex-direction: column; flex-shrink: 0; }
.admin-logo { color: #fff; font-size: 18px; font-weight: 700; padding: 20px; text-align: center; border-bottom: 1px solid #0d2137; }
.admin-menu { border: none; background: transparent; flex: 1; }
.admin-menu .el-menu-item { color: #ffffffb3; }
.admin-menu .el-menu-item:hover, .admin-menu .el-menu-item.is-active { color: #fff; background: #1890ff; }
.admin-back { padding: 16px; }
.admin-back .el-button { color: #ffffffb3; }
.admin-main { flex: 1; padding: 24px; overflow-y: auto; max-height: 100vh; }
</style>
