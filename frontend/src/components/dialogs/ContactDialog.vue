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
                            <v-flex xs6>
                                <v-text-field
                                        ref="nameField"
                                        v-model="editedContact.name"
                                        label="Vor- und Nachname"
                                        prepend-icon="person"
                                        suffix="*"
                                        required
                                        :rules="[v => !!v || 'Bitte geben Sie einen Namen an']"
                                        hide-details/>
                            </v-flex>
                            <v-flex xs6>
                                <v-combobox
                                        v-model="editedContact.manager"
                                        :items="this.usernames"
                                        label="Manager"
                                        prepend-icon="account_circle"
                                        suffix="*"
                                        required
                                        :rules="managerRules"
                                        hide-details/>
                            </v-flex>
                            <v-flex xs12>
                                <v-combobox
                                        v-model="companyName"
                                        :search-input.sync="companyNameEntered"
                                        :disabled="!this.companyFieldEnabled"
                                        :items="this.companyNames"
                                        label="Unternehmen"
                                        prepend-icon="business"
                                        hide-details>
                                    <template slot="no-data">
                                        <v-list-tile>
                                            <v-list-tile-content max-height="700">
                                                <v-list-tile-title>
                                                    Das Unternehmen "<strong class="primary--text">{{ companyNameEntered }}</strong>"
                                                    wurde gefunden. Beim <kbd>SPEICHERN</kbd> kann dies angelegt werden.
                                                </v-list-tile-title>
                                            </v-list-tile-content>
                                        </v-list-tile>
                                    </template>
                                </v-combobox>
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
                                        @label-added="setContactLabels"
                                        :current-labels="editedContact.labels"
                                        api-path="contact/get/labels" type="Labels"/>
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
                <v-btn color="primary" flat v-on:click="submit()" :disabled="!valid">Speichern</v-btn>
            </v-card-actions>
        </v-card>
        <confirm-dialog
                v-model="confirmDialogState"
                :questionToBeConfirmed="createCompanyMessage"
                @confirmed="submitCompany()"/>
    </v-dialog>
</template>

<script>
    import {mapActions, mapGetters, mapMutations} from 'vuex';
    import api from '../../utils/http-common'

    import LabelBox from "../core/LabelBox.vue";
    import ConfirmDialog from "../dialogs/ConfirmDialog.vue";

    export default {
        props: ["value", "companyNames"],
        components: {
            LabelBox,
            ConfirmDialog
        },
        data() {
            return {
                valid: false,
                companyFieldEnabled: true,
                confirmDialogState: false,
                createCompanyMessage: "Das angegebene Unternehmen existiert nicht, möchtest du es anlegen?",
                managerRules: [
                    v => !!v || "Bitte geben Sie einen Manager an!",
                    v => this.usernames.includes(v) || "Der Manager existiert nicht!"
                ],
                companyName: "",
                companyNameEntered: '',
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
                    manager: "",
                    labels: []
                }
            }
        },
        beforeMount() {
            this.getUsernames();
            this.editedContact.manager = this.username;
        },
        computed: {
            ...mapGetters({
                editedIndex: 'editedContactIndex',
                editedContact: 'editedContact',
                editedCompanyName: 'editedCompanyName',
                username: 'username',
                usernames: 'usernames'
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
            ...mapActions(['getUsernames']),
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
                    this.editedContact.manager = this.username;
                }, 300);

            },
            clearDialog() {
                this.$refs.form.reset();

                if (this.$route.name === 'companyView') {
                    setTimeout(() => this.companyName = this.getCompanyName());
                }

                this.editedContact.manager = this.username;
            },
            submit() {
                this.$refs.nameField.focus();

                setTimeout(() => {
                    if (this.companyName) {
                        if (this.companyNames.includes(this.companyName)) {
                            this.submitContact();
                        } else {
                            this.confirmDialogState = true;
                        }
                    } else {
                        this.submitContact();
                    }
                }, 10);
            },
            submitContact() {
                let maybeCompany = this.companyName ? `?companyName=${this.encodeString(this.companyName)}` : '';

                api.put(`contact/submit${maybeCompany}`, {
                    name: this.editedContact.name,
                    id: this.editedContact.id,
                    role: this.editedContact.role,
                    address: this.editedContact.address,
                    mail: this.editedContact.mail,
                    telephone: this.editedContact.telephone,
                    mobile: this.editedContact.mobile,
                    comment: this.editedContact.comment,
                    manager: this.editedContact.manager,
                    labels: this.editedContact.labels
                }).then(() => {
                    this.$parent.refreshTable();
                    this.closeDialog();
                }).catch(error => {
                    console.log(error);
                    alert(error);
                });
            },
            submitCompany() {
                api.put('company/submit', {
                    name: this.companyName,
                    id: -1,
                    street: "",
                    zipcode: "",
                    place: "",
                    homepage: "",
                    description: "",
                    other: "",
                    labels: []
                }).then(() => {
                    this.submitContact();
                }).catch(error => {
                    console.log(error);
                    alert(error);
                });
            },
            setContactLabels(labels) {
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
            },
            encodeString(value) {
                return value.replace("&", "%26").replace("|", "%7C");
            }
        }
    }
</script>