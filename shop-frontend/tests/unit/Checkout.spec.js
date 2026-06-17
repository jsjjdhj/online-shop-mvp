import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia } from 'pinia'
import { createRouter, createWebHistory } from 'vue-router'
import Checkout from '@/views/user/Checkout.vue'
import ElementPlus from 'element-plus'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/cart', name: 'cart', component: { template: '<div>Cart</div>' } },
    { path: '/order/success/:id', name: 'orderSuccess', component: { template: '<div>Success</div>' } }
  ]
})

describe('Checkout.vue', () => {
  it('渲染结算页面标题', () => {
    const wrapper = mount(Checkout, {
      global: {
        plugins: [createPinia(), router, ElementPlus],
        stubs: {
          NavHeader: { template: '<div />' },
          AppFooter: { template: '<div>软件质量与测试课 2025-2026-2 学期</div>' }
        }
      }
    })

    expect(wrapper.text()).toContain('订单结算')
  })

  it('渲染收货信息表单字段', () => {
    const wrapper = mount(Checkout, {
      global: {
        plugins: [createPinia(), router, ElementPlus],
        stubs: {
          NavHeader: { template: '<div />' },
          AppFooter: { template: '<div />' }
        }
      }
    })

    expect(wrapper.text()).toContain('收货人')
    expect(wrapper.text()).toContain('联系电话')
    expect(wrapper.text()).toContain('详细地址')
    expect(wrapper.text()).toContain('提交订单')
  })

  it('包含课程标识', () => {
    const wrapper = mount(Checkout, {
      global: {
        plugins: [createPinia(), router, ElementPlus],
        stubs: {
          NavHeader: { template: '<div />' },
          AppFooter: { template: '<div>软件质量与测试课 2025-2026-2 学期</div>' }
        }
      }
    })

    expect(wrapper.text()).toContain('软件质量与测试课')
  })
})
