<template>
  <div class="page-card">
    <el-card shadow="never">
      <div slot="header">
        <el-form :inline="true" :model="query" size="small">
          <el-form-item label="车牌号">
            <el-input v-model="query.plateNo" placeholder="如 京A88888" clearable/>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="query.status" placeholder="全部" clearable style="width:130px">
              <el-option label="待确认" :value="0"/>
              <el-option label="已加油" :value="1"/>
              <el-option label="已拒绝" :value="2"/>
              <el-option label="已取消" :value="3"/>
            </el-select>
          </el-form-item>
          <el-form-item label="预约日期">
            <el-date-picker v-model="dateRange" type="daterange" value-format="yyyy-MM-dd"
                            start-placeholder="开始" end-placeholder="结束" style="width:240px"/>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="load">查询</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="list" border stripe size="small">
        <el-table-column prop="reserveNo" label="预约单号" width="200"/>
        <el-table-column prop="plateNo" label="车牌号" width="110"/>
        <el-table-column prop="stationName" label="加油站" min-width="160" show-overflow-tooltip/>
        <el-table-column prop="fuelType" label="油品" width="70" align="center"/>
        <el-table-column label="油量" width="80" align="right">
          <template slot-scope="{row}">{{ row.liters }} 升</template>
        </el-table-column>
        <el-table-column label="预估金额" width="100" align="right">
          <template slot-scope="{row}">¥ {{ row.estAmount }}</template>
        </el-table-column>
        <el-table-column label="预计到达" width="160">
          <template slot-scope="{row}">{{ formatTime(row.expectTime) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template slot-scope="{row}">
            <el-tag size="mini" :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="预约时间" width="160">
          <template slot-scope="{row}">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template slot-scope="{row}">
            <el-button type="text" @click="openDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination style="margin-top:12px;text-align:right"
                     :current-page="query.current" :page-size="query.size" :total="total"
                     layout="total, prev, pager, next"
                     @current-change="pageChange"/>
    </el-card>

    <el-dialog title="预约详情" :visible.sync="detailVisible" width="520px">
      <el-descriptions :column="2" border size="small" v-if="detail">
        <el-descriptions-item label="预约单号">{{ detail.reserveNo }}</el-descriptions-item>
        <el-descriptions-item label="车牌号">{{ detail.plateNo }}</el-descriptions-item>
        <el-descriptions-item label="加油站">{{ detail.stationName }}</el-descriptions-item>
        <el-descriptions-item label="油品">{{ detail.fuelType }}</el-descriptions-item>
        <el-descriptions-item label="预约单价">{{ detail.unitPrice }} 元/升</el-descriptions-item>
        <el-descriptions-item label="预加油量">{{ detail.liters }} 升</el-descriptions-item>
        <el-descriptions-item label="预估金额">¥ {{ detail.estAmount }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ statusText(detail.status) }}</el-descriptions-item>
        <el-descriptions-item label="会员">{{ detail.memberNickname || '-' }}</el-descriptions-item>
        <el-descriptions-item label="会员手机">{{ detail.memberPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="预计到达">{{ formatTime(detail.expectTime) }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ formatTime(detail.handleTime) }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ formatTime(detail.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ detail.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="后台回复" :span="2">{{ detail.reply || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { reservePage } from '@/api/reserve'

export default {
  name: 'ReserveOrder',
  data() {
    return {
      list: [],
      total: 0,
      dateRange: null,
      query: { plateNo: '', status: null, startDate: '', endDate: '', current: 1, size: 10 },
      detailVisible: false,
      detail: null
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      const query = Object.assign({}, this.query)
      if (this.dateRange && this.dateRange.length === 2) {
        query.startDate = this.dateRange[0]
        query.endDate = this.dateRange[1]
      }
      reservePage(query).then(res => {
        this.list = res.records
        this.total = res.total
      })
    },
    pageChange(page) {
      this.query.current = page
      this.load()
    },
    openDetail(row) {
      this.detail = row
      this.detailVisible = true
    },
    formatTime(value) {
      if (!value) return '-'
      return String(value).replace('T', ' ').slice(0, 16)
    },
    statusText(status) {
      return ['待确认', '已加油', '已拒绝', '已取消'][status] || '未知'
    },
    statusType(status) {
      return ['warning', 'success', 'danger', 'info'][status] || 'info'
    }
  }
}
</script>
