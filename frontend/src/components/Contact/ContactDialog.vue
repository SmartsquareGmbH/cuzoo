<template>
    <v-dialog v-model="contactDialogState" persistent max-width="750">
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
                                        v-model="editedContact.name"
                                        label="Vor- und Nachname"
                                        prepend-icon="people"
                                        hide-details
                                        suffix="*"
                                        required
                                        :rules="[v => !!v]"></v-text-field>
                            </v-flex>
                            <v-flex xs6>
                                <v-combobox
                                        v-model="editedContact.company.name"
                                        :items="companyNames"
                                        label="Unternehmen"
                                        prepend-icon="business_center"
                                        hide-details>
                                </v-combobox>
                            </v-flex>
                            <v-flex xs6>
                                <v-text-field
                                        v-model="editedContact.role"
                                        label="Rolle"
                                        hide-details></v-text-field>
                            </v-flex>
                            <v-flex xs6>
                                <v-text-field
                                        v-model="editedContact.mail"
                                        prepend-icon="mail"
                                        label="E-Mail"
                                        hide-details></v-text-field>
                            </v-flex>
                            <v-flex xs6>
                                <v-text-field
                                        v-model="editedContact.telephone"
                                        label="Telefon"
                                        hide-details></v-text-field>
                            </v-flex>
                            <v-flex xs12>
                                <v-textarea
                                        v-model="editedContact.comment"
                                        name="input-7-4"
                                        label="Bemerkung"
                                        rows="3" hide-details
                                ></v-textarea>
                            </v-flex>
                        </v-layout>
                    </v-container>
                </v-form>
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="primary" flat v-on:click="closeDialog()">Abbrechen</v-btn>
                <v-btn color="primary" flat v-on:click="clearDialog()">Löschen</v-btn>
                <v-btn color="primary" flat v-on:click="submitContact()" :disabled="!valid">Speichern</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    import {mapState} from 'vuex'
    import api from '../../utils/http-common'

    export default {
        data() {
            return {
                contactDialog: false,
                valid: true,
                defaultContact: {
                    value: false,
                    id: 0,
                    name: "",
                    company: {
                        name: ""
                    },
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
            formTitle() {
                return this.editedIndex === -1 ? 'Ansprechpartner hinzufügen' : 'Ansprechpartner bearbeiten'
            },
            contactDialogState() {
                return this.$store.getters.getContactDialogState;
            },
            editedIndex() {
                return this.$store.getters.getEditedIndex;
            },
            ...mapState(['editedContact']),
            editedContact: {
                get() {
                    return this.$store.state.editedContact;
                }
            },
            companyNames() {
                return this.$store.getters.getCompanyNames;
            },
            companyName() {
                return this.editedContact.company.name;
            }
        },
        methods: {
            closeDialog() {
                this.$store.commit({
                    type: 'storeContactDialogState',
                    contactDialog: false
                })
                setTimeout(() => {
                    this.$store.commit({
                        type: 'storeEditedContactDetails',
                        editedIndex: -1,
                        editedContact: Object.assign({}, this.defaultContact)
                    })
                }, 300)
            },
            clearDialog() {
                this.$refs.form.reset();
            },
            submitContact() {
                if (this.companyName == null || this.companyName === "") {
                    this.editedContact.companyName = "";
                    this.editedContact.role = "Freiberufler";
                }

                let company = this.companyName.replace("&", "%26");
                let companyOrFreelancer = company ? `?companyName=${company}` : ''
                
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