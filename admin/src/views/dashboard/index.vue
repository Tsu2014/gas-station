<template>
  <div class="page-card">
    <!-- 预约加油实时看板 -->
    <el-card shadow="never" class="board-card">
      <div slot="header" class="board-header">
        <div class="board-title">
          <span>预约加油看板</span>
          <el-badge :value="boardList.length" :hidden="boardList.length === 0" type="warning">
            <el-tag size="small" type="success" effect="plain">
              <i class="el-icon-refresh" :class="{'is-loading': loadingBoard}"></i>
              实时刷新中
            </el-tag>
          </el-badge>
        </div>
        <div class="board-actions">
          <span class="refresh-time">上次刷新 {{ lastRefreshTime || '-' }}</span>
          <el-button size="mini" icon="el-icon-refresh" circle @click="loadBoard"/>
        </div>
      </div>

      <el-table :data="boardList" border stripe size="small" v-loading="loadingBoard"
                empty-text="暂无待确认的预约">
        <el-table-column label="预计到达" width="150" sortable :sort-method="sortByExpectTime">
          <template slot-scope="{row}">
            <div>{{ formatTime(row.expectTime) }}</div>
            <el-tag size="mini" :type="expectTagType(row.expectTime)">{{ expectCountdown(row.expectTime) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="plateNo" label="车牌号" width="110">
          <template slot-scope="{row}">
            <span class="plate">{{ row.plateNo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stationName" label="加油站" min-width="150" show-overflow-tooltip/>
        <el-table-column prop="fuelType" label="油品" width="70" align="center"/>
        <el-table-column label="油量" width="80" align="right">
          <template slot-scope="{row}">{{ row.liters }} 升</template>
        </el-table-column>
        <el-table-column label="预估金额" width="100" align="right">
          <template slot-scope="{row}">¥ {{ row.estAmount }}</template>
        </el-table-column>
        <el-table-column label="会员" width="130">
          <template slot-scope="{row}">
            <div>{{ row.memberNickname || '-' }}</div>
            <div class="muted">{{ row.memberPhone || '' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="预约时间" width="150">
          <template slot-scope="{row}">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="170" fixed="right">
          <template slot-scope="{row}">
            <el-button type="success" size="mini" @click="confirmFueled(row)">确认已加油</el-button>
            <el-button type="danger" size="mini" plain @click="openReject(row)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-row :gutter="16" style="margin-top:16px">
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

    <!-- 拒绝预约 -->
    <el-dialog title="拒绝预约" :visible.sync="rejectVisible" width="480px">
      <div v-if="rejectTarget" style="margin-bottom:12px;color:#606266">
        {{ rejectTarget.plateNo }} · {{ rejectTarget.stationName }} · {{ rejectTarget.fuelType }} {{ rejectTarget.liters }} 升
      </div>
      <el-input v-model="rejectReply" type="textarea" :rows="3" maxlength="255" show-word-limit
                placeholder="请填写回复给车主的原因，如：该油品暂时缺货"/>
      <div slot="footer">
        <el-button size="small" @click="rejectVisible = false">取 消</el-button>
        <el-button size="small" type="danger" :loading="rejecting" @click="submitReject">确认拒绝</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { summary } from '@/api/dashboard'
import { reserveBoard, confirmReserve, rejectReserve } from '@/api/reserve'

const BOARD_REFRESH_INTERVAL = 10000

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
      chart: null,
      boardList: [],
      loadingBoard: false,
      lastRefreshTime: '',
      boardTimer: null,
      rejectVisible: false,
      rejectTarget: null,
      rejectReply: '',
      rejecting: false
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
    this.loadBoard()
    this.boardTimer = setInterval(this.loadBoard, BOARD_REFRESH_INTERVAL)
    window.addEventListener('resize', this.resize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resize)
    if (this.boardTimer) clearInterval(this.boardTimer)
    if (this.chart) this.chart.dispose()
  },
  methods: {
    load() {
      summary().then(res => {
        this.data = res || this.data
        this.renderChart()
      })
    },
    loadBoard() {
      if (this.loadingBoard) return
      this.loadingBoard = true
      reserveBoard().then(res => {
        this.boardList = res || []
        const now = new Date()
        this.lastRefreshTime = now.toLocaleTimeString('zh-CN', { hour12: false })
      }).finally(() => {
        this.loadingBoard = false
      })
    },
    formatTime(value) {
      if (!value) return '-'
      return String(value).replace('T', ' ').slice(0, 16)
    },
    expectCountdown(expectTime) {
      const diff = new Date(expectTime).getTime() - Date.now()
      if (diff <= 0) return '已超时'
      const minutes = Math.ceil(diff / 60000)
      if (minutes < 60) return `${minutes} 分钟后`
      return `${Math.floor(minutes / 60)} 小时 ${minutes % 60} 分后`
    },
    expectTagType(expectTime) {
      const diff = new Date(expectTime).getTime() - Date.now()
      if (diff <= 0) return 'danger'
      if (diff <= 30 * 60000) return 'warning'
      return 'info'
    },
    sortByExpectTime(a, b) {
      return new Date(a.expectTime).getTime() - new Date(b.expectTime).getTime()
    },
    confirmFueled(row) {
      this.$confirm(`确认已为「${row.plateNo}」加注 ${row.fuelType} ${row.liters} 升？`, '确认已加油', { type: 'success' })
        .then(() => confirmReserve(row.id))
        .then(() => {
          this.$message.success('已确认加油，订单进入历史记录')
          this.loadBoard()
        })
        .catch(() => {})
    },
    openReject(row) {
      this.rejectTarget = row
      this.rejectReply = ''
      this.rejectVisible = true
    },
    submitReject() {
      if (!this.rejectReply.trim()) {
        this.$message.warning('请填写回复（拒绝原因）')
        return
      }
      this.rejecting = true
      rejectReserve(this.rejectTarget.id, this.rejectReply.trim()).then(() => {
        this.$message.success('已拒绝该预约')
        this.rejectVisible = false
        this.loadBoard()
      }).finally(() => {
        this.rejecting = false
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
.board-card /deep/ .el-card__header {
  padding: 12px 16px;
}

.board-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.board-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
  font-weight: 600;
}

.board-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.refresh-time {
  font-size: 12px;
  color: #909399;
  font-weight: normal;
}

.plate {
  font-weight: 600;
  color: #303133;
  background: #f0f5ff;
  border: 1px solid #d6e4ff;
  border-radius: 4px;
  padding: 2px 8px;
}

.muted {
  font-size: 12px;
  color: #909399;
}

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
