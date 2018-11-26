<template>
    <v-container fluid>
        <v-card-title>
            <h1 class="mr-3">KONTAKTPERSONEN</h1>
            <input class="input-file"
                   type="file"
                   id="file"
                   ref="file"
                   v-on:change="handleUpload()"/>
            <label for="file">
                <v-tooltip top>
                    <v-icon slot="activator" x-large color="primary">
                        publish
                    </v-icon>
                    <span>CSV Import</span>
                </v-tooltip>
            </label>
            <v-btn v-on:click="openDialog()" fab light small color="transparent" depressed flat>
                <v-tooltip top>
                    <v-icon x-large color="light-green accent-2" slot="activator">add</v-icon>
                    <span>Kontaktperson hinzufügen</span>
                </v-tooltip>
            </v-btn>
            <contact-dialog></contact-dialog>
            <v-spacer></v-spacer>
            <v-text-field
                    v-model="search"
                    append-icon="search"
                    label="Suche ..."
                    single-line
                    hide-details
            ></v-text-field>
        </v-card-title>
        <v-data-table
                :headers="headers"
                :items="contacts"
                :search="search"
                rows-per-page-text="Unternehmen pro Seite"
                :rows-per-page-items=[10,25,50,100]
                dark
        >
            <template slot="items" slot-scope="props">
                <td class="text-xs-left">{{ props.item.name }}</td>
                <td v-if="props.item.company != null" class="text-xs-left">{{ props.item.company.name }}</td>
                <td v-else class="text-xs-left">-</td>
                <td class="text-xs-left">{{ props.item.role }}</td>
                <td class="text-xs-left">{{ props.item.mail }}</td>
                <td class="text-xs-left">{{ props.item.telephone }}</td>
                <td class="text-xs-left">{{ props.item.comment }}</td>
                <td class="justify-center layout px-0">
                    <v-icon size="22px" class="mr-2" v-if="props.item.role == 'Freiberufler'"
                            @click="viewContact(props.item)">
                        account_box
                    </v-icon>
                    <v-icon size="22px" class="mr-2"
                            v-on:click="editContact(props.item)">
                        edit
                    </v-icon>
                    <v-icon size="22px" class="mr-2" color="red lighten-1"
                            v-on:click="deleteContact(props.item)">
                        delete
                    </v-icon>
                </td>
            </template>
            <template slot="no-data">
                <v-alert :value="true" color="error" icon="warning">
                    Keine Daten verfügbar :'(
                </v-alert>
            </template>
            <v-alert slot="no-results" :value="true" color="error" icon="warning">
                Deine Suche nach "{{ search }}" ergab keinen Treffer :'(
            </v-alert>
        </v-data-table>
    </v-container>
</template>

<script>
    import {mapState} from 'vuex';
    import api from '../utils/http-common'
    import ContactDialog from "@/components/Contact/ContactDialog.vue";

    export default {
        components: {
            ContactDialog
        },
        data() {
            return {
                file: '',
                search: '',
                companyNames: [],
                headers: [
                    {text: 'Name', align: 'left', value: 'name'},
                    {text: 'Unternehmen', value: "company.name"},
                    {text: 'Rolle', value: 'role'},
                    {text: 'E-Mail', value: 'mail'},
                    {text: 'Telefon', value: 'telephone'},
                    {text: 'Bemerkung', value: 'comment'},
                    {text: 'Aktionen', value: 'name', sortable: false}
                ]
            }
        },
        computed: {
            companies() {
                return this.$store.getters.getCompanies;
            },
            ...mapState(['contacts']),
            contacts: {
                get() {
                    return this.$store.state.contacts
                },
                set(contacts) {
                    this.$store.commit('storeContacts', contacts)
                }
            }
        },
        mounted() {
            this.refreshTable()
        },
        methods: {
            handleUpload() {
                this.file = this.$refs.file.files[0];
                this.submitFile();
            },
            submitFile() {
                let formData = new FormData();
                formData.append('file', this.file);
                api.post('contact/import', formData, {
                    auth: {
                        username: this.$store.getters.getLogName,
                        password: this.$store.getters.getLogPass
                    },
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                }).then(response => {
                    console.log(response.data);
                    this.refreshTable();
                }).catch(error => {
                    console.log(error);
                });
            },
            refreshTable() {
                this.$store.dispatch('getContacts');
            },
            editContact: function (item) {
                this.$store.commit({
                    type: 'storeEditedContactDetails',
                    editedIndex: this.contacts.indexOf(item),
                    editedContact: Object.assign({}, item)
                });
                this.openDialog();
            },
            deleteContact: function (item) {
                this.editedContact = Object.assign({}, item);

                if (confirm("Bist du dir sicher, dass du diese Kontaktperson löschen willst?")) {
                    api.delete(`contact/delete/${this.editedContact.id}`, {
                        auth: {
                            username: this.$store.getters.getLogName,
                            password: this.$store.getters.getLogPass
                        }
                    }).then(response => {
                        console.log(response.data);
                        this.refreshTable();
                    }).catch(error => {
                        console.log(error);
                        alert(error);
                    });
                }
            },
            openDialog() {
                this.companies.forEach(company => {
                    this.companyNames.push(company.name)
                });
                this.$store.commit({
                    type: 'storeCompanyNames',
                    companyNames: this.companyNames
                });
                this.$store.commit({
                    type: 'storeDialogState',
                    dialog: true
                });
            },
            viewContact: function (item) {
                const index = this.contacts.findIndex(contact => contact.id == item.id);
                this.$router.replace('/contacts/' + (index));
            }
        }
    }
</script>

<style scoped>
    .input-file {
        width: 0.1px;
        height: 0.1px;
        opacity: 0;
        overflow: hidden;
        position: absolute;
        z-index: -1;
    }

    .input-file + label {
        cursor: pointer;
    }
</style>