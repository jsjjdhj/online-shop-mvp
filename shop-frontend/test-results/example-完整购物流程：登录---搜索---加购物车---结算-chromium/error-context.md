# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: example.spec.js >> 完整购物流程：登录 -> 搜索 -> 加购物车 -> 结算
- Location: tests\e2e\example.spec.js:8:1

# Error details

```
Test timeout of 30000ms exceeded.
```

```
Error: page.fill: Test timeout of 30000ms exceeded.
Call log:
  - waiting for locator('input[placeholder="用户名"]')
    - locator resolved to <input type="text" tabindex="0" placeholder="用户名" id="el-id-5520-3" autocomplete="off" class="el-input__inner"/>
    - fill("testuser")
  - attempting fill action
    - waiting for element to be visible, enabled and editable

```

# Page snapshot

```yaml
- generic [ref=e4]:
  - generic [ref=e5]:
    - heading "用户登录" [level=2] [ref=e6]
    - generic [ref=e7]:
      - generic [ref=e11]:
        - img [ref=e14]
        - textbox "用户名" [active] [ref=e16]
      - generic [ref=e20]:
        - img [ref=e23]
        - textbox "密码" [ref=e26]
      - button "登录" [ref=e29] [cursor=pointer]:
        - generic [ref=e30]: 登录
    - generic [ref=e31]:
      - text: 还没有账号？
      - generic [ref=e33] [cursor=pointer]: 立即注册
  - generic [ref=e34]: 软件质量与测试课 2025-2026-2 学期
```

# Test source

```ts
  1  | import { test, expect } from '@playwright/test'
  2  | 
  3  | test('首页包含课程标识', async ({ page }) => {
  4  |   await page.goto('http://localhost:3000')
  5  |   await expect(page.locator('text=软件质量与测试课 2025-2026-2 学期')).toBeVisible()
  6  | })
  7  | 
  8  | test('完整购物流程：登录 -> 搜索 -> 加购物车 -> 结算', async ({ page }) => {
  9  |   await page.goto('http://localhost:3000/login')
> 10 |   await page.fill('input[placeholder="用户名"]', 'testuser')
     |              ^ Error: page.fill: Test timeout of 30000ms exceeded.
  11 |   await page.fill('input[placeholder="密码"]', 'Test1234')
  12 |   await page.click('button:has-text("登录")')
  13 |   await expect(page).toHaveURL('http://localhost:3000/')
  14 | 
  15 |   await page.fill('input[placeholder="搜索商品"]', '测试商品')
  16 |   await page.click('button:has-text("搜索")')
  17 |   await page.click('.el-card')
  18 |   await page.click('button:has-text("加入购物车")')
  19 |   await expect(page.locator('text=已成功加入购物车')).toBeVisible()
  20 | 
  21 |   await page.click('text=购物车')
  22 |   await page.click('button:has-text("去结算")')
  23 | 
  24 |   await page.fill('input[placeholder="请输入收货人姓名"]', '张三')
  25 |   await page.fill('input[placeholder="请输入11位手机号码"]', '13800138000')
  26 |   await page.fill('textarea[placeholder="请输入详细地址"]', '北京市海淀区测试路100号')
  27 |   await page.click('button:has-text("提交订单")')
  28 |   await expect(page.locator('text=订单提交成功')).toBeVisible()
  29 | })
  30 | 
```