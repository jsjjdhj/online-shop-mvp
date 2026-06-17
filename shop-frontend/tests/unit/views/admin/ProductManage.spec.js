import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { nextTick, h } from 'vue'

const mockGetAdminProductList = vi.fn()
const mockCreateProduct = vi.fn()
const mockUpdateProduct = vi.fn()
const mockDeleteProduct = vi.fn()

vi.mock('@/api/product', () => ({
  getAdminProductList: (...args) => mockGetAdminProductList(...args),
  createProduct: (...args) => mockCreateProduct(...args),
  updateProduct: (...args) => mockUpdateProduct(...args),
  deleteProduct: (...args) => mockDeleteProduct(...args)
}))

import ProductManage from '@/views/admin/ProductManage.vue'

const sampleProducts = [
  { id: 1, name: '测试商品A', price: 99.99, stock: 50, status: 0, description: '商品A描述' },
]

const stubs = {
  NavHeader: { template: '<div class="nav-header" />' },
  AppFooter: { template: '<div class="app-footer" />' },
  'el-icon': { template: '<span class="el-icon"><slot /></span>' },
  'el-button': { template: '<button><slot /></button>' },
  'el-table': { template: '<div class="el-table"><slot /></div>' },
  'el-table-column': {
    setup(props, { slots }) {
      return () => {
        if (slots.default) {
          return slots.default({ row: { status: 0, id: 1 } })
        }
        return h('span', props.label || '')
      }
    },
    props: ['prop', 'label', 'width']
  },
  'el-dialog': { template: '<div class="el-dialog"><slot /></div>' },
  'el-form': { template: '<form class="el-form"><slot /></form>', methods: { validate() { return Promise.resolve(true) } } },
  'el-form-item': { template: '<div class="el-form-item"><slot /></div>' },
  'el-input': { template: '<input class="el-input" />' },
  'el-input-number': { template: '<input class="el-input-number" />' },
  'el-popconfirm': { template: '<span class="el-popconfirm"><slot name="reference" /></span>' }
}

function mountProductManage() {
  return mount(ProductManage, { global: { stubs } })
}

describe('ProductManage.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    mockGetAdminProductList.mockResolvedValue({ code: 200, data: { records: sampleProducts, total: 1 } })
    mockCreateProduct.mockResolvedValue({ code: 200 })
    mockUpdateProduct.mockResolvedValue({ code: 200 })
    mockDeleteProduct.mockResolvedValue({ code: 200 })
  })

  it('loads products on mount and renders page title', async () => {
    const wrapper = mountProductManage()
    await flushPromises()
    expect(wrapper.text()).toContain('商品管理')
    expect(mockGetAdminProductList).toHaveBeenCalledWith({ page: 1, size: 100 })
  })

  it('includes NavHeader and AppFooter', () => {
    const wrapper = mountProductManage()
    expect(wrapper.find('.nav-header').exists()).toBe(true)
    expect(wrapper.find('.app-footer').exists()).toBe(true)
  })

  it('renders "新增商品" button', async () => {
    const wrapper = mountProductManage()
    await flushPromises()
    expect(wrapper.text()).toContain('新增商品')
  })

  it('renders edit and 下架 buttons', async () => {
    const wrapper = mountProductManage()
    await flushPromises()
    await nextTick()
    expect(wrapper.text()).toContain('编辑')
    expect(wrapper.text()).toContain('下架')
  })

  it('opens dialog when showDialog is set', () => {
    const wrapper = mountProductManage()
    expect(wrapper.vm.showDialog).toBe(false)
    wrapper.vm.showDialog = true
    expect(wrapper.vm.showDialog).toBe(true)
  })

  it('sets isEdit=false and default form for new product', () => {
    const wrapper = mountProductManage()
    wrapper.vm.showDialog = true
    expect(wrapper.vm.isEdit).toBe(false)
    expect(wrapper.vm.form.name).toBe('')
    expect(wrapper.vm.form.price).toBe(0.01)
    expect(wrapper.vm.form.stock).toBe(1)
  })

  it('resetForm clears all reactive form fields', () => {
    const wrapper = mountProductManage()
    wrapper.vm.isEdit = true
    wrapper.vm.editingId = 99
    wrapper.vm.form.name = '临时商品'
    wrapper.vm.form.description = 'desc'
    wrapper.vm.form.price = 25.00
    wrapper.vm.form.stock = 10
    wrapper.vm.resetForm()
    expect(wrapper.vm.isEdit).toBe(false)
    expect(wrapper.vm.editingId).toBeNull()
    expect(wrapper.vm.form.name).toBe('')
    expect(wrapper.vm.form.price).toBe(0.01)
    expect(wrapper.vm.form.stock).toBe(1)
  })

  it('editProduct fills form and opens dialog in edit mode', () => {
    const wrapper = mountProductManage()
    wrapper.vm.editProduct(sampleProducts[0])
    expect(wrapper.vm.isEdit).toBe(true)
    expect(wrapper.vm.editingId).toBe(1)
    expect(wrapper.vm.showDialog).toBe(true)
    expect(wrapper.vm.form.name).toBe('测试商品A')
    expect(wrapper.vm.form.price).toBe(99.99)
    expect(wrapper.vm.form.stock).toBe(50)
  })

  it('calls createProduct when saving a new product', async () => {
    const wrapper = mountProductManage()
    await flushPromises()
    wrapper.vm.form.name = '新商品'
    wrapper.vm.form.description = '新商品描述'
    wrapper.vm.form.price = 29.99
    wrapper.vm.form.stock = 100
    wrapper.vm.showDialog = true
    await wrapper.vm.handleSave()
    await flushPromises()
    expect(mockCreateProduct).toHaveBeenCalledWith({
      name: '新商品', description: '新商品描述', price: 29.99, stock: 100
    })
  })

  it('calls updateProduct when saving an existing product', async () => {
    const wrapper = mountProductManage()
    await flushPromises()
    wrapper.vm.isEdit = true
    wrapper.vm.editingId = 5
    wrapper.vm.form.name = '更新名称'
    wrapper.vm.form.description = '更新描述'
    wrapper.vm.form.price = 88.00
    wrapper.vm.form.stock = 30
    wrapper.vm.showDialog = true
    await wrapper.vm.handleSave()
    await flushPromises()
    expect(mockUpdateProduct).toHaveBeenCalledWith(5, {
      name: '更新名称', description: '更新描述', price: 88.00, stock: 30
    })
  })

  it('calls deleteProduct when handleDelete is called', async () => {
    const wrapper = mountProductManage()
    await flushPromises()
    await wrapper.vm.handleDelete(10)
    await flushPromises()
    expect(mockDeleteProduct).toHaveBeenCalledWith(10)
  })

  it('reloads product list after delete', async () => {
    const wrapper = mountProductManage()
    await flushPromises()
    mockGetAdminProductList.mockClear()
    await wrapper.vm.handleDelete(1)
    await flushPromises()
    expect(mockGetAdminProductList).toHaveBeenCalled()
  })

  it('handles deleteProduct API error gracefully', async () => {
    mockDeleteProduct.mockRejectedValue(new Error('删除失败'))
    const wrapper = mountProductManage()
    await flushPromises()
    await expect(wrapper.vm.handleDelete(99)).resolves.not.toThrow()
  })

  it('editProduct handles products with missing optional fields', () => {
    const wrapper = mountProductManage()
    wrapper.vm.editProduct({ id: 7, name: '部分字段', price: 15.00, stock: 3 })
    expect(wrapper.vm.isEdit).toBe(true)
    expect(wrapper.vm.editingId).toBe(7)
    expect(wrapper.vm.form.description).toBe('')
  })
})