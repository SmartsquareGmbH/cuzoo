import Vue from "vue";
import Router from "vue-router";
import Search from "./views/Search.vue";
import CompanyTable from "./views/company/CompanyTable.vue";
import CompanyView from "./views/company/CompanyView.vue";
import ContactTable from "./views/contact/ContactTable.vue";
import CPoints from "./views/CPoints.vue";

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
        },
        {
            path: "/:id",
            name: "contactpoints",
            component: CPoints
        }
    ]
});
