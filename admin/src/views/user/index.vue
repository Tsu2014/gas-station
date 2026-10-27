<template>
  <div class="page-card">
    <el-card shadow="never">
      <div slot="header">
        <el-button type="success" icon="el-icon-plus" size="small" @click="openForm()">新增账号</el-button>
      </div>

      <el-table :data="list" border stripe size="small">
        <el-table-column prop="id" label="ID" width="70"/>
        <el-table-column prop="username" label="用户名" width="140"/>
        <el-table-column prop="realName" label="姓名" width="120"/>
        <el-table-column prop="phone" label="手机号" width="130"/>
        <el-table-column label="角色" width="110">
          <template slot-scope="{row}">
            {{ roleText(row.role) }}
          </template>
        </el-table-column>
        <el-table-column prop="stationId" label="所属油站" width="110"/>
        <el-table-column label="状态" width="90">
          <template slot-scope="{row}">
            <el-tag size="mini" :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170"/>
        <el-table-column label="操作" width="160" fixed="right">
          <template slot-scope="{row}">
            <el-button type="text" @click="openForm(row)">编辑</el-button>
            <el-button type="text" style="color:#F56C6C" @click="del(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination style="margin-top:12px;text-align:right"
                     :current-page="query.current" :page-size="query.size" :total="total"
                     layout="total, prev, pager, next"
                     @current-change="pageChange"/>
    </el-card>

    <el-dialog :title="form.id ? '编辑账号' : '新增账号'" :visible.sync="formVisible" width="460px">
      <el-form :model="form" label-width="90px" size="small">
        <el-form-item label="用户名"><el-input v-model="form.username" :disabled="!!form.id"/></el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" :placeholder="form.id ? '留空表示不修改' : '默认 123456'"/>
        </el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.realName"/></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone"/></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" style="width:100%">
            <el-option label="超级管理员" value="ADMIN"/>
            <el-option label="油站站长" value="MANAGER"/>
            <el-option label="加油员" value="STAFF"/>
          </el-select>
        </el-form-item>
        <el-form-item label="所属油站">
          <el-select v-model="form.stationId" clearable style="width:100%" placeholder="超管可为空">
            <el-option v-for="station in stations" :key="station.id" :label="station.name" :value="station.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
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
import { userPage, saveUser, removeUser } from '@/api/user'
import { stationList } from '@/api/station'

export default {
  name: 'User',
  data() {
    return {
      list: [],
      total: 0,
      query: { current: 1, size: 10 },
      formVisible: false,
      form: { role: 'STAFF', status: 1 },
      stations: []
    }
  },
  created() {
    this.load()
    stationList().then(res => {
      this.stations = res
    })
  },
  methods: {
    load() {
      userPage(this.query).then(res => {
        this.list = res.records
        this.total = res.total
      })
    },
    pageChange(page) {
      this.query.current = page
      this.load()
    },
    openForm(row) {
      this.form = row ? Object.assign({}, row, { password: '' }) : { role: 'STAFF', status: 1 }
      this.formVisible = true
    },
    submit() {
      saveUser(this.form).then(() => {
        this.$message.success('保存成功')
        this.formVisible = false
        this.load()
      })
    },
    del(row) {
      this.$confirm(`确认删除账号「${row.username}」？`, '提示', { type: 'warning' }).then(() => {
        return removeUser(row.id)
      }).then(() => {
        this.$message.success('删除成功')
        this.load()
      })
    },
    roleText(role) {
      return { ADMIN: '超级管理员', MANAGER: '油站站长', STAFF: '加油员', MEMBER: '会员' }[role] || role
    }
  }
}
</script>
