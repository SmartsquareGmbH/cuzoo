<template>
    <v-container>
        <v-fade-transition>
            <v-layout v-if="!loadingData" row wrap text-xs-left>
                <v-flex xs12 class="mb-3">
                    <v-btn flat small @click="$router.go(-1)">
                        <v-icon size="22px" class="mr-1" dark>arrow_back</v-icon>
                        Zurück
                    </v-btn>
                    <v-divider/>
                </v-flex>
                <v-flex xs6>
                    <span :class="`display-1 ${getStateColor(opportunity.state)}--text`">
                        {{ opportunity.title }}
                    </span>
                    <p class="mt-2 mr-5">{{ opportunity.description }}</p>
                </v-flex>
                <v-flex xs6>
                    <v-timeline>
                        <v-timeline-item
                                icon="forum"
                                :color="`${getStateColor(opportunity.state)}`"
                                fill-dot
                                v-for="contactPoint in contactPoints"
                                v-bind:key="contactPoint.id">
                            <template v-slot:opposite>
                                <span>{{ dateFormatted(contactPoint.date) }} <span class="font-italic">mit {{ contactPoint.contact.name }}</span></span>
                            </template>
                            <v-card>
                                <v-card-title :class="`${getStateColor(opportunity.state)}`">

                                    <h2 class="headline white--text font-weight-light">{{ contactPoint.title }}</h2>
                                </v-card-title>
                                <v-container>
                                    <v-layout>
                                        <v-flex xs12>
                                            {{ contactPoint.comment }}
                                        </v-flex>
                                    </v-layout>
                                </v-container>
                            </v-card>
                        </v-timeline-item>
                    </v-timeline>
                </v-flex>
            </v-layout>
        </v-fade-transition>
        <v-layout v-if="loadingData">
            <v-flex xs12 class="text-xs-center">
                <v-progress-circular :size="128" color="primary" indeterminate/>
            </v-flex>
        </v-layout>
    </v-container>
</template>
<script>
    import api from '../../utils/http-common';
    import {mapActions, mapGetters} from 'vuex';

    const datefns = require('date-fns');
    const de = require('date-fns');

    export default {
        data() {
            return {
                loadingData: true,
                opportunityId: this.$route.params.opportunityId,
                contactPoints: []
            }
        },
        computed: {
            ...mapGetters(['opportunities']),
            opportunity() {
                return this.opportunities.find(it => {
                        return it.id == this.opportunityId;
                    }
                );
            }
        },
        beforeMount() {
            this.refreshData();
        },
        methods: {
            ...mapActions(['getOpportunities']),
            getStateColor(state) {
                switch (state) {
                    case 'Lead':
                        return 'primary';
                    case 'Prospect':
                        return 'warning';
                    case 'Quote':
                        return 'success';
                }
            },
            refreshData() {
                this.getOpportunities()
                    .then(() => {
                        api.get(`point/get/opportunity/${this.opportunityId}`).then(res => {
                            this.contactPoints = res.data.sort(compareContactPoints);
                            this.loadingData = false;
                        }).catch(err => alert(err));
                    });
            },
            dateFormatted(date) {
                return datefns.format(date, 'DD.MM.YYYY', {locale: de});
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
            return datefns.compareAsc(b.date, a.date);
        }
    }
</script>