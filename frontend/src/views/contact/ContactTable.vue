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
            <v-btn @click="openContactDialog()" fab light small color="transparent" depressed flat>
                <v-tooltip top>
                    <v-icon x-large color="light-green accent-2" slot="activator">add</v-icon>
                    <span>Ansprechpartner hinzufügen</span>
                </v-tooltip>
            </v-btn>
            <contact-dialog 
            v-model="contactDialogState"
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
                <td class="text-xs-left">{{ props.item.mobile }}</td>
                <td class="text-xs-left">{{ props.item.comment }}</td>
                <td class="justify-center layout px-0">
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
                    @click="openConfirmDialog(props.item)"
                    size="22px" 
                    class="mr-2 mt-2"
                    color="red lighten-1">
                        delete
                    </v-icon>
                </td>
                <confirm-dialog
                        v-model="confirmDialogState"
                        :questionToBeConfirmed="deleteContactMessage"
                        @confirmed="deleteContact"/>
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
    import {mapGetters, mapActions, mapMutations} from 'vuex';
    import api from '../../utils/http-common'

    import ContactDialog from "../../components/dialogs/ContactDialog.vue";
    import ConfirmDialog from "../../components/dialogs/ConfirmDialog.vue";

    export default {
        components: {
            ContactDialog,
            ConfirmDialog
        },
        data() {
            return {
                file: '',
                search: '',
                companyNames: [],
                loading: true,
                contactDialogState: false,
                headers: [
                    {text: 'Name', align: 'left', value: 'name'},
                    {text: 'Unternehmen', value: "company.name"},
                    {text: 'Rolle', value: 'role'},
                    {text: 'E-Mail', value: 'mail'},
                    {text: 'Telefon', value: 'telephone'},
                    {text: 'Mobil', value: 'mobile'},
                    {text: 'Bemerkung', value: 'comment'},
                    {text: 'Aktionen', value: 'name', sortable: false}
                ],
                confirmDialogState: false,
                deleteContactMessage: 'Bist du dir sicher, dass du diesen Ansprechpartner löschen willst?'
            }
        },
        computed: {
            ...mapGetters(['companies', 'contacts'])
        },
        mounted() {
            this.refreshTable()
        },
        methods: {
            ...mapActions(['getContacts']),
            ...mapMutations({
                storeEditedDetails: 'storeEditedContactDetails'
            }),
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
                this.getContacts().then(() => this.loading = false)
            },
            editContact(item) {
                this.storeEditedDetails({
                    editedIndex: this.contacts.indexOf(item),
                    editedContact: Object.assign({}, item)
                });

                this.openContactDialog();
            },
            deleteContact() {
                api.delete(`contact/delete/${this.editedContact.id}`).then(response => {
                    console.log(response.data);
                    this.refreshTable();
                }).catch(error => {
                    console.log(error);
                    alert(error);
                });
            },
            openConfirmDialog(item) {
                this.editedContact = Object.assign({}, item);

                this.confirmDialogState = true;
            },
            openContactDialog() {
                this.companies.forEach(company => {
                    this.companyNames.push(company.name)
                });

                this.companyNames.sort();                
                this.contactDialogState = true;
            },
            downloadInfo(item) {
                api.get("/contact/download/" + item.id)
                    .then(response => download(response.data, item.name));
            }
        }
    }

    function download(content, name) {
        const url = window.URL.createObjectURL(new Blob([content], {type : "text/plain"}));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', name.replace(' ', '_').toLowerCase() + '.txt');
        document.body.appendChild(link);
        link.click();
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