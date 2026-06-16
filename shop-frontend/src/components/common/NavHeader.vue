<template>
  <el-menu mode="horizontal" :ellipsis="false" class="nav-header">
    <el-menu-item index="home" @click="$router.push('/')">
      <el-icon><ShoppingCart /></el-icon>
      在线购物平台
    </el-menu-item>

    <div class="flex-grow" />

    <el-menu-item index="orders" @click="$router.push('/orders')">
      我的订单
    </el-menu-item>

    <el-menu-item index="cart" @click="$router.push('/cart')">
      <el-badge :value="cartStore.cartCount" :max="99">
        <el-icon size="20"><ShoppingCart /></el-icon>
      </el-badge>
      <span style="margin-left: 4px">购物车</span>
    </el-menu-item>

    <el-sub-menu index="user" v-if="userStore.isLoggedIn">
      <template #title>
        <el-icon><User /></el-icon>
        {{ userStore.username }}
      </template>
      <el-menu-item index="admin" v-if="userStore.isAdmin" @click="$router.push('/admin')">
        管理后台
      </el-menu-item>
      <el-menu-item index="logout" @click="handleLogout">退出登录</el-menu-item>
    </el-sub-menu>
  </el-menu>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { useCartStore } from '@/store/cart'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

onMounted(() => {
  if (userStore.isLoggedIn) {
    cartStore.fetchCartCount()
  }
})

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.nav-header {
  padding: 0 20px;
}
.flex-grow {
  flex-grow: 1;
}
</style>
