<template>
  <v-dialog v-model="value" persistent max-width="950" @input="$emit('input')">
    <v-card>
      <v-card-title class="headline primary" primary-title>
        {{ formTitle }}
      </v-card-title>
      <div v-if="loading" >
        <v-progress-linear class="mt-0" slot="progress" color="blue" indeterminate />
      </div>
      <v-card-text class="text-xs-right primary--text">
        <v-form ref="form" v-model="valid">
          <v-container grid-list-md>
            <v-layout wrap>
              <v-flex xs12>
                <v-textarea
                  v-model="editedTodo.description"
                  :rules="descriptionRules"
                  counter="255"
                  maxlength="255"
                  name="input-7-4"
                  prepend-icon="title"
                  label="TODO"
                  rows="3"
                  suffix="*"
                  :disabled="loading"
                />
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
                  min-width="290px"
                  :disabled="loading"
                >
                  <v-text-field
                    slot="activator"
                    v-model="dateFormatted"
                    :rules="dateRules"
                    prepend-icon="event"
                    label="Fällig am"
                    suffix="*"
                    :disabled="loading"
                  />
                  <v-date-picker v-model="date" :min="new Date().toISOString()" scrollable locale="de" :disabled="loading">
                    <v-spacer />
                    <v-btn flat color="primary" @click="menu = false">Abbrechen</v-btn>
                    <v-btn flat color="primary" @click="$refs.menu.save(date)">OK</v-btn>
                  </v-date-picker>
                </v-menu>
              </v-flex>
              <v-flex xs9>
                <v-combobox
                  v-model="editedTodoReminder"
                  :items="reminders"
                  :rules="reminderRules"
                  prepend-icon="notifications"
                  label="Erinnerung"
                  suffix="*"
                  :disabled="loading"
                >
                  <template slot="item" slot-scope="data">
                    {{ data.item }}
                  </template>
                </v-combobox>
              </v-flex>
              <v-flex xs12>
                <v-combobox
                  v-model="company"
                  :items="companies"
                  item-text="name"
                  item-value="name"
                  :search-input.sync="companyNameEntered"
                  :rules="companyRules"
                  prepend-icon="business"
                  suffix="*"
                  label="Unternehmen"
                  :disabled="loading"
                >
                  <template slot="no-data">
                    <v-list-tile>
                      <v-list-tile-content max-height="700">
                        <v-list-tile-title>
                          Das Unternehmen "<strong class="primary--text">{{ companyNameEntered }}</strong
                          >" wurde nicht gefunden.
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
        <v-btn color="primary" flat :disabled="loading" @click.native="closeDialog()">Abbrechen</v-btn>
        <v-btn color="primary" flat :disabled="loading" @click="clearDialog()">Zurücksetzen</v-btn>
        <v-btn color="primary" flat :disabled="!valid || loading" @click="submitTodo()">Speichern</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import api from "../../utils/http-common"
import { mapGetters, mapMutations } from "vuex"

const datefns = require("date-fns")
const de = require("date-fns/locale/de")

let expirationDate = new Date(new Date().getTime() + 24 * 60 * 60 * 1000)
let reminderDate = new Date()

export default {
  props: ["value", "companies"],
  data() {
    return {
      loading: false,
      date: expirationDate.toISOString().substr(0, 10),
      dateRules: [(v) => !!v || "Bitte geben Sie ein Fälligkeitsdatum an"],
      reminders: ["1 Tag vorher", "3 Tage vorher", "1 Woche vorher"],
      companyRules: [
        (v) => !!v || "Bitte geben Sie ein Unternehmen an",
        (v) => this.companies.some((it) => it.name === v?.name) || "Dieses Unternehmen existiert nicht",
      ],
      reminderRules: [
        (v) => !!v || "Bitte geben Sie an, wann Sie erinnert werden möchten",
        (v) => this.reminders.includes(v) || "Wählen Sie bitte einen gültigen Zeitpunkt aus",
      ],
      descriptionRules: [(v) => !!v || "Bitte geben Sie eine Beschreibung an"],
      menu: false,
      valid: true,
      company: "",
      companyNameEntered: "",
      defaultTodo: {
        id: 0,
        description: "",
        company: {},
        expiration: "",
        reminder: "",
        done: false,
      },
    }
  },
  computed: {
    ...mapGetters({
      username: "username",
      editedIndex: "editedTodoIndex",
      editedTodo: "editedTodo",
      editedCompany: "editedTodoCompany",
    }),
    formTitle() {
      return this.editedIndex === -1 ? "TODO hinzufügen" : "TODO bearbeiten"
    },
    dateFormatted: {
      get() {
        return datefns.format(this.date, "DD.MM.YY", { locale: de })
      },
      set(newDate) {
        this.date = newDate
      },
    },
    editedTodoReminder: {
      get() {
        if (this.editedTodo.reminder) {
          if (!this.reminders.includes(this.editedTodo.reminder)) {
            return this.getDateAsReminder()
          }
          return this.editedTodo.reminder
        } else {
          return ""
        }
      },
      set(reminder) {
        this.editedTodo.reminder = reminder
      },
    },
  },
  watch: {
    value() {
      this.$refs.form.resetValidation()

      if (this.editedTodo.expiration) {
        this.date = datefns.format(this.editedTodo.expiration, "YYYY-MM-DD", { locale: de })
      } else {
        this.date = new Date().toISOString().substr(0, 10)
      }
    },
    editedCompany(company) {
      this.company = company
    },
  },
  methods: {
    ...mapMutations({
      storeDetails: "storeEditedTodoDetails",
      storeCompany: "storeEditedTodoCompany",
    }),
    submitTodo() {
      let reminderAsDate
      if (this.reminders.includes(this.editedTodo.reminder)) {
        reminderAsDate = this.getReminderDate()
      } else {
        if (this.editedTodo.expiration && !datefns.isSameDay(datefns.parse(this.editedTodo.expiration), datefns.parse(this.date))) {
          // If the expiration date is changed, the reminder date has to be adjusted
          this.editedTodo.reminder = this.getDateAsReminder()
          reminderAsDate = this.getReminderDate()
        } else {
          reminderAsDate = datefns.parse(this.editedTodo.reminder).getTime()
        }
      }

      this.loading = true
      api
        .put(`todo/submit/${this.company.id}`, {
          id: this.editedTodo.id,
          description: this.editedTodo.description,
          expiration: datefns.parse(this.date).getTime(),
          reminder: reminderAsDate,
          creator: this.username,
        })
        .then(() => {
          this.$emit("refresh")
          this.closeDialog()
        })
        .catch((error) => {
          alert(error)
        })
        .finally(() => this.loading = false)
    },
    getReminderDate() {
      switch (this.editedTodo.reminder) {
        case this.reminders[0]:
          reminderDate = datefns.subDays(datefns.parse(this.date), 1)
          return reminderDate.getTime()
        case this.reminders[1]:
          reminderDate = datefns.subDays(datefns.parse(this.date), 3)
          return reminderDate.getTime()
        case this.reminders[2]:
          reminderDate = datefns.subDays(datefns.parse(this.date), 7)
          return reminderDate.getTime()
      }
    },
    getDateAsReminder() {
      const expirationDate = datefns.parse(this.editedTodo.expiration)
      const convertedReminder = datefns.parse(new Date(this.editedTodo.reminder))

      if (datefns.isSameDay(datefns.subDays(expirationDate,1), convertedReminder)) {
        return this.reminders[0]
      } else if (datefns.isSameDay(datefns.subDays(expirationDate,3), convertedReminder)) {
        return this.reminders[1]
      } else if (datefns.isSameDay(datefns.subDays(expirationDate,7), convertedReminder)) {
        return this.reminders[2]
      }
    },
    clearDialog() {
      this.$refs.form.reset()
      setTimeout(() => {
        this.company = ""
        this.date = new Date().toISOString().substr(0, 10)
      })
    },
    closeDialog() {
      this.$emit("input")

      setTimeout(() => {
        this.storeDetails({
          editedIndex: -1,
          editedTodo: Object.assign({}, this.defaultTodo),
        })
        this.storeCompany({})
      }, 300)
    },
  },
}
</script>
