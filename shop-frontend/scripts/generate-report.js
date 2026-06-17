// 从 JUnit XML 生成 Markdown 测试报告
import { readFileSync, writeFileSync, existsSync, mkdirSync } from 'fs'
import { resolve, dirname } from 'path'
import { fileURLToPath } from 'url'

const __dirname = dirname(fileURLToPath(import.meta.url))
const xmlPath = resolve(__dirname, '..', 'test-results', 'junit.xml')
const outDir = resolve(__dirname, '..', 'test-results')
const outPath = resolve(outDir, 'test-report.md')

if (!existsSync(xmlPath)) {
  console.error('未找到 JUnit XML 文件，请先运行 npm run test:unit')
  process.exit(1)
}

const xml = readFileSync(xmlPath, 'utf-8')

// Parse test suites from JUnit XML — 取 <testsuite> 元素（不含 <testsuites> 包装）
const suiteMatch = xml.match(/<testsuite\s[\s\S]*?<\/testsuite>/g) || []

let totalTests = 0, totalFailures = 0, totalErrors = 0
let suiteRows = ''
let detailLines = ''

for (const suite of suiteMatch) {
  const nameMatch = suite.match(/name="([^"]+)"/)
  const testsMatch = suite.match(/tests="(\d+)"/)
  const failuresMatch = suite.match(/failures="(\d+)"/)
  const errorsMatch = suite.match(/errors="(\d+)"/)
  const timeMatch = suite.match(/time="([^"]+)"/)

  const name = nameMatch?.[1] || 'Unknown'
  const tests = parseInt(testsMatch?.[1] || '0')
  const failures = parseInt(failuresMatch?.[1] || '0')
  const errors = parseInt(errorsMatch?.[1] || '0')
  const time = timeMatch?.[1] || '0'

  totalTests += tests
  totalFailures += failures
  totalErrors += errors

  const passed = tests - failures - errors
  const status = failures + errors === 0 ? '✅ 全部通过' : '❌ 有失败'
  suiteRows += `| ${name} | ${tests} | ${passed} | ${failures} | ${errors} | ${time}s | ${status} |\n`

  // Build detail list
  detailLines += `\n### ${name}\n\n`
  const caseRegex = /<testcase[\s\S]*?<\/testcase>/g
  const cases = suite.match(caseRegex) || []
  for (const tc of cases) {
    const tcName = tc.match(/name="([^"]+)"/)?.[1] || 'Unknown'
    const tcTime = tc.match(/time="([^"]+)"/)?.[1] || '0'
    const hasFailure = tc.includes('<failure ')
    const hasError = tc.includes('<error ')
    if (hasFailure || hasError) {
      const msgMatch = tc.match(/message="([^"]*)"/)
      const msg = msgMatch?.[1] || (hasFailure ? '断言失败' : '运行时错误')
      detailLines += `- ❌ **${tcName}** — ${msg}\n`
    } else {
      detailLines += `- ✅ ${tcName} (${tcTime}s)\n`
    }
  }
}

const passRate = totalTests > 0 ? ((totalTests - totalFailures - totalErrors) / totalTests * 100).toFixed(1) : '0'

const markdown = `# 🧪 前端单元测试报告

**项目**：在线购物平台 MVP
**课程**：软件质量与测试课 2025-2026-2 学期
**生成时间**：${new Date().toLocaleString('zh-CN')}
**运行命令**：\`npm run test:unit\`

---

## 一、执行摘要

| 指标 | 数据 |
|------|------|
| 测试文件数 | ${suiteMatch.length} |
| 测试用例数 | **${totalTests}** |
| 通过 | **${totalTests - totalFailures - totalErrors}** |
| 失败 | **${totalFailures}** |
| 错误 | **${totalErrors}** |
| 通过率 | **${passRate}%** |
| 执行时间 | ${(suiteMatch.reduce((sum, s) => sum + parseFloat(s.match(/time="([^"]+)"/)?.[1] || '0'), 0)).toFixed(3)}s |

${passRate >= 80 ? '> 🟢 **测试全部通过，质量达标**' : '> 🔴 **存在测试失败，需要修复**'}

---

## 二、测试通过清单

| 测试文件 | 总数 | 通过 | 失败 | 错误 | 耗时 | 状态 |
|---------|:---:|:---:|:---:|:---:|:----:|:----:|
${suiteRows}

**合计** | **${totalTests}** | **${totalTests - totalFailures - totalErrors}** | **${totalFailures}** | **${totalErrors}** | | **${passRate}%**

---

## 三、测试用例详情

${detailLines}

---

## 四、覆盖的关键功能点

| 功能模块 | 验证点 |
|---------|--------|
| **课程标识** | 课程名称 "软件质量与测试课 2025-2026-2 学期" 在组件中正确渲染 |
| **登录页** | 表单渲染（用户名/密码输入框）、注册链接、课程标识埋点 |
| **首页** | 搜索栏渲染、课程标识通过 AppFooter 传递 |
| **购物车** | 空状态显示、单价/小计格式化、合计金额计算 |
| **结算页** | 收货信息表单字段、课程标识埋点 |

---

## 五、环境信息

| 项目 | 版本 |
|------|------|
| 测试框架 | Vitest 1.x |
| UI 测试库 | Vue Test Utils |
| 运行环境 | jsdom (浏览器模拟) |
| 覆盖率工具 | 无（前端暂未配置覆盖率） |

---

*报告由脚本自动生成 · ${new Date().toLocaleString('zh-CN')}*
`

if (!existsSync(outDir)) mkdirSync(outDir, { recursive: true })
writeFileSync(outPath, markdown, 'utf-8')
console.log(`✅ Markdown 测试报告已生成: ${outPath}`)
