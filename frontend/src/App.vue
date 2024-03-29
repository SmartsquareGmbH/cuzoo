<template>
  <v-app dark>
    <v-content v-if="!authorized">
      <login></login>
    </v-content>
    <v-container v-else fluid>
      <v-toolbar class="elevation-0" light clipped-left fixed app>
        <v-toolbar-items class="hidden-sm-and-down">
          <v-btn color="primary" href="#/dashboard">
            <span class="headline font-weight-light black--text">CUZOO</span>
          </v-btn>
          <v-btn flat href="#/dashboard">
            <v-icon>dashboard</v-icon>
            <span class="ml-2">Dashboard</span>
          </v-btn>
          <v-btn flat href="#/companies">
            <v-icon>business</v-icon>
            <span class="ml-2">Unternehmen</span>
          </v-btn>
          <v-btn flat href="#/contacts">
            <v-icon>people</v-icon>
            <span class="ml-2">Ansprechpartner</span>
          </v-btn>
          <v-btn flat href="#/opportunities">
            <v-icon>bubble_chart</v-icon>
            <span class="ml-2">Opportunities</span>
          </v-btn>
        </v-toolbar-items>
        <v-spacer />
        <v-fade-transition>
          <v-combobox
            v-if="this.$route.name.includes('dashboard')"
            ref="searchBar"
            v-model="selectedCompany"
            clearable
            :loading="loadingCompanies"
            :items="companyNames"
            class="search-bar hidden-md-and-down"
            prepend-icon="search"
            label="Suche nach Unternehmen ..."
            hide-details
            solo
            @change="companySelected()"
          />
        </v-fade-transition>
        <v-toolbar-items>
          <v-menu offset-y>
            <v-btn slot="activator" color="primary">
              <v-icon color="black">person</v-icon>
              <span class="ml-2 black--text">{{ username }}</span>
            </v-btn>
            <v-list light class="ml-2">
              <v-list-tile @click="openSettings()">
                <v-list-tile-title class="mr-2">Einstellungen</v-list-tile-title>
                <v-icon color="black" light>settings</v-icon>
              </v-list-tile>
              <v-list-tile @click="logout()">
                <v-list-tile-title class="mr-2">Logout</v-list-tile-title>
                <v-icon style="transform: rotate(90deg)" color="black" light>publish</v-icon>
              </v-list-tile>
            </v-list>
          </v-menu>
        </v-toolbar-items>
      </v-toolbar>
      <settings v-model="settingState" />
      <v-content>
        <router-view></router-view>
      </v-content>
      <v-footer class="justify-center" dark app>
        &copy; 2019<a class="ml-1" href="https://smartsquare.de" target="_blank">Smartsquare GmbH</a>
      </v-footer>
    </v-container>
  </v-app>
</template>

<script>
import { mapActions, mapGetters, mapMutations } from "vuex"

import Login from "@/components/core/single/Login.vue"
import Settings from "@/components/core/single/Settings.vue"

export default {
  components: {
    Login,
    Settings,
  },
  data() {
    return {
      drawer: null,
      settingState: false,
      selectedCompany: "",
      companyNames: [],
      loadingCompanies: true,
    }
  },
  computed: {
    ...mapGetters({
      authorized: "authorized",
      username: "username",
      password: "password",
      companies: "companies",
    }),
  },
  watch: {
    authorized(value) {
      if (value) {
        this.getCompanyNames()
      }
    },
  },
  beforeMount() {
    if (this.authorized) {
      this.getCompanyNames()
    }
  },
  methods: {
    ...mapMutations(["storeLogData", "storeSelectedCompany"]),
    ...mapActions(["getCompanies"]),
    openSettings() {
      this.settingState = true
    },
    logout() {
      this.storeLogData({
        authorized: false,
      })
    },
    companySelected() {
      this.storeSelectedCompany({ selectedCompanyOnDash: this.selectedCompany })
    },
    getCompanyNames() {
      this.getCompanies().then(() => {
        this.companyNames = this.companies.map((it) => it.name)
        this.loadingCompanies = false
      })
    },
  },
}
</script>

<style>
html {
  overflow-y: auto !important;
}

.nav-header h1 {
  padding: 0.35em;
  text-align: center;
}

* {
  margin: 0;
  padding: 0;
  font-family: "Avenir", Helvetica, Arial, sans-serif;
}

.v-toolbar__content,
.v-toolbar__extension {
  padding-left: 0 !important;
  padding-right: 0 !important;
}

.search-bar {
  margin-right: 24px !important;
}
</style>
