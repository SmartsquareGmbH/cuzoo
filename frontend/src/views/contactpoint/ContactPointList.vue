<template>
    <v-container grid-list-md fluid>
        <v-layout row wrap class="text-xs-left">
            <v-flex xs12>
                <v-icon aboslute size="48px" class="mr-3" color="primary">business</v-icon>
                <span class="text-xs-left display-2 font-weight-thin">{{ company.name }}</span>
                <v-tooltip bottom>
                    <v-btn slot="activator"
                           @click="viewCompany()"
                           class="ml-5 mt-3"
                           absolute right
                           flat small>
                        QuickView
                    </v-btn>
                    Detailansicht des Unternehmens
                </v-tooltip>
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
                        :search="false"
                        v-bind:key="contactPoint.id"
                        v-for="contactPoint in companiesContactPoints"/>
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
                            v-for="todo in companiesTodos"/>
                </v-layout>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    import {mapActions, mapGetters} from 'vuex';

    import TodoDialog from '../../components/dialogs/TodoDialog.vue';
    import TodoCard from '../../components/cards/TodoCard.vue';
    import ContactPointDialog from "../../components/dialogs/ContactPointDialog.vue";
    import ContactPointCard from "../../components/cards/ContactPointCard.vue";

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
                todoDialogState: false
            }
        },
        computed: {
            ...mapGetters([
                'companies',
                'contacts',
                'contactPoints',
                'todos',
            ]),
            company() {
                return this.companies.find(company => {
                    return company.id == this.companyId;
                })
            },
            companiesContactPoints() {
                return this.contactPoints.filter(contactPoint => {
                    return contactPoint.contact.company.id === this.company.id;
                })
            },
            companiesTodos() {
                return this.todos.filter(todo => {
                    return todo.company.id === this.company.id;
                })
            }
        },
        mounted() {
            this.refreshData();
        },
        methods: {
            ...mapActions(['getContactPoints', 'getTodos']),
            refreshData() {
                this.getContactPoints().then(() => this.loadingContactPoints = false);
                this.getTodos().then(() => this.loadingTodos = false);
            },
            getContactsOfCompany() {
                return this.contacts.filter((contact) => {
                    if (contact.company) {
                        return contact.company.name === this.company.name
                    }
                })
            },
            addContactPoint() {
                this.contactNames = this.getContactsOfCompany().map(it => it.name).sort();
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
</script>