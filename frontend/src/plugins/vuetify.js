import Vue from "vue"
import { transitions, VApp, VBtn, VFooter, VGrid, VIcon, VList, VNavigationDrawer, VToolbar, Vuetify } from "vuetify"
import "vuetify/src/stylus/app.styl"

Vue.use(Vuetify, {
  components: {
    VApp,
    VNavigationDrawer,
    VFooter,
    VList,
    VBtn,
    VIcon,
    VGrid,
    VToolbar,
    transitions,
  },
  iconfont: "md",
})
