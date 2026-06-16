import request from '@/utils/request'

export function getCartList() {
  return request.get('/cart')
}

export function addToCart(data) {
  return request.post('/cart', data)
}

export function updateCartItem(cartId, data) {
  return request.put(`/cart/${cartId}`, data)
}

export function removeCartItem(cartId) {
  return request.delete(`/cart/${cartId}`)
}
