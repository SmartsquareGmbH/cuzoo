export default {
  state: {
    searchResults: [],
  },
  getters: {
    searchResults: (state) => state.searchResults,
  },
  mutations: {
    storeSearchResults(state, payload) {
      state.searchResults = payload.searchResults
    },
  },
}
