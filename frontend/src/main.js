import Vue from "vue"
import Vuetify from "vuetify/lib"
import App from "./App.vue"
import router from "./router"
import store from "./plugins/vuex"
import "@babel/polyfill"
import "roboto-fontface/css/roboto/roboto-fontface.css"
import "material-design-icons-iconfont/dist/material-design-icons.css"
import "emoji-mart-vue-fast/css/emoji-mart.css"
import "vuetify/src/stylus/app.styl"
import "plugins/hotjar"

import VueTruncateFilter from "vue-truncate-filter"
import VueClip from "vue-clip"
import PerfectScrollbar from "vue2-perfect-scrollbar"
import "vue2-perfect-scrollbar/dist/vue2-perfect-scrollbar.css"

Vue.config.productionTip = false

Vue.use(Vuetify, {
  theme: {
    primary: "#4FC3F7",
  },
})

Vue.use(VueTruncateFilter)
Vue.use(VueClip)
Vue.use(PerfectScrollbar)

new Vue({
  router,
  store,
  render: (h) => h(App),
}).$mount("#app")
