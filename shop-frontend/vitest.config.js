import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  test: {
    environment: 'jsdom',
    setupFiles: ['tests/unit/setup.js'],
    include: ['tests/unit/**/*.spec.js'],
    reporters: [
      'verbose',
      ['junit', { outputFile: 'test-results/junit.xml', suiteName: 'shop-frontend-unit-tests' }]
    ],
    outputFile: {
      junit: 'test-results/junit.xml'
    }
  }
})
