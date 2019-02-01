<template>
    <v-container fluid>
        <v-card-title>
            <h1 class="mr-3">UNTERNEHMEN</h1>
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
                    @click="openDialog()"
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
            <company-dialog v-model="dialogState"/>
            <v-layout row wrap class="pl-2 pt-1">
                <v-flex xl2 lg3 md4 xs5>
                    <v-checkbox
                            v-model="selectedStatus"
                            label="Leads"
                            value="Lead"
                            color="teal accent-2"/>
                </v-flex>
                <v-flex xl2 lg3 md4 xs5>
                    <v-checkbox
                            v-model="selectedStatus"
                            label="Bestandskunden"
                            value="Bestandskunde"
                            color="teal accent-2"/>
                </v-flex>
            </v-layout>
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
                :items="filteredCompanies"
                :loading=loading
                :search="search"
                rows-per-page-text="Unternehmen pro Seite"
                :rows-per-page-items=[10,25,50,100]
                dark>
            <v-progress-linear slot="progress" color="blue" indeterminate/>
            <template slot="items" slot-scope="props">
                <td class="text-xs-left">{{ props.item.name }}</td>
                <td class="text-xs-left">{{ props.item.street }}</td>
                <td class="text-xs-left">{{ props.item.zipCode }}</td>
                <td class="text-xs-left">{{ props.item.place }}</td>
                <td class="text-xs-left">{{ props.item.homepage }}</td>
                <td class="text-xs-left">{{ props.item.description }}</td>
                <td class="text-xs-left">{{ props.item.other }}</td>
                <td class="justify-center layout px-0">
                    <v-tooltip top>
                        <v-btn
                                @click="viewCompany(props.item)"
                                color="transparent"
                                slot="activator"
                                class="mr-2"
                                flat icon small>
                            <v-icon size="22px" color="white">
                                info
                            </v-icon>
                        </v-btn>
                        <span>Informationen</span>
                    </v-tooltip>
                    <v-icon
                            @click="editCompany(props.item)"
                            size="22px"
                            class="mr-2 mt-2">
                        edit
                    </v-icon>
                    <v-icon
                            @click="deleteCompany(props.item)"
                            size="22px"
                            color="red lighten-1"
                            class="mr-2 mt-2">
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
    import {mapActions, mapGetters, mapMutations} from 'vuex'
    import api from '../../utils/http-common'

    import CompanyDialog from "../../components/company/CompanyDialog.vue"

    export default {
        components: {
            CompanyDialog
        },
        data() {
            return {
                selectedStatus: ["Lead", "Bestandskunde"],
                dialogState: false,
                file: '',
                search: '',
                loading: true,
                headers: [
                    {text: 'Unternehmen', value: 'name', align: 'left'},
                    {text: 'Straße', value: 'street'},
                    {text: 'PLZ', value: 'zipCode'},
                    {text: 'Ort', value: 'place'},
                    {text: 'Homepage', value: 'homepage'},
                    {text: 'Beschreibung', value: 'description'},
                    {text: 'Sonstiges', value: 'other'},
                    {text: 'Aktionen', value: 'name', sortable: false}
                ]
            }
        },
        computed: {
            ...mapGetters(['companies']),
            filteredCompanies() {
                return this.companies.filter((company) => {
                    if (this.selectedStatus.indexOf("Lead") > -1 && this.selectedStatus.indexOf("Bestandskunde") > -1) {
                        return company;
                    } else if (this.selectedStatus.indexOf("Lead") > -1) {
                        return company.status === "Lead";
                    } else if (this.selectedStatus.indexOf("Bestandskunde") > -1) {
                        return company.status === "Bestandskunde";
                    } else {
                        return company;
                    }
                })
            }
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

                this.openDialog();
            },
            deleteCompany(item) {
                this.editedCompany = Object.assign({}, item);

                if (confirm("Bist du dir sicher, dass du das Unternehmen mit all dessen Ansprechpartnern löschen willst?")) {
                    api.delete(`company/delete/${this.editedCompany.id}`).then(() => {
                        this.refreshTable();
                    }).catch(error => {
                        console.log(error);
                        alert(error);
                    });
                }
            },
            openDialog() {
                this.dialogState = true
            },
            viewCompany(item) {
                const index = this.companies.findIndex(company => company.id === item.id);
                this.$router.push('/companies/' + (index));
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
