<template>
    <v-container grid-list-md text-xs-center fill-height fluid>
        <v-layout row wrap>
            <v-flex xs4></v-flex>
            <v-flex xs4>
                <span class="display-3 font-weight-thin">CUZOO</span>
            </v-flex>
            <v-flex xs2>
                <v-btn flat
                       class="mt-4 ml-5"
                       @click="expandOptionMenu = !expandOptionMenu">
                    Optionen
                    <v-icon v-if="!expandOptionMenu">keyboard_arrow_down</v-icon>
                    <v-icon v-if="expandOptionMenu">keyboard_arrow_up</v-icon>
                </v-btn>
            </v-flex>
            <v-flex xs2></v-flex>
            <v-flex xs2></v-flex>
            <v-flex xs8>
                <v-expand-transition>
                    <div v-if="expandOptionMenu">
                        <v-btn flat
                               :disabled="searchForCompanies"
                               @click="searchResultsToCompany()">
                            <v-icon class="mr-2">business_center</v-icon>
                            Unternehmen
                        </v-btn>
                        <v-btn flat
                               :disabled="searchForContactPoints"
                               @click="searchResultsToContactPoint()">
                            <v-icon class="mr-2">chat_bubble</v-icon>
                            Kontaktpunkte
                        </v-btn>
                    </div>
                </v-expand-transition>
                <v-text-field
                        :class="`mt-${expandOptionMenu ? 3 : 0}`"
                        color="primary"
                        ref="searchBar"
                        v-model="search"
                        append-icon="search"
                        label="Suche nach Unternehmen oder Ansprechpartnern"
                        hide-details
                        outline/>
                <v-progress-linear
                        v-if="loading"
                        slot="progress"
                        :size="50"
                        color="primary"
                        class="mt-3"
                        indeterminate/>
            </v-flex>
            <v-flex xs2/>
            <v-flex xs2/>
            <v-flex xs8>
                <v-fade-transition>
                    <company-results
                            v-if="searchForCompanies"
                            :search="this.search"/>
                </v-fade-transition>
                <v-fade-transition>
                    <contact-point-results
                            v-if="searchForContactPoints"
                            :search="this.search"/>
                </v-fade-transition>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    import {mapActions, mapGetters} from 'vuex';
    import CompanyResults from "../components/search/CompanyResults.vue";
    import ContactPointResults from "../components/search/ContactPointResults.vue";

    export default {
        components: {
            CompanyResults,
            ContactPointResults
        },
        data: () => ({
            search: '',
            searchForContactPoints: false,
            searchForCompanies: true,
            loading: true,
            expandOptionMenu: false
        }),
        mounted() {
            this.refreshData();
        },
        computed: {
            ...mapGetters([
                'companies',
                'contacts',
                'contactPoints'
            ]),
        },
        methods: {
            ...mapActions([
                'getCompanies',
                'getContacts',
                'getContactPoints'
            ]),
            refreshData() {
                this.getCompanies().then(() => {
                    this.getContacts().then(() => {
                        this.getContactPoints().then(() => {
                            this.loading = false;
                            this.doFocus();
                        });;
                    });
                });
            },
            searchResultsToContactPoint() {
                this.expandOptionMenu = false;
                setTimeout(() => this.searchForContactPoints = true, 300);

                this.searchForCompanies = false;
                this.doFocus();
            },
            searchResultsToCompany() {
                this.expandOptionMenu = false;
                setTimeout(() => this.searchForCompanies = true, 300);

                this.searchForContactPoints = false;
                this.doFocus();
            },
            doFocus() {
                this.$refs.searchBar.focus();
            }
        }
    }
</script>