const proxyTarget = process.env.VUE_APP_PROXY_TARGET || 'http://127.0.0.1:9000';

module.exports = {
  devServer: {
    port: 8081,
    proxy: {
      '/api': {
        target: proxyTarget,
        changeOrigin: true
      },
      '/storage': {
        target: proxyTarget,
        changeOrigin: true
      }
    }
  }
};
