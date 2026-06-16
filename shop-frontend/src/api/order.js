import request from '@/utils/request'

export function submitOrder(data) {
  return request.post('/orders', data)
}

export function getOrderList(params) {
  return request.get('/orders', { params })
}

export function getOrderDetail(orderId) {
  return request.get(`/orders/${orderId}`)
}

export function payOrder(orderId) {
  return request.post(`/orders/${orderId}/pay`)
}

export function cancelOrder(orderId) {
  return request.post(`/orders/${orderId}/cancel`)
}

export function confirmOrder(orderId) {
  return request.post(`/admin/orders/${orderId}/confirm`)
}

export function shipOrder(orderId) {
  return request.post(`/admin/orders/${orderId}/ship`)
}

export function getAdminOrderList(params) {
  return request.get('/admin/orders', { params })
}
