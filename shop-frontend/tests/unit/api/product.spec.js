import { describe, it, expect, vi } from 'vitest'

vi.mock('@/utils/request', () => ({
  default: { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() }
}))

import request from '@/utils/request'
import { getProductList, getProductDetail, createProduct, updateProduct, deleteProduct, getAdminProductList } from '@/api/product'

const mockReq = vi.mocked(request)

describe('API: product.js', () => {
  it('getProductList calls GET /products with params', async () => {
    mockReq.get.mockResolvedValue({ code: 200, data: { records: [], total: 0 } })
    await getProductList({ page: 1, size: 10, keyword: 'test' })
    expect(mockReq.get).toHaveBeenCalledWith('/products', { params: { page: 1, size: 10, keyword: 'test' } })
  })

  it('getProductDetail calls GET /products/:id', async () => {
    mockReq.get.mockResolvedValue({ code: 200, data: { id: 5 } })
    await getProductDetail(5)
    expect(mockReq.get).toHaveBeenCalledWith('/products/5')
  })

  it('createProduct calls POST /admin/products with data', async () => {
    mockReq.post.mockResolvedValue({ code: 200 })
    await createProduct({ name: '新产品', price: 99.99, stock: 10 })
    expect(mockReq.post).toHaveBeenCalledWith('/admin/products', { name: '新产品', price: 99.99, stock: 10 })
  })

  it('updateProduct calls PUT /admin/products/:id with data', async () => {
    mockReq.put.mockResolvedValue({ code: 200 })
    await updateProduct(3, { name: '更新名', price: 88.00, stock: 20 })
    expect(mockReq.put).toHaveBeenCalledWith('/admin/products/3', { name: '更新名', price: 88.00, stock: 20 })
  })

  it('deleteProduct calls DELETE /admin/products/:id', async () => {
    mockReq.delete.mockResolvedValue({ code: 200 })
    await deleteProduct(7)
    expect(mockReq.delete).toHaveBeenCalledWith('/admin/products/7')
  })

  it('getAdminProductList calls GET /admin/products with params', async () => {
    mockReq.get.mockResolvedValue({ code: 200, data: { records: [], total: 0 } })
    await getAdminProductList({ page: 1, size: 50 })
    expect(mockReq.get).toHaveBeenCalledWith('/admin/products', { params: { page: 1, size: 50 } })
  })

  it('rejects on network error', async () => {
    mockReq.get.mockRejectedValue(new Error('Network Error'))
    await expect(getProductList({})).rejects.toThrow('Network Error')
  })
})
