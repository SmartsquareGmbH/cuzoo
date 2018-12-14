<template>
    <v-container grid-list-md fluid>
        <v-layout row wrap>
            <v-flex xs12>
                <h1 class="ml-1 text-xs-left display-2 font-weight-thin">
                    {{ company.name }}
                </h1>
                <v-divider class="mt-1"/>
            </v-flex>
            <v-flex xs8>
                <h1 class="ml-1 text-xs-left headline font-weight-light">
                    Kontaktpunkte
                    <v-btn small flat fab
                    v-on:click="addCPoint()"
                    color="transparent">
                        <v-tooltip top>
                            <v-icon large
                            color="light-green accent-2"
                            slot="activator">
                                add
                            </v-icon>
                            <span>Kontaktpunkt hinzufügen</span>
                        </v-tooltip>
                    </v-btn>
                </h1>
                <c-point-dialog/>
            </v-flex>
            <v-flex xs4>
                <h1 class="text-xs-left headline font-weight-light">
                    TODOs
                    <v-btn small fab flat
                    v-on:click="addTODO()"
                    color="transparent">
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
                <v-progress-linear
                v-if="loading"
                slot="progress"
                :size="50"
                color="primary"
                class="mt-3"
                indeterminate/>
                <c-point-card
                :cPoint="cPoint"
                v-bind:key="cPoint.id"
                v-for="cPoint in this.cPoints"/>
            </v-flex>
            <v-flex xs4>
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
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
import { mapState } from 'vuex'
import store from '@/store.js'
import api from '@/utils/http-common'

import CPointDialog from "@/components/point/CPointDialog.vue"
import CPointCard from "@/components/point/CPointCard.vue"

export default {
    components: {
        CPointDialog,
        CPointCard
    },
    data() {
        return {
            loading: true,
            companyId: this.$route.params.id,
            contactNames: [],
            cPoints: []
        }
    },
    computed: {
        company() {
            return this.companies[this.companyId]; 
        },
        ...mapState(['companies']),
        companies: {
            get() {
                return store.state.companies
            },
            set(companies) {
                store.commit('storeCompanies', companies)
            }
        },
        ...mapState(['contacts']),
        contacts: {
            get() {
                return store.state.contacts
            },
            set(contacts) {
                store.commit('storeContacts', contacts)
            }
        }
    },
    mounted() {
        this.refreshData()
    },
    methods: {
        refreshData() {
            api.get(`point/get/${this.company.name}`, {
                auth: {
                    username: store.getters.getLogName,
                    password: store.getters.getLogPass
                }
            }).then(response => {
                this.cPoints = response.data.sort(compare);
                console.log(this.cPoints)
            }).catch(error => {
                console.log(error)
                alert(error)
            }).then(() => {
                this.loading = false
            })
        },
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

            store.commit({
                type: 'storeCPointDialogState',
                contactNames: this.contactNames,
                cPointDialog: true
            })
        },
    }
}

function compare(a,b) {
    if (a.date === b.date) {
        if (a.id < b.id)
            return 1;
        if (a.id > b.id)
            return -1;
    } else if (a.date < b.date) {
        return 1;
    } else if (a.date > b.date) {
        return -1;
    } else return 0;
}

</script>