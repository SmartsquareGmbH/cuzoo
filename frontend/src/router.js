import Vue from "vue";
import Router from "vue-router";
import Search from "./views/Search.vue";
import CompanyTable from "./views/tables/CompanyTable.vue";
import Company from "./views/detail/Company.vue";
import ContactTable from "./views/tables/ContactTable.vue";
import Opportunity from "./views/detail/Opportunity.vue"
import ContactPoint from "./views/detail/ContactPoint";
import Dashboard from "./views/Dashboard.vue";
import FilePreview from "./views/FilePreview.vue";

Vue.use(Router);

export default new Router({
    routes: [
        {
            path: "/",
            redirect: "/dashboard"
        },
        {
            path: "/dashboard",
            name: "dashboard",
            component: Dashboard
        },
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
            path: "/opportunities/:oppId",
            name: "oppView",
            component: Opportunity
        },
        {
            path: "/:companyId/:contactPointId",
            name: "contactPointView",
            component: ContactPoint
        },
        {
            path: "/:companyId/:contactPointId/:fileName",
            name: "filePreview",
            component: FilePreview
        }
    ]
});
