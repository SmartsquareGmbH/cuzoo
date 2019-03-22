<template>
    <v-layout row wrap class="text-xs-right pr-2 more-padding-top">
        <v-flex xs12 class="text-xs-left">
            <v-icon color="primary" size="24px">bubble_chart</v-icon>
            <span class="headline font-weight-light">
                Opportunities
            </span>
        </v-flex>
        <v-flex xs12>
            <v-layout row wrap>
                <div class="dash">
                    <perfect-scrollbar :options="settings">
                        <div :style="`height: ${this.windowHeight - 310}px`">
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
            OpportunityCard
        },
        computed: {
            ...mapGetters(['opportunities'])
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

    .more-padding-top {
        padding-top: 12px !important;
    }

</style>