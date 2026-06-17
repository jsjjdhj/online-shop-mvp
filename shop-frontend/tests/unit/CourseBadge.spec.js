import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import CourseBadge from '@/components/common/CourseBadge.vue'

describe('CourseBadge.vue', () => {
  it('渲染课程标识文本', () => {
    const wrapper = mount(CourseBadge)
    expect(wrapper.text()).toContain('软件质量与测试课')
    expect(wrapper.text()).toContain('2025-2026-2')
  })

  it('包含 course-badge CSS 类', () => {
    const wrapper = mount(CourseBadge)
    expect(wrapper.classes()).toContain('course-badge')
  })
})
