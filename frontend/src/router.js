import Vue from "vue"
import Router from "vue-router"
import Search from "./views/Search.vue"
import CompanyTable from "./views/company/CompanyTable.vue"
import Company from "./views/company/Company.vue"
import ContactTable from "./views/contact/ContactTable.vue"
import ContactPointList from "./views/contactpoint/ContactPointList.vue"
import ContactPoint from "./views/contactpoint/ContactPoint"

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
            path: "/companies/:companyId",
            name: "companyView",
            component: Company
        },
        {
            path: "/:companyId",
            name: "contactPoints",
            component: ContactPointList
        },
        {
            path: "/:companyId/:contactPointId",
            name: "contactPointView",
            component: ContactPoint
        }
    ]
});
