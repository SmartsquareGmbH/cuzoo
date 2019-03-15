export default {
    state: {
        darkState: true,
        selectedCompanyOnDash: ''
    },
    getters: {
        darkState: state => state.darkState,
        selectedCompany: state => state.selectedCompanyOnDash
    },
    mutations: {
        storeDarkState(state, payload) {
            state.darkState = payload.darkState
        },
        storeSelectedCompany(state, payload) {
            state.selectedCompanyOnDash = payload.selectedCompanyOnDash
        }
    }
};
