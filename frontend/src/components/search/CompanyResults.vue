<template>
  <div>
    <company-card
      v-for="company in searchResults"
      :key="company.id"
      :company="company"
      :contacts="getContactsOfCompany(company.name)"
    />
  </div>
</template>

<script>
import { mapGetters, mapMutations } from "vuex"
import CompanyCard from "../cards/CompanyCard.vue"

export default {
  components: {
    CompanyCard,
  },
  props: ["search"],
  data: () => ({
    searchTerms: [],
  }),
  computed: {
    ...mapGetters(["companies", "contacts", "contactPoints"]),
    searchResults() {
      return this.companies
        .filter((company) => {
          this.defineSearchTerms(company)

          if (this.search) {
            return this.searchTerms.some((term) => term.includes(this.search.toLowerCase()))
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
    defineSearchTerms(company) {
      this.searchTerms = []

      const contactsOfCompany = this.getContactsOfCompany(company.name)
      const contactPointsOfCompany = this.getContactPointsOfCompany(company.name)

      contactsOfCompany.forEach((contact) => {
        this.searchTerms.push(contact.manager)
        this.searchTerms.push(contact.name.toLowerCase())
        contact.labels.map((it) => this.searchTerms.push(it.toLowerCase()))
      })

      contactPointsOfCompany.forEach((contactPoint) => {
        contactPoint.labels.forEach((label) => {
          this.searchTerms.push(label.toLowerCase())
        })
      })

      for (let key in company) {
        // eslint-disable-next-line no-prototype-builtins
        if (company.hasOwnProperty(key) && company[key]) {
          this.searchTerms.push(company[key].toString().toLowerCase())
        }
      }
    },
    getContactsOfCompany(company) {
      return this.contacts.filter((contact) => {
        return contact.company?.name === company
      })
    },
    getContactPointsOfCompany(company) {
      return this.contactPoints.filter((contactPoint) => {
        return contactPoint.contact.company?.name === company
      })
    },
  },
}
</script>
