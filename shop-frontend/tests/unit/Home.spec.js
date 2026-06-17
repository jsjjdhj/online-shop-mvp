import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia } from 'pinia'
import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/user/Home.vue'
import ElementPlus from 'element-plus'

vi.mock('@/api/product', () => ({
  getProductList: vi.fn(() => Promise.resolve({
    code: 200,
    data: { records: [], total: 0 },
    message: 'success'
  }))
}))

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/product/:id', name: 'product', component: { template: '<div>Product</div>' } },
    { path: '/', name: 'home', component: { template: '<div>Home</div>' } }
  ]
})

describe('Home.vue', () => {
  it('渲染搜索栏', async () => {
    const wrapper = mount(Home, {
      global: {
        plugins: [createPinia(), router, ElementPlus],
        stubs: {
          NavHeader: { template: '<div class="nav-header-stub">NavHeader</div>' },
          AppFooter: { template: '<div class="app-footer-stub">AppFooter</div>' }
        }
      }
    })

    await new Promise(resolve => setTimeout(resolve, 0))

    // 包含搜索输入框
    const searchInput = wrapper.find('input[placeholder="搜索商品"]')
    expect(searchInput.exists()).toBe(true)

    // 包含搜索按钮
    expect(wrapper.text()).toContain('搜索')
  })

  it('渲染页面底部课程标识', async () => {
    const wrapper = mount(Home, {
      global: {
        plugins: [createPinia(), router, ElementPlus],
        stubs: {
          NavHeader: { template: '<div />' },
          AppFooter: { template: '<div>软件质量与测试课 2025-2026-2 学期</div>' }
        }
      }
    })

    await new Promise(resolve => setTimeout(resolve, 0))

    expect(wrapper.text()).toContain('软件质量与测试课')
  })
})
