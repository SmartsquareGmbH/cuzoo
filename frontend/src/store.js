import Vue from "vue"
import Vuex from "vuex"

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        darkState: true,
        authorized: false,
        username: '',
        password: ''
    },
    getters: {
        getDarkState: state => state.darkState,
        getAuthorized: state => state.authorized,
        getUsername: state => state.username,
        getPassword: state => state.password,
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
});
