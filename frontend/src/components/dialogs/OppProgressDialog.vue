<template>
    <v-dialog
            v-model="value"
            @input="$emit('input')"
            persistent
            max-width="950">
        <v-card>
            <v-card-title class="headline font-weight-light primary">
                Fortschritt hinzufügen
            </v-card-title>
            <v-card-text class="text-xs-right primary--text">
                <v-form ref="form" v-model="valid">
                    <v-container grid-list-md>
                        <v-layout row wrap>
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
                                <v-flex xs12>
                                    <v-textarea
                                            v-model="progressText"
                                            prepend-icon="timeline"
                                            label="Fortschritt"
                                            rows="5" hide-details/>
                                </v-flex>
                            </v-expand-transition>
                        </v-layout>
                    </v-container>
                </v-form>
                <div class="mr-2">* Pflichtfelder</div>
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="primary" flat @click.native="closeDialog()">Abbrechen</v-btn>
                <v-btn color="primary" flat v-on:click="clearDialog()">Zurücksetzen</v-btn>
                <v-btn color="primary" flat v-on:click="submitProgress()" :disabled="!valid">Speichern</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    import {mapGetters} from 'vuex';
    import api from '../../utils/http-common';

    export default {
        props: ['value'],
        data() {
            return {
                confirmDialog: false,
                valid: false,
                progressText: '',
                oppStatuses: ['Lose', 'Lead', 'Prospect', 'Quote', 'Win'],
                oppStatusRules: [
                    v => !!v || "Bitte geben Sie einen Status an",
                    v => this.oppStatuses.includes(v) || "Dieser Status existiert nicht"
                ],
                oppTitleRules: [
                    v => !!v || "Bitte geben Sie einen Titel an",
                    this.opportunityMenu === true
                ],
            }
        },
        computed: {
            ...mapGetters({
                editedIndex: 'editedContactPointIndex',
                editedOpportunity: 'editedOpportunity'
            }),
        },
        methods: {
            closeDialog() {
                this.$emit('input');
            },
            submitProgress() {
                api.put(`opportunity/submit/progress/${this.editedOpportunity.id}`, {
                    opportunityState: this.editedOpportunity.state,
                    progressText: this.progressText
                }).then(() => {
                    this.$emit('refresh');
                    this.closeDialog();
                });
            }
        }
    }
</script>