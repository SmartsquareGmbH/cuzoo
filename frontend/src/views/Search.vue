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
                ref="search"
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
                <v-card 
                class="secondary mt-3 clickable"
                @click.native="viewCompany(company)"
                v-bind:key="company.id"
                v-for="company in searchResults">
                    <v-card-text class="headline text-xs-left">
                        {{ company.name }}
                        <v-spacer/>
                        <v-chip
                        :color="randomColor(contact.id)"
                        class="mt-3"
                        v-bind:key="contact.id"
                        v-for="contact in getContactsOfCompany(company.name)">
                            <span class="headline black--text">
                                {{ contact.name }}
                            </span>
                        </v-chip>
                    </v-card-text>
                </v-card>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
import { mapState } from 'vuex';
import { clearInterval } from 'timers';

export default {
    data() {
        return {
            search: '',
            loading: true,
            colorCache: {},
            contactsOfCompany: [],
            searchTermsOfCompany: []
        }
    },
    mounted() {
        this.refreshData()
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
            return this.companies.filter(company => {
                this.defineSearchTerms(company);

                if (this.search != '') {
                    if (this.searchTermsOfCompany.length > 0) {
                        return this.searchTermsOfCompany.some(term => term.includes(this.search.toLowerCase()));
                    } else {
                        return company.name.toLowerCase().includes(this.search.toLowerCase());
                    }
                } else {
                    return null;
                }
            })
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
        randomColor(id) {
            const random = () => Math.floor(256 * Math.random()) + 112;

            return this.colorCache[id] || (this.colorCache[id] = `rgb(${random()}, ${random()}, ${random()})`);
        },
        viewCompany: function (item) {
            const index = this.companies.findIndex(company => company.id === item.id);
            this.$router.push('/companies/' + (index));
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
            this.$refs.search.focus();
        }
    }
}
</script>

<style>
.clickable {
    cursor: pointer;
}
</style>
