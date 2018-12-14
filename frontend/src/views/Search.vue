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
import { mapState } from 'vuex';
import { clearInterval } from 'timers';

import CompanyCard from "@/components/company/CompanyCard.vue";

export default {
    components: {
        CompanyCard
    },
    data() {
        return {
            dark: this.$store.getters.getDarkState,
            search: '',
            loading: true,
            contactsOfCompany: [],
            searchTermsOfCompany: []
        }
    },
    mounted() {
        this.refreshData();
    },
    computed: {
        ...mapState(['companies']),
        companies: {
            get() {
                return this.$store.state.companies
            },
            set(companies) {
                this.$store.commit('storeCompanies', companies)
            }
        },
        ...mapState(['contacts']),
        contacts: {
            get() {
                return this.$store.state.contacts
            },
            set(contacts) {
                this.$store.commit('storeContacts', contacts)
            }
        },
        searchResults() {
            return this.companies
            .filter(company => {
                this.defineSearchTerms(company);

                if (this.search != '') {
                    return this.searchTermsOfCompany.some(term => term.includes(this.search.toLowerCase()));
                } else {
                    return null;
                }
            })
            .splice(0, 10);
        }
    },
    methods: {
        getContactsOfCompany: function (item) {
            return this.contacts.filter((contact) => {
                if (contact.company != null) {
                    return contact.company.name === item;
                } else {
                    return null;
                }
            })
        },
        defineSearchTerms: function (company) {
            this.searchTermsOfCompany = [];
            this.contactsOfCompany = this.getContactsOfCompany(company.name);

            this.contactsOfCompany.forEach(contact => {
                this.searchTermsOfCompany.push(contact.name.toLowerCase());
            })

            for (var key in company) {
                if (company.hasOwnProperty(key) && company[key] != null) {
                    this.searchTermsOfCompany.push(company[key].toString().toLowerCase());
                }
            }
        },
        refreshData() {
            this.$store.dispatch('getCompanies');
            this.$store.dispatch('getContacts')
            .then(() => {
                this.loading = false;
                this.doFocus();
            });
        },
        doFocus() {
            this.$refs.searchBar.focus();
        }
    }
}
</script>

<style>
.clickable {
    cursor: pointer;
}
</style>
