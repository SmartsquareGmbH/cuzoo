import api from '../utils/http-common'

export default {
    state: {
        companies: [],
        editedIndex: -1,
        editedCompany: {
            value: false,
            id: 0,
            name: "",
            street: "",
            zipCode: "",
            place: "",
            homepage: "",
            status: "",
            description: "",
            other: ""
        }
    },
    getters: {
        companies: state => state.companies,
        editedCompany: state => state.editedCompany,
        editedCompanyIndex: state => state.editedIndex
    },
    mutations: {
        storeCompanies(state, payload) {
            state.companies = payload.companies
        },
        storeEditedCompanyDetails(state, payload) {
            state.editedIndex = payload.editedIndex,
            state.editedCompany = payload.editedCompany
        }
    },
    actions: {
        getCompanies() {
            return api.get('company/get').then(response => {
                console.log(response.data);
                this.commit({
                    type: 'storeCompanies',
                    companies: response.data
                })
            }).catch(error => {
                console.log(error);
            });
        }
    }
};
