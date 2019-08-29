import Vue from "vue"
import Vuex from "vuex"
import Main from "../store/main"
import Auth from "../store/auth"
import Companies from "../store/companies"
import Contacts from "../store/contacts"
import ContactPoints from "../store/contactpoints"
import Opportunities from "../store/opportunities"
import Todos from "../store/todos"
import SearchResults from "../store/searchResults"

import createPersistedState from "vuex-persistedstate"

Vue.use(Vuex)

export default new Vuex.Store({
  plugins: [
    createPersistedState({
      paths: ["Auth.authorized", "Auth.username", "Auth.password"],
    }),
  ],
  modules: {
    Main,
    Auth,
    Companies,
    Contacts,
    ContactPoints,
    Opportunities,
    Todos,
    SearchResults,
  },
})
