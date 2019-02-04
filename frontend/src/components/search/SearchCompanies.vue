<template>
    <div>
        <company-card
                :company="company"
                v-bind:key="company.id"
                v-for="company in searchResults"/>
    </div>
</template>

<script>
    import {mapGetters} from 'vuex'
    import CompanyCard from "../../components/company/CompanyCard.vue";

    export default {
        props: ['search'],
        components: {
            CompanyCard
        },
        data: () => ({
            searchTermsOfCompany: [],
        }),
        computed: {
            ...mapGetters(['companies', 'contacts', 'contactPoints']),
            searchResults() {
                return this.companies.filter(company => {
                    this.defineSearchTerms(company);
                    if (this.search) {
                        return this.searchTermsOfCompany.some(term => {
                            term.includes(this.search.toLowerCase())
                        });
                    }
                }).splice(0, 10);
            }
        },
        methods: {
            defineSearchTerms(company) {
                this.searchTermsOfCompany = [];

                this.getContactsOfCompany(company.name);
                this.getContactPointsOfCompany(company.name);

                for (let key in company) {
                    if (company.hasOwnProperty(key) && company[key] != null) {
                        this.searchTermsOfCompany.push(company[key].toString().toLowerCase());
                    }
                }
            },
            getContactsOfCompany(company) {
                this.contacts.filter(contact => {
                    if (contact.company) {
                        return contact.company.name === company;
                    }
                }).forEach(contact => {
                    this.searchTermsOfCompany.push(contact.name.toLowerCase());
                });
            },
            getContactPointsOfCompany(company) {
                this.contactPoints.filter(contactPoint => {
                    return contactPoint.contact.company.name === company;
                }).forEach(contactPoint => {
                    contactPoint.labels.forEach(label => {
                        this.searchTermsOfCompany.push(label.toLowerCase());
                    });
                });
            }
        }
    }
</script>