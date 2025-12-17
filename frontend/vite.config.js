import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  // === 新增下面这段 server 配置 ===
  server: {
    port: 5173, // 前端端口
    proxy: {
      // 这里的 '/api' 是我们自己约定的暗号
      // 意思是：凡是发给 '/api' 的请求，都自动转发给后端 8080
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '') // 去掉 /api 前缀再发给后端
      },
      '/uploads': {
        target: 'http://localhost:8080', // 后端地址
        changeOrigin: true
        // 注意：这里不需要 rewrite，因为后端确实就是映射的 /uploads 路径
      }
    }
  }
})