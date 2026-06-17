import { test, expect } from '@playwright/test'

test('首页包含课程标识', async ({ page }) => {
  await page.goto('http://localhost:3000')
  await expect(page.locator('text=软件质量与测试课 2025-2026-2 学期')).toBeVisible()
})

test('完整购物流程：登录 -> 搜索 -> 加购物车 -> 结算', async ({ page }) => {
  await page.goto('http://localhost:3000/login')
  await page.fill('input[placeholder="用户名"]', 'testuser')
  await page.fill('input[placeholder="密码"]', 'Test1234')
  await page.click('button:has-text("登录")')
  await expect(page).toHaveURL('http://localhost:3000/')

  await page.fill('input[placeholder="搜索商品"]', '测试商品')
  await page.click('button:has-text("搜索")')
  await page.click('.el-card')
  await page.click('button:has-text("加入购物车")')
  await expect(page.locator('text=已成功加入购物车')).toBeVisible()

  await page.click('text=购物车')
  await page.click('button:has-text("去结算")')

  await page.fill('input[placeholder="请输入收货人姓名"]', '张三')
  await page.fill('input[placeholder="请输入11位手机号码"]', '13800138000')
  await page.fill('textarea[placeholder="请输入详细地址"]', '北京市海淀区测试路100号')
  await page.click('button:has-text("提交订单")')
  await expect(page.locator('text=订单提交成功')).toBeVisible()
})
