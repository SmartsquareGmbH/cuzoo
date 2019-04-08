<template>
    <v-container>
        <v-layout row wrap text-xs-center>
            <v-flex xs1>
                <v-btn flat small @click="$router.go(-1)">
                    <v-icon size="22px" class="mr-1" dark>arrow_back</v-icon>
                    Zurück
                </v-btn>
            </v-flex>
            <v-flex xs10>
                <span>{{ opportunityId }}</span>
            </v-flex>
        </v-layout>
    </v-container>
</template>
<script>
    import api from '../../utils/http-common';
    import {mapActions, mapGetters} from 'vuex';

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
                this.getOpportunities().then(() => {
                    api.get(`point/get/opportunity/${this.opportunityId}`).then(res => {
                        this.contactPoints = res.data;
                        this.loadingData = false;
                    }).catch(err => alert(err));
                });
            }
        }
    }
</script>