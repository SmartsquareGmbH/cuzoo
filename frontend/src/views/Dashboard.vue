<template>
    <v-container grid-list-md fluid>
        <v-layout row wrap v-if="!loading">
            <v-flex xs6>
                <v-layout row wrap class="text-xs-right">
                    <v-flex xs3 class="text-xs-left more-padding-top">
                        <v-icon color="primary" size="24px">forum</v-icon>
                        <span class="headline font-weight-light">
                            Kontaktpunkte
                        </span>
                    </v-flex>
                    <v-flex xs3>
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
                    </v-flex>
                    <v-flex xs6>
                    <v-text-field
                            ref="searchBarDashboard"
                            v-model="search"
                            @keyup.enter="goToFirstResult()"
                            append-icon="search"
                            label="Suche nach Kontaktpunkten"
                            color="primary"
                            hide-details
                            solo/>
                    </v-flex>
                </v-layout>
                <v-layout row wrap>
                    <vue-perfect-scrollbar class="scroll-area" v-once :settings="settings" @ps-scroll-y="scrollHanle">
                        <div style="max-height: 675px">
                            <v-flex xs12>
                                <contact-point-card
                                        :contact-point="contactPoint"
                                        :search="true"
                                        v-bind:key="contactPoint.id"
                                        v-for="contactPoint in contactPoints"/>
                            </v-flex>
                        </div>
                    </vue-perfect-scrollbar>
                </v-layout>
            </v-flex>
            <v-flex xs6>
                <v-layout row wrap class="text-xs-right">
                    <v-flex xs3 class="text-xs-left more-padding-top">
                        <v-icon color="primary" size="24px">done_all</v-icon>
                        <span class="headline font-weight-light">
                            TODOs
                        </span>
                    </v-flex>
                    <v-flex xs3>
                        <v-btn small flat fab
                               @click="addTODO()"
                               color="transparent">
                            <v-tooltip top>
                                <v-icon large
                                        color="light-green accent-2"
                                        slot="activator">
                                    add
                                </v-icon>
                                <span>TODO hinzufügen</span>
                            </v-tooltip>
                        </v-btn>
                    </v-flex>
                    <v-flex xs6>
                        <v-text-field
                                ref="searchBarDashboard"
                                v-model="search"
                                @keyup.enter="goToFirstResult()"
                                append-icon="search"
                                label="Suche nach TODOs"
                                color="primary"
                                hide-details
                                solo/>
                    </v-flex>
                </v-layout>
                <v-layout row wrap>
                    <v-flex xs12>
                        <vue-perfect-scrollbar class="scroll-area" v-once :settings="settings"
                                               @ps-scroll-y="scrollHanle">
                            <div style="max-height: 350px">
                                <v-layout row wrap>
                                    <todo-card
                                            :todo="todo"
                                            :search="true"
                                            v-bind:key="todo.id"
                                            v-for="todo in todos"/>
                                </v-layout>
                            </div>
                        </vue-perfect-scrollbar>
                    </v-flex>
                </v-layout>
            </v-flex>
        </v-layout>
        <v-layout v-if="loading">
            <v-flex xs12 class="text-xs-center">
                <v-progress-circular :size="128" color="primary" indeterminate/>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    import {mapActions, mapGetters} from 'vuex';

    import TodoDialog from '../components/dialogs/TodoDialog.vue';
    import TodoCard from '../components/cards/TodoCard.vue';
    import ContactPointDialog from "../components/dialogs/ContactPointDialog.vue";
    import ContactPointCard from "../components/cards/ContactPointCard.vue";
    import VuePerfectScrollbar from "vue-perfect-scrollbar";

    export default {
        components: {
            VuePerfectScrollbar,
            TodoDialog,
            TodoCard,
            ContactPointDialog,
            ContactPointCard
        },
        data: () => ({
            contactPointDialogState: false,
            todoDialogState: false,
            loading: true,
            search: '',
            settings: {
                maxScrollbarLength: 120,
                wheelSpeed: 0.5
            },
        }),
        computed: {
            ...mapGetters([
                'companies',
                'contacts',
                'contactPoints',
                'todos',
            ]),
        },
        beforeMount() {
            this.refreshData();
        },
        methods: {
            ...mapActions([
                'getCompanies',
                'getContacts',
                'getContactPoints',
                'getTodos'
            ]),
            goToFirstResult() {
                const isCompany = containsKey(this.searchResults[0], 'name');
                let index;

                if (isCompany) {
                    index = this.companies.findIndex(company => company.id === this.searchResults[0].id);

                    this.$router.push('/' + (index));
                } else {
                    index = this.companies.findIndex(company => company.id === this.searchResults[0].contact.company.id);

                    this.$router.push(`/${index}/${this.searchResults[0].id}`);

                }
            },
            refreshData() {
                this.getCompanies();
                this.getContacts();
                this.getContactPoints().then(() => {
                    this.getTodos().then(() => this.loading = false);
                });
            },
            scrollHanle(evt) {
                console.log(evt)
            },
            addContactPoint() {
                this.contactPointDialogState = true;
            },
            addTODO() {
                this.todoDialogState = true;
            }
        }
    }
</script>

<style scoped>
    .more-padding-top {
        padding-top: 12px !important;
    }
</style>