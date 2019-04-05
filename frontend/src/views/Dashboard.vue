<template>
    <v-container grid-list-md fluid>
        <v-fade-transition>
            <v-layout row wrap v-if="!loadingData" v-resize="onResize">
                <v-flex xs7>
                    <opportunity-widget class="mr-1"/>
                </v-flex>
                <v-flex xs5 class="pt-0">
                    <todo-widget class="mb-4 pb-2"/>
                    <contact-point-widget/>
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
    import {mapActions} from 'vuex';

    import OpportunityWidget from '../components/dashboard/OpportunityWidget.vue';
    import TodoWidget from '../components/dashboard/TodoWidget.vue';
    import ContactPointWidget from '../components/dashboard/ContactPointWidget.vue';

    export default {
        components: {
            OpportunityWidget,
            TodoWidget,
            ContactPointWidget
        },
        data: () => ({
            loadingData: true,
        }),
        beforeMount() {
            this.refreshData();
        },
        methods: {
            ...mapActions([
                'getCompanies',
                'getContacts',
                'getContactPoints',
                'getOpportunities',
                'getTodos'
            ]),
            refreshData() {
                this.getCompanies();
                this.getContacts();
                this.getContactPoints().then(() => {
                    this.getOpportunities();
                    this.getTodos().then(() => this.loadingData = false);
                });
            }
        }
    }
</script>