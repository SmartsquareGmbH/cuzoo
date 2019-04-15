<template>
    <v-fade-transition>
        <v-hover>
            <v-card slot-scope="{ hover }"
                    :class="`mt-2 clickable elevation-${hover ? 6 : 2} font-weight-light`"
                    :color="`${hover ? '#616161' : ''}`"
                    @click="viewOpportunity(opportunity)">
                <v-card-title :class="`subheading no-padding-bottom ${getStateColor()}--text`">
                    {{ opportunity.title }}
                </v-card-title>
                <v-card-title class="opp-data">
                    <v-icon class="mx-1">business</v-icon>
                    <v-tooltip top>
                        <chip slot="activator">
                            {{ getCompanyName() | truncate(30) }}
                        </chip>
                        <span class="subheading">{{ getCompanyName() }}</span>
                    </v-tooltip>
                    <v-flex xs12/>
                    <v-icon :style="`transform: rotate(${hover ? 180 : 0}deg)`"
                            class="mx-1">
                        timeline
                    </v-icon>
                    <chip>
                        {{ lastProgress }}
                    </chip>
                </v-card-title>
            </v-card>
        </v-hover>
    </v-fade-transition>
</template>

<script>
    import {mapGetters, mapMutations} from 'vuex';

    import Chip from "../core/Chip.vue";

    const datefns = require('date-fns');
    const de = require('date-fns/locale/de');

    export default {
        props: ['opportunity'],
        components: {
            Chip
        },
        data: () => ({
        }),
        computed: {
            ...mapGetters(['opportunities', 'contactPoints']),
            oppContactPoints() {
                return this.contactPoints.filter(it => {
                    if (it.opportunity) return it.opportunity.id === this.opportunity.id;
                }).sort(compareContactPoints);
            },
            lastProgress() {
                return datefns.format(this.opportunity.lastProgress, 'DD.MM.YY', {locale: de});
            }
        },
        methods: {
            ...mapMutations({
                storeDetails: 'storeEditedOpportunityDetails',
            }),
            getCompanyName() {
                return this.oppContactPoints[0].contact.company.name;
            },
            getStateColor() {
                switch (this.opportunity.state) {
                    case 'Lead':
                        return 'error';
                    case 'Prospect':
                        return 'warning';
                    case 'Quote':
                        return 'success';
                }
            },
            viewOpportunity(opportunity) {
                this.$router.push(`/opportunities/${opportunity.id}`);
            }
        }
    }

    function compareContactPoints(a, b) {
        if (datefns.compareAsc(a.date, b.date) === 0) {
            if (a.id < b.id)
                return 1;
            if (a.id > b.id)
                return -1;
        } else {
            return datefns.compareAsc(a.date, b.date);
        }
    }
</script>

<style scoped>
    .opp-data {
        padding-left: 10px;
    }

    .no-padding-bottom {
        padding-bottom: 0px;
    }
</style>