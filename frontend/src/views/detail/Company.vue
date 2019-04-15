<template>
    <v-container grid-list-md text-xs-left fluid>
        <v-layout row wrap>
            <v-flex xs1>
                <v-btn flat small @click="goPageBack()">
                    <v-icon size="22px" class="mr-1" dark>arrow_back</v-icon>
                    Zurück
                </v-btn>
            </v-flex>
            <v-flex xs5 text-xs-right>
                <v-tooltip top>
                    <v-btn disabled flat small slot="activator" @click="viewContactPoints()">
                        <v-icon size="22px" class="mr-1" dark>forum</v-icon>
                        Kontaktpunktliste
                    </v-btn>
                    Anzeigen der Kontaktpunktliste
                </v-tooltip>
                <v-btn slot="activator" flat small @click="editCompany()">
                    <v-icon size="22px" class="mr-1" dark>edit</v-icon>
                    Unternehmen editieren
                </v-btn>
            </v-flex>
            <company-dialog v-model="companyDialogState"/>
            <v-flex xs1>
                <v-btn slot="activator" small flat @click="openContactDialog()">
                    <v-icon size="22px" class="mr-1" dark>add</v-icon>
                    Ansprechpartner hinzufügen
                </v-btn>
            </v-flex>
            <v-flex xs5 text-xs-right>
                <v-tooltip top>
                    <v-btn slot="activator" small flat
                           @click="downloadInfo(contactsOfCompany[contactsOfCompany.contact])">
                        <v-icon dark size="22px" style="transform: rotate(180deg)">publish</v-icon>
                        Datenauskunft
                    </v-btn>
                    Datenauskunft über Ansprechpartner herunterladen
                </v-tooltip>
                <v-btn slot="activator" v-if="contactsOfCompany.length > 0" small flat
                       @click="editContact(contactsOfCompany[contactsOfCompany.contact])">
                    <v-icon size="22px" dark>edit</v-icon>
                    Ansprechpartner editieren
                </v-btn>
            </v-flex>
            <contact-dialog
                    v-model="contactDialogState"
                    :companyNames="[this.company.name]"/>
            <v-flex xs6>
                <v-layout row wrap>
                    <v-flex xs2>
                        <v-card dark color="green">
                            <v-card-text class="headline text-xs-center">
                                <v-icon size="30px">business</v-icon>
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs10>
                        <v-card height="100%" dark>
                            <v-tooltip bottom>
                                <v-card-text slot="activator" class="headline text-xs-left font-weight-light">
                                    {{ this.company.name | truncate(60) }}
                                </v-card-text>
                                <span class="headline font-weight-light">{{ this.company.name }}</span>
                            </v-tooltip>
                        </v-card>
                    </v-flex>
                    <v-flex xs2>
                        <v-card dark color="green">
                            <v-card-text class="headline text-xs-center">
                                <v-icon size="30px">home</v-icon>
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs10>
                        <v-card dark>
                            <v-card-text
                                    v-if="this.company.homepage"
                                    class="headline text-xs-left font-weight-light">
                                <a :href=this.homepage target="_blank">{{ this.company.homepage }}</a>
                            </v-card-text>
                            <v-card-text
                                    v-else
                                    class="headline text-xs-left font-weight-light font-italic error--text">
                                N/A
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs2>
                        <v-card dark color="green" height="212%" class="centered">
                            <v-card-text class="headline text-xs-center font-weight-light">
                                <v-icon size="30px">place</v-icon>
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs2>
                        <v-card dark>
                            <v-card-text
                                    v-if="this.company.zipcode"
                                    class="headline text-xs-left font-weight-light">
                                {{ this.company.zipcode }}
                            </v-card-text>
                            <v-card-text
                                    v-else
                                    class="headline text-xs-left font-weight-light font-italic error--text">
                                N/A
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs8>
                        <v-card dark>
                            <v-card-text
                                    v-if="this.company.place"
                                    class="headline text-xs-left font-weight-light">
                                {{ this.company.place }}
                            </v-card-text>
                            <v-card-text
                                    v-else
                                    class="headline text-xs-left font-weight-light font-italic error--text">
                                N/A
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs2>
                        <v-card color="transparent elevation-0">

                        </v-card>
                    </v-flex>
                    <v-flex xs10>
                        <v-card dark>
                            <v-card-text
                                    v-if="this.company.street"
                                    class="headline text-xs-left font-weight-light">
                                {{ this.company.street }}
                            </v-card-text>
                            <v-card-text
                                    v-else
                                    class="headline text-xs-left font-weight-light font-italic error--text">
                                N/A
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs12 v-if="this.company.description">
                        <v-card-text class="headline text-xs-left no-padding-left">
                            <v-tooltip top>
                                <v-icon color="info" size="30px" slot="activator">info</v-icon>
                                Beschreibung des Unternehmens
                            </v-tooltip>
                            Beschreibung
                        </v-card-text>
                        <v-divider class="mb-3"/>
                    </v-flex>
                    <v-flex xs12 v-if="this.company.description">
                        <span class="marked" v-html="markdownify(company.description)"/>
                    </v-flex>
                    <v-flex xs12 v-if="this.company.other">
                        <v-card-text class="headline text-xs-left no-padding-left">
                            <v-tooltip top>
                                <v-icon color="info" size="30px" slot="activator">info</v-icon>
                                Sonstige Angaben zum Unternehmen
                            </v-tooltip>
                            Sonstiges
                        </v-card-text>
                        <v-divider class="mb-3"/>
                    </v-flex>
                    <v-flex xs12 v-if="this.company.other">>
                        <span class="marked" v-html="markdownify(company.other)"/>
                    </v-flex>
                    <v-flex xs2 v-if="this.company.labels.length > 0">
                        <v-card dark color="info">
                            <v-card-text class="headline text-xs-center">
                                <v-icon size="30px" class="pt-1">label</v-icon>
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs10 class="text-xs-left" v-if="this.company.labels.length > 0">
                        <v-chip
                                class="title mt-3"
                                v-for="label in this.company.labels"
                                v-bind:key="label">
                            {{ label }}
                        </v-chip>
                    </v-flex>
                </v-layout>
            </v-flex>
            <v-flex xs6>
                <v-tabs centered height="64%" grow slider-color="info"
                        v-model="contactsOfCompany.contact"
                        v-if="contactsOfCompany.length > 0">
                    <v-tab v-bind:key="contact.id" v-for="contact in contactsOfCompany">
                        {{ contact.name }}
                    </v-tab>
                    <v-tabs-items v-model="contactsOfCompany.contact">
                        <v-tab-item v-bind:key="contact.id" v-for="contact in contactsOfCompany">
                            <v-layout row wrap class="mt-1">
                                <v-flex xs2>
                                    <v-card dark color="green" height="100%">
                                        <v-card-text class="headline text-xs-center">
                                            <v-icon size="30px">work</v-icon>
                                        </v-card-text>
                                    </v-card>
                                </v-flex>
                                <v-flex xs10>
                                    <v-card dark height="100%">
                                        <v-card-text
                                                class="headline text-xs-left font-weight-light">
                                            {{ contact.role }}
                                        </v-card-text>
                                    </v-card>
                                </v-flex>
                                <v-flex xs2>
                                    <v-card dark color="info">
                                        <v-card-text class="headline text-xs-center">
                                            <v-icon size="30px">mail</v-icon>
                                        </v-card-text>
                                    </v-card>
                                </v-flex>
                                <v-flex xs10>
                                    <v-card dark>
                                        <v-card-text
                                                v-if="contact.mail"
                                                class="headline text-xs-left font-weight-light">
                                            {{ contact.mail }}
                                        </v-card-text>
                                        <v-card-text
                                                v-else
                                                class="headline text-xs-left font-weight-light font-italic error--text">
                                            N/A
                                        </v-card-text>
                                    </v-card>
                                </v-flex>
                                <v-flex xs2>
                                    <v-card dark color="info">
                                        <v-card-text class="headline text-xs-center">
                                            <v-icon size="30px">phone</v-icon>
                                        </v-card-text>
                                    </v-card>
                                </v-flex>
                                <v-flex xs4>
                                    <v-card dark>
                                        <v-card-text
                                                v-if="contact.telephone"
                                                class="headline text-xs-left font-weight-light">
                                            {{ contact.telephone }}
                                        </v-card-text>
                                        <v-card-text
                                                v-else
                                                class="headline text-xs-left font-weight-light font-italic error--text">
                                            N/A
                                        </v-card-text>
                                    </v-card>
                                </v-flex>
                                <v-flex xs2>
                                    <v-card dark color="info">
                                        <v-card-text class="headline text-xs-center">
                                            <v-icon size="30px">smartphone</v-icon>
                                        </v-card-text>
                                    </v-card>
                                </v-flex>
                                <v-flex xs4>
                                    <v-card dark>
                                        <v-card-text
                                                v-if="contact.mobile"
                                                class="headline text-xs-left font-weight-light">
                                            {{ contact.mobile }}
                                        </v-card-text>
                                        <v-card-text
                                                v-else
                                                class="headline text-xs-left font-weight-light font-italic error--text">
                                            N/A
                                        </v-card-text>
                                    </v-card>
                                </v-flex>
                                <v-flex xs12 v-if="contact.comment">
                                    <v-card-text class="headline text-xs-left no-padding-left">
                                        <v-tooltip top>
                                            <v-icon color="info" size="30px" slot="activator">info</v-icon>
                                            Kommentar zum Ansprechpartner
                                        </v-tooltip>
                                        Bemerkung
                                    </v-card-text>
                                    <v-divider class="mb-3"/>
                                </v-flex>
                                <v-flex xs12 v-if="contact.comment">
                                    <span class="marked" v-html="markdownify(contact.comment)"/>
                                </v-flex>
                                <v-flex xs2 v-if="contact.labels.length > 0">
                                    <v-card dark color="info">
                                        <v-card-text class="headline text-xs-center">
                                            <v-icon size="30px" class="pt-1">label</v-icon>
                                        </v-card-text>
                                    </v-card>
                                </v-flex>
                                <v-flex xs4 class="text-xs-left" v-if="contact.labels.length > 0">
                                    <v-chip
                                            class="title mt-3"
                                            v-for="label in contact.labels"
                                            v-bind:key="label">
                                        {{ label }}
                                    </v-chip>
                                </v-flex>
                                <v-flex xs2>
                                    <v-card dark color="info">
                                        <v-tooltip top>
                                            <v-card-text slot="activator"
                                                         class="headline text-xs-center">
                                                <v-icon size="30px">account_circle</v-icon>
                                            </v-card-text>
                                            Verantwortlicher Manager für diesen Kontakt
                                        </v-tooltip>
                                    </v-card>
                                </v-flex>
                                <v-flex xs4>
                                    <v-card dark>
                                        <v-card-text class="headline text-xs-left font-weight-light">
                                            {{ contact.manager }}
                                        </v-card-text>
                                    </v-card>
                                </v-flex>
                            </v-layout>
                        </v-tab-item>
                    </v-tabs-items>
                </v-tabs>
                <v-card dark color="transparent" class="elevation-0" v-else>
                    <v-card-text
                            class="headline text-xs-center font-weight-light font-italic error--text">
                        N/A
                    </v-card-text>
                </v-card>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    import {mapActions, mapGetters, mapMutations} from 'vuex'
    import api from '../../utils/http-common'
    import marked from 'marked'

    import CompanyDialog from '../../components/dialogs/CompanyDialog.vue'
    import ContactDialog from '../../components/dialogs/ContactDialog.vue'

    export default {
        components: {
            CompanyDialog,
            ContactDialog
        },
        data() {
            return {
                contactDialogState: false,
                companyDialogState: false,
                companyId: this.$route.params.companyId
            }
        },
        computed: {
            ...mapGetters(['companies', 'contacts']),
            company() {
                return this.companies.find(company => {
                    return company.id == this.companyId;
                })
            },
            contactsOfCompany() {
                return this.contacts.filter((contact) => {
                    if (contact.company != null && this.company.id === contact.company.id) {
                        return contact;
                    }
                })
            },
            homepage() {
                return `http://${this.company.homepage}`;
            }
        },
        mounted() {
            this.refreshTable();
        },
        methods: {
            ...mapActions(['getCompanies', 'getContacts']),
            ...mapMutations({
                storeCompanyDetails: 'storeEditedCompanyDetails',
                storeContactDetails: 'storeEditedContactDetails'
            }),
            goPageBack() {
                this.$router.go(-1);
            },
            editCompany() {
                this.storeCompanyDetails({
                    editedIndex: this.companies.indexOf(this.company),
                    editedCompany: Object.assign({}, this.company)
                });

                this.openCompanyDialog()
            },
            editContact(item) {
                this.storeContactDetails({
                    editedIndex: this.companies.indexOf(item),
                    editedContact: Object.assign({}, item)
                });

                this.openContactDialog()
            },
            downloadInfo(item) {
                api.get("/contact/download/" + item.id)
                    .then(response => download(response.data, item.name));
            },
            openCompanyDialog() {
                this.companyDialogState = true
            },
            openContactDialog() {
                this.contactDialogState = true
            },
            refreshTable() {
                this.getCompanies();
                this.getContacts();
            },
            viewContactPoints() {
                this.$router.push('/' + (this.companyId));
            },
            markdownify(value) {
                return marked(value, {sanitize: true}).trim();
            }
        }
    }

    function download(content, name) {
        const url = window.URL.createObjectURL(new Blob([content], {type: "text/plain"}));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', name.replace(' ', '_').toLowerCase() + '.txt');
        document.body.appendChild(link);
        link.click();
    }
</script>

<style scoped>
    .centered {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 100vh;
    }

    .no-padding-left {
        padding-left: 0px;
    }

    .marked {
        white-space: pre-wrap;
    }
</style>
