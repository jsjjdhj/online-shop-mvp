import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getCartList } from '@/api/cart'

export const useCartStore = defineStore('cart', () => {
  const cartCount = ref(0)
  const cartItems = ref([])

  async function fetchCartCount() {
    try {
      const res = await getCartList()
      cartItems.value = res.data || []
      cartCount.value = cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
    } catch {
      cartCount.value = 0
    }
  }

  function incrementCount(num) {
    cartCount.value += num
  }

  function refreshCount() {
    fetchCartCount()
  }

  return { cartCount, cartItems, fetchCartCount, incrementCount, refreshCount }
})
