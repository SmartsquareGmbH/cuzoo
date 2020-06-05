<template>
  <div>
    <v-dialog v-model="value" persistent max-width="950" @input="$emit('input')">
      <v-card>
        <v-card-title class="headline font-weight-light primary">
          Fortschritt hinzufügen
        </v-card-title>
        <div v-if="loading">
          <v-progress-linear slot="progress" class="mt-0" color="blue" indeterminate />
        </div>
        <v-card-text class="text-xs-right primary--text">
          <v-form ref="form" v-model="valid">
            <v-container grid-list-md>
              <v-layout row wrap>
                <v-flex xs8>
                  <v-text-field
                    v-model="editedOpportunity.title"
                    :rules="oppTitleRules"
                    suffix="*"
                    prepend-icon="title"
                    label="Opportunity-Titel"
                    hide-details
                    :disabled="loading"
                  />
                </v-flex>
                <v-flex xs4>
                  <v-combobox
                    v-model="editedOpportunity.state"
                    :items="oppStatuses"
                    :rules="oppStatusRules"
                    suffix="*"
                    prepend-icon="bubble_chart"
                    label="Status"
                    hide-details
                    :disabled="loading"
                  />
                </v-flex>
                <v-expand-transition>
                  <v-flex xs12>
                    <v-textarea
                      v-model="progressText"
                      prepend-icon="timeline"
                      label="Fortschritt"
                      rows="5"
                      hide-details
                      :disabled="loading"
                    />
                  </v-flex>
                </v-expand-transition>
              </v-layout>
            </v-container>
          </v-form>
          <div class="mr-2">* Pflichtfelder</div>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" flat :disabled="loading" @click.native="closeDialog()">Abbrechen</v-btn>
          <v-btn color="primary" flat :disabled="loading" @click="clearDialog()">Zurücksetzen</v-btn>
          <v-btn color="primary" flat :disabled="!valid || loading" @click="submitProgress()">Speichern</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-dialog :value="requireProgressTextDialog" persistent max-width="475">
      <v-card>
        <v-card-text class="text-xs-left headline font-weight-light">
          Wenn der Status sich nicht ändert, muss ein Fortschritt angegeben werden.
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="success" flat="flat" @click="requireProgressTextDialog = false">
            Bestätigen
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import { mapGetters } from "vuex"
import api from "../../utils/http-common"

export default {
  props: ["value"],
  data() {
    return {
      confirmDialog: false,
      valid: false,
      loading: false,
      progressText: "",
      oppStatuses: ["Lose", "Lead", "Prospect", "Quote", "Win"],
      oppStatusRules: [
        (v) => !!v || "Bitte geben Sie einen Status an",
        (v) => this.oppStatuses.includes(v) || "Dieser Status existiert nicht",
      ],
      oppTitleRules: [(v) => !!v || "Bitte geben Sie einen Titel an", this.opportunityMenu === true],
      requireProgressTextDialog: false,
      opportunityId: Number.parseInt(this.$route.params.opportunityId),
    }
  },
  computed: {
    ...mapGetters({
      opportunities: "opportunities",
      editedIndex: "editedContactPointIndex",
      editedOpportunity: "editedOpportunity",
    }),
    opportunity() {
      return this.opportunities.find((it) => it.id === this.opportunityId)
    },
  },
  methods: {
    closeDialog() {
      this.clearDialog()
      this.$emit("input")
    },
    clearDialog() {
      this.$refs.form.reset()

      this.$nextTick(() => {
        this.editedOpportunity.title = this.opportunity.title
        this.editedOpportunity.state = this.opportunity.state
        this.progressText = ""
      })
    },
    submitProgress() {
      if (this.editedOpportunity.state === this.opportunity.state && this.progressText === "") {
        this.requireProgressTextDialog = true
      } else {
        this.loading = true
        api
          .put(`opportunity/submit/progress/${this.editedOpportunity.id}`, {
            opportunityState: this.editedOpportunity.state,
            progressText: this.progressText,
          })
          .then(() => {
            this.$emit("refresh")
            this.closeDialog()
          })
          .finally(() => (this.loading = false))
      }
    },
  },
}
</script>
