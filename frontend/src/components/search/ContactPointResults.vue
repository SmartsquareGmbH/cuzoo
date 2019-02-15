<template>
    <div>
        <contact-point-card
                :contact-point="contactPoint"
                :search="true"
                v-bind:key="contactPoint.id"
                v-for="contactPoint in searchResults"/>
    </div>
</template>

<script>
    import {mapGetters, mapMutations} from 'vuex';
    import ContactPointCard from "../cards/ContactPointCard.vue";

    export default {
        props: ['search'],
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
                        return this.searchTerms.some(term =>
                            term.includes(this.search.toLowerCase())
                        );
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

                for (let key in contactPoint) {
                    if (contactPoint.hasOwnProperty(key) && contactPoint[key]) {
                        if (key === 'contact') {
                            this.searchTerms.push(contactPoint[key].name.toLowerCase());
                            this.searchTerms.push(contactPoint[key].company.name.toLowerCase());
                        } else {
                            this.searchTerms.push(contactPoint[key].toString().toLowerCase());
                        }
                    }
                }
            }
        }
    }
</script>