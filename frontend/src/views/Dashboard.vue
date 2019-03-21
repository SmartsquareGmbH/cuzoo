<template>
    <v-container grid-list-md fluid>
        <v-fade-transition>
            <v-layout row wrap v-if="!loadingData" v-resize="onResize">
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
                            <contact-point-dialog
                                    v-model="contactPointDialogState"
                                    :contactNames="this.contactNames"/>
                        </v-flex>
                        <v-flex xs6>
                            <v-text-field
                                    ref="searchBarContactPoints"
                                    v-model="searchContactPoints"
                                    @keyup.enter="goToFirstResult()"
                                    append-icon="search"
                                    label="Suche nach Kontaktpunkten"
                                    color="primary"
                                    hide-details
                                    solo/>
                        </v-flex>
                    </v-layout>
                    <v-layout row wrap>
                        <div class="dash">
                            <perfect-scrollbar :options="settings">
                                <div :style="`height: ${this.windowHeight - 245}px;`">
                                    <v-flex xs12 class="no-padding-top">
                                        <contact-point-results
                                                :search="this.searchContactPoints"
                                                :on-dashboard="true"/>
                                    </v-flex>
                                </div>
                            </perfect-scrollbar>
                            <div class="fade-out-gradient"/>
                        </div>
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
                            <todo-dialog
                                    v-model="todoDialogState"
                                    :companyNames="this.companyNames"/>
                        </v-flex>
                        <v-flex xs6>
                            <v-text-field
                                    ref="searchBarTodos"
                                    v-model="searchTodos"
                                    @keyup.enter="goToFirstResult()"
                                    append-icon="search"
                                    label="Suche nach TODOs"
                                    color="primary"
                                    hide-details
                                    solo/>
                        </v-flex>
                    </v-layout>
                    <v-layout row wrap>
                        <div class="dash">
                            <perfect-scrollbar :options="settings">
                                <div :style="`height: ${(this.windowHeight - 245) / 2}px`">
                                    <v-layout row wrap>
                                        <v-flex xs12>
                                            <todo-results :search="this.searchTodos"/>
                                        </v-flex>
                                    </v-layout>
                                </div>
                            </perfect-scrollbar>
                            <div class="fade-out-gradient"/>
                        </div>
                    </v-layout>
                </v-flex>
            </v-layout>
        </v-fade-transition>
        <v-layout v-if="loadingData">
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
    import TodoResults from '../components/search/TodoResults.vue'
    import ContactPointDialog from "../components/dialogs/ContactPointDialog.vue";
    import ContactPointCard from "../components/cards/ContactPointCard.vue";
    import ContactPointResults from "../components/search/ContactPointResults.vue";

    export default {
        components: {
            TodoDialog,
            TodoCard,
            TodoResults,
            ContactPointDialog,
            ContactPointCard,
            ContactPointResults
        },
        data: () => ({
            windowHeight: 0,
            contactNames: [],
            contactPointDialogState: false,
            companyNames: [],
            todoDialogState: false,
            loadingData: true,
            searchContactPoints: '',
            searchTodos: '',
            settings: {
                maxScrollbarLength: 120,
                wheelSpeed: 0.75,
                suppressScrollX: true
            },
        }),
        computed: {
            ...mapGetters([
                'companies',
                'contacts',
                'contactPoints',
                'todos',
                'searchResults',
                'selectedCompany'
            ])
        },
        beforeMount() {
            this.refreshData();
            this.onResize();
        },
        watch: {
            selectedCompany() {
                if (!this.selectedCompany || this.companies.map(it => it.name).includes(this.selectedCompany)) {
                    this.searchContactPoints = this.selectedCompany;
                    this.searchTodos = this.selectedCompany;
                }
            }
        },
        methods: {
            ...mapActions([
                'getCompanies',
                'getContacts',
                'getContactPoints',
                'getTodos'
            ]),
            addContactPoint() {
                this.contactNames = this.contacts.map(it => it.name).sort();
                this.contactPointDialogState = true;
            },
            addTODO() {
                this.companyNames = this.companies.map(it => it.name).sort();
                this.todoDialogState = true;
            },
            goToFirstResult() {
                let companyId = this.companies.find(it => it.id === this.searchResults[0].contact.company.id).id;

                this.$router.push(`/${companyId}/${this.searchResults[0].id}`);
            },
            refreshData() {
                this.getCompanies();
                this.getContacts();
                this.getContactPoints().then(() => {
                    this.getTodos().then(() => this.loadingData = false);
                });
            },
            onResize() {
                this.windowHeight = window.innerHeight;
            }
        }
    }
</script>

<style scoped>
    .dash {
        height: 100%;
        width: 100%;
        position: relative;
    }

    .fade-out-gradient {
        position: absolute;
        bottom: 0;
        left: 0;
        width: 100%;
        text-align: center;
        margin: 0;
        padding: 15px 0;

        background-image: linear-gradient(to bottom, transparent, #333333);
    }

    .more-padding-top {
        padding-top: 12px !important;
    }

    .no-padding-top {
        padding-top: 0px !important;
    }
</style>