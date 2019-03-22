<template>
    <v-flex xs12>
        <v-layout row wrap class="text-xs-right pt-2">
            <v-flex xs3 class="text-xs-left more-padding-top">
                <v-icon color="primary" size="24px">forum</v-icon>
                <span class="headline font-weight-light">
                            Kontaktpunkte
                        </span>
            </v-flex>
            <v-flex xs3>
                <v-btn small flat fab
                       @click="addContactPoint()"
                       color="transparent">
                    <v-tooltip top>
                        <v-icon large
                                color="light-green accent-2"
                                slot="activator">
                            add
                        </v-icon>
                        <span>Kontaktpunkt hinzufügen</span>
                    </v-tooltip>
                </v-btn>
                <contact-point-dialog
                        v-model="contactPointDialogState"
                        :contactNames="this.contactNames"
                        @refresh="refreshContactPoints()"/>
            </v-flex>
            <v-flex xs6>
                <v-text-field
                        ref="searchBarContactPoints"
                        v-model="searchContactPoints"
                        @keyup.enter="goToFirstResult()"
                        append-icon="search"
                        label="Suche nach Kontaktpunkten"
                        color="primary"
                        hide-details
                        solo/>
            </v-flex>
        </v-layout>
        <v-layout row wrap>
            <div class="dash">
                <perfect-scrollbar :options="settings">
                    <div :style="`height: ${(this.windowHeight - 320) / 2}px;`">
                        <v-flex xs12 class="no-padding-top">
                            <contact-point-results
                                    :search="this.searchContactPoints"
                                    :on-dashboard="true"/>
                        </v-flex>
                    </div>
                </perfect-scrollbar>
            </div>
        </v-layout>
    </v-flex>
</template>

<script>
    import {mapGetters} from 'vuex';

    import ContactPointDialog from "../dialogs/ContactPointDialog.vue";
    import ContactPointCard from "../cards/ContactPointCard.vue";
    import ContactPointResults from "../search/ContactPointResults.vue";

    export default {
        components: {
            ContactPointDialog,
            ContactPointCard,
            ContactPointResults
        },
        data: () => ({
            contactNames: [],
            contactPointDialogState: false,
            windowHeight: 0,
            searchContactPoints: '',
            settings: {
                maxScrollbarLength: 120,
                wheelSpeed: 0.75,
                suppressScrollX: true
            },
        }),
        computed: {
            ...mapGetters([
                'companies',
                'contacts',
                'contactPoints',
                'selectedCompany',
                'searchResults',
            ])
        },
        watch: {
            selectedCompany() {
                if (!this.selectedCompany || this.companies.map(it => it.name).includes(this.selectedCompany)) {
                    this.searchContactPoints = this.selectedCompany;
                }
            }
        },
        beforeMount() {
            this.onResize();
        },
        methods: {
            addContactPoint() {
                this.contactNames = this.contacts.map(it => it.name).sort();
                this.contactPointDialogState = true;
            },
            goToFirstResult() {
                let companyId = this.companies.find(it => it.id === this.searchResults[0].contact.company.id).id;

                this.$router.push(`/${companyId}/${this.searchResults[0].id}`);
            },
            onResize() {
                this.windowHeight = window.innerHeight;
            },
            refreshContactPoints() {
                this.$parent.refreshData();
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

    .v-sheet--offset {
        top: -24px;
        position: relative;
    }

    .more-padding-top {
        padding-top: 20px !important;
    }
</style>