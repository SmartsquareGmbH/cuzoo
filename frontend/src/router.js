import Vue from "vue";
import Router from "vue-router";
import Store from "./store";
import Search from "./views/Search.vue";
import CompanyTable from "./views/CompanyTable.vue";
import ContactTable from "./views/ContactTable.vue";
import CompanyView from "./components/Company/CompanyView.vue";

Vue.use(Router);

export default new Router({
    routes: [
        {
            path: "/search",
            name: "search",
            component: Search,
            beforeEnter: function (to, from, next) {
                Store.dispatch('getCompanies').then(
                    Store.dispatch('getContacts').then(next())
                );
            }
        },
        {
            path: "/companies",
            name: "companies",
            component: CompanyTable,
            beforeEnter: function (to, from, next) {
                Store.dispatch('getCompanies').then(next());
            }
        },
        {
            path: "/contacts",
            name: "contacts",
            component: ContactTable,
            beforeEnter: function (to, from, next) {
                Store.dispatch('getContacts').then(next());
            }
        },
        {
            path: "/companies/:id",
            name: "companyview",
            component: CompanyView
        }
    ]
});
