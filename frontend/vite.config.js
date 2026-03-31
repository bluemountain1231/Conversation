import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    host: '0.0.0.0',
    port: 5173,
    allowedHosts: ['frp-sea.com'], 
    proxy: {
      '/api': {
        target: 'https://localhost:8080',
        changeOrigin: true,
        secure: false
      },
      '/uploads': {
        target: 'https://localhost:8080',
        changeOrigin: true,
        secure: false
      },
      '/ws': {
        target: 'https://localhost:8080',
        changeOrigin: true,
        secure: false,
        ws: true
      }
    }
  }
})
