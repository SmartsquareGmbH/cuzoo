<template>
  <div v-if="searchResults.length > 0">
    <contact-point-card
      v-for="(contactPoint, index) in searchResults"
      :key="contactPoint.id"
      class="contact-point-results"
      :contact-point="contactPoint"
      :search="true"
    >
      <v-divider v-if="index !== searchResults.length - 1" />
    </contact-point-card>
  </div>
  <div v-else class="text-xs-center pt-4">
    <span class="error--text font-italic subheading">Keine Kontaktpunkte gefunden</span>
  </div>
</template>

<script>
import { mapGetters, mapMutations } from "vuex"
import ContactPointCard from "../cards/ContactPointCard.vue"

export default {
  components: {
    ContactPointCard,
  },
  props: ["search", "onDashboard"],
  data: () => ({
    searchTerms: [],
  }),
  computed: {
    ...mapGetters(["contactPoints"]),
    searchResults() {
      return this.contactPoints
        .filter((contactPoint) => {
          this.defineSearchTerms(contactPoint)

          if (this.search) {
            return this.searchTerms.some((it) => it.includes(this.search.toLowerCase()))
          } else if (this.onDashboard) {
            return this
          }
        })
        .splice(0, 10)
    },
  },
  watch: {
    searchResults() {
      this.storeSearchResults({ searchResults: this.searchResults })
    },
  },
  mounted() {
    this.storeSearchResults({ searchResults: this.searchResults })
  },
  methods: {
    ...mapMutations(["storeSearchResults"]),
    defineSearchTerms(contactPoint) {
      this.searchTerms = []

      Object.keys(contactPoint).forEach((key) => {
        let property = contactPoint[key]

        key === "contact" ? this.pushContactValues(property) : this.pushValue(property)
      })
    },
    pushContactValues(contact) {
      this.pushValue(contact.name)
      this.pushValue(contact.manager)

      contact.labels.map((it) => this.pushValue(it.title))

      if (contact.company) this.pushValue(contact.company.name)
    },
    pushValue(value) {
      if (value) this.searchTerms.push(value.toString().toLowerCase())
    },
  },
}
</script>

<style scoped>
.contact-point-results {
  margin: 0 !important;
  padding: 0 !important;
  margin-left: 4px !important;
  margin-right: 4px !important;
}
</style>
