<template>
  <div class="cart-page">
    <NavHeader />
    <div class="main-content">
      <h2>购物车</h2>

      <el-empty v-if="cartItems.length === 0" description="购物车是空的" />

      <el-table v-else :data="cartItems" style="width: 100%">
        <el-table-column label="商品名称">
          <template #default="{ row }">
            <el-link type="primary" @click="$router.push('/product/' + row.productId)">
              {{ row.productName || '商品#' + row.productId }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="120">
          <template #default="{ row }">
            ¥{{ unitPrice(row) }}
          </template>
        </el-table-column>
        <el-table-column label="数量" width="200">
          <template #default="{ row }">
            <el-input-number v-model="row.quantity" :min="1" :max="99" size="small"
              @change="(val) => updateQuantity(row, val)" />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="120">
          <template #default="{ row }">
            ¥{{ subtotal(row) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-popconfirm title="确认移除该商品？" @confirm="removeItem(row)">
              <template #reference>
                <el-button type="danger" size="small" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="cart-footer" v-if="cartItems.length > 0">
        <span class="total-price">合计：¥{{ totalAmount }}</span>
        <el-button type="primary" size="large" @click="$router.push('/checkout')">去结算</el-button>
      </div>
    </div>
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCartList, updateCartItem, removeCartItem } from '@/api/cart'
import { useCartStore } from '@/store/cart'
import NavHeader from '@/components/common/NavHeader.vue'
import AppFooter from '@/components/common/AppFooter.vue'

const cartStore = useCartStore()
const cartItems = ref([])

onMounted(async () => {
  try {
    const res = await getCartList()
    cartItems.value = res.data || []
    cartStore.cartCount = cartItems.value.reduce((s, i) => s + i.quantity, 0)
  } catch {
    cartItems.value = []
  }
})

function unitPrice(row) {
  return (row.productPrice || 0).toFixed(2)
}

function subtotal(row) {
  return ((row.productPrice || 0) * row.quantity).toFixed(2)
}

const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, row) => {
    return sum + (row.productPrice || 0) * row.quantity
  }, 0).toFixed(2)
})

async function updateQuantity(row, val) {
  try {
    await updateCartItem(row.id, { quantity: val })
    cartStore.fetchCartCount()
  } catch {
    row.quantity = val
  }
}

async function removeItem(row) {
  try {
    await removeCartItem(row.id)
    cartItems.value = cartItems.value.filter(i => i.id !== row.id)
    cartStore.fetchCartCount()
    ElMessage.success('已移除')
  } catch {
    ElMessage.error('移除失败')
  }
}
</script>

<style scoped>
.cart-page {
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
.cart-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 24px;
  margin-top: 24px;
}
.total-price {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}
</style>
