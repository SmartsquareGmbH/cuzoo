<template>
    <v-container grid-list-md fluid>
        <v-layout row wrap>
            <v-flex xs12>
                <h1 
                class="ml-1 text-xs-left display-2 font-weight-thin">
                    {{ company.name }}
                </h1>
                <v-divider class="mt-1"/>
            </v-flex>
            <v-flex xs8>
                <h1 
                class="ml-1 text-xs-left headline font-weight-light">
                    Kontaktpunkte
                    <v-btn
                    v-on:click="addCPoint()"
                    color="transparent"
                    small
                    flat
                    fab>
                        <v-tooltip top>
                            <v-icon
                            color="light-green accent-2"
                            slot="activator"
                            large>
                                add
                            </v-icon>
                            <span>Kontaktpunkt hinzufügen</span>
                        </v-tooltip>
                    </v-btn>
                    <c-point-dialog></c-point-dialog>
                </h1>
            </v-flex>
            <v-flex xs4>
                <h1 
                class="text-xs-left headline font-weight-light">
                    TODOs
                    <v-btn
                    v-on:click="addTODO()"
                    color="transparent"
                    small
                    fab
                    flat>
                        <v-tooltip top>
                            <v-icon
                            color="light-green accent-2"
                            slot="activator"
                            large>
                                add
                            </v-icon>
                            <span>TODO hinzufügen</span>
                        </v-tooltip>
                    </v-btn>
                </h1>
            </v-flex>
            <v-flex xs8>
                <div>
                    <v-layout row wrap>
                        <v-flex xs1>
                            <v-card color="info" height="100%" style="border-radius: 15px">
                                <v-card-text class="pt-4">
                                    <v-icon large class="pt-4">mail</v-icon>
                                </v-card-text>
                            </v-card>
                        </v-flex>
                        <v-flex xs11>
                            <v-card 
                            style="border-radius: 15px"
                            height="100%">
                                <v-card-title class="headline font-weight-light">
                                    Auftrag •                                     
                                    <span class="ml-2 mt-1 primary--text mr-2">
                                        06.12.18
                                    </span>
                                    •
                                    <v-icon class="ml-1">
                                        attach_file
                                    </v-icon>
                                    <span class="ml-1 mt-1 primary--text">
                                        0
                                    </span>
                                    <v-spacer></v-spacer>
                                    <v-chip
                                    v-for="contact in this.getContactsOfCompany(company.name)"
                                    v-bind:key="contact.id"
                                    class="mb-2"
                                    color="white">
                                        <v-avatar class="info white--text" size="35px">
                                            <v-icon>person</v-icon>
                                        </v-avatar>
                                        <span class="headline secondary--text">
                                            {{ contact.name }}
                                        </span>
                                    </v-chip>
                                </v-card-title>
                                <v-card-text class="subheading text-xs-left text-truncate">
                                    Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor 
                                    invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam 
                                    et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.
                                </v-card-text>
                            </v-card>
                        </v-flex>
                        <v-flex xs1>
                            <v-card color="info" height="100%" style="border-radius: 15px">
                                <v-card-text class="pt-4">
                                    <v-icon large class="pt-4">phone</v-icon>
                                </v-card-text>
                            </v-card>
                        </v-flex>
                        <v-flex xs11>
                            <v-card 
                            style="border-radius: 15px"
                            height="100%">
                                <v-card-title class="headline font-weight-light">
                                    Beratungsgespräch •
                                    <span class="ml-2 mt-1 primary--text mr-2">
                                        03.12.18
                                    </span>
                                    •
                                    <v-icon class="ml-1">
                                        attach_file
                                    </v-icon>
                                    <span class="ml-1 mt-1 primary--text">
                                        2
                                    </span>
                                    <v-spacer></v-spacer>
                                    <v-chip
                                    class="mb-2"
                                    color="white"
                                    v-bind:key="contact.id"
                                    v-for="contact in this.getContactsOfCompany(company.name)">
                                        <v-avatar class="info white--text" size="35px">
                                            <v-icon>person</v-icon>
                                        </v-avatar>
                                        <span class="headline secondary--text">
                                            {{ contact.name }}
                                        </span>
                                    </v-chip>
                                </v-card-title>
                                <v-card-text class="subheading text-xs-left text-truncate">
                                    Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor 
                                    invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam 
                                    et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.
                                </v-card-text>
                            </v-card>
                        </v-flex>
                    </v-layout>
                </div>
            </v-flex>
            <v-flex xs4>
                <div>
                    <v-layout row wrap>
                        <v-flex xs12>
                            <v-card style="border-radius: 15px" height="100%" color="error">
                                <v-card-text class="secondary--text headline text-xs-left">
                                    <v-icon size="30px" class="mr-2" color="secondary">
                                        error
                                    </v-icon>
                                    Michael anrufen
                                </v-card-text>
                            </v-card>
                        </v-flex>
                        <v-flex xs12>
                            <v-card style="border-radius: 15px" height="100%" color="warning">
                                <v-card-text class="secondary--text headline text-xs-left">
                                    <v-icon size="30px" class="mr-2" color="secondary">
                                        warning
                                    </v-icon>
                                    Pflichtenheft schreiben
                                </v-card-text>
                            </v-card>
                        </v-flex>
                        <v-flex xs12>
                            <v-card style="border-radius: 15px" height="100%" color="secondary">
                                <v-card-text class="headline text-xs-left">
                                    <v-icon size="30px" class="mr-2">
                                        warning
                                    </v-icon>
                                    Pflichtenheft schreiben
                                </v-card-text>
                            </v-card>
                        </v-flex>
                    </v-layout>
                </div>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
import { mapState } from 'vuex';
import CPointDialog from "@/components/CPointDialog.vue";

export default {
    components: {
        CPointDialog
    },
    data() {
        return {
            companyId: this.$route.params.id,
            contactNames: []
        }
    },
    computed: {
        company() {
            return this.companies[this.companyId]; 
        },
        ...mapState(['companies']),
        companies: {
            get() {
                return this.$store.state.companies
            },
            set(companies) {
                this.$store.commit('storeCompanies', companies)
            }
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
    methods: {
        getContactsOfCompany() {
            return this.contacts.filter((contact) => {
                if (contact.company != null) {
                    return contact.company.name === this.company.name;
                } else {
                    return null;
                }
            })
        },
        addCPoint() {
            this.getContactsOfCompany().forEach(contact => {
                this.contactNames.push(contact.name);
            })

            this.$store.commit({
                type: 'storeCPointDialogState',
                contactNames: this.contactNames,
                cPointDialog: true
            })
        }
    }
}
</script>