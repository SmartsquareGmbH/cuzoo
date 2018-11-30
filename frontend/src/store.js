import Vue from "vue";
import Vuex from "vuex";
import api from './utils/http-common'

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        authorized: false,
        logName: '',
        logPass: '',
        companies: [],
        companyNames: [],
        contacts: [],
        companyDialog: false,
        contactDialog: false,
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
            purpose: "",
            other: ""
        },
        editedContact: {
            value: false,
            id: 0,
            name: "",
            company: {
                name: ""
            },
            role: "",
            mail: "",
            telephone: "",
            lastContact: "",
            lastAnswer: "",
            comment: ""
        }
    },
    getters: {
        getAuthorized: state => state.authorized,
        getLogName: state => state.logName,
        getLogPass: state => state.logPass,
        getCompanies: state => state.companies,
        getContacts: state => state.contacts,
        getCompanyDialogState: state => state.companyDialog,
        getContactDialogState: state => state.contactDialog,
        getCompanyNames: state => state.companyNames,
        getEditedIndex: state => state.editedIndex
    },
    mutations: {
        storeLogData(state, payload) {
            state.authorized = payload.authorized,
            state.logName = payload.username,
            state.logPass = payload.password
        },
        storeCompanies(state, payload) {
            state.companies = payload.companies
        },
        storeContacts(state, payload) {
            state.contacts = payload.contacts
        },
        storeCompanyNames(state, payload) {
            state.companyNames = payload.companyNames
        },
        storeCompanyDialogState(state, payload) {
            state.companyDialog = payload.companyDialog
        },
        storeContactDialogState(state, payload) {
            state.contactDialog = payload.contactDialog
        },
        storeEditedCompanyDetails(state, payload) {
            state.editedIndex = payload.editedIndex,
            state.editedCompany = payload.editedCompany
        },
        storeEditedContactDetails(state, payload) {
            state.editedIndex = payload.editedIndex,
            state.editedContact = payload.editedContact
        }
    },
    actions: {
        getCompanies() {
            return api.get('company/get', {
                auth: {
                    username: this.getters.getLogName,
                    password: this.getters.getLogPass
                }
            }).then(response => {
                console.log(response.data);
                this.commit({
                    type: 'storeCompanies',
                    companies: response.data
                })
            }).catch(error => {
                console.log(error);
            });
        },
        getContacts() {
            return api.get('contact/get', {
                auth: {
                    username: this.getters.getLogName,
                    password: this.getters.getLogPass
                }
            }).then(response => {
                console.log(response.data);
                this.commit({
                    type: 'storeContacts',
                    contacts: response.data
                })
            }).catch(error => {
                console.log(error);
            });
        }
    }
});
