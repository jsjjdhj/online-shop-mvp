# 在线购物平台 MVP - RESTful API 接口文档

## 用户模块
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/user/register | 用户注册 |
| POST | /api/user/login | 用户登录 |
| GET | /api/user/info | 获取当前用户信息(需登录) |

## 商品模块
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/products | 商品列表(分页+模糊搜索) |
| GET | /api/products/{id} | 商品详情 |

## 购物车模块
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/cart | 购物车列表 |
| POST | /api/cart | 添加商品到购物车 |
| PUT | /api/cart/{cartId} | 修改购物车商品数量 |
| DELETE | /api/cart/{cartId} | 删除购物车商品 |

## 订单模块
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/orders | 提交订单(结算) |
| GET | /api/orders | 订单列表 |
| GET | /api/orders/{orderId} | 订单详情 |
| POST | /api/orders/{orderId}/pay | 付款 |
| POST | /api/orders/{orderId}/cancel | 取消订单 |

## 管理员模块
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/admin/products | 新增商品 |
| PUT | /api/admin/products/{id} | 编辑商品 |
| DELETE | /api/admin/products/{id} | 下架商品 |
| POST | /api/admin/orders/{orderId}/confirm | 确认订单 |
| POST | /api/admin/orders/{orderId}/ship | 发货 |
| GET | /api/admin/orders | 订单管理列表 |
