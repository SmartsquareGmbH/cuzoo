<template>
    <v-dialog :value="value" @input="$emit('input')" persistent max-width="750">
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
                                        v-model="editedContact.name"
                                        label="Vor- und Nachname"
                                        prepend-icon="person"
                                        suffix="*"
                                        required
                                        :rules="contactFieldRules"/>
                            </v-flex>
                            <v-flex xs12>
                                <v-combobox
                                        v-model="companyName"
                                        :disabled="!this.companyFieldEnabled"
                                        :items="this.companyNames"
                                        label="Unternehmen"
                                        prepend-icon="business"
                                        hide-details/>
                            </v-flex>
                            <v-flex xs6>
                                <v-text-field
                                        v-model="editedContact.mail"
                                        prepend-icon="mail"
                                        label="E-Mail"
                                        hide-details/>
                            </v-flex>
                            <v-flex xs6>
                                <v-text-field
                                        v-model="editedContact.role"
                                        label="Rolle"
                                        prepend-icon="work"
                                        hide-details/>
                            </v-flex>
                            <v-flex xs6>
                                <v-text-field
                                        v-model="editedContact.telephone"
                                        prepend-icon="call"
                                        label="Telefon"
                                        hide-details/>
                            </v-flex>
                            <v-flex xs6>
                                <v-text-field
                                        v-model="editedContact.mobile"
                                        prepend-icon="smartphone"
                                        label="Mobil"
                                        hide-details/>
                            </v-flex>
                            <v-flex xs12>
                                <v-textarea
                                        v-model="editedContact.comment"
                                        name="input-7-4"
                                        label="Bemerkung"
                                        rows="3"/>
                            </v-flex>
                            <v-flex xs12>
                                <label-box
                                        @label-added="setCurrentLabels"
                                        :current-labels="editedContact.labels"
                                        api-path="contact" type="Labels"/>
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
                <v-btn color="primary" flat v-on:click="submitContact()" :disabled="!valid">Speichern</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    import {mapGetters, mapMutations} from 'vuex';
    import api from '../../utils/http-common'

    import LabelBox from "../main/small/LabelBox.vue";

    export default {
        props: ["value", "companyNames"],
        components: { LabelBox },
        data() {
            return {
                valid: false,
                companyFieldEnabled: true,
                contactFieldRules: [
                    v => !!v || "Bitte geben Sie einen Namen an"
                ],
                companyName: "",
                defaultContact: {
                    value: false,
                    id: 0,
                    name: "",
                    company: {},
                    role: "",
                    mail: "",
                    telephone: "",
                    mobile: "",
                    comment: "",
                    labels: []
                }
            }
        },
        computed: {
            ...mapGetters({
                editedIndex: 'editedContactIndex',
                editedContact: 'editedContact',
                editedCompanyName: 'editedCompanyName'
            }),
            formTitle() {
                return this.editedIndex === -1 ? 'Ansprechpartner hinzufügen' : 'Ansprechpartner bearbeiten'
            }
        },
        watch: {
            value() {
                this.$refs.form.resetValidation();
            },
            editedCompanyName() {
                this.companyName = this.editedCompanyName;
            },
            companyNames() {
                if (this.companyNames.length === 1) {
                    this.companyName = this.companyNames[0];
                    this.companyFieldEnabled = false;
                } else {
                    this.companyFieldEnabled = true;
                    this.companyName = '';
                }
            }
        },
        methods: {
            ...mapMutations({
                storeContactDetails: 'storeEditedContactDetails',
                storeCompanyName: 'storeEditedCompanyName'
            }),
            closeDialog() {
                this.$emit('input');

                setTimeout(() => {
                    this.storeContactDetails({
                        editedIndex: -1,
                        editedContact: Object.assign({}, this.defaultContact)
                    });

                    this.storeCompanyName({editedCompanyName: ''});

                    this.companyName = "";
                }, 300)
            },
            clearDialog() {
                this.$refs.form.reset();

                if (this.$route.name === 'companyView') {
                    setTimeout(() => this.companyName = this.getCompanyName());
                }
            },
            submitContact() {
                console.log(this.companyName);
                let maybeCompany = this.companyName ? `?companyName=${this.companyName.replace("&", "%26")}` : '';

                api.put(`contact/submit${maybeCompany}`, {
                    name: this.editedContact.name,
                    id: this.editedContact.id,
                    role: this.editedContact.role,
                    address: this.editedContact.address,
                    mail: this.editedContact.mail,
                    telephone: this.editedContact.telephone,
                    mobile: this.editedContact.mobile,
                    comment: this.editedContact.comment,
                    labels: this.editedContact.labels
                }).then(() => {
                    this.$parent.refreshTable();
                    this.closeDialog();
                }).catch(error => {
                    console.log(error);
                    alert(error);
                });
            },
            setCurrentLabels(labels) {
                this.editedContact.labels = labels;
            },
            getCompanyName() {
                if (this.editedContact.company) {
                    return this.editedContact.company.name;
                } else if (this.companyNames.length === 1) {
                    this.companyFieldEnabled = false;
                    return this.companyNames[0];
                } else {
                    return null;
                }
            }
        }
    }
</script>