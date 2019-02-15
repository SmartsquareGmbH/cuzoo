import api from '../utils/http-common'

export default {
    state: {
        labels: [],
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
            description: "",
            other: "",
            labels: []
        }
    },
    getters: {
        companies: state => state.companies,
        editedCompany: state => state.editedCompany,
        editedCompanyIndex: state => state.editedIndex,
        companyLabels: state => state.labels
    },
    mutations: {
        storeCompanies(state, payload) {
            state.companies = payload.companies
        },
        storeEditedCompanyDetails(state, payload) {
            state.editedIndex = payload.editedIndex,
            state.editedCompany = payload.editedCompany
        },
        storeCompanyLabels(state, payload) {
            state.labels = payload.labels
        }
    },
    actions: {
        getCompanies() {
            return api.get('company/get').then(response => {
                let companies = response.data;

                companies.forEach(company => {
                    company.labels = company.labels.map(label => {
                        return label.title;
                    });
                });

                this.commit({
                    type: 'storeCompanies',
                    companies: companies
                })
            }).catch(error => {
                console.log(error);
            });
        }
    }
};
