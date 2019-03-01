<template>
    <v-container grid-list-md fluid>
        <v-layout row wrap v-if="!loading">
            <v-flex xs6>
                <v-card style="border-style: solid; border-color: #4FC3F7; border-width: 0px">
                    <v-card-title class="secondary title font-weight-light">
                        Kontaktpunkte
                        <v-btn small flat fab
                               absolute right
                               @click="expandSearchBar()"
                               color="transparent">
                            <v-tooltip top>
                                <v-icon large
                                        color="primary"
                                        slot="activator">
                                    search
                                </v-icon>
                                <span>Kontaktpunkte durchsuchen</span>
                            </v-tooltip>
                        </v-btn>
                        <v-btn small flat fab
                               absolute right
                               class="mr-5"
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
                    </v-card-title>
                    <v-expand-transition>
                        <div v-if="searchBar">
                            <v-text-field
                                    ref="searchBarDashboard"
                                    v-model="search"
                                    @keyup.enter="goToFirstResult()"
                                    append-icon="search"
                                    label="Suche nach Kontaktpunkten"
                                    color="primary"
                                    background-color="#303030"
                                    hide-details
                                    solo/>
                        </div>
                    </v-expand-transition>
                </v-card>
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
                <v-card>
                    <v-card-title class="title font-weight-light">
                        TODOs
                        <v-btn small fab flat
                               absolute right
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
                    </v-card-title>
                </v-card>
                <v-layout row wrap>

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
    import {mapGetters, mapActions} from 'vuex';

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
            searchBar: false,
            loading: true,
            settings: {
                maxScrollbarLength: 120
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
                'getContactPoints'
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
                this.getContactPoints().then(() => this.loading = false);
            },
            expandSearchBar() {
                this.searchBar = !this.searchBar;
                setTimeout(() => {
                    this.$refs.searchBarDashboard.focus();
                }, 30);
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