<template>
  <v-dialog :value="value" persistent max-width="750" @input="$emit('input')">
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
              <v-flex xs6>
                <v-text-field
                  ref="nameField"
                  v-model="editedContact.name"
                  label="Vor- und Nachname"
                  prepend-icon="person"
                  suffix="*"
                  required
                  :rules="[(v) => !!v || 'Bitte geben Sie einen Namen an']"
                  hide-details
                  :disabled="loading"
                />
              </v-flex>
              <v-flex xs6>
                <v-combobox
                  v-model="editedContact.manager"
                  :items="usernames"
                  label="Manager"
                  prepend-icon="account_circle"
                  suffix="*"
                  required
                  :rules="managerRules"
                  hide-details
                  :disabled="loading"
                />
              </v-flex>
              <v-flex xs12>
                <v-combobox
                  v-model="company"
                  :items="companies"
                  item-text="name"
                  item-value="name"
                  :search-input.sync="companyNameEntered"
                  :disabled="!companyFieldEnabled || loading"
                  label="Unternehmen"
                  prepend-icon="business"
                  hide-details
                >
                  <template slot="no-data">
                    <v-list-tile>
                      <v-list-tile-content max-height="700">
                        <v-list-tile-title>
                          Das Unternehmen "<strong class="primary--text">{{ companyNameEntered }}</strong
                          >" wurde nicht gefunden. Beim <kbd>SPEICHERN</kbd> kann dies angelegt werden.
                        </v-list-tile-title>
                      </v-list-tile-content>
                    </v-list-tile>
                  </template>
                </v-combobox>
              </v-flex>
              <v-flex xs6>
                <v-text-field v-model="editedContact.mail" prepend-icon="mail" label="E-Mail" hide-details :disabled="loading" />
              </v-flex>
              <v-flex xs6>
                <v-text-field v-model="editedContact.role" label="Rolle" prepend-icon="work" hide-details :disabled="loading" />
              </v-flex>
              <v-flex xs6>
                <v-text-field v-model="editedContact.telephone" prepend-icon="call" label="Telefon" hide-details :disabled="loading" />
              </v-flex>
              <v-flex xs6>
                <v-text-field v-model="editedContact.mobile" prepend-icon="smartphone" label="Mobil" hide-details :disabled="loading" />
              </v-flex>
              <v-flex xs12>
                <v-textarea v-model="editedContact.comment" name="input-7-4" label="Bemerkung" rows="3" :disabled="loading" />
              </v-flex>
              <v-flex xs12>
                <label-box
                  :current-labels="editedContact.labels"
                  api-path="contact/get/labels"
                  type="Labels"
                  @label-added="setContactLabels"
                  :disabled="loading"
                />
              </v-flex>
            </v-layout>
          </v-container>
        </v-form>
        <div class="mr-2">* Pflichtfelder</div>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" flat :disabled="loading" @click="closeDialog()">Abbrechen</v-btn>
        <v-btn color="primary" flat :disabled="loading" @click="clearDialog()">Zurücksetzen</v-btn>
        <v-btn color="primary" flat :disabled="!valid || loading" @click="submit()">Speichern</v-btn>
      </v-card-actions>
    </v-card>
    <confirm-dialog
      v-model="confirmDialogState"
      :question-to-be-confirmed="createCompanyMessage"
      @confirmed="submitCompany()"
    />
  </v-dialog>
</template>

<script>
import { mapActions, mapGetters, mapMutations } from "vuex"
import api from "../../utils/http-common"

import LabelBox from "../core/LabelBox.vue"
import ConfirmDialog from "../dialogs/ConfirmDialog.vue"

export default {
  components: {
    LabelBox,
    ConfirmDialog,
  },
  props: ["value", "companies", "selectedCompany"],
  data() {
    return {
      loading: false,
      valid: false,
      companyFieldEnabled: true,
      confirmDialogState: false,
      createCompanyMessage: "Das angegebene Unternehmen existiert nicht, möchtest du es anlegen?",
      managerRules: [
        (v) => !!v || "Bitte geben Sie einen Manager an!",
        (v) => this.usernames.includes(v) || "Der Manager existiert nicht!",
      ],
      company: "",
      companyNameEntered: "",
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
        labels: [],
      },
    }
  },
  computed: {
    ...mapGetters({
      editedIndex: "editedContactIndex",
      editedContact: "editedContact",
      editedCompany: "editedContactCompany",
      username: "username",
      usernames: "usernames",
    }),
    formTitle() {
      return this.editedIndex === -1 ? "Ansprechpartner hinzufügen" : "Ansprechpartner bearbeiten"
    },
  },
  watch: {
    value() {
      this.$refs.form.resetValidation()
    },
    editedCompany(company) {
      this.company = company
    },
    companies() {
      this.companyFieldEnabled = true
      this.company = undefined
    },
    selectedCompany() {
      this.companyFieldEnabled = false
      this.company = this.selectedCompany
    }
  },
  beforeMount() {
    this.getUsernames()
    this.editedContact.manager = this.username
  },
  methods: {
    ...mapActions(["getUsernames"]),
    ...mapMutations({
      storeContactDetails: "storeEditedContactDetails",
      storeCompany: "storeEditedContactCompany",
    }),
    closeDialog() {
      this.$emit("input")

      setTimeout(() => {
        this.storeContactDetails({
          editedIndex: -1,
          editedContact: Object.assign({}, this.defaultContact),
        })

        this.storeCompany({})
        this.editedContact.manager = this.username
        this.company = this.getCompany()
      }, 300)
    },
    clearDialog() {
      this.$refs.form.reset()

      setTimeout(() => {
        if (this.selectedCompany) {
          this.company = this.getCompany()
        }
        this.editedContact.manager = this.username
      })
    },
    submit() {
      if (!this.selectedCompany && this.companyNameEntered) {
        if (this.companies.some((it) => it.name === this.companyNameEntered)) {
          this.submitContact()
        } else {
          this.confirmDialogState = true
        }
      } else {
        this.submitContact()
      }
    },
    submitContact(savedCompanyId) {
      const maybeCompany = this.company?.id ? `?companyId=${this.company.id}` : (savedCompanyId ? `?companyId=${savedCompanyId}` : "")
      this.loading = true

      api
        .put(`contact/submit${maybeCompany}`, {
          name: this.editedContact.name,
          id: this.editedContact.id,
          role: this.editedContact.role,
          address: this.editedContact.address,
          mail: this.editedContact.mail,
          telephone: this.editedContact.telephone,
          mobile: this.editedContact.mobile,
          comment: this.editedContact.comment,
          manager: this.editedContact.manager,
          labels: this.editedContact.labels,
        })
        .then(() => {
          this.$parent.refreshTable()
          this.closeDialog()
        })
        .catch((error) => {
          console.log(error)
          alert(error)
        })
        .finally(() => this.loading = false)
    },
    submitCompany() {
      this.loading = true
      api
        .put("company/submit", {
          name: this.companyNameEntered,
          street: "",
          zipcode: "",
          place: "",
          homepage: "",
          description: "",
          other: "",
          labels: [],
        })
        .then((response) => {
          this.submitContact(response.data)
        })
        .catch((error) => {
          this.loading = false
          console.log(error)
          alert(error)
        })
    },
    setContactLabels(labels) {
      this.editedContact.labels = labels
    },
    getCompany() {
      if (this.selectedCompany) {
        this.companyFieldEnabled = false
        return this.selectedCompany
      } else {
        return ""
      }
    },
    encodeString(value) {
      return value.replace(/&/g, "%26").replace(/\|/g, "%7C")
    },
  },
}
</script>