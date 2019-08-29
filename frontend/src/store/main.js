export default {
  state: {
    selectedCompanyOnDash: "",
  },
  getters: {
    selectedCompany: (state) => state.selectedCompanyOnDash,
  },
  mutations: {
    storeSelectedCompany(state, payload) {
      state.selectedCompanyOnDash = payload.selectedCompanyOnDash
    },
  },
}
