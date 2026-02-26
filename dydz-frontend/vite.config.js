import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:10001'
    }
  },
  build: {
    // 构建产物直接输出到后端 static 目录，打包后无需单独部署前端
    outDir: '../dydz-backend/src/main/resources/static',
    emptyOutDir: true
  }
});
