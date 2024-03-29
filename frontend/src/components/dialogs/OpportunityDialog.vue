<template>
  <v-dialog v-model="value" persistent max-width="750" @input="$emit('input')">
    <v-card>
      <v-card-title class="headline primary" primary-title>
        {{ formTitle }}
      </v-card-title>
      <div v-if="loading">
        <v-progress-linear slot="progress" class="mt-0" color="blue" indeterminate />
      </div>
      <v-card-text class="text-xs-right primary--text">
        <v-form ref="form" v-model="valid">
          <v-container grid-list-md>
            <v-layout wrap>
              <v-flex xs12>
                <v-text-field
                  v-model="editedOpportunity.title"
                  label="Opportunity"
                  prepend-icon="business_center"
                  hide-details
                  suffix="*"
                  :rules="opportunityTitleRules"
                  :disabled="loading"
                ></v-text-field>
              </v-flex>
              <v-flex xs6>
                <v-combobox
                  v-model="editedOpportunity.state"
                  label="Status"
                  prepend-icon="bubble_chart"
                  hide-details
                  suffix="*"
                  disabled
                />
              </v-flex>
              <v-flex xs12>
                <v-textarea
                  v-model="editedOpportunity.description"
                  counter="1000"
                  maxlength="1000"
                  name="input-7-4"
                  label="Beschreibung"
                  rows="3"
                  :disabled="loading"
                ></v-textarea>
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
        <v-btn color="primary" flat :disabled="!valid || loading" @click="submitOpportunity()">Speichern</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import { mapGetters, mapMutations } from "vuex"
import api from "../../utils/http-common"

export default {
  props: ["value"],
  data() {
    return {
      valid: false,
      loading: false,
      opportunityTitleRules: [(v) => !!v || "Bitte geben Sie eine Opportunity an"],
      defaultOpportunity: {
        value: false,
        id: 0,
        title: "",
        state: "Lead",
        description: "",
        lastProgress: "",
      },
      formTitle: "Opportunity bearbeiten",
    }
  },
  computed: {
    ...mapGetters({
      editedOpportunity: "editedOpportunity",
    }),
  },
  watch: {
    value() {
      this.$refs.form.resetValidation()
    },
  },
  methods: {
    ...mapMutations({
      storeOpportunityDetails: "storeEditedOpportunityDetails",
      storeProgress: "storeOpportunityProgress",
    }),
    closeDialog() {
      this.$emit("input")

      setTimeout(() => {
        this.storeOpportunityDetails({
          editedIndex: -1,
          editedOpportunity: Object.assign({}, this.defaultOpportunity),
        })
      }, 300)
    },
    clearDialog() {
      const tempState = this.editedOpportunity.state
      this.$refs.form.reset()
      this.editedOpportunity.description = ""
      this.$nextTick(() => (this.editedOpportunity.state = tempState))
    },
    submitOpportunity() {
      this.loading = true
      api
        .put(`opportunity/submit`, {
          id: this.editedOpportunity.id,
          title: this.editedOpportunity.title,
          state: this.editedOpportunity.state,
          description: this.editedOpportunity.description,
        })
        .then(() => {
          this.$parent.refreshTable()
          this.closeDialog()
        })
        .catch((error) => {
          console.log(error)
          alert(error)
        })
        .finally(() => (this.loading = false))
    },
  },
}
</script>
