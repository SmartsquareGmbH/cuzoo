import Vue from "vue"
import Vuex from "vuex"
import api from '@/utils/http-common'
import store from '@/store.js'

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        contacts: [],
        editedIndex: -1,
        editedContact: {
            value: false,
            id: 0,
            name: "",
            company: {},
            role: "",
            mail: "",
            telephone: "",
            lastContact: "",
            lastAnswer: "",
            comment: ""
        }
    },
    getters: {
        getContacts: state => state.contacts,
        getEditedIndex: state => state.editedIndex
    },
    mutations: {
        storeContacts(state, payload) {
            state.contacts = payload.contacts
        },
        storeEditedContactDetails(state, payload) {
            state.editedIndex = payload.editedIndex,
            state.editedContact = payload.editedContact
        }
    },
    actions: {
        getContacts() {
            return api.get('contact/get', {
                auth: {
                    username: store.getters.getUsername,
                    password: store.getters.getPassword
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
