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
                        v-model="contactPointDialogState"
                        :contactNames="this.contactNames"/>
            </v-flex>
            <v-flex xs4>
                <h1 class="text-xs-left headline font-weight-light">
                    TODOs
                    <v-btn small fab flat
                           @click="addTODO()"
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
                <todo-dialog
                        v-model="todoDialogState"
                        :company="this.company"/>
            </v-flex>
            <v-flex xs8>
                <v-progress-circular
                        v-if="loadingContactPoints"
                        slot="progress"
                        :size="50"
                        color="primary"
                        indeterminate/>
                <contact-point-card
                        v-if="!loadingContactPoints"
                        :contactPoint="contactPoint"
                        v-bind:key="contactPoint.id"
                        v-for="contactPoint in sortedContactPoints"/>
            </v-flex>
            <v-flex xs4>
                <v-layout row wrap>
                    <v-progress-circular
                            v-if="loadingContactPoints"
                            slot="progress"
                            :size="50"
                            color="primary"
                            indeterminate/>
                    <todo-card
                            v-if="!loadingTodos"
                            :todo="todo"
                            v-bind:key="todo.id"
                            v-for="todo in sortedTodos"/>
                </v-layout>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    import {mapGetters, mapMutations} from 'vuex'
    import api from '../../utils/http-common'

    import TodoDialog from '../../components/todo/TodoDialog.vue'
    import TodoCard from '../../components/todo/TodoCard.vue'
    import ContactPointDialog from "../../components/contactpoint/ContactPointDialog.vue"
    import ContactPointCard from "../../components/contactpoint/ContactPointCard.vue"

    const datefns = require('date-fns');

    export default {
        components: {
            TodoDialog,
            TodoCard,
            ContactPointDialog,
            ContactPointCard
        },
        data() {
            return {
                loadingContactPoints: true,
                loadingTodos: true,
                companyId: this.$route.params.companyId,
                contactNames: [],
                contactPointDialogState: false,
                todoDialogState: false,
                taskDone: false
            }
        },
        computed: {
            ...mapGetters([
                'companies',
                'contacts',
                'contactPoints',
                'sortedContactPoints',
                'todos',
                'sortedTodos'
            ]),
            company() {
                return this.companies[this.companyId];
            }
        },
        mounted() {
            this.refreshContactPoints();
            this.refreshTodos();
        },
        methods: {
            ...mapMutations(['storeContactPoints', 'storeTodos']),
            refreshData() {
                this.refreshContactPoints();
                this.refreshTodos();
            },
            refreshContactPoints() {
                api.get(`point/get/${this.company.name}`).then(response => {
                    let contactPoints = response.data;

                    contactPoints.forEach(contactPoint => {
                        contactPoint.labels = contactPoint.labels.map(label => {
                            return label.title;
                        });
                    });

                    let sortedContactPoints = contactPoints.sort(compareContactPoints);

                    this.storeContactPoints({
                        contactPoints: contactPoints,
                        sortedContactPoints: sortedContactPoints
                    })
                }).catch(error => {
                    console.log(error)
                }).then(() => {
                    this.loadingContactPoints = false
                })
            },
            refreshTodos() {
                api.get(`todo/get/${this.company.name}`).then(response => {
                    let todos = response.data;
                    let sortedTodos = todos.sort(compareTodos);

                    this.storeTodos({
                        todos: todos,
                        sortedTodos: sortedTodos
                    })
                }).catch(error => {
                    console.log(error)
                }).then(() => {
                    this.loadingTodos = false
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
                this.contactPointDialogState = true;
            },
            addTODO() {
                this.todoDialogState = true;
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
        if (datefns.compareAsc(a.date, b.date) === 0) {
            if (a.id < b.id)
                return 1;
            if (a.id > b.id)
                return -1;
        } else {
            return datefns.compareAsc(b.date, a.date);
        }
    }

    function compareTodos(a, b) {
        if (datefns.compareAsc(a.expiration, b.expiration) === 0) {
            if (a.id < b.id)
                return 1;
            if (a.id > b.id)
                return -1;
        } else {
            return datefns.compareAsc(a.expiration, b.expiration);
        }
    }
</script>