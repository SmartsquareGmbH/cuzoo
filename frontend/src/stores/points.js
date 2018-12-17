import Vue from "vue"
import Vuex from "vuex"

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        contactPoints: [],
        contactNames: [],
        editedIndex: -1,
        editedCPoint: {
            value: false,
            id: 0,
            title: "",
            contact: {},
            date: "",
            comment: "",
            type: ""
        }
    },
    getters: {
        getContactPoints: state => state.contactPoints,
        getContactNames: state => state.contactNames,
        getEditedIndex: state => state.editedIndex
    },
    mutations: {
        storeContactPoints(state, payload) {
            state.contactPoints = payload.contactPoints
        },
        storeContactNames(state, payload) {
            state.contactNames = payload.contactNames
        },
        storeEditedCPointDetails(state, payload) {
            state.editedIndex = payload.editedIndex,
            state.editedCPoint = payload.editedCPoint
        }
    }
});
