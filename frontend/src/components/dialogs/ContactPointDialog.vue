<template>
    <v-dialog v-model="value" @input="$emit('input')" persistent max-width="950">
        <v-card>
            <v-card-title class="headline primary" primary-title>
                {{ formTitle }}
            </v-card-title>
            <v-card-text class="text-xs-right primary--text">
                <v-form ref="form" v-model="valid">
                    <v-container grid-list-md>
                        <v-layout wrap>
                            <v-flex xs7>
                                <v-text-field
                                        v-model="editedContactPoint.title"
                                        :rules="compulsory"
                                        prepend-icon="title"
                                        label="Titel"
                                        suffix="*"
                                        hide-details/>
                            </v-flex>
                            <v-flex xs5>
                                <label-box
                                        @label-added="setContactPointTypes"
                                        :current-labels="editedContactPoint.types"
                                        api-path="point/get/types" type="Art"
                                        hide-details/>
                            </v-flex>
                            <v-flex xs7>
                                <v-combobox
                                        v-model="editedContactPoint.contact.name"
                                        :items="this.contactNames"
                                        :rules="contactRules"
                                        prepend-icon="person"
                                        suffix="*"
                                        label="Ansprechpartner"
                                        hide-details/>
                            </v-flex>
                            <v-flex xs5>
                                <v-menu
                                        ref="menu"
                                        v-model="menu"
                                        :close-on-content-click="false"
                                        :nudge-right="35"
                                        :return-value.sync="date"
                                        transition="scale-transition"
                                        offset-y
                                        full-width
                                        min-width="290px"
                                        hide-details>
                                    <v-text-field
                                            slot="activator"
                                            v-model="dateFormatted"
                                            :rules="compulsory"
                                            prepend-icon="event"
                                            label="Datum"
                                            suffix="*"
                                            readonly
                                            hide-details/>
                                    <v-date-picker
                                            v-model="date"
                                            :max="new Date().toISOString()"
                                            scrollable
                                            locale="de">
                                        <v-spacer/>
                                        <v-btn flat color="primary" @click="menu = false">Abbrechen</v-btn>
                                        <v-btn flat color="primary" @click="$refs.menu.save(date)">OK</v-btn>
                                    </v-date-picker>
                                </v-menu>
                            </v-flex>
                            <v-flex xs12>
                                <v-textarea
                                        v-model="editedContactPoint.comment"
                                        prepend-icon="comment"
                                        label="Kommentar"
                                        rows="5"/>
                            </v-flex>
                            <v-flex xs12>
                                <label-box
                                        @label-added="setContactPointLabels"
                                        :current-labels="editedContactPoint.labels"
                                        api-path="point/get/labels" type="Labels" hide-details
                                        :class="`mb-${opportunityMenu ? 2 : 0}`"/>
                            </v-flex>
                            <v-flex xs12>
                                <v-expand-transition>
                                    <v-layout row wrap v-if="opportunityMenu || opportunity">
                                        <v-flex xs8>
                                            <v-text-field
                                                    v-model="editedOpportunity.title"
                                                    :rules="oppTitleRules"
                                                    suffix="*"
                                                    prepend-icon="title"
                                                    label="Opportunity-Titel"
                                                    hide-details/>
                                        </v-flex>
                                        <v-flex xs4>
                                            <v-combobox
                                                    v-model="editedOpportunity.state"
                                                    :items="oppStatuses"
                                                    :rules="oppStatusRules"
                                                    suffix="*"
                                                    prepend-icon="bubble_chart"
                                                    label="Status"
                                                    hide-details/>
                                        </v-flex>
                                        <v-expand-transition>
                                            <v-flex xs12 v-if="newOpportunity">
                                                <v-textarea
                                                        v-model="editedOpportunity.description"
                                                        prepend-icon="description"
                                                        label="Kurzbeschreibung"
                                                        rows="5" hide-details/>
                                            </v-flex>
                                        </v-expand-transition>
                                    </v-layout>
                                </v-expand-transition>
                            </v-flex>
                        </v-layout>
                    </v-container>
                </v-form>
                <div class="mr-2">* Pflichtfelder</div>
            </v-card-text>
            <v-card-actions>
                <div v-if="!opportunity">
                    <v-btn v-if="this.companyOpportunities.length === 0 || (opportunityMenu && newOpportunity)"
                           @click.native="createOpportunity()"
                           color="success" flat>
                        Neue Opportunity
                        <v-icon v-if="opportunityMenu">keyboard_arrow_up</v-icon>
                        <v-icon v-if="!opportunityMenu">keyboard_arrow_down</v-icon>
                    </v-btn>
                    <v-menu v-else top offset-y>
                        <v-btn slot="activator" color="success" flat @click="opportunityList = !opportunityList">
                            {{ opportunityButtonTitle }}
                            <v-icon v-if="opportunityList">keyboard_arrow_down</v-icon>
                            <v-icon v-if="!opportunityList">keyboard_arrow_up</v-icon>
                        </v-btn>
                        <v-list class="py-0">
                            <v-list-tile @click="updateOpportunity(opp)"
                                         v-for="opp in companyOpportunities"
                                         v-bind:key="opp.id">
                                <v-icon :color="getStateColor(opp.state)"
                                        class="mr-2">
                                    bubble_chart
                                </v-icon>
                                <v-list-tile-title>{{ opp.title }}</v-list-tile-title>
                            </v-list-tile>
                            <v-divider class="my-0"/>
                            <v-list-tile @click="createOpportunity()" class="my-0 py-0">
                                <v-icon color="light-green accent-2" class="mr-2">add</v-icon>
                                <v-list-tile-title>Neue Opportunity</v-list-tile-title>
                            </v-list-tile>
                        </v-list>
                    </v-menu>
                </div>
                <v-spacer></v-spacer>
                <v-btn color="primary" flat @click.native="closeDialog()">Abbrechen</v-btn>
                <v-btn color="primary" flat v-on:click="clearDialog()">Zurücksetzen</v-btn>
                <v-btn color="primary" flat v-on:click="submitContactPoint()" :disabled="!valid">Speichern</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    import {mapActions, mapGetters, mapMutations} from 'vuex';
    import api from '../../utils/http-common';

    import LabelBox from "../core/LabelBox.vue";

    const datefns = require('date-fns');
    const de = require('date-fns/locale/de');

    export default {
        props: ["value", "contactNames", "opportunity"],
        components: {
            LabelBox
        },
        data() {
            return {
                opportunityMenu: false,
                opportunityList: false,
                date: new Date().toISOString().substr(0, 10),
                menu: false,
                valid: false,
                compulsory: [v => !!v || "Bitte geben Sie etwas ein"],
                contactRules: [
                    v => !!v || "Bitte geben Sie einen Ansprechpartner an",
                    v => this.contactNames.includes(v) || "Dieser Ansprechpartner existiert nicht"
                ],
                newOpportunity: false,
                companyOpportunities: [],
                oppStatuses: ['Lose', 'Lead', 'Prospect', 'Quote', 'Win'],
                oppStatusRules: [
                    v => !!v || "Bitte geben Sie einen Status an",
                    v => this.oppStatuses.includes(v) || "Dieser Status existiert nicht",
                    this.opportunityMenu === true
                ],
                oppTitleRules: [
                    v => !!v || "Bitte geben Sie einen Titel an",
                    this.opportunityMenu === true
                ],
                defaultContactPoint: {
                    value: false,
                    id: 0,
                    title: "",
                    contact: {},
                    contactName: "",
                    date: "",
                    comment: "",
                    opportunityState: "",
                    types: [],
                    labels: []
                },
                defaultOpportunity: {
                    value: false,
                    id: 0,
                    title: "",
                    state: "Lead",
                    description: "",
                    lastProgress: ""
                }
            }
        },
        watch: {
            value() {
                this.$refs.form.resetValidation();

                if (this.editedContactPoint.date) {
                    this.date = datefns.format(this.editedContactPoint.date, 'YYYY-MM-DD', {locale: de});
                } else {
                    this.date = new Date().toISOString().substr(0, 10);
                }
            },
            contactName(value) {
                if (!this.opportunity) {
                    this.resetEditedOpportunity();
                    this.opportunityMenu = false;

                    if (value) {
                        let contact = this.contacts.find(it => it.name === value);
                        this.getOpportunities(contact.company.id);
                    } else {
                        this.newOpportunity = true;
                        this.companyOpportunities = [];
                    }
                }
            }
        },
        computed: {
            ...mapGetters({
                editedIndex: 'editedContactPointIndex',
                editedContactPoint: 'editedContactPoint',
                editedOpportunity: 'editedOpportunity',
                username: 'username',
                contacts: 'contacts',
                contactPoints: 'contactPoints'
            }),
            formTitle() {
                return this.editedIndex === -1 ? 'Kontaktpunkt hinzufügen' : 'Kontaktpunkt bearbeiten'
            },
            dateFormatted: {
                get() {
                    return datefns.format(this.date, 'DD.MM.YY', {locale: de});
                },
                set(newDate) {
                    this.date = newDate;
                }
            },
            contactName() {
                return this.editedContactPoint.contact.name;
            },
            opportunityButtonTitle() {
                return this.companyOpportunities.length + " Opportunities";
            }
        },
        methods: {
            ...mapActions(['getContactPointLabels']),
            ...mapMutations({
                storeContactPointDetails: 'storeEditedContactPointDetails',
                storeOpportunityDetails: 'storeEditedOpportunityDetails',
            }),
            clearDialog() {
                let tempContactName = this.editedContactPoint.contact.name;
                this.$refs.form.reset();
                this.editedContactPoint.contact.name = tempContactName;

                setTimeout(() => {
                    this.date = new Date().toISOString().substr(0, 10);
                    this.editedOpportunity.state = "Lead";
                    this.opportunityMenu = false;
                });
            },
            closeDialog() {
                this.$emit('input');

                setTimeout(() => {
                    this.storeContactPointDetails({
                        editedIndex: -1,
                        editedContactPoint: Object.assign({}, this.defaultContactPoint)
                    });

                    this.resetEditedOpportunity();

                    this.opportunityMenu = false;
                }, 300)
            },
            submitContactPoint() {
                let contact = this.contacts.find(it => it.name.includes(this.editedContactPoint.contact.name));

                api.put(`point/submit/${contact.id}`, {
                    title: this.editedContactPoint.title,
                    id: this.editedContactPoint.id,
                    date: datefns.parse(this.date).getTime(),
                    comment: this.editedContactPoint.comment,
                    opportunityState: this.getOpportunityState(),
                    types: this.editedContactPoint.types,
                    labels: this.editedContactPoint.labels,
                    creator: this.username
                }).then(res => {
                    let contactPointId = res.data;

                    if (this.opportunityMenu || this.opportunity) {
                        api.put(`opportunity/submit/${contactPointId}`, {
                            id: this.editedOpportunity.id,
                            title: this.editedOpportunity.title,
                            state: this.editedOpportunity.state,
                            description: this.editedOpportunity.description,
                            progress: this.editedOpportunity.progress
                        }).then(() => {
                            this.$emit('refresh');
                            this.closeDialog();
                        }).catch(error => {
                            console.log(error);
                            alert(error);
                        });
                    } else {
                        this.$emit('refresh');
                        this.closeDialog();
                    }
                }).catch(error => {
                    console.log(error);
                    alert(error);
                });
            },
            setContactPointLabels(labels) {
                this.editedContactPoint.labels = labels;
            },
            setContactPointTypes(types) {
                this.editedContactPoint.types = types;
            },
            getOpportunities(companyId) {
                api.get(`opportunity/get/list/${companyId}`).then(res => {
                    this.companyOpportunities = res.data
                        .filter(it => it.state !== 'Win' && it.state !== 'Lose');
                });
            },
            createOpportunity() {
                this.resetEditedOpportunity();

                if (this.opportunityMenu && this.newOpportunity) {
                    this.opportunityMenu = false;
                } else {
                    this.newOpportunity = true;
                    this.opportunityMenu = true;
                }
            },
            updateOpportunity(opportunity) {
                if (this.editedOpportunity.title.includes(opportunity.title)) {
                    this.resetEditedOpportunity();

                    this.opportunityMenu = false;
                } else {
                    this.resetEditedOpportunity(opportunity);

                    this.newOpportunity = false;
                    this.opportunityMenu = true;
                }
            },
            resetEditedOpportunity(opp) {
                this.storeOpportunityDetails({
                    editedIndex: opp ? opp.id : -1,
                    editedOpportunity: Object.assign({}, opp ? opp : this.defaultOpportunity)
                });
            },
            getOpportunityState() {
                if (this.opportunityMenu || this.opportunity) return this.editedOpportunity.state;
                if (this.editedContactPoint.id > 0) return this.editedContactPoint.opportunityState;

                return "Lead";
            },
            getStateColor(state) {
                switch (state) {
                    case 'Lead':
                        return 'error';
                    case 'Prospect':
                        return 'warning';
                    case 'Quote':
                        return 'success';
                }
            }
        }
    }
</script>