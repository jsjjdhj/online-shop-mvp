<template>
  <div class="success-page">
    <NavHeader />
    <div class="main-content">
      <el-result icon="success" title="订单提交成功" :sub-title="'订单编号：' + order.orderNo">
        <template #extra>
          <el-button type="primary" @click="$router.push('/orders/' + order.id)">查看订单详情</el-button>
          <el-button @click="$router.push('/')">返回首页</el-button>
        </template>
      </el-result>
    </div>
    <AppFooter />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useOrderStore } from '@/store/order'
import NavHeader from '@/components/common/NavHeader.vue'
import AppFooter from '@/components/common/AppFooter.vue'

const orderStore = useOrderStore()
const order = computed(() => orderStore.lastOrder || { orderNo: '' })
</script>

<style scoped>
.success-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.main-content {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
