<template>
    <v-layout row wrap>
        <v-dialog
                :value="value"
                @input="$emit('input')"
                persistent
                max-width="950">
            <v-card>
                <v-card-title class="text-xs-left headline font-weight-light primary">
                    Fortschritt hinzufügen
                </v-card-title>
                <v-card-text>
                    <v-form ref="form" v-model="valid">
                        <v-container grid-list-md>
                            <v-layout row wrap>
                                <v-flex xs8>
                                    <v-text-field
                                            v-model="editedOpportunity.title"
                                            suffix="*"
                                            prepend-icon="title"
                                            label="Opportunity-Titel"
                                            hide-details/>
                                </v-flex>
                                <v-flex xs4>
                                    <v-combobox
                                            v-model="editedOpportunity.state"
                                            suffix="*"
                                            prepend-icon="bubble_chart"
                                            label="Status"
                                            hide-details/>
                                </v-flex>
                                <v-expand-transition>
                                    <v-flex xs12>
                                        <v-textarea
                                                v-model="editedOpportunity.description"
                                                prepend-icon="timeline"
                                                label="Fortschritt"
                                                rows="5" hide-details/>
                                    </v-flex>
                                </v-expand-transition>
                            </v-layout>
                        </v-container>
                    </v-form>
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn
                            color="error"
                            flat="flat"
                            @click="closeDialog()">
                        Abbrechen
                    </v-btn>
                    <v-btn
                            color="success"
                            flat="flat"
                            @click="confirm()">
                        Bestätigen
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-layout>
</template>

<script>
    import {mapGetters} from 'vuex';

    export default {
        props: ['value'],
        data() {
            return {
                confirmDialog: false,
                valid: false
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
            }
        }
    }
</script>