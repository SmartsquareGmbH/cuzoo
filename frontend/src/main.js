import Vue from "vue";
import Vuetify from 'vuetify/lib';
import VueClip from 'vue-clip';
import App from "./App.vue";
import router from "./router";
import store from './plugins/vuex';
import './plugins/datefns';
import '@babel/polyfill';
import 'roboto-fontface/css/roboto/roboto-fontface.css';
import 'material-design-icons-iconfont/dist/material-design-icons.css';
import 'vuetify/src/stylus/app.styl';


Vue.config.productionTip = false;
  
Vue.use(Vuetify, {
    theme: {
        primary: '#4FC3F7'
    }
});

Vue.use(VueClip);

new Vue({
    router,
    store,
    render: h => h(App)
}).$mount("#app");