import Vuetify from 'vuetify'
import '@babel/polyfill'
import Vue from "vue";
import './plugins/vuetify'
import App from "./App.vue";
import router from "./router";
import store from "./store";
import 'roboto-fontface/css/roboto/roboto-fontface.css'
import 'material-design-icons-iconfont/dist/material-design-icons.css'
import 'vuetify/dist/vuetify.min.css'

Vue.config.productionTip = false;

Vue.use(Vuetify, {
    theme: {
        primary: '#4FC3F7'
    }
});

const app = new Vue({
    router,
    store,
    render: h => h(App)
}).$mount("#app");