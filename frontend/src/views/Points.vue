<template>
    <v-container grid-list-md text-xs-center fill-height fluid>
        <v-layout row wrap>
            <v-flex xs12>
                <span class="display-3 font-weight-thin primary--text">CUZOO</span>
            </v-flex>
            <v-flex xs2></v-flex>
            <v-flex xs8>
                <v-text-field
                color="primary"
                v-model="search"
                append-icon="search"
                label="Suche nach Unternehmen oder Ansprechpartnern"
                hide-details
                outline>
                </v-text-field>
            </v-flex>
            <v-flex xs2></v-flex>
            <v-flex xs2></v-flex>
            <v-flex xs8>
                <v-card 
                class="secondary mt-3 clickable"
                @click.native="viewCompany(company)"
                v-bind:key="company.id"
                v-for="company in filteredCompanies">
                    <v-card-text class="headline text-xs-left">
                        {{ company.name }}
                        <v-spacer/>
                        <v-card-text
                        class="blue-grey darken-1 pa-2 mt-2"
                        v-bind:key="contact.id"
                        v-for="contact in contactsOfCompany(company.name)">
                            {{ contact.name }}
                        </v-card-text>
                    </v-card-text>
                </v-card>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
export default {
    data() {
        return {
            search: '',
            companies: this.$store.getters.getCompanies,
            contacts: this.$store.getters.getContacts
        }
    },
    computed: {
        filteredCompanies() {
            return this.companies.filter(company => {
                if (this.search != '') {
                    return company.name.toLowerCase().includes(this.search.toLowerCase());
                } else {
                    return null;
                }
            })
        },
        filteredContacts() {
            return this.contacts.filter(contact => {
                return contact.name.toLowerCase().includes(this.search.toLowerCase());
            })
        }
    },
    methods: {
        contactsOfCompany: function (item) {
            return this.contacts.filter((contact) => {
                if (contact.company != null) {
                    return contact.company.name === item;
                } else {
                    return null;
                }
            })
        },
        viewCompany: function (item) {
            const index = this.companies.findIndex(company => company.id === item.id);
            this.$router.push('/companies/' + (index));
        }
    }
}
</script>

<style>
.clickable {
    cursor: pointer;
}
</style>
