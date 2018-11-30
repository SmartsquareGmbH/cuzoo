<template>
    <v-container grid-list-md text-xs-center fluid>
        <v-layout row wrap>
            <v-flex xs1>
                <v-btn block color="secondary" @click="goPageBack()">
                    <v-icon large dark>arrow_back</v-icon>
                </v-btn>
            </v-flex>
            <v-flex xs4>
                <h1 class="display-1 text-xs-center">Unternehmen</h1>
            </v-flex>
            <v-flex xs1>
                <v-btn block color="secondary" @click="editCompany(companies[companyId])">
                    <v-icon large dark>edit</v-icon>
                </v-btn>
            </v-flex>
            <company-dialog/>
            <v-flex xs1>
                <v-btn block color="secondary" @click="addContact()">
                    <v-icon large dark>add</v-icon>
                </v-btn>
            </v-flex>
            <v-flex xs4>
                <h1 class="display-1 text-xs-center">Ansprechpartner</h1>
            </v-flex>
            <v-flex xs1>
                <v-btn v-if="contactsOfCompany.length > 0" block color="secondary" @click="editContact(contactsOfCompany[contactsOfCompany.contact])">
                    <v-icon large dark>edit</v-icon>
                </v-btn>
            </v-flex>
            <contact-dialog/>
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
                                {{ this.companies[this.companyId].name }}
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
                                    v-if="this.companies[this.companyId].homepage != null &&
                        this.companies[this.companyId].homepage != ''"
                                    class="headline text-xs-left">
                                <a :href=this.homepage target="_blank">{{ this.companies[this.companyId].homepage }}</a>
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
                                    v-if="this.companies[this.companyId].zipCode != null &&
                        this.companies[this.companyId].zipCode != ''"
                                    class="headline text-xs-left">
                                {{ this.companies[this.companyId].zipCode }}
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
                                    v-if="this.companies[this.companyId].place != null &&
                        this.companies[this.companyId].place != ''"
                                    class="headline text-xs-left">
                                {{ this.companies[this.companyId].place }}
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
                                    v-if="this.companies[this.companyId].street != null &&
                        this.companies[this.companyId].street != ''"
                                    class="headline text-xs-left">
                                {{ this.companies[this.companyId].street }}
                            </v-card-text>
                            <v-card-text
                                    v-else
                                    class="headline text-xs-left font-weight-light font-italic error--text">
                                N/A
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs12>
                        <v-card dark color="transparent" class="elevation-0">
                            <v-card-text class="headline text-xs-left">
                                <v-tooltip top>
                                    <v-icon color="info" size="36px" slot="activator">info</v-icon>
                                    Hauptfunktionen des Unternehmens
                                </v-tooltip>
                                Unternehmenszweck
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs12>
                        <v-card dark>
                            <v-card-text
                                    v-if="this.companies[this.companyId].purpose != null &&
                        this.companies[this.companyId].purpose !== ''"
                                    class="headline text-xs-left">
                                {{ this.companies[this.companyId].purpose }}
                            </v-card-text>
                            <v-card-text
                                    v-else
                                    class="headline text-xs-left font-weight-light font-italic error--text">
                                N/A
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs9>
                        <v-card dark color="transparent" class="elevation-0">
                            <v-card-text class="headline text-xs-left">
                                <v-tooltip top>
                                    <v-icon color="info" size="36px" slot="activator">info</v-icon>
                                    Sonstige Angaben zum Unternehmen
                                </v-tooltip>
                                Sonstiges
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs3>
                        <v-card dark v-if="this.companies[this.companyId].status === 'Bestandskunde'" color="info">
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
                                    v-if="this.companies[this.companyId].other != null &&
                        this.companies[this.companyId].other !== ''"
                                    class="headline text-xs-left">
                                {{ this.companies[this.companyId].other }}
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
                                    <v-card dark color="transparent" class="elevation-0">
                                        <v-card-text class="headline text-xs-left">
                                            <v-tooltip top>
                                                <v-icon color="info" size="36px" slot="activator">info</v-icon>
                                                Sonstige Angaben zur Kontaktperson
                                            </v-tooltip>
                                            Sonstiges
                                        </v-card-text>
                                    </v-card>
                                </v-flex>
                                <v-flex xs12>
                                    <v-card dark>
                                        <v-card-text
                                                v-if="contact.comment != null &&
                                contact.comment !== ''"
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
    import {mapState} from 'vuex';
    import CompanyDialog from "@/components/Company/CompanyDialog.vue";
    import ContactDialog from "@/components/Contact/ContactDialog.vue";

    export default {
        components: {
            CompanyDialog,
            ContactDialog
        },
        computed: {
            companyId() {
                return this.$route.params.id;
            },
            companies() {
                return this.$store.getters.getCompanies;
            },
            homepage() {
                return `http://${this.companies[this.companyId].homepage}`;
            },
            ...mapState(['contacts']),
            contacts: {
                get() {
                    return this.$store.state.contacts
                },
                set(contacts) {
                    this.$store.commit('storeContacts', contacts)
                }
            },
            contactsOfCompany() {
                return this.contacts.filter((contact) => {
                    if (contact.company != null && this.companies[this.companyId].id === contact.company.id) {
                        return contact;
                    }
                })
            }
        },
        methods: {
            goPageBack() {
                this.$router.go(-1);
            },
            editCompany: function (item) {
                this.$store.commit({
                    type: 'storeEditedCompanyDetails',
                    editedIndex: this.companies.indexOf(item),
                    editedCompany: Object.assign({}, item)
                });
                this.openCompanyDialog();
            },
            openCompanyDialog() {
                this.$store.commit({
                    type: 'storeCompanyDialogState',
                    companyDialog: true
                })
            },
            editContact: function (item) {
                console.log(item);
                this.$store.commit({
                    type: 'storeEditedContactDetails',
                    editedIndex: this.contacts.indexOf(item),
                    editedContact: Object.assign({}, item)
                });
                this.openContactDialog();
            },
            openContactDialog() {
                this.$store.commit({
                    type: 'storeContactDialogState',
                    contactDialog: true
                })
            },
            addContact() {
                this.$store.commit({
                    type: 'storeContactDialogState',
                    contactDialog: true
                });
            },
            refreshTable() {
                this.$store.dispatch('getCompanies');
                this.$store.dispatch('getContacts');
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
