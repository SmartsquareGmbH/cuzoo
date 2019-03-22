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
                                    <v-layout row wrap v-if="opportunityMenu">
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
                                                    prepend-icon="timeline"
                                                    label="Status"
                                                    hide-details/>
                                        </v-flex>
                                        <v-flex xs12>
                                            <v-textarea
                                                    v-model="editedOpportunity.description"
                                                    prepend-icon="description"
                                                    label="Kurzbeschreibung"
                                                    rows="5" hide-details/>
                                        </v-flex>
                                    </v-layout>
                                </v-expand-transition>
                            </v-flex>
                        </v-layout>
                    </v-container>
                </v-form>
                <div class="mr-2">* Pflichtfelder</div>
            </v-card-text>
            <v-card-actions>
                <v-btn color="success" flat @click.native="opportunityMenu = !opportunityMenu">Neue
                    Opportunity
                    <v-icon v-if="opportunityMenu">keyboard_arrow_down</v-icon>
                    <v-icon v-if="!opportunityMenu">keyboard_arrow_up</v-icon>
                </v-btn>
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
        props: ["value", "contactNames"],
        components: {
            LabelBox
        },
        data() {
            return {
                opportunityMenu: false,
                date: new Date().toISOString().substr(0, 10),
                menu: false,
                valid: false,
                compulsory: [v => !!v || "Bitte geben Sie etwas ein"],
                contactRules: [
                    v => !!v || "Bitte geben Sie einen Ansprechpartner an",
                    v => this.contactNames.includes(v) || "Dieser Ansprechpartner existiert nicht"
                ],
                oppStatuses: ['Lead', 'Prospect', 'Quote'],
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
                    types: [],
                    labels: []
                },
                defaultOpportunity: {
                    value: false,
                    id: 0,
                    title: "",
                    state: "Lead",
                    description: ""
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
            }
        },
        computed: {
            ...mapGetters({
                editedIndex: 'editedContactPointIndex',
                editedContactPoint: 'editedContactPoint',
                editedOpportunity: 'editedOpportunity',
                username: 'username'
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
            }
        },
        methods: {
            ...mapActions(['getContactPointLabels']),
            ...mapMutations({
                storeContactPointDetails: 'storeEditedContactPointDetails',
                storeOpportunityDetails: 'storeEditedOpportunityDetails',
            }),
            clearDialog() {
                this.$refs.form.reset();
                this.editedContactPoint.contact.name = '';

                setTimeout(() => {
                    this.date = new Date().toISOString().substr(0, 10);
                    this.editedOpportunity.state = "Lead";
                    this.opportunityMenu = false;
                });
            },
            closeDialog() {
                this.$emit('input');
                this.editedContactPoint.contact.name = '';

                setTimeout(() => {
                    this.storeContactPointDetails({
                        editedIndex: -1,
                        editedContactPoint: Object.assign({}, this.defaultContactPoint)
                    });

                    this.storeOpportunityDetails({
                        editedIndex: -1,
                        editedOpportunity: Object.assign({}, this.defaultOpportunity)
                    });

                    this.opportunityMenu = false;
                }, 300)
            },
            submitContactPoint() {
                api.put(`point/submit/${this.editedContactPoint.contact.name}`, {
                    title: this.editedContactPoint.title,
                    id: this.editedContactPoint.id,
                    date: datefns.parse(this.date).getTime(),
                    comment: this.editedContactPoint.comment,
                    types: this.editedContactPoint.types,
                    labels: this.editedContactPoint.labels,
                    creator: this.username
                }).then(res => {
                    let contactPointId = res.data;

                    if (this.opportunityMenu) {
                        api.put(`opportunity/submit/${contactPointId}`, {
                            id: this.editedOpportunity.id,
                            title: this.editedOpportunity.title,
                            state: this.editedOpportunity.state,
                            description: this.editedOpportunity.description
                        });
                    }

                    this.$emit('refresh');
                    this.closeDialog();
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
            }
        }
    }
</script>

<style scoped>
    .no-padding-top {
        padding-top: 0px !important;
    }
</style>