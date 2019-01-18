import Vue from "vue"
import Vuex from "vuex"

Vue.use(Vuex);

export default {
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
        contactPoints: state => state.contactPoints,
        sortedContactPoints: state => state.sortedContactPoints,
        contactNames: state => state.contactNames,
        editedContactPoint: state => state.editedContactPoint,
        editedContactPointIndex: state => state.editedIndex
    },
    mutations: {
        storeContactPoints(state, payload) {
            state.contactPoints = payload.contactPoints,
            state.sortedContactPoints = payload.sortedContactPoints
        },
        storeEditedContactPointDetails(state, payload) {
            state.editedIndex = payload.editedIndex,
            state.editedContactPoint = payload.editedContactPoint
        }
    }
};
