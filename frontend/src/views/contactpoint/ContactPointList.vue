<template>
    <v-container grid-list-md fluid>
        <v-layout row wrap>
            <v-flex xs1>
                <v-btn block color="secondary" @click="goPageBack()">
                    <v-icon large dark>arrow_back</v-icon>
                </v-btn>
            </v-flex>
            <v-flex xs10>
                <h1 class="text-xs-center display-2 font-weight-thin">
                    {{ company.name }}
                </h1>
            </v-flex>
            <v-flex xs1>
                <v-btn block color="secondary" @click="viewCompany()">
                    <v-icon large dark>search</v-icon>
                </v-btn>
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
                <contact-point-dialog
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
                <contact-point-card
                        v-if="!loading"
                        :contactPoint="contactPoint"
                        v-bind:key="contactPoint.id"
                        v-for="contactPoint in sortedContactPoints"/>
            </v-flex>
            <v-flex xs4>
                <v-layout row wrap>
                    <v-scroll-x-transition>
                        <v-flex xs12 v-if="this.taskDone === false">
                            <v-card style="border-radius: 15px" height="100%" color="error">
                                <v-btn absolute top right fab small color="secondary" @click="taskIsDone()"
                                       class="elevation-12">
                                    <v-icon size="24px" color="success" class="ml-3">
                                        done_outline
                                    </v-icon>
                                </v-btn>
                                <v-card-text class="secondary--text headline text-xs-left">
                                    <v-icon size="30px" class="mr-2" color="secondary">
                                        error
                                    </v-icon>
                                    Michael Bescheid geben
                                </v-card-text>
                            </v-card>
                        </v-flex>
                    </v-scroll-x-transition>
                    <v-flex xs12>
                        <v-card style="border-radius: 15px" height="100%" color="warning">
                            <v-btn absolute top right fab small color="secondary" @click="" class="elevation-12">
                                <v-icon size="24px" color="success" class="ml-3">
                                    done_outline
                                </v-icon>
                            </v-btn>
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
                            <v-btn absolute top right fab small color="secondary" @click="" class="elevation-12">
                                <v-icon size="24px" color="success" class="ml-3">
                                    done_outline
                                </v-icon>
                            </v-btn>
                            <v-card-text class="headline text-xs-left">
                                <v-icon size="30px" class="mr-2">
                                    warning
                                </v-icon>
                                Mama anrufen
                            </v-card-text>
                        </v-card>
                    </v-flex>
                </v-layout>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    import {mapState} from 'vuex'
    import api from '@/utils/http-common'

    import pointStore from '@/stores/points.js'
    import companyStore from '@/stores/companies.js'
    import contactStore from '@/stores/contacts.js'

    import ContactPointDialog from "@/components/contactpoint/ContactPointDialog.vue"
    import ContactPointCard from "@/components/contactpoint/ContactPointCard.vue"

    export default {
        components: {
            ContactPointDialog,
            ContactPointCard
        },
        data() {
            return {
                loading: true,
                companyId: this.$route.params.companyId,
                contactNames: [],
                dialogState: false,
                taskDone: false
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
                }
            },
            ...mapState(['sortedContactPoints']),
            sortedContactPoints: {
                get() {
                    return pointStore.state.sortedContactPoints
                }
            }
        },
        mounted() {
            this.refreshData();
        },
        methods: {
            refreshData() {
                api.get(`point/get/${this.company.name}`).then(response => {
                    let contactPoints = response.data;
                    let sortedContactPoints = contactPoints.sort(compareContactPoints);

                    pointStore.commit({
                        type: 'storeContactPoints',
                        contactPoints: contactPoints,
                        sortedContactPoints: sortedContactPoints
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
                });

                this.contactNames.sort();
                this.dialogState = true;
            },
            taskIsDone() {
                this.taskDone = true;

                setTimeout(() => {
                    this.taskDone = false;
                }, 2000)
            },
            goPageBack() {
                this.$router.go(-1)
            },
            viewCompany() {
                this.$router.push('/companies/' + (this.companyId));
            }
        }
    }

    function compareContactPoints(a, b) {
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