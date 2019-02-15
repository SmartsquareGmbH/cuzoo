<template>
    <v-dialog v-model="value" @input="$emit('input')" persistent max-width="750">
        <v-card>
            <v-card-title class="headline primary" primary-title>
                {{ formTitle }}
            </v-card-title>
            <v-card-text class="text-xs-right primary--text">
                <v-form ref="form" v-model="valid">
                    <v-container grid-list-md>
                        <v-layout wrap>
                            <v-flex xs12>
                                <v-text-field
                                        v-model="editedCompany.name"
                                        label="Unternehmen"
                                        prepend-icon="business_center"
                                        hide-details
                                        suffix="*"
                                        :rules="companyFieldRules"></v-text-field>
                            </v-flex>
                            <v-flex xs6>
                                <v-text-field
                                        v-model="editedCompany.street"
                                        label="Straße &amp; Hsnr."
                                        prepend-icon="place"
                                        hide-details></v-text-field>
                            </v-flex>
                            <v-flex xs3>
                                <v-text-field
                                        v-model="editedCompany.zipCode"
                                        label="PLZ"
                                        hide-details
                                        mask="#####"
                                        counter min="5"></v-text-field>
                            </v-flex>
                            <v-flex xs3>
                                <v-text-field
                                        v-model="editedCompany.place"
                                        label="Ort"
                                        hide-details></v-text-field>
                            </v-flex>
                            <v-flex xs12>
                                <v-text-field
                                        v-model="editedCompany.homepage"
                                        label="Homepage"
                                        prepend-icon="home"></v-text-field>
                            </v-flex>
                            <v-flex xs12>
                                <v-textarea
                                        v-model="editedCompany.description"
                                        counter="255"
                                        maxlength='255'
                                        name="input-7-4"
                                        label="Beschreibung"
                                        rows="3"
                                ></v-textarea>
                            </v-flex>
                            <v-flex xs12>
                                <v-textarea
                                        v-model="editedCompany.other"
                                        counter="255"
                                        maxlength="255"
                                        name="input-7-4"
                                        label="Sonstiges"
                                        rows="3"
                                ></v-textarea>
                            </v-flex>

                            <v-flex xs12>
                                <v-combobox
                                        v-model="editedCompany.labels"
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
                <v-btn color="primary" flat v-on:click="closeDialog()">Abbrechen</v-btn>
                <v-btn color="primary" flat v-on:click="clearDialog()">Zurücksetzen</v-btn>
                <v-btn color="primary" flat v-on:click="submitCompany()" :disabled="!valid">Speichern</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    import {mapGetters, mapMutations} from 'vuex';
    import api from '../../utils/http-common';
    import debounce from 'lodash.debounce';

    const debouncedLabelApiCall = debounce(getLabelsByInput, 150, {leading: true});

    export default {
        props: ["value"],
        data() {
            return {
                valid: false,
                labelBoxInput: '',
                companyFieldRules: [
                    v => !!v || "Bitte geben Sie ein Unternehmen an"
                ],
                defaultCompany: {
                    value: false,
                    id: 0,
                    name: "",
                    street: "",
                    zipCode: "",
                    place: "",
                    homepage: "",
                    description: "",
                    other: "",
                    labels: []
                }
            }
        },
        computed: {
            ...mapGetters({
                editedCompany: 'editedCompany',
                editedIndex: 'editedCompanyIndex',
                labels: 'companyLabels'
            }),
            formTitle() {
                return this.editedIndex === -1 ? 'Unternehmen hinzufügen' : 'Unternehmen bearbeiten'
            },
            temporaryLabels() {
                return this.editedCompany.labels;
            },
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
            temporaryLabels() {
                this.editedCompany.labels.forEach(label => {
                    if (removeNonLetters(label) === '') {
                        this.removeLabel(label);
                    }
                })
            }
        },
        methods: {
            ...mapMutations({
                storeCompanyDetails: 'storeEditedCompanyDetails',
                storeLabels: 'storeCompanyLabels'
            }),
            closeDialog() {
                this.$emit('input');

                setTimeout(() => {
                    this.storeCompanyDetails({
                        editedIndex: -1,
                        editedCompany: Object.assign({}, this.defaultCompany)
                    })
                }, 300)
            },
            clearDialog() {
                this.$refs.form.reset();
            },
            submitCompany() {
                api.put('company/submit', {
                    name: this.editedCompany.name,
                    id: this.editedCompany.id,
                    street: this.editedCompany.street,
                    zipCode: this.editedCompany.zipCode,
                    place: this.editedCompany.place,
                    homepage: this.editedCompany.homepage,
                    description: this.editedCompany.description,
                    other: this.editedCompany.other,
                    labels: this.editedCompany.labels
                }).then(() => {
                    this.$parent.refreshTable();
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
            removeLabel(item) {
                this.editedCompany.labels.splice(this.editedCompany.labels.indexOf(item), 1);
                this.editedCompany.labels = [...this.editedCompany.labels]
            },
        }
    }

    function getLabelsByInput(input) {
        return api.get(`company/get/labels/${removeNonLetters(input)}`).then(response => {
            return {labels: response.data};
        })
    }

    function removeNonLetters(string) {
        return string.replace(/-/g, '').replace(/ /g, '').toLowerCase();
    }
</script>