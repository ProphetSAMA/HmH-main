<template>
  <el-container>
    <el-main>
      <el-row :gutter="20">
        <!-- 统计卡片 -->
        <el-col :span="6" v-for="(item, index) in statsCards" :key="index">
          <el-card class="stats-card" :body-style="{ padding: '20px' }">
            <div class="card-content">
              <el-icon class="card-icon" :class="item.color">
                <component :is="item.icon" />
              </el-icon>
              <div class="card-info">
                <div class="card-title">{{ item.title }}</div>
                <div class="card-value">{{ item.value }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="chart-row">
        <!-- 报销金额趋势图 -->
        <el-col :span="16">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>报销金额趋势</span>
                <el-radio-group v-model="timeRange" size="small">
                  <el-radio-button label="week">本周</el-radio-button>
                  <el-radio-button label="month">本月</el-radio-button>
                  <el-radio-button label="year">本年</el-radio-button>
                </el-radio-group>
              </div>
            </template>
            <div class="chart-container">
              <div ref="lineChart" style="height: 300px"></div>
            </div>
          </el-card>
        </el-col>

        <!-- 报销类型分布 -->
        <el-col :span="8">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>报销类型分布</span>
              </div>
            </template>
            <div class="chart-container">
              <div ref="pieChart" style="height: 300px"></div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { Money, Tickets, User, TrendCharts } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { request } from '../utils/request'

// 统计卡片数据
const statsCards = ref([
  {
    title: '本月报销总额',
    value: '¥0',
    icon: Money,
    color: 'success'
  },
  {
    title: '待审批报销',
    value: '0',
    icon: Tickets,
    color: 'warning'
  },
  {
    title: '本月报销人数',
    value: '0',
    icon: User,
    color: 'primary'
  },
  {
    title: '同比增长',
    value: '0%',
    icon: TrendCharts,
    color: 'info'
  }
])

// 时间范围选择
const timeRange = ref('month')

// 图表实例
let lineChart = null
let pieChart = null

// 初始化折线图
const initLineChart = async () => {
  const chartDom = document.querySelector('#lineChart')
  lineChart = echarts.init(chartDom)
  await updateLineChart()
}

// 更新折线图数据
const updateLineChart = async () => {
  try {
    const response = await request(`/api/stats/trend?timeRange=${timeRange.value}`)
    if (response?.data) {
      const dates = response.data.map(item => item.date)
      const amounts = response.data.map(item => item.amount)
      
      const option = {
        tooltip: {
          trigger: 'axis',
          formatter: '{b}<br/>金额: ¥{c}'
        },
        xAxis: {
          type: 'category',
          data: dates
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: '¥{value}'
          }
        },
        series: [
          {
            data: amounts,
            type: 'line',
            smooth: true,
            areaStyle: {}
          }
        ]
      }
      lineChart.setOption(option)
    }
  } catch (error) {
    console.error('获取趋势数据失败:', error)
  }
}

// 初始化饼图
const initPieChart = async () => {
  const chartDom = document.querySelector('#pieChart')
  pieChart = echarts.init(chartDom)
  
  try {
    const response = await request('/api/stats/types')
    if (response?.data) {
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            type: 'pie',
            radius: '50%',
            data: response.data,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }
      pieChart.setOption(option)
    }
  } catch (error) {
    console.error('获取类型统计失败:', error)
  }
}

// 获取统计数据
const fetchStats = async () => {
  try {
    const response = await request('/api/stats/summary')
    // 更新统计卡片数据
    if (response?.data) {
      statsCards.value[0].value = `¥${response.data.monthlyTotal}`
      statsCards.value[1].value = response.data.pendingCount
      statsCards.value[2].value = response.data.monthlyUserCount
      statsCards.value[3].value = `${response.data.growthRate}%`
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 监听时间范围变化
watch(timeRange, async () => {
  await updateLineChart()
})

onMounted(() => {
  initLineChart()
  initPieChart()
  fetchStats()

  // 监听窗口大小变化，重绘图表
  window.addEventListener('resize', () => {
    lineChart?.resize()
    pieChart?.resize()
  })
})
</script>

<style scoped>
.stats-card {
  margin-bottom: 20px;
}

.card-content {
  display: flex;
  align-items: center;
}

.card-icon {
  font-size: 48px;
  margin-right: 16px;
}

.success {
  color: #67C23A;
}

.warning {
  color: #E6A23C;
}

.primary {
  color: #409EFF;
}

.info {
  color: #909399;
}

.card-info {
  flex: 1;
}

.card-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
}

.chart-row {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  padding: 10px;
}
</style>