import Vue from "vue"
import Vuex from "vuex"
import Auth from "../store/auth"
import Companies from "../store/companies"
import Contacts from "../store/contacts"
import ContactPoints from "../store/contactpoints"
import Todos from "../store/todos"

Vue.use(Vuex);

export default new Vuex.Store({
    modules: {
        Auth,
        Companies,
        Contacts,
        ContactPoints,
        Todos
    }
})