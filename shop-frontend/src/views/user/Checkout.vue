<template>
  <div class="checkout-page">
    <NavHeader />
    <div class="main-content">
      <h2>订单结算</h2>

      <el-card v-if="cartItems.length > 0" style="margin-bottom: 24px">
        <template #header>订单商品</template>
        <div v-for="item in cartItems" :key="item.id" class="cart-item">
          <span class="item-name">{{ item.productName || '商品#' + item.productId }}</span>
          <span class="item-qty">x{{ item.quantity }}</span>
          <span class="item-price">¥{{ ((item.productPrice || 0) * item.quantity).toFixed(2) }}</span>
        </div>
        <el-divider />
        <div class="total-line">
          <span>合计：</span>
          <span class="total-amount">¥{{ totalAmount }}</span>
        </div>
      </el-card>

      <el-card>
        <template #header>收货信息</template>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
          <el-form-item label="收货人" prop="recipientName">
            <el-input v-model="form.recipientName" placeholder="请输入收货人姓名" />
          </el-form-item>
          <el-form-item label="联系电话" prop="recipientPhone">
            <el-input v-model="form.recipientPhone" placeholder="请输入11位手机号码" maxlength="11" />
          </el-form-item>
          <el-form-item label="详细地址" prop="recipientAddress">
            <el-input v-model="form.recipientAddress" type="textarea" :rows="3" placeholder="请输入详细地址" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" @click="submitOrder" :loading="submitting">提交订单</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCartList } from '@/api/cart'
import { submitOrder as submitOrderApi } from '@/api/order'
import NavHeader from '@/components/common/NavHeader.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import { useOrderStore } from '@/store/order'

const router = useRouter()
const orderStore = useOrderStore()
const formRef = ref(null)
const cartItems = ref([])
const submitting = ref(false)

const form = reactive({
  recipientName: '',
  recipientPhone: '',
  recipientAddress: ''
})

const rules = {
  recipientName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  recipientPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的11位手机号码', trigger: 'blur' }
  ],
  recipientAddress: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

onMounted(async () => {
  try {
    const res = await getCartList()
    cartItems.value = res.data || []
    if (cartItems.value.length === 0) {
      ElMessage.warning('购物车中没有商品，无法结算')
      router.push('/cart')
    }
  } catch {
    router.push('/cart')
  }
})

const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => {
    return sum + (item.productPrice || 0) * item.quantity
  }, 0).toFixed(2)
})

async function submitOrder() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    const res = await submitOrderApi(form)
    orderStore.setLastOrder(res.data)
    ElMessage.success('订单提交成功')
    router.push('/order/success/' + res.data.id)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.checkout-page {
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
.cart-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}
.total-line {
  text-align: right;
  font-size: 16px;
}
.total-amount {
  color: #f56c6c;
  font-size: 22px;
  font-weight: bold;
}
</style>
