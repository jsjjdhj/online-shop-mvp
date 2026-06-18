<template>
  <div class="order-detail-page">
    <NavHeader />
    <div class="main-content" v-if="order">
      <el-descriptions title="订单信息" :column="1" border>
        <el-descriptions-item label="订单编号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ formatTime(order.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="statusType(order.status)">{{ statusText(order.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ formatAmount(order.totalAmount) }}</el-descriptions-item>
        <el-descriptions-item label="收货人">{{ order.recipientName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ order.recipientPhone }}</el-descriptions-item>
        <el-descriptions-item label="收货地址">{{ order.recipientAddress }}</el-descriptions-item>
      </el-descriptions>

      <h3 class="section-title">商品明细</h3>
      <el-table :data="items" border stripe style="width: 100%" empty-text="暂无商品明细">
        <el-table-column prop="productName" label="商品名称" min-width="180" />
        <el-table-column prop="productPrice" label="单价" width="120">
          <template #default="{ row }">¥{{ formatAmount(row.productPrice) }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="subtotal" label="小计" width="120">
          <template #default="{ row }">¥{{ formatAmount(row.subtotal) }}</template>
        </el-table-column>
      </el-table>
    </div>
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getOrderDetail } from '@/api/order'
import NavHeader from '@/components/common/NavHeader.vue'
import AppFooter from '@/components/common/AppFooter.vue'

const route = useRoute()
const order = ref(null)
const items = ref([])

const STATUS_MAP = {
  0: { text: '待确认', type: 'warning' },
  1: { text: '已确认', type: 'primary' },
  2: { text: '已付款', type: 'success' },
  3: { text: '已发货', type: '' },
  4: { text: '已完成', type: 'success' },
  5: { text: '已取消', type: 'info' }
}

function statusText(status) { return STATUS_MAP[status]?.text || '未知' }
function statusType(status) { return STATUS_MAP[status]?.type || 'info' }

function formatAmount(amount) {
  if (amount === undefined || amount === null) return '0.00'
  return Number(amount).toFixed(2)
}

function formatTime(time) {
  if (!time) return '-'
  const date = new Date(time)
  if (isNaN(date.getTime())) return time
  const pad = (n) => String(n).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

onMounted(async () => {
  try {
    const res = await getOrderDetail(route.params.orderId)
    order.value = res.data.order
    items.value = res.data.items || []
  } catch {
    ElMessage.error('加载订单详情失败')
  }
})
</script>

<style scoped>
.order-detail-page {
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
.section-title {
  margin: 24px 0 16px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
</style>
