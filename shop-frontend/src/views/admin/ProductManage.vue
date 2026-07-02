<template>
  <div class="admin-products">
    <NavHeader />
    <div class="main-content">
      <div class="header">
        <h2>商品管理</h2>
        <el-button type="primary" @click="showDialog = true">新增商品</el-button>
      </div>

      <el-table :data="products" stripe>
        <el-table-column prop="name" label="商品名称" />
        <el-table-column prop="price" label="价格" width="120" />
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">{{ row.status === 0 ? '上架' : '下架' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-cell">
              <el-button size="small" @click="editProduct(row)">编辑</el-button>
              <el-popconfirm title="确认下架该商品？" @confirm="handleDelete(row.id)">
                <template #reference>
                  <el-button size="small" type="danger" link>下架</el-button>
                </template>
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑 对话框 -->
    <el-dialog v-model="showDialog" :title="isEdit ? '编辑商品' : '新增商品'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0.01" :max="999999.99" :precision="2" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" :max="99999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">确认</el-button>
      </template>
    </el-dialog>

    <AppFooter />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminProductList, createProduct, updateProduct, deleteProduct } from '@/api/product'
import NavHeader from '@/components/common/NavHeader.vue'
import AppFooter from '@/components/common/AppFooter.vue'

const products = ref([])
const showDialog = ref(false)
const isEdit = ref(false)
const editingId = ref(null)
const saving = ref(false)
const formRef = ref(null)

const form = reactive({ name: '', description: '', price: 0.01, stock: 1 })
const rules = {
  name: [
    { required: true, message: '请输入商品名称', trigger: 'blur' },
    { min: 2, max: 50, message: '商品名称长度为2-50个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入商品描述', trigger: 'blur' },
    { min: 10, max: 500, message: '商品描述长度为10-500个字符', trigger: 'blur' }
  ],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

onMounted(() => loadProducts())

async function loadProducts() {
  const res = await getAdminProductList({ page: 1, size: 100 })
  products.value = res.data.records || []
}

function editProduct(row) {
  isEdit.value = true
  editingId.value = row.id
  form.name = row.name
  form.description = row.description || ''
  form.price = row.price
  form.stock = row.stock
  showDialog.value = true
}

function resetForm() {
  isEdit.value = false
  editingId.value = null
  form.name = ''
  form.description = ''
  form.price = 0.01
  form.stock = 1
}

async function handleSave() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    if (isEdit.value) {
      await updateProduct(editingId.value, form)
      ElMessage.success('商品已更新')
    } else {
      await createProduct(form)
      ElMessage.success('商品已上架')
    }
    showDialog.value = false
    loadProducts()
  } finally {
    saving.value = false
  }
}

async function handleDelete(id) {
  try {
    await deleteProduct(id)
    ElMessage.success('商品已下架')
    loadProducts()
  } catch {}
}
</script>

<style scoped>
.admin-products {
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
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
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
