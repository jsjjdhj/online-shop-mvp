<template>
  <div class="home-page">
    <NavHeader />
    <div class="main-content">
      <div class="search-bar">
        <el-input v-model="keyword" placeholder="搜索商品" clearable @keyup.enter="search" style="width: 360px">
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="search">搜索</el-button>
      </div>

      <el-empty v-if="products.length === 0 && !loading" description="未找到相关商品" />

      <el-row :gutter="20" v-else>
        <el-col :span="6" v-for="item in products" :key="item.id" style="margin-bottom: 20px">
          <el-card :body-style="{ padding: '16px' }" shadow="hover" @click="$router.push('/product/' + item.id)" style="cursor: pointer">
            <div class="product-image-placeholder">
              <el-icon :size="48"><Goods /></el-icon>
            </div>
            <div class="product-info">
              <h4 class="product-name">{{ item.name }}</h4>
              <p class="product-price">¥{{ item.price }}</p>
              <p class="product-stock">库存: {{ item.stock }}</p>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <div class="pagination-bar" v-if="total > 0">
        <el-pagination
          v-model:current-page="page"
          :page-size="10"
          :total="total"
          layout="prev, pager, next"
          @current-change="loadProducts"
        />
      </div>
    </div>
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getProductList } from '@/api/product'
import NavHeader from '@/components/common/NavHeader.vue'
import AppFooter from '@/components/common/AppFooter.vue'

const keyword = ref('')
const products = ref([])
const total = ref(0)
const page = ref(1)
const loading = ref(false)

onMounted(() => {
  loadProducts()
})

async function loadProducts() {
  loading.value = true
  try {
    const res = await getProductList({ page: page.value, size: 10, keyword: keyword.value })
    products.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

function search() {
  page.value = 1
  loadProducts()
}
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.main-content {
  flex: 1;
  padding: 24px;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
}
.search-bar {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-bottom: 24px;
}
.product-image-placeholder {
  height: 140px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 12px;
}
.product-name {
  font-size: 15px;
  color: #303133;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.product-price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
  margin: 0 0 4px 0;
}
.product-stock {
  font-size: 13px;
  color: #909399;
  margin: 0;
}
.pagination-bar {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
