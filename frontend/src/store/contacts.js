import api from '../utils/http-common'

export default {
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
        contacts: state => state.contacts,
        editedContact: state => state.editedContact,
        editedContactIndex: state => state.editedIndex
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
            return api.get('contact/get').then(response => {
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
};
