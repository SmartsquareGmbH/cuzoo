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
                                prepend-icon="people"
                                hide-details
                                suffix="*"
                                required
                                :rules="contactFieldRules"/>
                            </v-flex>
                            <v-flex xs6>
                                <v-combobox
                                v-model="companyName"
                                :disabled="!this.companyFieldEnabled"
                                :items="companyNames"
                                label="Unternehmen"
                                prepend-icon="business_center"
                                hide-details/>
                            </v-flex>
                            <v-flex xs6>
                                <v-text-field
                                v-model="editedContact.role"
                                label="Rolle"
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
                                v-model="editedContact.telephone"
                                label="Telefon"
                                hide-details/>
                            </v-flex>
                            <v-flex xs12>
                                <v-textarea
                                v-model="editedContact.comment"
                                name="input-7-4"
                                label="Bemerkung"
                                rows="3" hide-details/>
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

    export default {
        props: ["value", "companyNames"],
        data() {
            return {
                valid: false,
                companyFieldEnabled: true,
                companyName: '',
                contactFieldRules: [
                    v => !!v || "Bitte geben Sie einen Namen an"
                ],
                defaultContact: {
                    value: false,
                    id: 0,
                    name: "",
                    company: {},
                    role: "",
                    mail: "",
                    telephone: "",
                    lastContact: "",
                    lastAnswer: "",
                    comment: ""
                }
            }
        },
        computed: {
            ...mapGetters({
                editedIndex: 'editedContactIndex',
                editedContact: 'editedContact'
            }),
            formTitle() {
                return this.editedIndex === -1 ? 'Ansprechpartner hinzufügen' : 'Ansprechpartner bearbeiten'
            }
        },
        mounted() {
            this.companyName = this.getCompanyName();
        },
        methods: {
            ...mapMutations({
                storeContactDetails: 'storeEditedContactDetails'
            }),
            closeDialog() {
                this.$emit('input');

                setTimeout(() => {
                    this.storeContactDetails({
                        editedIndex: -1,
                        editedContact: Object.assign({}, this.defaultContact)
                    })
                }, 300)
            },
            clearDialog() {
                this.$refs.form.reset();
            },
            submitContact() {
                if (this.getCompanyName() == null || this.getCompanyName() === "") {
                    this.editedContact.companyName = "";
                    this.editedContact.role = "Freiberufler";
                }

                this.companyName.replace("&", "%26");

                let companyOrFreelancer = this.companyName ? `?companyName=${this.companyName}` : '';
                
                api.put(`contact/submit${companyOrFreelancer}`, {
                    name: this.editedContact.name,
                    id: this.editedContact.id,
                    role: this.editedContact.role,
                    address: this.editedContact.address,
                    mail: this.editedContact.mail,
                    telephone: this.editedContact.telephone,
                    lastContact: this.editedContact.lastContact,
                    lastAnswer: this.editedContact.lastAnswer,
                    comment: this.editedContact.comment
                }).then(() => {
                    this.$parent.refreshTable();
                    this.closeDialog();
                }).catch(error => {
                    console.log(error);
                    alert(error);
                });
            },
            getCompanyName() {
                if (this.companyNames.length === 1) {
                    this.companyFieldEnabled = false;
                    return this.companyNames[0];
                } else {
                    this.companyFieldEnabled = true;
                    return this.companyName;
                }
            }
        }
    }
</script>