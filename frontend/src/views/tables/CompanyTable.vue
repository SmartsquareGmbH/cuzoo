<template>
    <v-container fluid>
        <v-card-title class="px-1">
            <h1 class="mr-3">
                <v-icon size="32px">business</v-icon>
                UNTERNEHMEN
            </h1>
            <input
                    class="input-file"
                    type="file"
                    id="file"
                    ref="file"
                    @change="handleUpload()"/>
            <label for="file">
                <v-tooltip top>
                    <v-icon
                            slot="activator"
                            color="primary"
                            x-large>
                        publish
                    </v-icon>
                    <span>CSV Import</span>
                </v-tooltip>
            </label>
            <v-btn
                    @click="openCompanyDialog()"
                    color="transparent"
                    fab
                    small
                    depressed
                    flat>
                <v-tooltip top>
                    <v-icon
                            color="light-green accent-2"
                            slot="activator"
                            x-large>
                        add
                    </v-icon>
                    <span>Unternehmen hinzufügen</span>
                </v-tooltip>
            </v-btn>
            <company-dialog v-model="companyDialogState"
                            @input="companyDialogState = false"/>
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
                :items="companies"
                :loading=loading
                :search="search"
                rows-per-page-text="Unternehmen pro Seite"
                :rows-per-page-items=[10,25,50,100]
                dark>
            <v-progress-linear slot="progress" color="blue" indeterminate/>
            <template slot="items" slot-scope="props">
                <tr v-if="!!props.item"
                    style="cursor: pointer;"
                    @click="viewCompany(props.item)"
                    class="text-xs-left vertical-center">
                    <td>{{ props.item.name }}</td>
                    <td>{{ props.item.place }}</td>
                    <td>{{ props.item.homepage }}</td>
                    <v-tooltip top v-if="props.item.description.length > 100" max-width="750">
                        <td slot="activator" class="vertical-center">
                            {{ props.item.description | truncate(100) }}
                        </td>
                        {{ props.item.description }}
                    </v-tooltip>
                    <td v-else>{{ props.item.description }}</td>
                    <td>
                        <v-tooltip top>
                            <v-icon @click.stop="editCompany(props.item)" size="22px" slot="activator" class="mr-2">
                                edit
                            </v-icon>
                            Unternehmen bearbeiten
                        </v-tooltip>
                        <v-tooltip top>
                            <v-icon @click.stop="openConfirmDialog(props.item)" size="22px" color="red lighten-1"
                                    slot="activator">
                                delete
                            </v-icon>
                            Unternehmen löschen
                        </v-tooltip>
                    </td>
                </tr>
            </template>
            <span slot="no-data">
                Keine Daten verfügbar :'(
            </span>
            <v-alert slot="no-results" :value="true" color="error" icon="warning">
                Deine Suche nach "{{ search }}" ergab keinen Treffer :'(
            </v-alert>
        </v-data-table>
        <confirm-dialog
                v-model="confirmDialogState"
                :questionToBeConfirmed="deleteCompanyMessage"
                @confirmed="deleteCompany()"/>
    </v-container>
</template>

<script>
    import {mapActions, mapGetters, mapMutations} from 'vuex';
    import api from '../../utils/http-common';

    import CompanyDialog from "../../components/dialogs/CompanyDialog.vue";
    import ConfirmDialog from "../../components/dialogs/ConfirmDialog.vue";

    export default {
        components: {
            CompanyDialog,
            ConfirmDialog
        },
        data() {
            return {
                selectedStatus: ["Lead", "Bestandskunde"],
                companyDialogState: false,
                confirmDialogState: false,
                deleteCompanyMessage: 'Bist du dir sicher, dass du das Unternehmen ' +
                    'mit all dessen Ansprechpartnern und Kontaktpunkten löschen willst?',
                file: '',
                search: '',
                loading: true,
                headers: [
                    {text: 'Unternehmen', value: 'name', align: 'left'},
                    {text: 'Ort', value: 'place'},
                    {text: 'Homepage', value: 'homepage'},
                    {text: 'Beschreibung', value: 'description'},
                    {text: 'Aktionen', value: 'name', sortable: false}
                ]
            }
        },
        computed: {
            ...mapGetters(['companies'])
        },
        mounted() {
            this.refreshTable()
        },
        methods: {
            ...mapActions(['getCompanies']),
            ...mapMutations({
                storeCompanyDetails: 'storeEditedCompanyDetails'
            }),
            handleUpload() {
                this.loading = true;
                this.file = this.$refs.file.files[0];
                this.submitFile();
            },
            submitFile() {
                let formData = new FormData();
                formData.append('file', this.file);

                api.post('company/import', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                }).then(() => {
                    this.refreshTable();
                }).catch(error => {
                    console.log(error);
                });
            },
            refreshTable() {
                this.getCompanies().then(() => this.loading = false);
            },
            editCompany(item) {
                this.storeCompanyDetails({
                    editedIndex: this.companies.indexOf(item),
                    editedCompany: Object.assign({}, item)
                });

                this.openCompanyDialog();
            },
            deleteCompany() {
                api.delete(`company/delete/${this.editedCompany.id}`).then(() => {
                    this.refreshTable();
                }).catch(error => {
                    console.log(error);
                    alert(error);
                });
            },
            openCompanyDialog() {
                this.companyDialogState = true;
            },
            openConfirmDialog(item) {
                this.editedCompany = Object.assign({}, item);

                this.confirmDialogState = true;
            },
            viewCompany(item) {
                this.$router.push('/companies/' + (item.id));
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

    .vertical-center {
        vertical-align: middle !important;
    }
</style>
