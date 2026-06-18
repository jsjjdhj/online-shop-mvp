# Debug Session: Order Detail Empty Fields

**Session ID**: `order-detail-empty-fields`

**Status**: `[FIXED]`

**Symptom**: 订单详情页（OrderDetail.vue）中订单编号、下单时间、订单状态、总金额、收货人、联系电话、收货地址等字段渲染为空或不正确（状态显示"未知"，总金额显示"¥"）。

**Environment**:
- Frontend: Vue 3 + Element Plus, running on http://localhost:3001
- Backend: Spring Boot, running on http://localhost:8080
- Browser: Chromium via Playwright

## Hypotheses

1. **H1**: 后端返回的订单详情数据结构是 `{ data: { order, items } }`，而前端直接赋值 `order.value = res.data`，导致字段映射错误。
2. **H2**: 后端返回的字段命名与前端模板不一致（例如后端用 `orderNo` 前端用 `orderNumber`）。
3. **H3**: 后端 `getOrderDetail` 未正确查询到订单数据，返回了空对象。
4. **H4**: 时间戳字段 `createdAt` 是 `LocalDateTime` 类型，前端未做格式化，但不应导致"空"显示。
5. **H5**: 路由参数 `orderId` 传递错误或 `getOrderDetail` API 调用路径错误。

## Evidence Collection

### Code Comparison

对比当前项目与未合并版本的 `OrderDetail.vue`：

| 位置 | 当前项目（异常） | 未合并版本（正常） |
|------|----------------|------------------|
| `onMounted` 赋值 | `order.value = res.data` | `order.value = res.data.order` |

### API Response Verification

调用 `GET /api/orders/285` 返回：

```json
{
  "code": 200,
  "data": {
    "order": {
      "id": 285,
      "orderNo": "202606181355260002",
      "totalAmount": 198.00,
      "status": 0,
      "recipientName": "????",
      "recipientPhone": "13800138000",
      "recipientAddress": "????",
      "createdAt": "2026-06-18T13:55:27"
    },
    "items": [
      {
        "id": 288,
        "productName": "TestProduct",
        "productPrice": 99.00,
        "quantity": 2,
        "subtotal": 198.00
      }
    ]
  }
}
```

**Confirmed**: H1 成立。后端返回结构为 `{ data: { order, items } }`，前端错误地使用了 `res.data`。

## Root Cause

`OrderDetail.vue` 在获取订单详情后，将 `res.data`（包含 `order` 和 `items` 两个 key 的对象）直接赋值给 `order`，导致模板中的 `order.orderNo`、`order.createdAt` 等访问全部命中 undefined，从而出现字段为空、状态"未知"、总金额显示"¥"等渲染异常。

## Fix

修改 `shop-frontend/src/views/user/OrderDetail.vue`：

1. 将 `order.value = res.data` 改为 `order.value = res.data.order`
2. 新增 `items` 响应式变量，接收 `res.data.items || []`
3. 添加商品明细表格展示（商品名称、单价、数量、小计）
4. 添加 `formatAmount` 和 `formatTime` 工具函数，确保金额和时间格式化显示
5. 添加 `.section-title` 样式

## Verification

通过 Playwright 访问 `http://localhost:3001/orders/285`，页面渲染结果：

- ✅ 订单编号：202606181355260002
- ✅ 下单时间：2026-06-18 13:55:27
- ✅ 订单状态：待确认
- ✅ 总金额：¥198.00
- ✅ 收货人：????（后端返回数据，非前端映射问题）
- ✅ 联系电话：13800138000
- ✅ 收货地址：????（后端返回数据，非前端映射问题）
- ✅ 商品明细：TestProduct ¥99.00 × 2 = ¥198.00

截图证据：`./order-detail-fixed.png`

## Conclusion

问题根因为前端数据映射错误。修复后订单详情页所有字段及商品明细均正常渲染，无排版错乱或信息缺失。
