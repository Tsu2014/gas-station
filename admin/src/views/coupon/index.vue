<template>
  <div class="page-card">
    <el-card shadow="never">
      <div slot="header">
        <el-button type="success" icon="el-icon-plus" size="small" @click="openForm()">新增优惠券</el-button>
      </div>

      <el-table :data="list" border stripe size="small">
        <el-table-column prop="id" label="ID" width="70"/>
        <el-table-column prop="name" label="券名称" min-width="180"/>
        <el-table-column label="类型" width="90">
          <template slot-scope="{row}">
            {{ row.type === 2 ? '折扣券' : '满减券' }}
          </template>
        </el-table-column>
        <el-table-column label="优惠内容" width="160">
          <template slot-scope="{row}">
            <span v-if="row.type === 2">{{ Number(row.discount) * 10 }} 折</span>
            <span v-else>满 {{ row.threshold }} 减 {{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="total" label="总量" width="90"/>
        <el-table-column prop="remain" label="剩余" width="90"/>
        <el-table-column prop="startTime" label="生效时间" width="170"/>
        <el-table-column prop="endTime" label="过期时间" width="170"/>
        <el-table-column label="状态" width="90">
          <template slot-scope="{row}">
            <el-tag size="mini" :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template slot-scope="{row}">
            <el-button type="text" @click="openForm(row)">编辑</el-button>
            <el-button type="text" @click="toggle(row)">{{ row.status === 1 ? '下架' : '上架' }}</el-button>
            <el-button type="text" style="color:#F56C6C" @click="del(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination style="margin-top:12px;text-align:right"
                     :current-page="query.current" :page-size="query.size" :total="total"
                     layout="total, prev, pager, next"
                     @current-change="pageChange"/>
    </el-card>

    <el-dialog :title="form.id ? '编辑优惠券' : '新增优惠券'" :visible.sync="formVisible" width="520px">
      <el-form :model="form" label-width="100px" size="small">
        <el-form-item label="券名称"><el-input v-model="form.name"/></el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="form.type">
            <el-radio :label="1">满减券</el-radio>
            <el-radio :label="2">折扣券</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.type === 1" label="使用门槛">
          <el-input-number v-model="form.threshold" :min="0" :precision="2"/>
        </el-form-item>
        <el-form-item v-if="form.type === 1" label="优惠金额">
          <el-input-number v-model="form.amount" :min="0" :precision="2"/>
        </el-form-item>
        <el-form-item v-else label="折扣率">
          <el-input-number v-model="form.discount" :min="0.1" :max="0.99" :step="0.05" :precision="2"/>
          <div style="color:#909399;font-size:12px">0.95 表示 95 折</div>
        </el-form-item>
        <el-form-item label="发放总量"><el-input-number v-model="form.total" :min="1"/></el-form-item>
        <el-form-item label="有效期">
          <el-date-picker v-model="form.dateRange" type="daterange" value-format="yyyy-MM-dd HH:mm:ss"
                          start-placeholder="开始" end-placeholder="结束" style="width:100%"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button size="small" @click="formVisible = false">取 消</el-button>
        <el-button size="small" type="primary" @click="submit">保 存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { couponPage, saveCoupon, changeCouponStatus, removeCoupon } from '@/api/coupon'

export default {
  name: 'Coupon',
  data() {
    return {
      list: [],
      total: 0,
      query: { current: 1, size: 10 },
      formVisible: false,
      form: { type: 1, status: 1, total: 100, threshold: 0, amount: 0, discount: 0.95, dateRange: null }
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      couponPage(this.query).then(res => {
        this.list = res.records
        this.total = res.total
      })
    },
    pageChange(page) {
      this.query.current = page
      this.load()
    },
    openForm(row) {
      if (row) {
        this.form = Object.assign({}, row, { dateRange: [row.startTime, row.endTime] })
      } else {
        this.form = { type: 1, status: 1, total: 100, threshold: 0, amount: 0, discount: 0.95, dateRange: null }
      }
      this.formVisible = true
    },
    submit() {
      const data = Object.assign({}, this.form)
      if (data.dateRange && data.dateRange.length === 2) {
        data.startTime = data.dateRange[0]
        data.endTime = data.dateRange[1]
      }
      delete data.dateRange
      saveCoupon(data).then(() => {
        this.$message.success('保存成功')
        this.formVisible = false
        this.load()
      })
    },
    toggle(row) {
      changeCouponStatus(row.id, row.status === 1 ? 0 : 1).then(() => {
        this.$message.success('操作成功')
        this.load()
      })
    },
    del(row) {
      this.$confirm(`确认删除「${row.name}」？`, '提示', { type: 'warning' }).then(() => {
        return removeCoupon(row.id)
      }).then(() => {
        this.$message.success('删除成功')
        this.load()
      })
    }
  }
}
</script>
