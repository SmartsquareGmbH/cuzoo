<template>
    <v-container>
        <v-fade-transition>
            <v-layout v-if="!loadingData" row wrap text-xs-left>
                <v-flex xs12 class="ma-0 pa-0">
                    <v-layout row wrap>
                        <v-flex xs6>
                            <v-btn flat small @click="$router.go(-1)">
                                <v-icon size="22px" class="mr-1" dark>arrow_back</v-icon>
                                Zurück
                            </v-btn>
                        </v-flex>
                        <v-flex xs6 text-xs-right>
                            <v-menu bottom left offset-y>
                                <v-btn slot="activator" flat small>
                                    <v-icon size="22px" class="mr-1" dark>add</v-icon>
                                    Fortschritt
                                </v-btn>
                                <v-list class="py-0">
                                    <v-list-tile @click="addProgress()">
                                        <v-icon color="primary" class="mr-2">timeline</v-icon>
                                        <v-list-tile-title>Status ändern</v-list-tile-title>
                                    </v-list-tile>
                                    <v-list-tile @click="addContactPoint()">
                                        <v-icon color="light-green accent-2" class="mr-2">forum</v-icon>
                                        <v-list-tile-title>Kontaktpunkt hinzufügen</v-list-tile-title>
                                    </v-list-tile>
                                </v-list>
                            </v-menu>
                            <v-btn flat small @click="openConfirmDialog()">
                                <v-icon size="22px" class="mr-1" dark>delete</v-icon>
                                Opportunity löschen
                            </v-btn>
                        </v-flex>
                    </v-layout>
                    <v-divider/>
                </v-flex>
                <v-flex xs4 class="pt-4">
                    <p :class="`display-1 ${getStateColor(opportunity.state)}--text mr-5`">
                        {{ opportunity.title }}
                    </p>
                    <v-icon>business</v-icon>
                    <chip class="ml-2 mb-2">
                        {{ companyName }}
                    </chip>
                    <p class="mt-2 mr-5" v-html="markdownify(opportunity.description)"/>
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
                                    <v-card-title style="background-color: #616161;"
                                                  :class="`${getStateColor(contactPoint.opportunityState)}
                                        headline white--text font-weight-light`">
                                        {{ contactPoint.title }}
                                    </v-card-title>
                                    <v-tooltip top max-width="750" v-show="contactPoint.comment">
                                        <v-container slot="activator">
                                        <span class="marked"
                                              v-html="truncatedDescription(contactPoint.comment)"/>
                                        </v-container>
                                        <span v-html="markdownify(contactPoint.comment)"/>
                                    </v-tooltip>
                                </v-card>
                            </v-hover>
                        </v-timeline-item>
                    </v-timeline>
                    <v-divider/>
                </v-flex>
            </v-layout>
        </v-fade-transition>
        <v-fade-transition>
            <v-layout v-if="loadingData">
                <v-flex xs12 class="text-xs-center">
                    <v-progress-circular :size="128" color="primary" indeterminate/>
                </v-flex>
            </v-layout>
        </v-fade-transition>
        <opp-progress-dialog
                v-model="oppProgressDialogState"/>
        <contact-point-dialog
                v-model="contactPointDialogState"
                :contactNames="contactNames"
                :opportunity="opportunity"
                @refresh="refreshData()"/>
        <confirm-dialog
                v-model="confirmDialogState"
                :questionToBeConfirmed="deleteOpportunityMessage"
                @confirmed="deleteOpportunity()"/>
    </v-container>
</template>
<script>
    import api from '../../utils/http-common';
    import {mapActions, mapGetters, mapMutations} from 'vuex';
    import marked from 'marked';

    import Chip from '../../components/core/Chip.vue'
    import OppProgressDialog from '../../components/dialogs/OppProgressDialog.vue'
    import ContactPointDialog from '../../components/dialogs/ContactPointDialog.vue'
    import ConfirmDialog from "../../components/dialogs/ConfirmDialog.vue"

    const datefns = require('date-fns');
    const de = require('date-fns');

    export default {
        components: {
            Chip,
            OppProgressDialog,
            ContactPointDialog,
            ConfirmDialog
        },
        data() {
            return {
                loadingData: true,
                opportunityId: this.$route.params.opportunityId,
                confirmDialogState: false,
                deleteOpportunityMessage: 'Bist du dir sicher, dass du diese Opportunity entgültig löschen willst?',
                oppProgressDialogState: false,
                contactPointDialogState: false,
                contactPoints: [],
                contactNames: []
            }
        },
        computed: {
            ...mapGetters(['opportunities', 'contacts']),
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
            ...mapMutations(['storeEditedOpportunityDetails']),
            ...mapActions(['getOpportunities', 'getContacts']),
            getStateColor(state) {
                switch (state) {
                    case 'Lead':
                        return 'error';
                    case 'Prospect':
                        return 'warning';
                    case 'Quote':
                        return 'success';
                    case 'Win':
                        return 'primary';
                    case 'Lose':
                        return '#616161';
                }
            },
            viewContactPoint(contactPoint) {
                this.$router.push(`/${contactPoint.contact.company.id}/${contactPoint.id}`);
            },
            refreshData() {
                this.loadingData = true;
                this.getContacts().then(() => {
                    this.getOpportunities()
                        .then(() => {
                            api.get(`point/get/opportunity/${this.opportunityId}`)
                                .then(res => {
                                        this.contactPoints = res.data.sort(compareContactPoints);
                                    }
                                ).catch(err => alert(err))
                                .then(() => this.loadingData = false);
                        });
                });
            },
            dateFormatted(date) {
                return datefns.format(date, 'DD.MM.YYYY', {locale: de});
            },
            addProgress() {
                this.storeEditedOpportunityDetails({
                    editedIndex: this.opportunity.id,
                    editedOpportunity: Object.assign({}, this.opportunity)
                });

                this.oppProgressDialogState = true;
            },
            addContactPoint() {
                this.storeEditedOpportunityDetails({
                    editedIndex: this.opportunity.id,
                    editedOpportunity: Object.assign({}, this.opportunity)
                });

                this.contactNames = this.contacts
                    .filter(it => it.company && it.company.name.includes(this.companyName))
                    .map(it => it.name)
                    .sort();
                this.contactPointDialogState = true;
            },
            openConfirmDialog() {
                this.confirmDialogState = true;
            },
            deleteOpportunity() {
                api.delete(`opportunity/delete/${this.opportunityId}`)
                    .then(() => this.$router.go(-1))
                    .catch(error => {
                        console.log(error);
                        alert(error);
                    });
            },
            markdownify(value) {
                return marked(value, {sanitize: true}).trim();
            },
            truncatedDescription(value) {
                let markedValue = this.markdownify(value);
                if (markedValue.length < 300) return markedValue;

                let lastIndexOfSpace = markedValue.substr(0, 300).lastIndexOf(' ');
                return markedValue.substr(0, lastIndexOfSpace) + "...";
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

<style scoped>
    .marked {
        white-space: pre-wrap;
    }

    >>> .marked p {
        margin-bottom: 0px !important;
    }

    >>> .marked ul {
        padding-top: 0px !important;
        padding-bottom: 0px !important;
    }

    >>> .marked ul li {
        padding: 0px !important;
    }

    .menuable__content__active {
        margin-bottom: 0px !important;
        padding-bottom: 0px !important;
    }
</style>