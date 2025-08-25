import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from 'tailwindcss'; // Use import statement
import autoprefixer from 'autoprefixer'; // Use import statement

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  css: {
    postcss: {
      plugins: [
        tailwindcss('./tailwind.config.js'), // No 'require' here, just the imported function
        autoprefixer(),
      ],
    },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8082', // Your Spring Boot backend
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '/api'),
      },
    },
  },
});
