import request from '@/utils/request'

export function getProductList(params) {
  return request.get('/products', { params })
}

export function getProductDetail(id) {
  return request.get(`/products/${id}`)
}

export function createProduct(data) {
  return request.post('/admin/products', data)
}

export function updateProduct(id, data) {
  return request.put(`/admin/products/${id}`, data)
}

export function deleteProduct(id) {
  return request.delete(`/admin/products/${id}`)
}

export function getAdminProductList(params) {
  return request.get('/admin/products', { params })
}
