const { defineConfig, devices } = require('@playwright/test')
const path = require('path')

module.exports = defineConfig({
  testDir: './tests/e2e',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: [
    ['html', { outputFolder: 'e2e-report' }],
    ['junit', { outputFile: 'test-results/e2e-junit.xml' }]
  ],
  use: {
    baseURL: 'http://localhost:3000',
    trace: 'on-first-retry',
    headless: true,
    screenshot: 'only-on-failure',
    launchOptions: {
      executablePath: 'C:\\Users\\HP\\AppData\\Local\\ms-playwright\\chromium-1223\\chrome-win64\\chrome.exe'
    }
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] }
    }
  ]
})
