<template>
    <v-dialog
            v-model="value"
            @input="$emit('input')"
            persistent
            max-width="950">
        <v-card>
            <v-card-title class="headline primary" primary-title>
                {{ formTitle }}
            </v-card-title>
            <v-card-text class="text-xs-right primary--text">
                <v-form ref="form" v-model="valid">
                    <v-container grid-list-md>
                        <v-layout wrap>
                            <v-flex xs12>
                                <v-textarea
                                        v-model="editedTodo.description"
                                        :rules="this.descriptionRules"
                                        counter="255"
                                        maxlength="255"
                                        name="input-7-4"
                                        prepend-icon="title"
                                        label="TODO"
                                        rows="3"
                                        suffix="*"/>
                            </v-flex>
                            <v-flex xs3>
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
                                            label="Fällig am"
                                            suffix="*"
                                            readonly/>
                                    <v-date-picker
                                            v-model="date"
                                            :min="new Date().toISOString()"
                                            scrollable
                                            locale="de">
                                        <v-spacer/>
                                        <v-btn flat color="primary" @click="menu = false">Abbrechen</v-btn>
                                        <v-btn flat color="primary" @click="$refs.menu.save(date)">OK</v-btn>
                                    </v-date-picker>
                                </v-menu>
                            </v-flex>
                            <v-flex xs9>
                                <v-combobox
                                        v-model="editedTodo.reminder"
                                        :items="this.reminders"
                                        :rules="this.reminderRules"
                                        prepend-icon="timer"
                                        label="Erinnerung"
                                        suffix="*">
                                    <template slot="item" slot-scope="data">
                                        {{ data.item }}
                                    </template>
                                </v-combobox>
                            </v-flex>
                            <v-flex xs12>
                                <v-combobox
                                        v-model="companyName"
                                        :items="companyNames"
                                        :rules="companyRules"
                                        prepend-icon="business"
                                        suffix="*"
                                        label="Unternehmen"/>
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
                <v-btn color="primary" flat v-on:click="submitTodo()" :disabled="!valid">Speichern</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    import api from '../../utils/http-common'
    import {mapGetters, mapMutations} from 'vuex'

    const datefns = require('date-fns');
    const de = require('date-fns/locale/de');

    let expirationDate = new Date(new Date().getTime() + 24 * 60 * 60 * 1000);
    let reminderDate = new Date();

    export default {
        props: ["value", "companyNames"],
        data() {
            return {
                formTitle: 'TODO hinzufügen',
                date: expirationDate.toISOString().substr(0, 10),
                dateRules: [v => !!v || "Bitte geben Sie ein Fälligkeitsdatum an"],
                reminders: [
                    '1 Tag vorher',
                    '3 Tage vorher',
                    '1 Woche vorher'
                ],
                companyRules: [
                    v => !!v || "Bitte geben Sie ein Unternehmen an",
                    v => this.companyNames.includes(v) || "Dieses Unternehmen existiert nicht"
                ],
                reminderRules: [v => !!v || "Bitte geben Sie an, wann Sie erinnert werden möchten"],
                descriptionRules: [v => !!v || "Bitte geben Sie eine Beschreibung an"],
                menu: false,
                valid: true,
                companyName: '',
                defaultTodo: {
                    id: 0,
                    description: '',
                    company: {},
                    expiration: '',
                    reminder: '',
                    done: false
                }
            }
        },
        computed: {
            ...mapGetters({
                username: 'username',
                editedIndex: 'editedTodoIndex',
                editedTodo: 'editedTodo'
            }),
            dateFormatted() {
                return datefns.format(this.date, 'DD.MM.YY', {locale: de})
            }
        },
        watch: {
            value() {
                this.$refs.form.resetValidation()
            }
        },
        methods: {
            ...mapMutations({
                storeDetails: 'storeEditedTodoDetails'
            }),
            submitTodo() {
                api.put(`todo/submit?companyName=${this.companyName.replace("&", "%26")}`, {
                    id: this.editedTodo.id,
                    description: this.editedTodo.description,
                    expiration: datefns.parse(this.date).getTime(),
                    reminder: this.getReminderDate(),
                    creator: this.username
                }).then(() => {
                    this.$parent.refreshData();
                    this.closeDialog();
                }).catch(error => {
                    console.log(error);
                    alert(error);
                });
            },
            getReminderDate() {
                switch (this.editedTodo.reminder) {
                    case this.reminders[0]:
                        reminderDate.setDate((datefns.parse(this.date).getDate()) - 1);
                        return reminderDate.getTime();
                    case this.reminders[1]:
                        reminderDate.setDate((datefns.parse(this.date).getDate()) - 3);
                        return reminderDate.getTime();
                    case this.reminders[2]:
                        reminderDate.setDate((datefns.parse(this.date).getDate()) - 7);
                        return reminderDate.getTime();
                }
            },
            clearDialog() {
                this.$refs.form.reset();
                this.companyName = '';
            },
            closeDialog() {
                this.$emit('input');
                this.companyName = '';

                setTimeout(() => {
                    this.storeDetails({
                        editedIndex: -1,
                        editedTodo: Object.assign({}, this.defaultTodo)
                    });
                }, 300)
            }
        }
    }
</script>