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
                                        :rules="titleRules"
                                        prepend-icon="title"
                                        label="Titel"
                                        suffix="*"/>
                            </v-flex>
                            <v-flex xs5>
                                <v-combobox
                                        v-model="editedContactPoint.types"
                                        :items="types"
                                        :search-input.sync="mediumLabelBoxInput"
                                        @change="resetTypes()"
                                        :rules="pointRules"
                                        prepend-icon="share"
                                        color="primary"
                                        label="Art"
                                        chips
                                        outline
                                        multiple
                                        solo
                                        hide-details>
                                    <template slot="selection" slot-scope="type">
                                        <v-chip
                                                class="subheading"
                                                :selected="type.selected"
                                                close
                                                @input="removeType(type.item)">
                                            {{ type.item }}
                                        </v-chip>
                                    </template>
                                    <template slot="no-data" v-if="mediumLabelBoxInput">
                                        <v-list-tile v-if="mediumLabelBoxInput.replace(/ /g, '') !== ''">
                                            <v-list-tile-content>
                                                <v-list-tile-title>
                                                    Keine Labels für
                                                    "<strong class="primary--text">{{ mediumLabelBoxInput }}</strong>"
                                                    gefunden. Drücke <kbd>Enter</kbd> um es zu erstellen.
                                                </v-list-tile-title>
                                            </v-list-tile-content>
                                        </v-list-tile>
                                    </template>
                                </v-combobox>
                            </v-flex>
                            <v-flex xs7>
                                <v-combobox
                                        v-model="editedContactPoint.contact.name"
                                        :items="this.contactNames"
                                        :rules="contactRules"
                                        prepend-icon="person"
                                        suffix="*"
                                        label="Ansprechpartner"/>
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
                                        min-width="290px">
                                    <v-text-field
                                            slot="activator"
                                            v-model="dateFormatted"
                                            :rules="dateRules"
                                            prepend-icon="event"
                                            label="Datum"
                                            suffix="*"
                                            readonly/>
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
                                <v-combobox
                                        v-model="editedContactPoint.labels"
                                        :items="labels"
                                        :search-input.sync="labelBoxInput"
                                        @change="resetLabels()"
                                        prepend-icon="label"
                                        color="primary"
                                        label="Labels"
                                        outline
                                        clearable
                                        multiple
                                        solo
                                        hide-details>
                                    <template slot="selection" slot-scope="label" tabindex="-1">
                                        <v-chip tabindex="-1"
                                                class="title"
                                                :selected="label.selected"
                                                close
                                                @input="removeLabel(label.item)">
                                            {{ label.item }}
                                        </v-chip>
                                    </template>
                                    <template slot="no-data" v-if="labelBoxInput" tabindex="-1">
                                        <v-list-tile v-if="labelBoxInput.replace(/ /g, '') !== ''">
                                            <v-list-tile-content>
                                                <v-list-tile-title>
                                                    Keine Labels für
                                                    "<strong class="primary--text">{{ labelBoxInput }}</strong>"
                                                    gefunden. Drücke <kbd>Enter</kbd> um es zu erstellen.
                                                </v-list-tile-title>
                                            </v-list-tile-content>
                                        </v-list-tile>
                                    </template>
                                </v-combobox>
                            </v-flex>
                        </v-layout>
                    </v-container>
                </v-form>
                <div class="mr-2">* Pflichtfelder</div>
            </v-card-text>
            <v-card-actions>
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
    import debounce from 'lodash.debounce';

    const datefns = require('date-fns');
    const de = require('date-fns/locale/de');

    const debouncedLabelApiCall = debounce(getLabelsByInput, 150, {leading: true});
    const debouncedTypeApiCall = debounce(getTypesByInput, 150, {leading: true});

    export default {
        props: ["value", "contactNames"],
        data() {
            return {
                date: new Date().toISOString().substr(0, 10),
                menu: false,
                valid: false,
                labelBoxInput: null,
                mediumLabelBoxInput: null,
                pointTypes: ["Telefon", "E-Mail", "Social Media", "Persönlich"],
                pointRules: [
                    v => !!v || "Bitte geben Sie eine Kontaktpunktart an"
                ],
                contactRules: [
                    v => !!v || "Bitte geben Sie einen Ansprechpartner an",
                    v => this.contactNames.includes(v) || "Dieser Ansprechpartner existiert nicht"
                ],
                titleRules: [v => !!v || "Bitte geben Sie einen Titel an"],
                dateRules: [v => !!v || "Bitte geben Sie ein Datum an"],
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
                }
            }
        },
        watch: {
            value() {
                this.$refs.form.resetValidation()
            },
            labelBoxInput(input) {
                if (input && removeNonLetters(input) !== '') {
                    let call = debouncedLabelApiCall(input);

                    if (call) {
                        call.then(res => this.storeLabels(res));
                    }

                    this.labels.forEach(label => {
                        if (removeNonLetters(label) === removeNonLetters(input)) {
                            this.labelBoxInput = label;
                        }
                    });
                }
            },
            mediumLabelBoxInput(input) {
                if (input && removeNonLetters(input) !== '') {
                    let call = debouncedTypeApiCall(input);

                    if (call) {
                        call.then(res => this.storeTypes(res));
                    }

                    this.types.forEach(type => {
                        if (removeNonLetters(type) === removeNonLetters(input)) {
                            this.mediumLabelBoxInput = type;
                        }
                    });
                }
            },
            temporaryLabels() {
                this.editedContactPoint.labels.forEach(label => {
                    if (removeNonLetters(label) === '') {
                        this.removeLabel(label);
                    }
                })
            },
            temporaryTypes() {
                this.editedContactPoint.types.forEach(type => {
                    if (removeNonLetters(type) === '') {
                        this.removeType(type);
                    }
                })
            }
        },
        computed: {
            ...mapGetters({
                editedIndex: 'editedContactPointIndex',
                editedContactPoint: 'editedContactPoint',
                labels: 'contactPointLabels',
                types: 'contactPointTypes'
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
            temporaryLabels() {
                return this.editedContactPoint.labels;
            },
            temporaryTypes() {
                return this.editedContactPoint.types;
            }
        },
        methods: {
            ...mapActions(['getContactPointLabels']),
            ...mapMutations({
                storeDetails: 'storeEditedContactPointDetails',
                storeLabels: 'storeContactPointLabels',
                storeTypes: 'storeContactPointTypes'
            }),
            clearDialog() {
                this.$refs.form.reset();
                this.$refs.form.resetValidation();

                this.dateFormatted = new Date().toISOString().substr(0, 10);
            },
            closeDialog() {
                this.$emit('input');

                setTimeout(() => {
                    this.storeDetails({
                        editedIndex: -1,
                        editedContactPoint: Object.assign({}, this.defaultContactPoint)
                    });
                }, 300)
            },
            submitContactPoint() {
                api.put(`point/submit/${this.editedContactPoint.contact.name}`, {
                    title: this.editedContactPoint.title,
                    id: this.editedContactPoint.id,
                    date: datefns.parse(this.date).getTime(),
                    comment: this.editedContactPoint.comment,
                    types: this.editedContactPoint.types,
                    labels: this.editedContactPoint.labels
                }).then(() => {
                    this.$parent.refreshData();
                    this.closeDialog();
                }).catch(error => {
                    console.log(error);
                    alert(error);
                });
            },
            resetLabels() {
                this.labelBoxInput = '';
                this.storeLabels({labels: []});
            },
            resetTypes() {
                this.mediumLabelBoxInput = '';
                this.storeTypes({types: []});
            },
            removeLabel(item) {
                this.editedContactPoint.labels.splice(this.editedContactPoint.labels.indexOf(item), 1);
                this.editedContactPoint.labels = [...this.editedContactPoint.labels]
            },
            removeType(item) {
                this.editedContactPoint.types.splice(this.editedContactPoint.types.indexOf(item), 1);
                this.editedContactPoint.types = [...this.editedContactPoint.types]
            }
        }
    }

    function getLabelsByInput(input) {
        return api.get(`point/get/labels/${removeNonLetters(input)}`).then(response => {
            return {labels: response.data};
        })
    }

    function getTypesByInput(input) {
        return api.get(`point/get/types/${removeNonLetters(input)}`).then(response => {
            return {types: response.data};
        })
    }

    function removeNonLetters(string) {
        return string.replace(/-/g, '').replace(/ /g, '').toLowerCase();
    }
</script>
