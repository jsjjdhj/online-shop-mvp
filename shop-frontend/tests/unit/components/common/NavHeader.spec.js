import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { setActivePinia, createPinia } from 'pinia'

const mockPush = vi.hoisted(() => vi.fn())

vi.mock('vue-router', async (importOriginal) => {
  const mod = await importOriginal()
  return { ...mod, useRouter: () => ({ push: mockPush }), useRoute: () => ({}) }
})

vi.mock('@/api/cart', () => ({
  getCartList: vi.fn().mockResolvedValue({ code: 200, data: [] })
}))

import NavHeader from '@/components/common/NavHeader.vue'

const stubs = {
  'el-menu': { template: '<div><slot /></div>' },
  'el-menu-item': { template: '<div class="el-menu-item"><slot /></div>' },
  'el-sub-menu': { template: '<div><slot /><slot name="title" /></div>' },
  'el-badge': { template: '<span class="el-badge"><slot /></span>' },
  'el-icon': { template: '<span class="el-icon"><slot /></span>' },
  ShoppingCart: { template: '<span>cart-icon</span>' },
  User: { template: '<span>user-icon</span>' }
}

describe('NavHeader.vue', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    mockPush.mockClear()
  })

  it('renders app title and navigation items', () => {
    const wrapper = mount(NavHeader, { global: { mocks: { $router: { push: mockPush } }, stubs } })
    expect(wrapper.text()).toContain('在线购物平台')
    expect(wrapper.text()).toContain('我的订单')
    expect(wrapper.text()).toContain('购物车')
  })

  it('does not show user menu when not logged in', () => {
    const wrapper = mount(NavHeader, { global: { mocks: { $router: { push: mockPush } }, stubs } })
    expect(wrapper.text()).not.toContain('退出登录')
  })
})
