<template>
  <div class="admin-dashboard">
    <NavHeader />
    <div class="main-content">
      <h2>管理后台</h2>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card shadow="hover" @click="$router.push('/admin/products')" style="cursor: pointer">
            <el-icon :size="40" color="#409eff"><Goods /></el-icon>
            <h3>商品管理</h3>
            <p>新增、编辑、下架商品</p>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" @click="$router.push('/admin/orders')" style="cursor: pointer">
            <el-icon :size="40" color="#67c23a"><List /></el-icon>
            <h3>订单管理</h3>
            <p>确认订单、发货管理</p>
          </el-card>
        </el-col>
      </el-row>
    </div>
    <AppFooter />
  </div>
</template>

<script setup>
import NavHeader from '@/components/common/NavHeader.vue'
import AppFooter from '@/components/common/AppFooter.vue'
</script>

<style scoped>
.admin-dashboard {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.main-content {
  flex: 1;
  max-width: 1000px;
  margin: 24px auto;
  padding: 0 24px;
  width: 100%;
}
.main-content h2 {
  margin-bottom: 24px;
}
.el-card {
  text-align: center;
  padding: 24px 0;
}
</style>
