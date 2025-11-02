import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/login': 'http://localhost:8080/api/v1/auth/login',
      '/register': 'http://localhost:8080/api/v1/auth/register',
      '/refresh': 'http://localhost:8080/api/v1/auth/refresh',
      '/chat': 'http://localhost:8080/api/v1/chat'
    }
  }
})