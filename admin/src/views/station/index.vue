<template>
  <div class="page-card">
    <el-card shadow="never">
      <div slot="header">
        <el-form :inline="true" :model="query" size="small">
          <el-form-item label="油站名称">
            <el-input v-model="query.keyword" placeholder="请输入关键字" clearable/>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="query.status" placeholder="全部" clearable style="width:120px">
              <el-option label="营业中" :value="1"/>
              <el-option label="已停业" :value="0"/>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="load">查询</el-button>
            <el-button type="success" icon="el-icon-plus" @click="openForm()">新增油站</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="list" border stripe size="small">
        <el-table-column prop="id" label="ID" width="70"/>
        <el-table-column prop="name" label="油站名称" min-width="180"/>
        <el-table-column prop="code" label="编码" width="110"/>
        <el-table-column prop="city" label="城市" width="100"/>
        <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip/>
        <el-table-column prop="phone" label="电话" width="130"/>
        <el-table-column prop="businessHours" label="营业时间" width="120"/>
        <el-table-column label="状态" width="90">
          <template slot-scope="{row}">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="mini">
              {{ row.status === 1 ? '营业中' : '已停业' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template slot-scope="{row}">
            <el-button type="text" @click="openPrice(row)">油价</el-button>
            <el-button type="text" @click="openGun(row)">油枪</el-button>
            <el-button type="text" @click="openForm(row)">编辑</el-button>
            <el-button type="text" style="color:#F56C6C" @click="del(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination style="margin-top:12px;text-align:right"
                     :current-page="query.current" :page-size="query.size" :total="total"
                     layout="total, sizes, prev, pager, next"
                     :page-sizes="[10,20,50]"
                     @current-change="pageChange" @size-change="sizeChange"/>
    </el-card>

    <!-- 油站表单 -->
    <el-dialog :title="form.id ? '编辑油站' : '新增油站'" :visible.sync="formVisible" width="560px">
      <el-form :model="form" label-width="90px" size="small">
        <el-form-item label="油站名称"><el-input v-model="form.name"/></el-form-item>
        <el-form-item label="油站编码"><el-input v-model="form.code" placeholder="留空自动生成"/></el-form-item>
        <el-form-item label="省市">
          <el-input v-model="form.province" placeholder="省" style="width:48%"/>
          <el-input v-model="form.city" placeholder="市" style="width:48%;margin-left:4%"/>
        </el-form-item>
        <el-form-item label="详细地址"><el-input v-model="form.address"/></el-form-item>
        <el-form-item label="经纬度">
          <el-input v-model="form.lng" placeholder="经度" style="width:48%"/>
          <el-input v-model="form.lat" placeholder="纬度" style="width:48%;margin-left:4%"/>
        </el-form-item>
        <el-form-item label="联系电话"><el-input v-model="form.phone"/></el-form-item>
        <el-form-item label="营业时间"><el-input v-model="form.businessHours"/></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">营业中</el-radio>
            <el-radio :label="0">已停业</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button size="small" @click="formVisible = false">取 消</el-button>
        <el-button size="small" type="primary" @click="submit">保 存</el-button>
      </div>
    </el-dialog>

    <!-- 油价 -->
    <el-dialog :title="`油价管理 - ${currentStation.name || ''}`" :visible.sync="priceVisible" width="760px">
      <el-table :data="prices" border size="small">
        <el-table-column prop="fuelType" label="油品" width="90"/>
        <el-table-column prop="listPrice" label="挂牌价" width="110"/>
        <el-table-column prop="salePrice" label="售价" width="110"/>
        <el-table-column prop="effectiveTime" label="生效时间" width="170"/>
        <el-table-column label="操作" width="80">
          <template slot-scope="{row}">
            <el-button type="text" style="color:#F56C6C" @click="delPrice(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-form :inline="true" :model="priceForm" size="small" style="margin-top:12px">
        <el-form-item label="油品">
          <el-select v-model="priceForm.fuelType" style="width:90px">
            <el-option label="92#" value="92#"/>
            <el-option label="95#" value="95#"/>
            <el-option label="98#" value="98#"/>
            <el-option label="0#" value="0#"/>
          </el-select>
        </el-form-item>
        <el-form-item label="挂牌价"><el-input v-model.number="priceForm.listPrice" style="width:100px"/></el-form-item>
        <el-form-item label="售价"><el-input v-model.number="priceForm.salePrice" style="width:100px"/></el-form-item>
        <el-form-item><el-button type="primary" @click="savePrice">新增/更新</el-button></el-form-item>
      </el-form>
    </el-dialog>

    <!-- 油枪 -->
    <el-dialog :title="`油枪管理 - ${currentStation.name || ''}`" :visible.sync="gunVisible" width="760px">
      <el-table :data="guns" border size="small">
        <el-table-column prop="gunNo" label="枪号" width="90"/>
        <el-table-column prop="fuelType" label="油品" width="90"/>
        <el-table-column label="状态" width="120">
          <template slot-scope="{row}">
            <el-tag size="mini" :type="row.status === 1 ? 'success' : (row.status === 2 ? 'warning' : 'danger')">
              {{ gunStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template slot-scope="{row}">
            <el-button type="text" @click="setGunStatus(row, 1)">置空闲</el-button>
            <el-button type="text" style="color:#F56C6C" @click="delGun(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-form :inline="true" :model="gunForm" size="small" style="margin-top:12px">
        <el-form-item label="枪号"><el-input v-model="gunForm.gunNo" style="width:90px"/></el-form-item>
        <el-form-item label="油品">
          <el-select v-model="gunForm.fuelType" style="width:90px">
            <el-option label="92#" value="92#"/>
            <el-option label="95#" value="95#"/>
            <el-option label="98#" value="98#"/>
            <el-option label="0#" value="0#"/>
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="saveGun">新 增</el-button></el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import { stationPage, saveStation, removeStation, priceList, savePrice, removePrice, gunList, saveGun, removeGun, changeGunStatus } from '@/api/station'

export default {
  name: 'Station',
  data() {
    return {
      list: [],
      total: 0,
      query: { keyword: '', status: null, current: 1, size: 10 },
      formVisible: false,
      form: { status: 1 },
      priceVisible: false,
      gunVisible: false,
      currentStation: {},
      prices: [],
      guns: [],
      priceForm: { fuelType: '92#', listPrice: null, salePrice: null },
      gunForm: { gunNo: '', fuelType: '92#' }
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      stationPage(this.query).then(res => {
        this.list = res.records
        this.total = res.total
      })
    },
    pageChange(page) {
      this.query.current = page
      this.load()
    },
    sizeChange(size) {
      this.query.size = size
      this.load()
    },
    openForm(row) {
      this.form = row ? Object.assign({}, row) : { status: 1 }
      this.formVisible = true
    },
    submit() {
      saveStation(this.form).then(() => {
        this.$message.success('保存成功')
        this.formVisible = false
        this.load()
      })
    },
    del(row) {
      this.$confirm(`确认删除油站「${row.name}」？`, '提示', { type: 'warning' }).then(() => {
        return removeStation(row.id)
      }).then(() => {
        this.$message.success('删除成功')
        this.load()
      })
    },
    openPrice(row) {
      this.currentStation = row
      this.priceVisible = true
      this.loadPrices()
    },
    loadPrices() {
      priceList(this.currentStation.id).then(res => {
        this.prices = res
      })
    },
    savePrice() {
      savePrice(Object.assign({ stationId: this.currentStation.id }, this.priceForm)).then(() => {
        this.$message.success('保存成功')
        this.loadPrices()
      })
    },
    delPrice(row) {
      removePrice(row.id).then(() => {
        this.$message.success('删除成功')
        this.loadPrices()
      })
    },
    openGun(row) {
      this.currentStation = row
      this.gunVisible = true
      this.loadGuns()
    },
    loadGuns() {
      gunList(this.currentStation.id).then(res => {
        this.guns = res
      })
    },
    saveGun() {
      saveGun(Object.assign({ stationId: this.currentStation.id, status: 1 }, this.gunForm)).then(() => {
        this.$message.success('新增成功')
        this.gunForm.gunNo = ''
        this.loadGuns()
      })
    },
    setGunStatus(row, status) {
      changeGunStatus(row.id, status).then(() => {
        this.$message.success('已更新')
        this.loadGuns()
      })
    },
    delGun(row) {
      removeGun(row.id).then(() => {
        this.$message.success('删除成功')
        this.loadGuns()
      })
    },
    gunStatusText(status) {
      return status === 1 ? '空闲' : (status === 2 ? '使用中' : '故障')
    }
  }
}
</script>
