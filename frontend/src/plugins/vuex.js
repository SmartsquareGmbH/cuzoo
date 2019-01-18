import Vue from "vue"
import Vuex from "vuex"
import Auth from "../store/auth"
import Companies from "../store/companies"
import Contacts from "../store/contacts"
import ContactPoints from "../store/contactpoints"

Vue.use(Vuex);

export default new Vuex.Store({
    modules: {
        Auth,
        Companies,
        Contacts,
        ContactPoints
    }
})