<template>
    <v-dialog :value="value" @input="$emit('input')" persistent max-width="750">
        <v-card>
            <v-card-title class="headline primary" primary-title>
                {{ formTitle }}
            </v-card-title>
            <v-card-text>
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
                                        required
                                        :rules="[v => !!v]"></v-text-field>
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
                                <v-radio-group v-model="editedCompany.status" required row>
                                    <v-radio label="Lead" value="Lead" color="primary"/>
                                    <v-radio label="Bestandskunde" value="Bestandskunde" color="primary"/>
                                </v-radio-group>
                            </v-flex>
                            <v-flex xs12>
                                <v-textarea
                                        v-model="editedCompany.purpose"
                                        counter="255"
                                        maxlength='255'
                                        name="input-7-4"
                                        label="Unternehmenszweck"
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
                        </v-layout>
                    </v-container>
                </v-form>
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
    import {mapState} from 'vuex'
    import api from '../../utils/http-common'

    export default {
        props: ["value"],
        data() {
            return {
                companyDialog: false,
                valid: true,
                defaultCompany: {
                    value: false,
                    id: 0,
                    name: "",
                    street: "",
                    zipCode: "",
                    place: "",
                    homepage: "",
                    status: "Lead",
                    purpose: "",
                    other: ""
                }
            }
        },
        computed: {
            formTitle() {
                return this.editedIndex === -1 ? 'Unternehmen hinzufügen' : 'Unternehmen bearbeiten'
            },
            companyDialogState() {
                return this.$store.getters.getCompanyDialogState;
            },
            editedIndex() {
                return this.$store.getters.getEditedIndex;
            },
            ...mapState(['editedCompany']),
            editedCompany: {
                get() {
                    return this.$store.state.editedCompany
                }
            }
        },
        methods: {
            closeDialog() {
                this.$emit('input')
                setTimeout(() => {
                    this.$store.commit({
                        type: 'storeEditedCompanyDetails',
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
                    status: this.editedCompany.status,
                    purpose: this.editedCompany.purpose,
                    other: this.editedCompany.other
                }, {
                    auth: {
                        username: this.$store.getters.getLogName,
                        password: this.$store.getters.getLogPass
                    }
                }).then(response => {
                    this.$parent.refreshTable();
                    this.closeDialog();
                }).catch(error => {
                    console.log(error);
                    alert(error);
                });
            },
        }
    }
</script>