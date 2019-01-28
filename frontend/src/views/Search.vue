<template>
    <v-container grid-list-md text-xs-center fill-height fluid>
        <v-layout row wrap>
            <v-flex xs12>
                <span class="display-3 font-weight-thin">CUZOO</span>
            </v-flex>
            <v-flex xs2></v-flex>
            <v-flex xs8>
                <v-text-field
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
                        :company="company"
                        v-bind:key="company.id"
                        v-for="company in searchResults"/>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    import {mapActions, mapGetters} from 'vuex'
    import CompanyCard from "../components/company/CompanyCard.vue"

    export default {
        components: {
            CompanyCard
        },
        data() {
            return {
                search: '',
                loading: true,
                searchTermsOfCompany: []
            }
        },
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
            doFocus() {
                this.$refs.searchBar.focus();
            }
        }
    }
</script>