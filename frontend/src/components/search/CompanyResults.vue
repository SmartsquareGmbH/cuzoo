<template>
    <div>
        <company-card
                :company="company"
                :contacts="getContactsOfCompany(company.name)"
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
            searchTerms: [],
        }),
        computed: {
            ...mapGetters([
                'companies',
                'contacts',
                'contactPoints'
            ]),
            searchResults() {
                return this.companies.filter(company => {
                    this.defineSearchTerms(company);

                    if (this.search) {
                        return this.searchTerms.some(term =>
                            term.includes(this.search.toLowerCase())
                        );
                    }
                }).splice(0, 10);
            }
        },
        methods: {
            defineSearchTerms(company) {
                this.searchTerms = [];

                let contactsOfCompany = this.getContactsOfCompany(company.name);
                let contactPointsOfCompany = this.getContactPointsOfCompany(company.name);

                contactsOfCompany.forEach(contact => {
                    this.searchTerms.push(contact.name.toLowerCase());
                });

                contactPointsOfCompany.forEach(contactPoint => {
                    contactPoint.labels.forEach(label => {
                        this.searchTerms.push(label.toLowerCase())
                    });
                });

                for (let key in company) {
                    if (company.hasOwnProperty(key) && company[key]) {
                        this.searchTerms.push(company[key].toString().toLowerCase());
                    }
                }
            },
            getContactsOfCompany(company) {
                return this.contacts.filter(contact => {
                    if (contact.company) {
                        return contact.company.name === company;
                    }
                })
            },
            getContactPointsOfCompany(company) {
                return this.contactPoints.filter(contactPoint => {
                    return contactPoint.contact.company.name === company;
                })
            }
        }
    }
</script>