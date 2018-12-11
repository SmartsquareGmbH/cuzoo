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
        contactNames: [],
        companyDialog: false,
        contactDialog: false,
        cPointDialog: false,
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
        },
        editedCPoint: {
            value: false,
            id: 0,
            title: "",
            contact: {},
            date: "",
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
        getCPointDialogState: state => state.cPointDialog,
        getCompanyNames: state => state.companyNames,
        getContactNames: state => state.contactNames,
        getEditedIndex: state => state.editedIndex
    },
    mutations: {
        storeDarkState(state, payload) {
            state.darkState = payload.darkState
        },
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
        storeCPointDialogState(state, payload) {
            state.contactNames = payload.contactNames,
            state.cPointDialog = payload.cPointDialog
        },
        storeEditedCompanyDetails(state, payload) {
            state.editedIndex = payload.editedIndex,
            state.editedCompany = payload.editedCompany
        },
        storeEditedContactDetails(state, payload) {
            state.editedIndex = payload.editedIndex,
            state.editedContact = payload.editedContact
        },
        storeEditedCPointDetails(state, payload) {
            state.editedIndex = payload.editedIndex,
            state.editedCPoint = payload.editedCPoint
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
