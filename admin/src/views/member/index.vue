<template>
  <div class="page-card">
    <el-card shadow="never">
      <div slot="header">
        <el-form :inline="true" :model="query" size="small">
          <el-form-item label="关键字">
            <el-input v-model="query.keyword" placeholder="昵称 / 手机号" clearable/>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="load">查询</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="list" border stripe size="small">
        <el-table-column prop="id" label="ID" width="70"/>
        <el-table-column prop="nickname" label="昵称" width="140"/>
        <el-table-column prop="phone" label="手机号" width="130"/>
        <el-table-column prop="balance" label="余额" width="100"/>
        <el-table-column prop="points" label="积分" width="90"/>
        <el-table-column label="等级" width="90">
          <template slot-scope="{row}">
            {{ ['', '普通', '银卡', '金卡'][row.level] || '普通' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template slot-scope="{row}">
            <el-tag size="mini" :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="170"/>
        <el-table-column label="操作" width="240" fixed="right">
          <template slot-scope="{row}">
            <el-button type="text" @click="openAdjust(row)">调整余额</el-button>
            <el-button type="text" @click="openLogs(row)">流水</el-button>
            <el-button type="text" @click="toggle(row)">{{ row.status === 1 ? '禁用' : '启用' }}</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination style="margin-top:12px;text-align:right"
                     :current-page="query.current" :page-size="query.size" :total="total"
                     layout="total, prev, pager, next"
                     @current-change="pageChange"/>
    </el-card>

    <el-dialog title="调整余额" :visible.sync="adjustVisible" width="420px">
      <el-form :model="adjustForm" label-width="80px" size="small">
        <el-form-item label="变动金额">
          <el-input-number v-model="adjustForm.amount" :precision="2" :step="10"/>
          <div style="color:#909399;font-size:12px">正数为充值，负数为扣减</div>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="adjustForm.remark"/></el-form-item>
      </el-form>
      <div slot="footer">
        <el-button size="small" @click="adjustVisible = false">取 消</el-button>
        <el-button size="small" type="primary" @click="submitAdjust">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="余额流水" :visible.sync="logsVisible" width="720px">
      <el-table :data="logs" border size="small">
        <el-table-column label="类型" width="90">
          <template slot-scope="{row}">
            {{ ['', '充值', '消费', '退款', '调整'][row.type] }}
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="变动金额" width="110"/>
        <el-table-column prop="balanceAfter" label="变动后余额" width="120"/>
        <el-table-column prop="bizNo" label="业务单号" width="200"/>
        <el-table-column prop="remark" label="备注"/>
        <el-table-column prop="createTime" label="时间" width="170"/>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { memberPage, changeMemberStatus, adjustBalance, balanceLogs } from '@/api/member'

export default {
  name: 'Member',
  data() {
    return {
      list: [],
      total: 0,
      query: { keyword: '', current: 1, size: 10 },
      adjustVisible: false,
      adjustForm: { memberId: null, amount: 0, remark: '' },
      logsVisible: false,
      logs: []
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      memberPage(this.query).then(res => {
        this.list = res.records
        this.total = res.total
      })
    },
    pageChange(page) {
      this.query.current = page
      this.load()
    },
    openAdjust(row) {
      this.adjustForm = { memberId: row.id, amount: 0, remark: '' }
      this.adjustVisible = true
    },
    submitAdjust() {
      adjustBalance(this.adjustForm).then(() => {
        this.$message.success('操作成功')
        this.adjustVisible = false
        this.load()
      })
    },
    openLogs(row) {
      balanceLogs(row.id).then(res => {
        this.logs = res
        this.logsVisible = true
      })
    },
    toggle(row) {
      const status = row.status === 1 ? 0 : 1
      changeMemberStatus(row.id, status).then(() => {
        this.$message.success('操作成功')
        this.load()
      })
    }
  }
}
</script>
