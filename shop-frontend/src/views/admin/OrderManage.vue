<template>
  <div class="admin-orders">
    <NavHeader />
    <div class="main-content">
      <h2>订单管理</h2>

      <el-table :data="orders" stripe>
        <el-table-column prop="orderNo" label="订单编号" width="200" />
        <el-table-column prop="recipientName" label="收货人" width="120" />
        <el-table-column prop="totalAmount" label="总金额" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-cell">
              <el-button v-if="row.status === 0" size="small" type="primary" @click="handleConfirm(row)">确认</el-button>
              <el-button v-if="row.status === 2" size="small" type="success" @click="handleShip(row)">发货</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminOrderList, confirmOrder, shipOrder } from '@/api/order'
import NavHeader from '@/components/common/NavHeader.vue'
import AppFooter from '@/components/common/AppFooter.vue'

const orders = ref([])

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
  const res = await getAdminOrderList({ page: 1, size: 100 })
  orders.value = res.data.records || []
}

async function handleConfirm(order) {
  try {
    await confirmOrder(order.id)
    ElMessage.success('订单已确认')
    loadOrders()
  } catch {}
}

async function handleShip(order) {
  try {
    await shipOrder(order.id)
    ElMessage.success('已发货')
    loadOrders()
  } catch {}
}
</script>

<style scoped>
.admin-orders {
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
</style>
