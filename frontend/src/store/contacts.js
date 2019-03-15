import api from '../utils/http-common'

export default {
    state: {
        contacts: [],
        editedIndex: -1,
        editedCompanyName: "",
        editedContact: {
            value: false,
            id: 0,
            name: "",
            company: {},
            role: "",
            mail: "",
            telephone: "",
            mobile: "",
            comment: "",
            manager: "",
            labels: []
        }
    },
    getters: {
        contacts: state => state.contacts,
        editedCompanyName: state => state.editedCompanyName,
        editedContact: state => state.editedContact,
        editedContactIndex: state => state.editedIndex,
    },
    mutations: {
        storeContacts(state, payload) {
            state.contacts = payload.contacts
        },
        storeEditedContactDetails(state, payload) {
            state.editedIndex = payload.editedIndex,
            state.editedContact = payload.editedContact
        },
        storeEditedCompanyName(state, payload) {
            state.editedCompanyName = payload.editedCompanyName
        }
    },
    actions: {
        getContacts() {
            return api.get('contact/get').then(response => {
                let contacts = response.data;

                contacts.forEach(contact => {
                    contact.labels = contact.labels.map(label => {
                        return label.title;
                    });

                    contact.manager = contact.manager.username;
                });

                this.commit({
                    type: 'storeContacts',
                    contacts: contacts
                })
            }).catch(error => {
                console.log(error);
            });
        }
    }
};
