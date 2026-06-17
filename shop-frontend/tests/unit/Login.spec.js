import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia } from 'pinia'
import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/user/Login.vue'
import ElementPlus from 'element-plus'

// Create a minimal router
const router = createRouter({
  history: createWebHistory(),
  routes: [{ path: '/', name: 'home', component: { template: '<div>Home</div>' } }]
})

describe('Login.vue', () => {
  it('渲染登录表单和课程标识', async () => {
    const wrapper = mount(Login, {
      global: {
        plugins: [createPinia(), router, ElementPlus]
      }
    })

    // 页面包含登录标题
    expect(wrapper.find('h2').text()).toContain('用户登录')

    // 包含用户名输入框
    const usernameInput = wrapper.find('input[placeholder="用户名"]')
    expect(usernameInput.exists()).toBe(true)

    // 包含密码输入框
    const passwordInput = wrapper.find('input[placeholder="密码"]')
    expect(passwordInput.exists()).toBe(true)

    // 包含登录按钮
    expect(wrapper.text()).toContain('登录')

    // 包含课程标识
    expect(wrapper.text()).toContain('软件质量与测试课')
  })

  it('包含注册链接', async () => {
    const wrapper = mount(Login, {
      global: {
        plugins: [createPinia(), router, ElementPlus]
      }
    })

    expect(wrapper.text()).toContain('立即注册')
    expect(wrapper.text()).toContain('还没有账号')
  })
})
