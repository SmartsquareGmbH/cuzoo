const VuetifyLoaderPlugin = require('vuetify-loader/lib/plugin')
module.exports = {
  runtimeCompiler: true,
  configureWebpack: {
    plugins: [
      new VuetifyLoaderPlugin()
    ]
  },

  pluginOptions: {
  }
}
