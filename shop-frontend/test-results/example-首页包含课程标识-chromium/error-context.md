# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: example.spec.js >> 首页包含课程标识
- Location: tests\e2e\example.spec.js:3:1

# Error details

```
Test timeout of 30000ms exceeded.
```

# Page snapshot

```yaml
- generic [ref=e4]:
  - generic [ref=e5]:
    - heading "用户登录" [level=2] [ref=e6]
    - generic [ref=e7]:
      - generic [ref=e11]:
        - img [ref=e14]
        - textbox "用户名" [ref=e16]
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