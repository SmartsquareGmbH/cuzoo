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
                                        v-model="editedContactPoint.contactName"
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
                                            :max="new Date().toISOString().substr(0, 10)"
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
                                        rows="10"/>
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
    import {mapState} from 'vuex'
    import pointStore from '@/stores/points.js'
    import api from '@/utils/http-common'

    export default {
        props: ["value", "contactNames"],
        data() {
            return {
                editedIndex: pointStore.getters.getEditedIndex,
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
                    contactName: "",
                    date: "",
                    comment: "",
                    type: ""
                }
            }
        },
        computed: {
            ...mapState(['editedContactPoint']),
            editedContactPoint: {
                get() {
                    return pointStore.state.editedContactPoint
                }
            },
            formTitle() {
                return this.editedIndex === -1 ? 'Kontaktpunkt hinzufügen' : 'Kontaktpunkt bearbeiten'
            },
            dateFormatted() {
                return this.formatDate(this.date)
            }
        },
        methods: {
            formatDate(date) {
                if (!date) return null;

                const [year, month, day] = date.split('-');
                return `${day}.${month}.${year.substr(2, 2)}`
            },
            getPointTypeIconOf: function (type) {
                return this.pointTypeIcons[this.pointTypes.indexOf(type)];
            },
            clearDialog() {
                this.$refs.form.reset();
            },
            closeDialog() {
                this.$emit('input');

                setTimeout(() => {
                    pointStore.commit({
                        type: 'storeEditedContactPointDetails',
                        editedIndex: -1,
                        editedContactPoint: Object.assign({}, this.defaultContactPoint)
                    });
                }, 300)
            },
            submitContactPoint() {
                api.put(`point/submit?contactName=${this.editedContactPoint.contactName}`, {
                    title: this.editedContactPoint.title,
                    id: this.editedContactPoint.id,
                    type: this.editedContactPoint.type,
                    date: this.dateFormatted,
                    comment: this.editedContactPoint.comment
                }).then(() => {
                    this.$parent.refreshData();
                    this.closeDialog();
                }).catch(error => {
                    console.log(error);
                    alert(error);
                });
            },
        }
    }
</script>
