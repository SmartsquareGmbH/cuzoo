export default {
    state: {
        labels: [],
        contactPoints: [],
        sortedContactPoints: [],
        contactNames: [],
        editedIndex: -1,
        editedContactPoint: {
            value: false,
            id: 0,
            title: "",
            contact: {},
            contactName: "",
            date: "",
            comment: "",
            type: "",
            labels: []
        }
    },
    getters: {
        contactPoints: state => state.contactPoints,
        sortedContactPoints: state => state.sortedContactPoints,
        contactNames: state => state.contactNames,
        editedContactPoint: state => state.editedContactPoint,
        editedContactPointIndex: state => state.editedIndex,
        contactPointLabels: state => state.labels
    },
    mutations: {
        storeContactPoints(state, payload) {
            state.contactPoints = payload.contactPoints,
                state.sortedContactPoints = payload.sortedContactPoints
        },
        storeEditedContactPointDetails(state, payload) {
            state.editedIndex = payload.editedIndex,
                state.editedContactPoint = payload.editedContactPoint
        },
        storeContactPointLabels(state, payload) {
            state.labels = payload.labels
        }
    }
}
