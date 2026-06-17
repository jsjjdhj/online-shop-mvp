import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import AppFooter from '@/components/common/AppFooter.vue'

describe('AppFooter.vue', () => {
  it('renders copyright text', () => {
    const wrapper = mount(AppFooter, {
      global: { stubs: { CourseBadge: true } }
    })
    expect(wrapper.text()).toContain('© 2026 在线购物平台 MVP')
  })

  it('renders with correct scoped class', () => {
    const wrapper = mount(AppFooter, {
      global: { stubs: { CourseBadge: true } }
    })
    expect(wrapper.find('.app-footer').exists()).toBe(true)
    expect(wrapper.find('.footer-content').exists()).toBe(true)
  })

  it('includes CourseBadge component', () => {
    const wrapper = mount(AppFooter, {
      global: { stubs: { CourseBadge: { template: '<div class="course-badge">软件质量与测试课</div>' } } }
    })
    expect(wrapper.text()).toContain('软件质量与测试课')
  })
})
