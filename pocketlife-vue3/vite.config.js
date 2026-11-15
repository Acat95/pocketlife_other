import { fileURLToPath, URL } from 'node:url'
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), 'POCKETLIFE_')
  const proxy = {
    '/api': {
      target: process.env.POCKETLIFE_GATEWAY_URL || env.POCKETLIFE_GATEWAY_URL || 'http://127.0.0.1:8000',
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api(?=\/|$)/, ''),
    },
  }
  return {
    plugins: [vue(), vueDevTools(), tailwindcss()],
    resolve: { alias: { '@': fileURLToPath(new URL('./src', import.meta.url)) } },
    server: { host: '127.0.0.1', proxy },
    preview: { host: '127.0.0.1', proxy },
  }
})
