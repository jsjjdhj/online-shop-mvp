<template>
  <div class="detail-page">
    <NavHeader />
    <div class="main-content" v-if="product">
      <el-descriptions title="商品详情" :column="1" border>
        <el-descriptions-item label="商品名称">{{ product.name }}</el-descriptions-item>
        <el-descriptions-item label="商品描述">{{ product.description || '暂无描述' }}</el-descriptions-item>
        <el-descriptions-item label="价格">¥{{ product.price }}</el-descriptions-item>
        <el-descriptions-item label="库存">{{ product.stock }}</el-descriptions-item>
      </el-descriptions>

      <div class="action-bar">
        <el-input-number v-model="quantity" :min="1" :max="Math.min(99, product.stock)" :disabled="product.stock === 0" />
        <el-button type="primary" :disabled="product.stock === 0" @click="addToCart" :loading="adding">
          加入购物车
        </el-button>
      </div>
    </div>
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProductDetail } from '@/api/product'
import { addToCart as addToCartApi } from '@/api/cart'
import { useCartStore } from '@/store/cart'
import NavHeader from '@/components/common/NavHeader.vue'
import AppFooter from '@/components/common/AppFooter.vue'

const route = useRoute()
const cartStore = useCartStore()
const product = ref(null)
const quantity = ref(1)
const adding = ref(false)

onMounted(async () => {
  try {
    const res = await getProductDetail(route.params.id)
    product.value = res.data
  } catch {
    ElMessage.error('加载商品详情失败')
  }
})

async function addToCart() {
  adding.value = true
  try {
    await addToCartApi({ productId: product.value.id, quantity: quantity.value })
    ElMessage.success('已成功加入购物车')
    cartStore.fetchCartCount()
  } finally {
    adding.value = false
  }
}
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.main-content {
  flex: 1;
  max-width: 800px;
  margin: 24px auto;
  padding: 0 24px;
  width: 100%;
}
.action-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 24px;
}
</style>
