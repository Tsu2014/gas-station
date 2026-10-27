<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside">
      <div class="logo">
        <i class="el-icon-s-shop"></i>
        <span>智慧加油站</span>
      </div>
      <el-menu :default-active="activeMenu" background-color="#304156" text-color="#bfcbd9"
               active-text-color="#ffd04b" router unique-opened>
        <template v-for="menu in menus">
          <el-submenu v-if="menu.children.length > 1" :key="menu.path" :index="menu.path">
            <template slot="title">
              <i :class="menu.meta.icon"></i>
              <span>{{ menu.meta.title }}</span>
            </template>
            <el-menu-item v-for="child in menu.children" :key="menu.path + '/' + child.path" :index="menu.path + '/' + child.path">
              {{ child.meta.title }}
            </el-menu-item>
          </el-submenu>
          <el-menu-item v-else :key="menu.path" :index="menu.redirect || menu.path">
            <i :class="menu.meta.icon"></i>
            <span slot="title">{{ menu.meta.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <span class="title">{{ currentTitle }}</span>
        <div class="right">
          <span class="user">{{ userInfo.realName || userInfo.username || '未登录' }}</span>
          <el-button type="text" icon="el-icon-switch-button" @click="logout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view/>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: 'Layout',
  computed: {
    menus() {
      return this.$router.options.routes.filter(route => !route.hidden && route.meta && route.meta.title)
    },
    activeMenu() {
      return this.$route.path
    },
    currentTitle() {
      return this.$route.meta.title || ''
    },
    userInfo() {
      return this.$store.state.user.info || {}
    }
  },
  methods: {
    logout() {
      this.$store.dispatch('user/logout')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.layout {
  height: 100%;
}

.aside {
  background-color: #304156;
  overflow-x: hidden;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  background-color: #263445;
}

.header {
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 21, 41, .08);
}

.title {
  font-size: 16px;
  font-weight: 500;
}

.right .user {
  margin-right: 12px;
  color: #606266;
}

.main {
  padding: 0;
  overflow: auto;
}
</style>
