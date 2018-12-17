import Vue from "vue"
import Vuex from "vuex"

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        contactPoints: [],
        contactNames: []
    },
    getters: {
        getContactPoints: state => state.contactPoints,
        getContactNames: state => state.contactNames
    },
    mutations: {
        storeContactPoints(state, payload) {
            state.contactPoints = payload.contactPoints
        },
        storeContactNames(state, payload) {
            state.contactNames = payload.contactNames
        }
    }
});
