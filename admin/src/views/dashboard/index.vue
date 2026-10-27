<template>
  <div class="page-card">
    <el-row :gutter="16">
      <el-col :span="6" v-for="card in cards" :key="card.label">
        <el-card shadow="hover" class="stat-card">
          <div class="label">{{ card.label }}</div>
          <div class="value">{{ card.value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top:16px">
      <div slot="header">近 7 日销售额趋势</div>
      <div ref="chart" style="height:320px"></div>
    </el-card>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { summary } from '@/api/dashboard'

export default {
  name: 'Dashboard',
  data() {
    return {
      data: {
        stationCount: 0,
        memberCount: 0,
        todayOrderCount: 0,
        todaySales: 0,
        unpaidOrderCount: 0,
        dailyStats: []
      },
      chart: null
    }
  },
  computed: {
    cards() {
      return [
        { label: '油站数量', value: this.data.stationCount },
        { label: '会员总数', value: this.data.memberCount },
        { label: '今日订单', value: this.data.todayOrderCount },
        { label: '今日销售额（元）', value: this.data.todaySales }
      ]
    }
  },
  mounted() {
    this.load()
    window.addEventListener('resize', this.resize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resize)
    if (this.chart) this.chart.dispose()
  },
  methods: {
    load() {
      summary().then(res => {
        this.data = res || this.data
        this.renderChart()
      })
    },
    renderChart() {
      const stats = this.data.dailyStats || []
      this.chart = echarts.init(this.$refs.chart)
      this.chart.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: stats.map(item => item.day), boundaryGap: false },
        yAxis: { type: 'value', name: '元' },
        series: [{
          name: '销售额',
          type: 'line',
          smooth: true,
          areaStyle: { opacity: 0.25 },
          itemStyle: { color: '#409EFF' },
          data: stats.map(item => Number(item.amount))
        }]
      })
    },
    resize() {
      if (this.chart) this.chart.resize()
    }
  }
}
</script>

<style scoped>
.stat-card {
  text-align: center;
}

.label {
  font-size: 13px;
  color: #909399;
}

.value {
  margin-top: 8px;
  font-size: 26px;
  font-weight: bold;
  color: #303133;
}
</style>
