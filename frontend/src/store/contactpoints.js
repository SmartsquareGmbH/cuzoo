import api from '../utils/http-common';

const datefns = require('date-fns');

export default {
    state: {
        labels: [],
        types: [],
        contactPoints: [],
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
            types: [],
            labels: []
        }
    },
    getters: {
        contactPoints: state => state.contactPoints,
        contactNames: state => state.contactNames,
        editedContactPoint: state => state.editedContactPoint,
        editedContactPointIndex: state => state.editedIndex,
        contactPointLabels: state => state.labels,
        contactPointTypes: state => state.types
    },
    mutations: {
        storeContactPoints(state, payload) {
            state.contactPoints = payload.contactPoints
        },
        storeEditedContactPointDetails(state, payload) {
            state.editedIndex = payload.editedIndex,
                state.editedContactPoint = payload.editedContactPoint
        },
        storeContactPointLabels(state, payload) {
            state.labels = payload.labels
        },
        storeContactPointTypes(state, payload) {
            state.types = payload.types
        }
    },
    actions: {
        getContactPoints() {
            return api.get('point/get').then(response => {
                let contactPoints = response.data;

                contactPoints.forEach(contactPoint => {
                    contactPoint.labels = contactPoint.labels.map(label => {
                        return label.title;
                    });

                    contactPoint.types = contactPoint.types.map(label => {
                        return label.title;
                    });
                });


                this.commit({
                    type: 'storeContactPoints',
                    contactPoints: contactPoints.sort(compareContactPoints)
                })
            }).catch(error => {
                console.log(error);
            });
        }
    }
}

function compareContactPoints(a, b) {
    if (datefns.compareAsc(a.date, b.date) === 0) {
        if (a.id < b.id)
            return 1;
        if (a.id > b.id)
            return -1;
    } else {
        return datefns.compareAsc(b.date, a.date);
    }
}