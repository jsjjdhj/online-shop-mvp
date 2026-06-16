import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useOrderStore = defineStore('order', () => {
  const lastOrder = ref(null)

  function setLastOrder(order) {
    lastOrder.value = order
  }

  function clearLastOrder() {
    lastOrder.value = null
  }

  return { lastOrder, setLastOrder, clearLastOrder }
})
