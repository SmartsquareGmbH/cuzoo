<template>
    <v-layout row wrap>
        <v-flex xs4>
            <number-card
                    color="primary"
                    title="Leads"
                    :number="numberOfLeads"/>
        </v-flex>
        <v-flex xs4>
            <number-card
                    color="warning"
                    title="Prospects"
                    :number="numberOfProspects"/>
        </v-flex>
        <v-flex xs4>
            <number-card
                    color="success"
                    title="Quotes"
                    :number="numberOfQuotes"/>
        </v-flex>
        <v-flex xs12 class="text-xs-left mt-4">
            <v-icon color="primary" size="24px">bubble_chart</v-icon>
            <span class="headline font-weight-light">
                Opportunities
            </span>
        </v-flex>
        <v-flex xs12>
            <v-layout row wrap  class="text-xs-right opp-dash">
                <div class="dash">
                    <perfect-scrollbar :options="settings">
                        <div :style="`height: ${this.windowHeight - 365}px`">
                            <v-layout row wrap>
                                <v-flex xs12>
                                    <opportunity-card
                                            v-bind:key="opportunity.id"
                                            v-for="opportunity in opportunities"
                                            :opportunity="opportunity"/>
                                </v-flex>
                            </v-layout>
                        </div>
                    </perfect-scrollbar>
                    <div v-if="opportunities.length >= 3" class="fade-out-gradient"/>
                </div>
            </v-layout>
        </v-flex>
    </v-layout>
</template>

<script>
    import {mapActions, mapGetters} from 'vuex';

    import NumberCard from '../cards/NumberCard.vue';
    import OpportunityCard from '../cards/OpportunityCard.vue'

    export default {
        data: () => ({
            windowHeight: 0,
            settings: {
                maxScrollbarLength: 120,
                wheelSpeed: 0.75,
                suppressScrollX: true
            }
        }),
        components: {
            NumberCard,
            OpportunityCard
        },
        computed: {
            ...mapGetters(['opportunities']),
            numberOfLeads() {
                return this.opportunities.filter(it => it.state === "Lead").length;
            },
            numberOfProspects() {
                return this.opportunities.filter(it => it.state === "Prospect").length;
            },
            numberOfQuotes() {
                return this.opportunities.filter(it => it.state === "Quote").length;
            }
        },
        beforeMount() {
            this.getOpportunities();
            this.onResize();
        },
        methods: {
            ...mapActions(['getOpportunities']),
            onResize() {
                this.windowHeight = window.innerHeight;
            }
        }
    }
</script>


<style scoped>
    .dash {
        height: 100%;
        width: 100%;
        position: relative;
    }

    .fade-out-gradient {
        position: absolute;
        bottom: 0;
        left: 0;
        width: 100%;
        text-align: center;
        margin: 0;
        padding: 6px 0;

        background-image: linear-gradient(to bottom, transparent, #333333);
    }

    .opp-dash {
        padding: 5px !important;
    }

</style>