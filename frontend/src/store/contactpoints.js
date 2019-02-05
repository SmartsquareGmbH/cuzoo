import api from '../utils/http-common';

const datefns = require('date-fns');

export default {
    state: {
        labels: [],
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
            type: "",
            labels: []
        }
    },
    getters: {
        contactPoints: state => state.contactPoints,
        contactNames: state => state.contactNames,
        editedContactPoint: state => state.editedContactPoint,
        editedContactPointIndex: state => state.editedIndex,
        contactPointLabels: state => state.labels
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