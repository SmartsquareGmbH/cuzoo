<template>
  <v-flex xs12>
    <v-card class="pl-0 mb-3 mt-1" elevation="6" height="75">
      <v-layout row wrap text-xs-left>
        <v-flex xs2>
          <v-sheet elevation="8" class="v-sheet--offset text-xs-center ml-3" height="75" width="100" color="primary">
            <v-icon color="secondary" size="56px" class="pt-2">
              forum
            </v-icon>
          </v-sheet>
        </v-flex>
        <v-flex xs2 class="more-padding-top">
          <span v-show="breakpoint.width > 1680" class="headline font-weight-light pl-3">
            Kontaktpunkte
          </span>
        </v-flex>
        <v-flex xs2 style="padding-top: 12px;" class="text-xs-right">
          <v-btn small flat fab color="transparent" @click="addContactPoint()">
            <v-tooltip top>
              <v-icon slot="activator" large color="light-green accent-2">
                add
              </v-icon>
              <span>Kontaktpunkt hinzufügen</span>
            </v-tooltip>
          </v-btn>
          <contact-point-dialog
            v-model="contactPointDialogState"
            :contact-names="contactNames"
            :companies="transformedCompanies"
            @refresh="refreshContactPoints()"
            @input="contactPointDialogState = false"
          />
        </v-flex>
        <v-flex xs6>
          <v-text-field
            ref="searchBarContactPoints"
            v-model="searchContactPoints"
            class="pr-3"
            append-icon="search"
            label="Suche nach Kontaktpunkten"
            color="primary"
            hide-details
            @keyup.enter="goToFirstResult()"
          />
        </v-flex>
      </v-layout>
    </v-card>
    <v-card elevation="6">
      <v-layout v-resize="onResize" row wrap>
        <div class="dash">
          <perfect-scrollbar :options="settings">
            <v-flex xs12 class="pa-0" :style="`height: ${widgetHeight}px`">
              <contact-point-results :search="searchContactPoints" :on-dashboard="true" />
            </v-flex>
          </perfect-scrollbar>
        </div>
      </v-layout>
    </v-card>
  </v-flex>
</template>

<script>
import { mapGetters } from "vuex"
import ContactPointDialog from "../dialogs/ContactPointDialog.vue"
import ContactPointResults from "../search/ContactPointResults.vue"

export default {
  components: {
    ContactPointDialog,
    ContactPointResults,
  },
  data: () => ({
    contactNames: [],
    contactPointDialogState: false,
    windowHeight: 0,
    searchContactPoints: "",
    settings: {
      maxScrollbarLength: 120,
      wheelSpeed: 0.75,
      suppressScrollX: true,
    },
  }),
  computed: {
    ...mapGetters(["companies", "contacts", "contactPoints", "selectedCompany", "searchResults"]),
    breakpoint() {
      return this.$vuetify.breakpoint
    },
    widgetHeight() {
      const height = this.windowHeight / 2.5725
      const cardHeight = 89
      const excess = height % cardHeight

      if (excess < cardHeight / 2) return height - excess
      else return height + (cardHeight - excess)
    },
    transformedCompanies() {
      return this.companies.map((company) => Object.assign({}, { id: company.id, name: company.name })).sort()
    },
  },
  watch: {
    selectedCompany() {
      if (!this.selectedCompany || this.companies.map((it) => it.name).includes(this.selectedCompany)) {
        this.searchContactPoints = this.selectedCompany
      }
    },
  },
  beforeMount() {
    this.onResize()
  },
  methods: {
    addContactPoint() {
      this.contactNames = this.contacts.map((it) => it.name).sort()
      this.contactPointDialogState = true
    },
    goToFirstResult() {
      if (this.searchResults[0].contact.company) {
        const companyId = this.companies.find((it) => it.id === this.searchResults[0].contact.company.id).id
        this.$router.push(`/contactpoints/${this.searchResults[0].id}/${companyId}`)
      } else {
        this.$router.push(`/contactpoints/${this.searchResults[0].id}`)
      }
    },
    onResize() {
      this.windowHeight = window.innerHeight
    },
    refreshContactPoints() {
      this.$parent.refreshData()
    },
  },
}
</script>

<style scoped>
.dash {
  height: 100%;
  width: 100%;
  position: relative;
}

.v-sheet--offset {
  top: -24px;
  position: relative;
}

.more-padding-top {
  padding-top: 20px !important;
}
</style>
