import { describe, it, expect, vi } from 'vitest'

vi.mock('@/utils/request', () => ({
  default: { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() }
}))

import request from '@/utils/request'
import { submitOrder, getOrderList, getOrderDetail, payOrder, cancelOrder, confirmOrder, shipOrder, getAdminOrderList } from '@/api/order'

const mockReq = vi.mocked(request)

describe('API: order.js', () => {
  it('submitOrder calls POST /orders with data', async () => {
    mockReq.post.mockResolvedValue({ code: 200, data: { id: 1 } })
    const data = { recipientName: '张三', recipientPhone: '13800138000', recipientAddress: '北京' }
    await submitOrder(data)
    expect(mockReq.post).toHaveBeenCalledWith('/orders', data)
  })

  it('getOrderList calls GET /orders with params', async () => {
    mockReq.get.mockResolvedValue({ code: 200, data: { records: [], total: 0 } })
    await getOrderList({ page: 1, size: 10 })
    expect(mockReq.get).toHaveBeenCalledWith('/orders', { params: { page: 1, size: 10 } })
  })

  it('getOrderDetail calls GET /orders/:orderId', async () => {
    mockReq.get.mockResolvedValue({ code: 200, data: { id: 42 } })
    await getOrderDetail(42)
    expect(mockReq.get).toHaveBeenCalledWith('/orders/42')
  })

  it('payOrder calls POST /orders/:id/pay', async () => {
    mockReq.post.mockResolvedValue({ code: 200 })
    await payOrder(10)
    expect(mockReq.post).toHaveBeenCalledWith('/orders/10/pay')
  })

  it('cancelOrder calls POST /orders/:id/cancel', async () => {
    mockReq.post.mockResolvedValue({ code: 200 })
    await cancelOrder(5)
    expect(mockReq.post).toHaveBeenCalledWith('/orders/5/cancel')
  })

  it('confirmOrder calls POST /admin/orders/:id/confirm', async () => {
    mockReq.post.mockResolvedValue({ code: 200 })
    await confirmOrder(3)
    expect(mockReq.post).toHaveBeenCalledWith('/admin/orders/3/confirm')
  })

  it('shipOrder calls POST /admin/orders/:id/ship', async () => {
    mockReq.post.mockResolvedValue({ code: 200 })
    await shipOrder(7)
    expect(mockReq.post).toHaveBeenCalledWith('/admin/orders/7/ship')
  })

  it('getAdminOrderList calls GET /admin/orders with params', async () => {
    mockReq.get.mockResolvedValue({ code: 200, data: { records: [], total: 0 } })
    await getAdminOrderList({ page: 1, size: 20, status: 0 })
    expect(mockReq.get).toHaveBeenCalledWith('/admin/orders', { params: { page: 1, size: 20, status: 0 } })
  })

  it('rejects on network error', async () => {
    mockReq.get.mockRejectedValue(new Error('Server Error'))
    await expect(getOrderList({})).rejects.toThrow('Server Error')
  })
})
