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
            component: Search
        },
        {
            path: "/companies",
            name: "companies",
            component: CompanyTable
        },
        {
            path: "/contacts",
            name: "contacts",
            component: ContactTable
        },
        {
            path: "/companies/:id",
            name: "companyview",
            component: CompanyView
        }
    ]
});
