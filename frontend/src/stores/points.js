import Vue from "vue"
import Vuex from "vuex"

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        contactPoints: [],
        sortedContactPoints: [],
        contactNames: [],
        editedIndex: -1,
        editedContactPoint: {
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
        getSortedContactPoints: state => state.sortedContactPoints,
        getContactNames: state => state.contactNames,
        getEditedIndex: state => state.editedIndex
    },
    mutations: {
        storeContactPoints(state, payload) {
            state.contactPoints = payload.contactPoints,
            state.sortedContactPoints = payload.sortedContactPoints
        },
        storeContactNames(state, payload) {
            state.contactNames = payload.contactNames
        },
        storeEditedContactPointDetails(state, payload) {
            state.editedIndex = payload.editedIndex,
            state.editedContactPoint = payload.editedContactPoint
        }
    }
});
