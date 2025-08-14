import { defineConfig, loadEnv } from 'vite'
import path from 'path'
import createVitePlugins from './vite/plugins'


const baseUrl = 'http://localhost:8080' // 后端接口

// https://vitejs.dev/config/
export default defineConfig(({ mode, command }) => {
  const env = loadEnv(mode, process.cwd())
  const { VITE_APP_ENV } = env
  return {
    // 部署生产环境和开发环境下的URL。
    // 默认情况下，vite 会假设你的应用是被部署在一个域名的根路径上
    // 例如 https://www.ruoyi.vip/。如果应用被部署在一个子路径上，你就需要用这个选项指定这个子路径。例如，如果你的应用被部署在 https://www.ruoyi.vip/admin/，则设置 baseUrl 为 /admin/。
    base: VITE_APP_ENV === 'production' ? '/' : '/',
    plugins: createVitePlugins(env, command === 'build'),
    resolve: {
      // https://cn.vitejs.dev/config/#resolve-alias
      alias: {
        // 设置路径
        '~': path.resolve(__dirname, './'),
        // 设置别名
        '@': path.resolve(__dirname, './src')
      },
      // https://cn.vitejs.dev/config/#resolve-extensions
      extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue']
    },
    // 打包配置
    build: {
      // https://vite.dev/config/build-options.html
      sourcemap: command === 'build' ? false : 'inline',
      outDir: 'dist',
      assetsDir: 'assets',
      chunkSizeWarningLimit: 2000,
      rollupOptions: {
        output: {
          chunkFileNames: 'static/js/[name]-[hash].js',
          entryFileNames: 'static/js/[name]-[hash].js',
          assetFileNames: 'static/[ext]/[name]-[hash].[ext]'
        }
      }
    },

    // vite 相关配置
    server: {
      port: 80,
      host: true,
      open: true,
      proxy: {
        // 原有 dev-api 代理
        '/dev-api': {
          target: baseUrl,
          changeOrigin: true,
          rewrite: (p) => p.replace(/^\/dev-api/, '')
        },
        // 原有 springdoc 代理
        '^/v3/api-docs/(.*)': {
          target: baseUrl,
          changeOrigin: true,
        },
        // ✅ 新增 rijks API 代理
        '/api/rijks': {
          target: 'https://waterwebservices.rijkswaterstaat.nl',
          changeOrigin: true,
          rewrite: path => path.replace(/^\/api\/rijks/, ''),
        },

        // ✅ Rijks REST API 代理：waterinfo.rws.nl
        '/api/waterinfo': {
          target: 'https://waterinfo.rws.nl',
          changeOrigin: true,
          rewrite: path => path.replace(/^\/api\/waterinfo/, '')
        },
        // ✅ Rijks Matroos + Lobith 代理：v14.rws.nl
        '/api/v14rws': {
          target: 'https://v14.rws.nl',
          changeOrigin: true,
          rewrite: path => path.replace(/^\/api\/v14rws/, '')
        },

        // 1) 更具体的 section 路由，必须优先匹配
        '^/api/vessels/section': {
          target: 'https://www.eurisportal.eu',
          changeOrigin: true,
          secure: false, // 如无 TLS 问题可去掉或设 true
          rewrite: (path) =>
            path.replace(/^\/api\/vessels\/section/, '/visuris/api/TracksV2/GetTracksBySection'),
        },

        // 2) 通用 vessels 路由（保留原来的 BBoxV2 重写）
        '/api/vessels': {
          target: 'https://www.eurisportal.eu',
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api\/vessels/, '/visuris/api/TracksV2/GetTracksByBBoxV2')
        },

        '/api/nts40': {
          target: 'https://www.vaarweginformatie.nl',
          changeOrigin: true,
          secure: true, // 若目标证书有问题可改成 false（仅在 dev 环境）
          rewrite: (path) => path.replace(/^\/api\/nts40/, '/fdd/nts40'),
        },



      }
    },





    css: {
      postcss: {
        plugins: [
          {
            postcssPlugin: 'internal:charset-removal',
            AtRule: {
              charset: (atRule) => {
                if (atRule.name === 'charset') {
                  atRule.remove()
                }
              }
            }
          }
        ]
      }
    }
  }
})
