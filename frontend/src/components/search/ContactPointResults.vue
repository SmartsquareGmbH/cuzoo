<template>
    <div>
        <contact-point-card
                class="contact-point-results"
                v-bind:key="contactPoint.id"
                v-for="contactPoint in searchResults"
                :contact-point="contactPoint"
                :search="true"/>
    </div>
</template>

<script>
    import {mapGetters, mapMutations} from 'vuex';
    import ContactPointCard from "../cards/ContactPointCard.vue";

    export default {
        props: ['search', 'onDashboard'],
        components: {
            ContactPointCard
        },
        data: () => ({
            searchTerms: [],
        }),
        computed: {
            ...mapGetters(['contactPoints']),
            searchResults() {
                return this.contactPoints.filter(contactPoint => {
                    this.defineSearchTerms(contactPoint);

                    if (this.search) {
                        return this.searchTerms.some(it =>
                            it.includes(this.search.toLowerCase())
                        );
                    } else if (this.onDashboard) {
                        return this;
                    }
                }).splice(0, 10);
            }
        },
        mounted() {
            this.storeSearchResults({searchResults: this.searchResults});
        },
        watch: {
            searchResults() {
                this.storeSearchResults({searchResults: this.searchResults});
            }
        },
        methods: {
            ...mapMutations(['storeSearchResults']),
            defineSearchTerms(contactPoint) {
                this.searchTerms = [];

                Object.keys(contactPoint).forEach(key => {
                    let property = contactPoint[key];

                    key === 'contact' ? this.pushContactValues(property) : this.pushValue(property);
                });
            },
            pushContactValues(contact) {
                this.pushValue(contact.name);

                contact.labels.map(it => this.pushValue(it.title));

                if (contact.company) this.pushValue(contact.company.name);
            },
            pushValue(value) {
                if (value) this.searchTerms.push(value.toString().toLowerCase());
            }
        }
    }
</script>

<style scoped>
    .contact-point-results {
        margin: 0px !important;
        padding: 0px !important;
        margin-left: 4px !important;
        margin-right: 4px !important;
    }
</style>