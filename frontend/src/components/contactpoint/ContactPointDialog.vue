<template>
    <v-dialog :value="value" @input="$emit('input')" persistent max-width="950">
        <v-card>
            <v-card-title class="headline primary" primary-title>
                {{ formTitle }}
            </v-card-title>
            <v-card-text>
                <v-form ref="form" v-model="valid">
                    <v-container grid-list-md>
                        <v-layout wrap>
                            <v-flex xs8>
                                <v-text-field
                                        v-model="editedContactPoint.title"
                                        :rules="titleRules"
                                        prepend-icon="title"
                                        label="Titel"
                                        suffix="*"/>
                            </v-flex>
                            <v-flex xs4>
                                <v-combobox
                                        v-model="editedContactPoint.type"
                                        :items="this.pointTypes"
                                        :rules="pointRules"
                                        prepend-icon="share"
                                        label="Art">
                                    <template slot="item" slot-scope="data">
                                        <v-icon class="mr-1">
                                            {{ getPointTypeIconOf(data.item) }}
                                        </v-icon>
                                        {{ data.item }}
                                    </template>
                                </v-combobox>
                            </v-flex>
                            <v-flex xs8>
                                <v-combobox
                                        v-model="editedContactPoint.contact.name"
                                        :items="this.contactNames"
                                        :rules="contactRules"
                                        prepend-icon="person"
                                        label="Ansprechpartner"/>
                            </v-flex>
                            <v-flex xs4>
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
                                        hide-details
                                        outline
                                        color="primary"
                                        v-model="editedContactPoint.labels"
                                        :items="labels"
                                        label="Tags"
                                        chips
                                        clearable
                                        prepend-icon="label"
                                        solo
                                        multiple>
                                    <template slot="selection" slot-scope="data">
                                        <v-chip
                                                class="title"
                                                :selected="data.selected"
                                                close
                                                @input="removeLabel(data.item)">
                                            {{ data.item }}
                                        </v-chip>
                                    </template>
                                </v-combobox>
                            </v-flex>
                        </v-layout>
                    </v-container>
                </v-form>
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
    import {mapGetters, mapMutations, mapActions} from 'vuex'
    import api from '../../utils/http-common'

    const datefns = require('date-fns');
    const de = require('date-fns/locale/de');

    export default {
        props: ["value", "contactNames"],
        data() {
            return {
                date: new Date().toISOString().substr(0, 10),
                menu: false,
                valid: true,
                pointTypes: ["Telefon", "E-Mail", "Social Media", "Persönlich"],
                pointTypeIcons: ["phone", "mail", "share", "people"],
                pointRules: [
                    v => !!v || "Bitte geben Sie eine Kontaktpunktart an",
                    v => this.pointTypes.includes(v) || "Diese Kontaktpunktart existiert nicht"
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
                    type: "",
                    labels: []
                }
            }
        },
        computed: {
            ...mapGetters({
                editedIndex: 'editedContactPointIndex',
                editedContactPoint: 'editedContactPoint',
                labels: 'contactPointLabels'
            }),
            formTitle() {
                return this.editedIndex === -1 ? 'Kontaktpunkt hinzufügen' : 'Kontaktpunkt bearbeiten'
            },
            dateFormatted() {
                return datefns.format(this.date, 'DD.MM.YY', {locale: de});
            }
        },
        methods: {
            ...mapMutations({
                storeDetails: 'storeEditedContactPointDetails'
            }),
            ...mapActions(['getContactPointLabels']),
            getPointTypeIconOf: function (type) {
                return this.pointTypeIcons[this.pointTypes.indexOf(type)];
            },
            clearDialog() {
                this.$refs.form.reset();
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
                console.log(this.editedContactPoint.labels);

                api.put(`point/submit/${this.editedContactPoint.contact.name}?labels=${this.editedContactPoint.labels}`, {
                    title: this.editedContactPoint.title,
                    id: this.editedContactPoint.id,
                    type: this.editedContactPoint.type,
                    date: datefns.parse(this.date).getTime(),
                    comment: this.editedContactPoint.comment
                }).then(() => {
                    this.$parent.refreshData();
                    this.getContactPointLabels();
                    this.closeDialog();
                }).catch(error => {
                    console.log(error);
                    alert(error);
                });
            },
            removeLabel(item) {
                this.labels.splice(this.labels.indexOf(item), 1)
                this.labels = [...this.labels]
            }
        }
    }
</script>
