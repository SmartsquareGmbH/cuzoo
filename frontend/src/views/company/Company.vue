<template>
    <v-container grid-list-md text-xs-center fluid>
        <v-layout row wrap>
            <v-flex xs1>
                <v-btn block color="secondary" @click="goPageBack()">
                    <v-icon large dark>arrow_back</v-icon>
                </v-btn>
            </v-flex>
            <v-flex xs3>
                <h1 class="display-1 text-xs-right"></h1>
            </v-flex>
            <v-flex xs1>
                <v-tooltip top>
                    <v-btn slot="activator" block color="secondary" @click="viewContactPoints()">
                        <v-icon large dark>import_contacts</v-icon>
                    </v-btn>
                    Anzeigen der Kontaktpunktliste
                </v-tooltip>
            </v-flex>
            <v-flex xs1>
                <v-tooltip top>
                    <v-btn slot="activator" block color="secondary" @click="editCompany(companies[companyId])">
                        <v-icon large dark>edit</v-icon>
                    </v-btn>
                    Unternehmen editieren
                </v-tooltip>
            </v-flex>
            <company-dialog v-model="companyDialogState"/>
            <v-flex xs1>
                <v-tooltip top>
                    <v-btn slot="activator" block color="secondary" @click="openContactDialog()">
                        <v-icon large dark>add</v-icon>
                    </v-btn>
                    Ansprechpartner hinzufügen
                </v-tooltip>
            </v-flex>
            <v-flex xs3>
            </v-flex>
            <v-flex xs1>
                <v-tooltip top>
                    <v-btn slot="activator" v-if="contactsOfCompany.length > 0" block color="secondary"
                           @click="editContact(contactsOfCompany[contactsOfCompany.contact])">
                        <v-icon large dark>edit</v-icon>
                    </v-btn>
                    Ansprechpartner editieren
                </v-tooltip>
            </v-flex>
            <contact-dialog
                    v-model="contactDialogState"
                    :companyNames="[this.company.name]"/>
            <v-flex xs6>
                <v-layout row wrap>
                    <v-flex xs2>
                        <v-card height="100%" dark color="green">
                            <v-card-text class="headline text-xs-center">
                                <v-icon size="30px">business_center</v-icon>
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs10>
                        <v-card height="100%" dark>
                            <v-card-text class="headline text-xs-left">
                                {{ this.company.name }}
                            </v-card-text>
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
                                    v-if="this.company.homepage != null && this.company.homepage !== ''"
                                    class="headline text-xs-left">
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
                            <v-card-text class="headline text-xs-center">
                                <v-icon size="30px">place</v-icon>
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs2>
                        <v-card dark>
                            <v-card-text
                                    v-if="this.company.zipCode != null && this.company.zipCode !== ''"
                                    class="headline text-xs-left">
                                {{ this.company.zipCode }}
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
                                    v-if="this.company.place != null && this.company.place !== ''"
                                    class="headline text-xs-left">
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
                                    v-if="this.company.street != null && this.company.street !== ''"
                                    class="headline text-xs-left">
                                {{ this.company.street }}
                            </v-card-text>
                            <v-card-text
                                    v-else
                                    class="headline text-xs-left font-weight-light font-italic error--text">
                                N/A
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs12>
                        <v-card-text class="headline text-xs-left">
                            <v-tooltip top>
                                <v-icon color="info" size="36px" slot="activator">info</v-icon>
                                Hauptfunktionen des Unternehmens
                            </v-tooltip>
                            Unternehmenszweck
                        </v-card-text>
                    </v-flex>
                    <v-flex xs12>
                        <v-card dark>
                            <v-card-text
                                    v-if="this.company.purpose != null && this.company.purpose !== ''"
                                    class="headline text-xs-left">
                                {{ this.company.purpose }}
                            </v-card-text>
                            <v-card-text
                                    v-else
                                    class="headline text-xs-left font-weight-light font-italic error--text">
                                N/A
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs9>
                        <v-card-text class="headline text-xs-left">
                            <v-tooltip top>
                                <v-icon color="info" size="36px" slot="activator">info</v-icon>
                                Sonstige Angaben zum Unternehmen
                            </v-tooltip>
                            Sonstiges
                        </v-card-text>
                    </v-flex>
                    <v-flex xs3>
                        <v-card dark v-if="this.company.status === 'Bestandskunde'" color="info">
                            <v-card-text class="headline text-xs-center font-italic">
                                Bestandskunde
                            </v-card-text>
                        </v-card>
                        <v-card dark v-else color="warning">
                            <v-card-text class="headline text-xs-center font-italic">
                                Lead
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs12>
                        <v-card dark>
                            <v-card-text
                                    v-if="this.company.other != null && this.company.other !== ''"
                                    class="headline text-xs-left">
                                {{ this.company.other }}
                            </v-card-text>
                            <v-card-text
                                    v-else
                                    class="headline text-xs-left font-weight-light font-italic error--text">
                                N/A
                            </v-card-text>
                        </v-card>
                    </v-flex>
                </v-layout>
            </v-flex>
            <v-flex xs6>
                <v-tabs centered height="64%" grow slider-color="info" v-model="contactsOfCompany.contact"
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
                                                class="headline text-xs-left">
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
                                                v-if="contact.mail != null && contact.mail !== ''"
                                                class="headline text-xs-left">
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
                                <v-flex xs10>
                                    <v-card dark>
                                        <v-card-text
                                                v-if="contact.telephone != null && contact.telephone !== ''"
                                                class="headline text-xs-left">
                                            {{ contact.telephone }}
                                        </v-card-text>
                                        <v-card-text
                                                v-else
                                                class="headline text-xs-left font-weight-light font-italic error--text">
                                            N/A
                                        </v-card-text>
                                    </v-card>
                                </v-flex>
                                <v-flex xs12>
                                    <v-card-text class="headline text-xs-left">
                                        <v-tooltip top>
                                            <v-icon color="info" size="36px" slot="activator" class="pt-0">info</v-icon>
                                            Sonstige Angaben zum Ansprechpartner
                                        </v-tooltip>
                                        Sonstiges
                                    </v-card-text>
                                </v-flex>
                                <v-flex xs12>
                                    <v-card dark>
                                        <v-card-text
                                                v-if="contact.comment != null && contact.comment !== ''"
                                                class="headline text-xs-left">
                                            {{ contact.comment }}
                                        </v-card-text>
                                        <v-card-text
                                                v-else
                                                class="headline text-xs-left font-weight-light font-italic error--text">
                                            N/A
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

    import CompanyDialog from '../../components/company/CompanyDialog.vue'
    import ContactDialog from '../../components/contact/ContactDialog.vue'

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
                return this.companies[this.companyId]
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
        methods: {
            ...mapActions(['getCompanies', 'getContacts']),
            ...mapMutations({
                storeCompanyDetails: 'storeEditedCompanyDetails',
                storeContactDetails: 'storeEditedContactDetails'
            }),
            goPageBack() {
                this.$router.go(-1);
            },
            editCompany(item) {
                this.storeCompanyDetails({
                    editedIndex: this.companies.indexOf(item),
                    editedCompany: Object.assign({}, item)
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
            }
        }
    }
</script>

<style>
    .centered {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 100vh;
    }
</style>
