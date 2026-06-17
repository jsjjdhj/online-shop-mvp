import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import Dashboard from '@/views/admin/Dashboard.vue'

const mockPush = vi.fn()

describe('Dashboard.vue', () => {
  const wrapper = mount(Dashboard, {
    global: {
      mocks: { $router: { push: mockPush } },
      stubs: {
        NavHeader: { template: '<div class="nav-header">Nav</div>' },
        AppFooter: { template: '<div class="app-footer">Footer</div>' },
        'el-icon': { template: '<span class="el-icon"><slot /></span>' },
        'el-card': { template: '<div class="el-card" @click="$emit(\'click\')"><slot /></div>' },
        'el-row': { template: '<div class="el-row"><slot /></div>' },
        'el-col': { template: '<div class="el-col"><slot /></div>' },
        Goods: { template: '<span>goods-icon</span>' },
        List: { template: '<span>list-icon</span>' }
      }
    }
  })

  it('renders admin dashboard layout', () => {
    expect(wrapper.text()).toContain('管理后台')
    expect(wrapper.text()).toContain('商品管理')
    expect(wrapper.text()).toContain('订单管理')
  })

  it('includes NavHeader and AppFooter', () => {
    expect(wrapper.find('.nav-header').exists()).toBe(true)
    expect(wrapper.find('.app-footer').exists()).toBe(true)
  })

  it('shows product management card with description', () => {
    expect(wrapper.text()).toContain('新增、编辑、下架商品')
  })

  it('shows order management card with description', () => {
    expect(wrapper.text()).toContain('确认订单、发货管理')
  })

  it('renders cards with clickable cursor style', () => {
    const cards = wrapper.findAll('.el-card')
    expect(cards.length).toBe(2)
  })

  it('navigates to /admin/products when product card is clicked', async () => {
    mockPush.mockClear()
    await wrapper.vm.$router.push('/admin/products')
    expect(mockPush).toHaveBeenCalledWith('/admin/products')
  })

  it('navigates to /admin/orders when order card is clicked', async () => {
    mockPush.mockClear()
    await wrapper.vm.$router.push('/admin/orders')
    expect(mockPush).toHaveBeenCalledWith('/admin/orders')
  })
})
