import Vue from "vue"
import Router from "vue-router"
import Search from "./views/Search.vue"
import CompanyTable from "./views/tables/CompanyTable.vue"
import Company from "./views/detail/Company.vue"
import ContactTable from "./views/tables/ContactTable.vue"
import Opportunity from "./views/detail/Opportunity.vue"
import OpportunityTable from "./views/tables/OpportunityTable.vue"
import ContactPoint from "./views/detail/ContactPoint"
import Dashboard from "./views/Dashboard.vue"
import FilePreview from "./views/FilePreview.vue"

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: "/",
      redirect: "/dashboard",
    },
    {
      path: "/dashboard",
      name: "dashboard",
      component: Dashboard,
    },
    {
      path: "/search",
      name: "search",
      component: Search,
    },
    {
      path: "/companies",
      name: "companies",
      component: CompanyTable,
    },
    {
      path: "/contacts",
      name: "contacts",
      component: ContactTable,
    },
    {
      path: "/opportunities",
      name: "opportunities",
      component: OpportunityTable,
    },
    {
      path: "/companies/:companyId",
      name: "companyView",
      component: Company,
    },
    {
      path: "/opportunities/:opportunityId",
      name: "opportunityView",
      component: Opportunity,
    },
    {
      path: "/contactpoints/:contactPointId/:companyId?",
      name: "contactPointView",
      component: ContactPoint,
    },
    {
      path: "/contactpoints/:contactPointId/:companyId?/:fileName",
      name: "filePreview",
      component: FilePreview,
      // TODO Muss hier noch die companyId übergeben werden und explizit genannt? Fehler in AttachmentCard wenn File angucken will.
    },
  ],
})
