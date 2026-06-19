<template>
  <div class="order-list-page">
    <NavHeader />
    <div class="main-content">
      <h2>我的订单</h2>

      <el-empty v-if="orders.length === 0 && !loading" description="暂无订单" />

      <el-table v-else :data="orders" style="width: 100%">
        <el-table-column prop="orderNo" label="订单编号" width="200" />
        <el-table-column label="下单时间" width="180">
          <template #default="{ row }">{{ row.createdAt }}</template>
        </el-table-column>
        <el-table-column label="总金额" width="120">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <div class="action-cell">
              <el-button size="small" @click="$router.push('/orders/' + row.id)">详情</el-button>
              <el-button v-if="row.status === 1" size="small" type="success" @click="handlePay(row)">付款</el-button>
              <el-popconfirm v-if="row.status === 0 || row.status === 1" title="确认取消订单？" @confirm="handleCancel(row)">
                <template #reference>
                  <el-button size="small" type="danger" link>取消</el-button>
                </template>
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar" v-if="total > 10">
        <el-pagination v-model:current-page="page" :page-size="10" :total="total" layout="prev, pager, next" @current-change="loadOrders" />
      </div>
    </div>
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getOrderList, payOrder, cancelOrder } from '@/api/order'
import NavHeader from '@/components/common/NavHeader.vue'
import AppFooter from '@/components/common/AppFooter.vue'

const orders = ref([])
const total = ref(0)
const page = ref(1)
const loading = ref(false)

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

onMounted(() => loadOrders())

async function loadOrders() {
  loading.value = true
  try {
    const res = await getOrderList({ page: page.value, size: 10 })
    orders.value = res.data.records || []
    total.value = res.data.total || 0
  } finally { loading.value = false }
}

async function handlePay(order) {
  try {
    await payOrder(order.id)
    ElMessage.success('付款成功')
    loadOrders()
  } catch {}
}

async function handleCancel(order) {
  try {
    await cancelOrder(order.id)
    ElMessage.success('订单已取消')
    loadOrders()
  } catch {}
}
</script>

<style scoped>
.order-list-page {
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
:deep(.el-table) {
  width: 100%;
}
:deep(.el-table .el-table__header-wrapper table),
:deep(.el-table .el-table__body-wrapper table) {
  width: 100% !important;
}
.action-cell {
  display: flex;
  flex-wrap: nowrap;
  align-items: center;
  gap: 8px;
  justify-content: flex-start;
}
.action-cell .el-button {
  margin-left: 0 !important;
  flex-shrink: 0;
}
.pagination-bar {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
