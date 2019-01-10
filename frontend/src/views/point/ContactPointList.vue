<template>
    <v-container grid-list-md fluid>
        <v-layout row wrap>
            <v-flex xs1>
                <v-btn block color="secondary" @click="goPageBack()">
                    <v-icon large dark>arrow_back</v-icon>
                </v-btn>
            </v-flex>
            <v-flex xs11>
                <h1 class="text-xs-left display-2 font-weight-thin">
                    {{ company.name }}
                </h1>
            </v-flex>

            <v-flex xs12>
                <v-divider/>
            </v-flex>
            <v-flex xs8>
                <h1 class="ml-1 text-xs-left headline font-weight-light">
                    Kontaktpunkte
                    <v-btn small flat fab
                    @click="addContactPoint()"
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
                <c-point-dialog 
                v-model="dialogState"
                :contactNames="this.contactNames"/>
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
                <v-progress-circular
                v-if="loading"
                slot="progress"
                :size="50"
                color="primary"
                indeterminate/>
                <c-point-card
                v-if="!loading"
                :contactPoint="contactPoint"
                v-bind:key="contactPoint.id"
                v-for="contactPoint in this.contactPoints"/>
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
import api from '@/utils/http-common'

import store from '@/store.js'
import pointStore from '@/stores/points.js'
import companyStore from '@/stores/companies.js'
import contactStore from '@/stores/contacts.js'

import ContactPointDialog from "@/components/point/ContactPointDialog.vue"
import ContactPointCard from "@/components/point/ContactPointCard.vue"

export default {
    components: {
        ContactPointDialog,
        ContactPointCard
    },
    data() {
        return {
            loading: true,
            companyId: this.$route.params.id,
            contactNames: [],
            dialogState: false
        }
    },
    computed: {
        company() {
            return this.companies[this.companyId]; 
        },
        ...mapState(['companies']),
        companies: {
            get() {
                return companyStore.state.companies
            }
        },
        ...mapState(['contacts']),
        contacts: {
            get() {
                return contactStore.state.contacts
            }
        },
        ...mapState(['contactPoints']),
        contactPoints: {
            get() {
                return pointStore.state.contactPoints
            },
            set(contactPoints) {
                pointStore.commit('storeContactPoints', contactPoints)
            }
        }
    },
    mounted() {
        this.refreshData()
    },
    methods: {
        refreshData() {
            api.get(`point/get/${this.company.name}`).then(response => {
                console.log(response);
                let sortedContactPoints = response.data.sort(compareContactPoints)
                pointStore.commit({
                    type: 'storeContactPoints',
                    contactPoints: sortedContactPoints
                })
            }).catch(error => {
                console.log(error)
            }).then(() => {
                this.loading = false
            })
        },
        getContactsOfCompany() {
            return this.contacts.filter((contact) => {
                if (contact.company != null) {
                    return contact.company.name === this.company.name
                } else {
                    return null
                }
            })
        },
        addContactPoint() {
            this.getContactsOfCompany().forEach(contact => {
                this.contactNames.push(contact.name)
            })

            this.contactNames.sort()
            this.dialogState = true
        },
        goPageBack() {
            this.$router.go(-1)
        }
    }
}

function compareContactPoints(a,b) {
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