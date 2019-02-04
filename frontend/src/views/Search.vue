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
                <company-card
                        v-if="searchForCompanies"
                        :company="company"
                        v-bind:key="company.id"
                        v-for="company in searchResults"/>
                <contact-point-card
                        v-if="searchForContactPoints"
                        :contact-point="contactPoint"
                        v-bind:key="contactPoint.id"
                        v-for="contactPoint in contactPoints"/>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    import {mapActions, mapGetters} from 'vuex'
    import CompanyCard from "../components/company/CompanyCard.vue"
    import ContactPointCard from "../components/contactpoint/ContactPointCard";

    export default {
        components: {
            ContactPointCard,
            CompanyCard
        },
        data: () => ({
            search: '',
            searchForContactPoints: false,
            searchForCompanies: true,
            searchTermsOfCompany: [],
            loading: true,
            expandOptionMenu: false
        }),
        mounted() {
            this.refreshData();
        },
        computed: {
            ...mapGetters(['companies', 'contacts', 'contactPoints']),
            searchResults() {
                return this.companies
                    .filter(company => {
                        this.defineSearchTerms(company);
                        if (this.search !== '') {
                            return this.searchTermsOfCompany.some(term => term.includes(this.search.toLowerCase()));
                        } else {
                            return null;
                        }
                    })
                    .splice(0, 10);
            }
        },
        methods: {
            ...mapActions(['getCompanies', 'getContacts']),
            getContactsOfCompany(company) {
                return this.contacts.filter(contact => {
                    if (contact.company != null) {
                        return contact.company.name === company;
                    } else {
                        return null;
                    }
                })
            },
            getContactPointsOfCompany(company) {
                return this.contactPoints.filter(contactPoint => {
                    return contactPoint.contact.company.name === company;
                })
            },
            defineSearchTerms(company) {
                this.searchTermsOfCompany = [];

                let contactsOfCompany = this.getContactsOfCompany(company.name);
                let contactPointsOfCompany = this.getContactPointsOfCompany(company.name);

                contactsOfCompany.forEach(contact => {
                    this.searchTermsOfCompany.push(contact.name.toLowerCase());
                });

                contactPointsOfCompany.forEach(contactPoint => {
                    contactPoint.labels.forEach(label => {
                        this.searchTermsOfCompany.push(label.toLowerCase())
                    });
                });

                for (var key in company) {
                    if (company.hasOwnProperty(key) && company[key] != null) {
                        this.searchTermsOfCompany.push(company[key].toString().toLowerCase());
                    }
                }
            },
            refreshData() {
                this.getCompanies().then(() => {
                    this.getContacts().then(() => {
                        this.loading = false;
                        this.doFocus();
                    })
                });
            },
            searchResultsToContactPoint() {
                setTimeout(() => this.searchForContactPoints = true, 300);

                this.searchForCompanies = false
            },
            searchResultsToCompany() {
                setTimeout(() => this.searchForCompanies = true, 300);

                this.searchForContactPoints = false
            },
            doFocus() {
                this.$refs.searchBar.focus();
            }
        }
    }
</script>