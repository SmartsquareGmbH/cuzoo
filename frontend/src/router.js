import Vue from "vue"
import Router from "vue-router"
import Search from "./views/Search.vue"
import CompanyTable from "./views/company/CompanyTable.vue"
import Company from "./views/company/Company.vue"
import ContactTable from "./views/contact/ContactTable.vue"
import CPointList from "./views/point/CPointList.vue"
import CPoint from "./views/point/CPoint"

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
            component: Company
        },
        {
            path: "/:id",
            name: "contactpoints",
            component: CPointList
        },
        {
            path: "/:id/:id",
            name: "contactpointview",
            component: CPoint
        }
    ]
});
