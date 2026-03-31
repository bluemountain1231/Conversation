<template>
  <div>
    <h2 style="margin-bottom:24px">数据总览</h2>
    <el-row :gutter="16" class="stat-cards">
      <el-col :span="6" v-for="(s, i) in statCards" :key="i">
        <el-card shadow="hover"><el-statistic :title="s.title" :value="s.value" /></el-card>
      </el-col>
    </el-row>
    <el-row :gutter="16" style="margin-top:24px">
      <el-col :span="12">
        <el-card shadow="never"><h4 style="margin-bottom:12px">用户注册趋势</h4><div ref="userChartRef" style="height:300px"></div></el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never"><h4 style="margin-bottom:12px">帖子发布趋势</h4><div ref="postChartRef" style="height:300px"></div></el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getStatsOverview, getStatsTrends } from '../../api/admin'

const userChartRef = ref(null)
const postChartRef = ref(null)
const statCards = ref([
  { title: '总用户数', value: 0 },
  { title: '总帖子数', value: 0 },
  { title: '总圈子数', value: 0 },
  { title: '今日新帖', value: 0 }
])

function renderChart(el, data, color) {
  const chart = echarts.init(el)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: data.map(d => d.date) },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{ data: data.map(d => d.count), type: 'line', smooth: true, areaStyle: { opacity: 0.15 }, itemStyle: { color } }],
    grid: { left: 40, right: 20, top: 20, bottom: 30 }
  })
}

onMounted(async () => {
  try {
    const ov = await getStatsOverview()
    statCards.value[0].value = ov.data.totalUsers || 0
    statCards.value[1].value = ov.data.totalPosts || 0
    statCards.value[2].value = ov.data.totalCircles || 0
    statCards.value[3].value = ov.data.todayPosts || 0
  } catch {}
  try {
    const tr = await getStatsTrends()
    if (userChartRef.value) renderChart(userChartRef.value, tr.data.userTrend || [], '#409eff')
    if (postChartRef.value) renderChart(postChartRef.value, tr.data.postTrend || [], '#67c23a')
  } catch {}
})
</script>

<style scoped>
.stat-cards .el-card { border-radius: 10px; }
</style>
