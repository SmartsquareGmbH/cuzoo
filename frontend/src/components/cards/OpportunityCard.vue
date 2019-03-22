<template>
    <v-fade-transition>
        <v-hover>
            <v-card slot-scope="{ hover }"
                    :class="`mt-2 clickable elevation-${hover ? 8 : 2} font-weight-light`">
                <v-card-title class="no-padding-bottom font-weight-light title">
                    {{ opportunity.title }}
                </v-card-title>
                <v-card-title class="low-padding-left title">
                    <v-icon :style="`transform: rotate(${hover ? 180 : 0}deg)`"
                            class="mx-1">
                        timeline
                    </v-icon>
                    <chip :font-color="getStateColor()">
                        {{ opportunity.state }}
                    </chip>
                    <v-icon class="mx-1">business</v-icon>
                    <chip slot="activator"
                          font-color="primary">
                        {{ getCompanyName() }}
                    </chip>
                </v-card-title>
            </v-card>
        </v-hover>
    </v-fade-transition>
</template>

<script>
    import {mapGetters, mapMutations} from 'vuex';

    import Chip from "../main/Chip.vue";

    const datefns = require('date-fns');
    const de = require('date-fns/locale/de');

    export default {
        props: ['opportunity'],
        components: {
            Chip
        },
        data() {
            return {}
        },
        computed: {
            ...mapGetters(['opportunities', 'contactPoints']),
            oppContactPoints() {
                return this.contactPoints.filter(it => {
                    if (it.opportunity) return it.opportunity.id === this.opportunity.id;
                });
            }
        },
        mounted() {
            console.log(this.oppContactPoints);
            //this.refreshData();
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
                    case 'Lead': return 'primary';
                    case 'Prospect': return 'warning';
                    case 'Quote': return 'success';
                }
            }
        }
    }
</script>

<style scoped>
    .no-padding-bottom {
        padding-bottom: 0px;
    }

    .low-padding-left {
        padding-left: 10px;
    }
</style>