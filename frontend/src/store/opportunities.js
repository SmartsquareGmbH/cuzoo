import api from '../utils/http-common';

export default {
    state: {
        opportunities: [],
        editedIndex: -1,
        editedOpportunity: {
            value: false,
            id: 0,
            title: "",
            state: "Lead",
            description: ""
        }
    },
    getters: {
        opportunities: state => state.opportunities,
        editedOpportunity: state => state.editedOpportunity
    },
    mutations: {
        storeOpportunities(state, payload) {
            state.opportunities = payload.opportunities
        },
        storeEditedOpportunityDetails(state, payload) {
            state.editedIndex = payload.editedIndex,
            state.editedOpportunity = payload.editedOpportunity
        }
    },
    actions: {
        getOpportunities() {
            return api.get('opportunity/get').then(res => {
                let opportunities = res.data;

                this.commit({
                    type: 'storeOpportunities',
                    opportunities: opportunities
                })
            }).catch(error => {
                console.log(error);
            });
        }
    }
}