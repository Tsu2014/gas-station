<template>
  <div class="page-card">
    <el-card shadow="never">
      <div slot="header">
        <el-form :inline="true" :model="query" size="small">
          <el-form-item label="订单号"><el-input v-model="query.orderNo" clearable/></el-form-item>
          <el-form-item label="会员手机"><el-input v-model="query.memberPhone" clearable/></el-form-item>
          <el-form-item label="状态">
            <el-select v-model="query.status" placeholder="全部" clearable style="width:130px">
              <el-option label="待支付" :value="0"/>
              <el-option label="已支付" :value="1"/>
              <el-option label="已完成" :value="2"/>
              <el-option label="已取消" :value="3"/>
              <el-option label="已退款" :value="4"/>
            </el-select>
          </el-form-item>
          <el-form-item label="下单日期">
            <el-date-picker v-model="dateRange" type="daterange" value-format="yyyy-MM-dd"
                            start-placeholder="开始" end-placeholder="结束" style="width:240px"/>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="load">查询</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="list" border stripe size="small">
        <el-table-column prop="orderNo" label="订单号" width="200"/>
        <el-table-column prop="stationName" label="油站" min-width="160" show-overflow-tooltip/>
        <el-table-column prop="fuelType" label="油品" width="80"/>
        <el-table-column prop="unitPrice" label="单价" width="80"/>
        <el-table-column prop="liters" label="升数" width="80"/>
        <el-table-column prop="amount" label="金额" width="90"/>
        <el-table-column prop="discountAmount" label="优惠" width="80"/>
        <el-table-column prop="payAmount" label="实付" width="90"/>
        <el-table-column prop="payType" label="支付方式" width="90">
          <template slot-scope="{row}">
            {{ row.payType === 'balance' ? '余额' : (row.payType === 'wx' ? '微信' : '-') }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template slot-scope="{row}">
            <el-tag size="mini" :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="170"/>
        <el-table-column label="操作" width="160" fixed="right">
          <template slot-scope="{row}">
            <el-button type="text" @click="openDetail(row)">详情</el-button>
            <el-button v-if="row.status === 1" type="text" @click="finish(row)">完成</el-button>
            <el-button v-if="row.status === 1 || row.status === 2" type="text" style="color:#F56C6C" @click="refund(row)">退款</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination style="margin-top:12px;text-align:right"
                     :current-page="query.current" :page-size="query.size" :total="total"
                     layout="total, prev, pager, next"
                     @current-change="pageChange"/>
    </el-card>

    <el-dialog title="订单详情" :visible.sync="detailVisible" width="520px">
      <el-descriptions :column="2" border size="small" v-if="detail">
        <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="油站">{{ detail.stationName }}</el-descriptions-item>
        <el-descriptions-item label="油品">{{ detail.fuelType }}</el-descriptions-item>
        <el-descriptions-item label="油枪">{{ detail.gunNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="单价">{{ detail.unitPrice }}</el-descriptions-item>
        <el-descriptions-item label="升数">{{ detail.liters }}</el-descriptions-item>
        <el-descriptions-item label="金额">{{ detail.amount }}</el-descriptions-item>
        <el-descriptions-item label="优惠">{{ detail.discountAmount }}</el-descriptions-item>
        <el-descriptions-item label="实付">{{ detail.payAmount }}</el-descriptions-item>
        <el-descriptions-item label="积分">{{ detail.points }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ statusText(detail.status) }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ detail.payTime || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { orderPage, orderDetail, finishOrder, refundOrder } from '@/api/order'

export default {
  name: 'Order',
  data() {
    return {
      list: [],
      total: 0,
      dateRange: null,
      query: { orderNo: '', memberPhone: '', status: null, startDate: '', endDate: '', current: 1, size: 10 },
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
      orderPage(query).then(res => {
        this.list = res.records
        this.total = res.total
      })
    },
    pageChange(page) {
      this.query.current = page
      this.load()
    },
    openDetail(row) {
      orderDetail(row.id).then(res => {
        this.detail = res
        this.detailVisible = true
      })
    },
    finish(row) {
      finishOrder(row.id).then(() => {
        this.$message.success('已标记完成')
        this.load()
      })
    },
    refund(row) {
      this.$confirm('确认退款？金额将原路退回会员余额。', '提示', { type: 'warning' }).then(() => {
        return refundOrder(row.id)
      }).then(() => {
        this.$message.success('退款成功')
        this.load()
      })
    },
    statusText(status) {
      return ['待支付', '已支付', '已完成', '已取消', '已退款'][status] || '未知'
    },
    statusType(status) {
      return ['warning', 'primary', 'success', 'info', 'danger'][status] || 'info'
    }
  }
}
</script>
