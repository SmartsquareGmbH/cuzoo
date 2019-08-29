import api from "../utils/http-common"

const datefns = require("date-fns")

export default {
  state: {
    opportunities: [],
    editedIndex: -1,
    editedOpportunity: {
      value: false,
      id: 0,
      title: "",
      state: "Lead",
      description: "",
      lastProgress: "",
    },
  },
  getters: {
    opportunities: (state) => state.opportunities,
    editedOpportunity: (state) => state.editedOpportunity,
  },
  mutations: {
    storeOpportunities(state, payload) {
      state.opportunities = payload.opportunities
    },
    storeEditedOpportunityDetails(state, payload) {
      state.editedIndex = payload.editedIndex
      state.editedOpportunity = payload.editedOpportunity
    },
  },
  actions: {
    getOpportunities() {
      return api
        .get("opportunity/get")
        .then((res) => {
          let opportunities = res.data

          this.commit({
            type: "storeOpportunities",
            opportunities: opportunities.sort(compareOpportunities),
          })
        })
        .catch((error) => {
          console.log(error)
        })
    },
  },
}

function compareOpportunities(a, b) {
  if (datefns.compareAsc(a.lastProgress, b.lastProgress) === 0) {
    if (a.id < b.id) return 1
    if (a.id > b.id) return -1
  } else {
    return datefns.compareAsc(a.lastProgress, b.lastProgress)
  }
}
