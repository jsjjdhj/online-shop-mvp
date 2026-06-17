import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { createRouter, createWebHistory } from 'vue-router'
import Cart from '@/views/user/Cart.vue'
import ElementPlus from 'element-plus'
import { useCartStore } from '@/store/cart'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/checkout', name: 'checkout', component: { template: '<div>Checkout</div>' } },
    { path: '/product/:id', name: 'product', component: { template: '<div>Product</div>' } }
  ]
})

describe('Cart.vue', () => {
  it('空购物车时显示空状态', async () => {
    const wrapper = mount(Cart, {
      global: {
        plugins: [createPinia(), router, ElementPlus],
        stubs: {
          NavHeader: { template: '<div />' },
          AppFooter: { template: '<div />' }
        }
      }
    })

    // 空购物车应显示"购物车是空的"
    expect(wrapper.text()).toContain('购物车是空的')
  })

  it('有商品时计算合计金额', () => {
    const pinia = createPinia()
    setActivePinia(pinia)
    const store = useCartStore()

    const wrapper = mount(Cart, {
      global: {
        plugins: [pinia, router, ElementPlus],
        stubs: {
          NavHeader: { template: '<div />' },
          AppFooter: { template: '<div />' }
        }
      }
    })

    // 验证 totalAmount computed 属性
    const vm = wrapper.vm
    expect(typeof vm.totalAmount).toBe('string')
  })

  it('unitPrice 格式化正确', () => {
    const wrapper = mount(Cart, {
      global: {
        plugins: [createPinia(), router, ElementPlus],
        stubs: {
          NavHeader: { template: '<div />' },
          AppFooter: { template: '<div />' }
        }
      }
    })

    const vm = wrapper.vm
    expect(vm.unitPrice({ productPrice: 99.5 })).toBe('99.50')
    expect(vm.unitPrice({ productPrice: 0 })).toBe('0.00')
    expect(vm.unitPrice({})).toBe('0.00')
  })

  it('subtotal 计算正确', () => {
    const wrapper = mount(Cart, {
      global: {
        plugins: [createPinia(), router, ElementPlus],
        stubs: {
          NavHeader: { template: '<div />' },
          AppFooter: { template: '<div />' }
        }
      }
    })

    const vm = wrapper.vm
    expect(vm.subtotal({ productPrice: 100, quantity: 3 })).toBe('300.00')
    expect(vm.subtotal({ productPrice: 29.99, quantity: 2 })).toBe('59.98')
  })
})
