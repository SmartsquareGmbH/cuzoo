<template>
    <v-container fluid>
        <v-card-title>
            <h1 class="mr-3">ANSPRECHPARTNER</h1>
            <input 
            class="input-file"
            type="file"
            id="file"
            ref="file"
            @change="handleUpload()">
            <label for="file">
                <v-tooltip top>
                    <v-icon slot="activator" x-large color="primary">
                        publish
                    </v-icon>
                    <span>CSV Import</span>
                </v-tooltip>
            </label>
            <v-btn @click="openDialog()" fab light small color="transparent" depressed flat>
                <v-tooltip top>
                    <v-icon x-large color="light-green accent-2" slot="activator">add</v-icon>
                    <span>Ansprechpartner hinzufügen</span>
                </v-tooltip>
            </v-btn>
            <contact-dialog 
            v-model="dialogState"
            :companyNames="this.companyNames"/>
            <v-spacer></v-spacer>
            <v-text-field
            v-model="search"
            append-icon="search"
            label="Suche ..."
            single-line
            hide-details/>
        </v-card-title>
        <v-data-table
        :headers="headers"
        :items="contacts"
        :search="search"
        :loading=loading
        rows-per-page-text="Unternehmen pro Seite"
        :rows-per-page-items=[10,25,50,100]
        dark>
            <v-progress-linear slot="progress" color="blue" indeterminate/>
            <template slot="items" slot-scope="props">
                <td class="text-xs-left">{{ props.item.name }}</td>
                <td v-if="props.item.company != null" class="text-xs-left">{{ props.item.company.name }}</td>
                <td v-else class="text-xs-center">-</td>
                <td class="text-xs-left">{{ props.item.role }}</td>
                <td class="text-xs-left">{{ props.item.mail }}</td>
                <td class="text-xs-left">{{ props.item.telephone }}</td>
                <td class="text-xs-left">{{ props.item.comment }}</td>
                <td class="justify-center layout px-0">
                    <v-icon 
                    v-if="props.item.role == 'Freiberufler'"
                    @click="viewContact(props.item)"
                    size="22px" 
                    class="mr-2 mt-2">
                        account_box
                    </v-icon>
                    <v-icon 
                    @click="editContact(props.item)"
                    size="22px" 
                    class="mr-0 mt-2">
                        edit
                    </v-icon>
                    <v-tooltip top>
                        <v-btn
                        @click="downloadInfo(props.item)"
                        color="transparent"
                        class="mr-2"
                        slot="activator"
                        flat icon small>
                            <v-icon size="22px" color="white" style="transform: rotate(180deg)" dark>
                                publish
                            </v-icon>
                        </v-btn>
                        <span>Export Informationen</span>
                    </v-tooltip>
                    <v-icon 
                    @click="deleteContact(props.item)"
                    size="22px" 
                    class="mr-2 mt-2"
                    color="red lighten-1">
                        delete
                    </v-icon>
                </td>
            </template>
            <span slot="no-data">
                Keine Daten verfügbar :'(
            </span>
            <v-alert slot="no-results" :value="true" color="error" icon="warning">
                Deine Suche nach "{{ search }}" ergab keinen Treffer :'(
            </v-alert>
        </v-data-table>
    </v-container>
</template>

<script>
    import {mapState} from 'vuex';
    import api from '@/utils/http-common'
    import store from '@/store.js'
    import contactStore from '@/stores/contacts.js'
    import companyStore from '@/stores/companies.js'

    import ContactDialog from "@/components/contact/ContactDialog.vue";

    export default {
        components: {
            ContactDialog
        },
        data() {
            return {
                file: '',
                search: '',
                companyNames: [],
                loading: true,
                dialogState: false,
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
                return companyStore.getters.getCompanies
            },
            ...mapState(['contacts']),
            contacts: {
                get() {
                    return contactStore.state.contacts
                },
                set(contacts) {
                    contactStore.commit('storeContacts', contacts)
                }
            }
        },
        mounted() {
            this.refreshTable()
        },
        methods: {
            handleUpload() {
                this.loading = true;
                this.file = this.$refs.file.files[0];
                this.submitFile();
            },
            submitFile() {
                let formData = new FormData();
                formData.append('file', this.file);
                api.post('contact/import', formData, {
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
                contactStore.dispatch('getContacts')
                    .then(() => this.loading = false)
            },
            editContact: function (item) {
                contactStore.commit({
                    type: 'storeEditedContactDetails',
                    editedIndex: this.contacts.indexOf(item),
                    editedContact: Object.assign({}, item)
                });
                this.openDialog();
            },
            deleteContact: function (item) {
                this.editedContact = Object.assign({}, item);

                if (confirm("Bist du dir sicher, dass du diesen Ansprechpartner löschen willst?")) {
                    api.delete(`contact/delete/${this.editedContact.id}`).then(response => {
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

                this.companyNames.sort();                
                this.dialogState = true;
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