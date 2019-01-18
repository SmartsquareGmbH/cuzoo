import Vue from "vue"
import Vuex from "vuex"

Vue.use(Vuex);

export default {
    state: {
        darkState: true,
        authorized: false,
        username: '',
        password: ''
    },
    getters: {
        getDarkState: state => state.darkState,
        authorized: state => state.authorized,
        username: state => state.username,
        password: state => state.password,
    },
    mutations: {
        storeDarkState(state, payload) {
            state.darkState = payload.darkState
        },
        storeLogData(state, payload) {
            state.authorized = payload.authorized,
            state.username = payload.username,
            state.password = payload.password
        }
    }
};
