const port = process.env.port || 9528

module.exports = {
  publicPath: '/',
  outputDir: 'dist',
  assetsDir: 'static',
  lintOnSave: false,
  productionSourceMap: false,
  devServer: {
    port: Number(port),
    open: false,
    hot: true,
    proxy: {
      '/api': {
        target: process.env.VUE_APP_SERVER || 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  configureWebpack: {
    name: 'gas-station-admin',
    resolve: {
      alias: {
        '@': require('path').resolve(__dirname, 'src')
      }
    }
  }
}
