<template>
    <v-container>
        <v-fade-transition>
            <v-layout v-if="!loadingData" row wrap text-xs-left>
                <v-flex xs12>
                    <v-btn flat small @click="$router.go(-1)">
                        <v-icon size="22px" class="mr-1" dark>arrow_back</v-icon>
                        Zurück
                    </v-btn>
                    <v-divider class="mt-2"/>
                </v-flex>
                <v-flex xs4 class="pt-4">
                    <p :class="`display-1 ${getStateColor(opportunity.state)}--text`">
                        {{ opportunity.title }}
                    </p>
                    <v-icon>business</v-icon>
                    <chip class="ml-2 mb-2">
                        {{ companyName }}
                    </chip>
                    <p class="mt-2 mr-5" style="white-space: pre-wrap;">{{ opportunity.description }}</p>
                </v-flex>
                <v-flex xs8>
                    <v-timeline>
                        <v-timeline-item
                                fill-dot
                                icon="forum"
                                :color="`${getStateColor(contactPoint.opportunityState)}`"
                                v-for="contactPoint in contactPoints"
                                v-bind:key="contactPoint.id">
                            <template v-slot:opposite>
                                <span class="font-italic">
                                    {{ dateFormatted(contactPoint.date) }} mit
                                    <span :class="`${getStateColor(contactPoint.opportunityState)}--text`">
                                        {{ contactPoint.contact.name }}
                                    </span>
                                </span>
                                <p class="font-italic ma-0">
                                    via
                                    <span v-for="type in contactPoint.types"
                                          v-bind:key="type.id"
                                          :class="`${getStateColor(contactPoint.opportunityState)}--text`">
                                        {{ type.title }}
                                    </span>
                                </p>
                            </template>
                            <v-hover>
                                <v-card slot-scope="{ hover }"
                                        class="clickable"
                                        @click="viewContactPoint(contactPoint)"
                                        :color="`${hover ? '#616161' : ''}`">
                                    <v-card-title
                                            :class="`${getStateColor(contactPoint.opportunityState)}
                                        headline white--text font-weight-light`">
                                        {{ contactPoint.title }}
                                    </v-card-title>
                                    <v-container>
                                        <v-layout>
                                            <v-flex xs12>
                                                <span style="white-space: pre-wrap;">{{ contactPoint.comment | truncate(500) }}</span>
                                            </v-flex>
                                        </v-layout>
                                    </v-container>
                                </v-card>
                            </v-hover>
                        </v-timeline-item>
                    </v-timeline>
                    <v-divider/>
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

    import Chip from '../../components/core/Chip.vue'

    const datefns = require('date-fns');
    const de = require('date-fns');

    export default {
        components: {
            Chip
        },
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
            },
            companyName() {
                return this.contactPoints[0].contact.company.name;
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
            viewContactPoint(contactPoint) {
                this.$router.push(`/${contactPoint.contact.company.id}/${contactPoint.id}`);
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