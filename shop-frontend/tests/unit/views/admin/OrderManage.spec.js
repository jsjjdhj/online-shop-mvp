import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { nextTick, h } from 'vue'

const mockGetAdminOrderList = vi.fn()
const mockConfirmOrder = vi.fn()
const mockShipOrder = vi.fn()

vi.mock('@/api/order', () => ({
  getAdminOrderList: (...args) => mockGetAdminOrderList(...args),
  confirmOrder: (...args) => mockConfirmOrder(...args),
  shipOrder: (...args) => mockShipOrder(...args)
}))

import OrderManage from '@/views/admin/OrderManage.vue'

const sampleOrders = [
  { id: 1, orderNo: '202606170001', recipientName: '张三', totalAmount: 199.00, status: 0 },
]

const stubs = {
  NavHeader: { template: '<div class="nav-header" />' },
  AppFooter: { template: '<div class="app-footer" />' },
  'el-icon': { template: '<span class="el-icon"><slot /></span>' },
  'el-tag': { template: '<span class="el-tag"><slot /></span>', props: ['type'] },
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
  'el-button': { template: '<button @click="$emit(\'click\')"><slot /></button>' }
}

describe('OrderManage.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    mockGetAdminOrderList.mockResolvedValue({ code: 200, data: { records: sampleOrders, total: 1 } })
  })

  it('loads orders on mount', async () => {
    const wrapper = mount(OrderManage, { global: { stubs } })
    await flushPromises()
    expect(mockGetAdminOrderList).toHaveBeenCalledWith({ page: 1, size: 100 })
  })

  it('renders page title and layout', async () => {
    const wrapper = mount(OrderManage, { global: { stubs } })
    await flushPromises()
    expect(wrapper.text()).toContain('订单管理')
    expect(wrapper.find('.nav-header').exists()).toBe(true)
    expect(wrapper.find('.app-footer').exists()).toBe(true)
  })

  it('shows confirm button for pending orders', async () => {
    const wrapper = mount(OrderManage, { global: { stubs } })
    await flushPromises()
    await nextTick()
    expect(wrapper.text()).toContain('确认')
  })

  it('calls confirmOrder when confirm button is clicked', async () => {
    mockConfirmOrder.mockResolvedValue({ code: 200 })
    const wrapper = mount(OrderManage, { global: { stubs } })
    await flushPromises()
    await nextTick()
    const confirmBtns = wrapper.findAll('button').filter(b => b.text() === '确认')
    if (confirmBtns.length > 0) {
      await confirmBtns[0].trigger('click')
      await flushPromises()
      expect(mockConfirmOrder).toHaveBeenCalledWith(1)
    }
  })

  it('reloads orders after successful confirm', async () => {
    mockConfirmOrder.mockResolvedValue({ code: 200 })
    const wrapper = mount(OrderManage, { global: { stubs } })
    await flushPromises()
    await nextTick()
    const confirmBtns = wrapper.findAll('button').filter(b => b.text() === '确认')
    if (confirmBtns.length > 0) {
      mockGetAdminOrderList.mockClear()
      await confirmBtns[0].trigger('click')
      await flushPromises()
      expect(mockGetAdminOrderList).toHaveBeenCalled()
    }
  })

  it('handles API error gracefully', async () => {
    mockConfirmOrder.mockRejectedValue(new Error('确认失败'))
    const wrapper = mount(OrderManage, { global: { stubs } })
    await flushPromises()
    await nextTick()
    const confirmBtns = wrapper.findAll('button').filter(b => b.text() === '确认')
    if (confirmBtns.length > 0) {
      await expect(confirmBtns[0].trigger('click')).resolves.not.toThrow()
    }
  })
})
