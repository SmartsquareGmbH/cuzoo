export default {
    state: {
        darkState: true
    },
    getters: {
        darkState: state => state.darkState
    },
    mutations: {
        storeDarkState(state, payload) {
            state.darkState = payload.darkState
        }
    }
};
