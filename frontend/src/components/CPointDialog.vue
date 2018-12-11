<template>
    <v-dialog v-model="cPointDialogState" persistent max-width="950">
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
                                v-model="editedCPoint.title"
                                label="Titel"
                                prepend-icon="share"
                                hide-details
                                suffix="*"
                                required
                                :rules="[v => !!v]"/>
                            </v-flex>
                            <v-flex xs4>
                                <v-menu
                                ref="menu"
                                :close-on-content-click="false"
                                v-model="menu"
                                :nudge-right="40"
                                :return-value.sync="date"
                                lazy
                                transition="scale-transition"
                                offset-y
                                full-width
                                min-width="290px">
                                    <v-text-field
                                    slot="activator"
                                    v-model="dateFormatted"
                                    prepend-icon="event"
                                    hide-details
                                    label="Datum"
                                    readonly/>
                                    <v-date-picker v-model="date" scrollable locale="de" :max="new Date().toISOString().substr(0, 10)">
                                        <v-spacer/>
                                        <v-btn flat color="primary" @click="menu = false">Abbrechen</v-btn>
                                        <v-btn flat color="primary" @click="$refs.menu.save(date)">OK</v-btn>
                                    </v-date-picker>
                                </v-menu>
                            </v-flex>
                            <v-flex xs12>
                                <v-combobox
                                v-model="editedCPoint.contactName"
                                :items="this.contactNames"
                                label="Ansprechpartner"
                                prepend-icon="person"
                                hide-details/>
                            </v-flex>
                        </v-layout>
                    </v-container>
                </v-form>
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="primary" flat v-on:click="closeDialog()">Abbrechen</v-btn>
                <v-btn color="primary" flat v-on:click="clearDialog()">Zurücksetzen</v-btn>
                <v-btn color="primary" flat v-on:click="submitCPoint()" :disabled="!valid">Speichern</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
import { mapState } from 'vuex';

export default {
    data() {
        return {
            date: new Date().toISOString().substr(0, 10),
            menu: false,
            editedIndex: this.$store.getters.getEditedIndex,
            valid: true,
            defaultCPoint: {
                value: false,
                id: 0,
                title: "",
                contactName: "",
                date: "",
                comment: ""
            }
        }
    },
    computed: {
        ...mapState(['editedCPoint']),
        editedCPoint: {
            get() {
                return this.$store.state.editedCPoint
            }
        },
        contactNames() {
            return this.$store.getters.getContactNames
        },
        cPointDialogState() {
            return this.$store.getters.getCPointDialogState
        },
        formTitle() {
            return this.editedIndex === -1 ? 'Kontaktpunkt hinzufügen' : 'Kontaktpunkt bearbeiten'
        },
        dateFormatted() {
            return this.formatDate(this.date)
        }
    },
    methods: {
        clearDialog() {
            this.$refs.form.reset();
        },
        closeDialog() {
            this.$store.commit({
                type: 'storeCPointDialogState',
                companyDialog: false
            })
            setTimeout(() => {
                this.$store.commit({
                    type: 'storeEditedCPointDetails',
                    editedIndex: -1,
                    editedCPoint: Object.assign({}, this.defaultCPoint)
                })
            }, 300)
        },
        formatDate (date) {
            if (!date) return null

            const [year, month, day] = date.split('-')
            return `${day}.${month}.${year}`
        },
    }
}
</script>
