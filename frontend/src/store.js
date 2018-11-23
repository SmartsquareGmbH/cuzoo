import Vue from "vue";
import Vuex from "vuex";
import axios from 'axios';

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        authorized: false,
        logName: '',
        logPass: '',
        companies: [],
        companyNames: [],
        contacts: [],
        dialog: false,
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
            company: "",
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
        getDialogState: state => state.dialog,
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
        storeDialogState(state, payload) {
            state.dialog = payload.dialog
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
            axios.get(`${process.env.VUE_APP_API_URL}company/get`, {
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
            axios.get(`${process.env.VUE_APP_API_URL}contact/get`, {
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
