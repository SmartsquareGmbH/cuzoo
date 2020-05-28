<template>
  <v-dialog v-model="value" persistent max-width="750" @input="$emit('input')">
    <v-card>
      <v-card-title class="headline primary" primary-title>
        {{ formTitle }}
      </v-card-title>
      <div v-if="loading">
        <v-progress-linear class="mt-0" slot="progress" color="blue" indeterminate />
      </div>
      <v-card-text class="text-xs-right primary--text">
        <v-form ref="form" v-model="valid">
          <v-container grid-list-md>
            <v-layout wrap>
              <v-flex xs12>
                <v-text-field
                  v-model="editedCompany.name"
                  label="Unternehmen"
                  prepend-icon="business_center"
                  hide-details
                  suffix="*"
                  :rules="companyFieldRules"
                  :disabled="loading"
                ></v-text-field>
              </v-flex>
              <v-flex xs6>
                <v-text-field
                  v-model="editedCompany.street"
                  label="Straße &amp; Hsnr."
                  prepend-icon="place"
                  hide-details
                  :disabled="loading"
                ></v-text-field>
              </v-flex>
              <v-flex xs3>
                <v-text-field
                  v-model="editedCompany.zipcode"
                  label="PLZ"
                  hide-details
                  mask="#####"
                  counter
                  min="5"
                  :disabled="loading"
                ></v-text-field>
              </v-flex>
              <v-flex xs3>
                <v-text-field v-model="editedCompany.place" label="Ort" hide-details :disabled="loading"></v-text-field>
              </v-flex>
              <v-flex xs12>
                <v-text-field v-model="editedCompany.homepage" label="Homepage" prepend-icon="home" :disabled="loading"></v-text-field>
              </v-flex>
              <v-flex xs12>
                <v-textarea
                  v-model="editedCompany.description"
                  counter="1000"
                  maxlength="1000"
                  name="input-7-4"
                  label="Beschreibung"
                  rows="3"
                  :disabled="loading"
                ></v-textarea>
              </v-flex>
              <v-flex xs12>
                <v-textarea
                  v-model="editedCompany.other"
                  counter="255"
                  maxlength="255"
                  name="input-7-4"
                  label="Sonstiges"
                  rows="3"
                  :disabled="loading"
                ></v-textarea>
              </v-flex>
              <v-flex xs12>
                <label-box
                  :current-labels="editedCompany.labels"
                  api-path="company/get/labels"
                  type="Labels"
                  @label-added="setCompanyLabels"
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
        <v-btn color="primary" flat :disabled="!valid || loading" @click="submitCompany()">Speichern</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import { mapGetters, mapMutations } from "vuex"
import api from "../../utils/http-common"

import LabelBox from "../core/LabelBox.vue"

export default {
  components: { LabelBox },
  props: ["value"],
  data() {
    return {
      valid: false,
      loading: false,
      labelBoxInput: "",
      companyFieldRules: [(v) => !!v || "Bitte geben Sie ein Unternehmen an"],
      defaultCompany: {
        value: false,
        id: 0,
        name: "",
        street: "",
        zipcode: "",
        place: "",
        homepage: "",
        description: "",
        other: "",
        labels: [],
      },
    }
  },
  computed: {
    ...mapGetters({
      editedCompany: "editedCompany",
      editedIndex: "editedCompanyIndex",
    }),
    formTitle() {
      return this.editedIndex === -1 ? "Unternehmen hinzufügen" : "Unternehmen bearbeiten"
    },
  },
  watch: {
    value() {
      this.$refs.form.resetValidation()
    },
  },
  methods: {
    ...mapMutations({
      storeCompanyDetails: "storeEditedCompanyDetails",
      storeLabels: "storeCompanyLabels",
    }),
    closeDialog() {
      this.$emit("input")

      this.$nextTick(() => {
        this.storeCompanyDetails({
          editedIndex: -1,
          editedCompany: Object.assign({}, this.defaultCompany),
        })
      })
    },
    clearDialog() {
      this.$refs.form.reset()
    },
    submitCompany() {
      this.loading = true
      api
        .put("company/submit", {
          name: this.editedCompany.name,
          id: this.editedCompany.id,
          street: this.editedCompany.street,
          zipcode: this.editedCompany.zipcode,
          place: this.editedCompany.place,
          homepage: this.editedCompany.homepage,
          description: this.editedCompany.description,
          other: this.editedCompany.other,
          labels: this.editedCompany.labels,
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
    setCompanyLabels(labels) {
      this.editedCompany.labels = labels
    },
  },
}
</script>
