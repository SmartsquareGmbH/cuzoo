export default {
    state: {
        authorized: false,
        username: '',
        password: ''
    },
    getters: {
        authorized: state => state.authorized,
        username: state => state.username,
        password: state => state.password,
    },
    mutations: {
        storeLogData(state, payload) {
            state.authorized = payload.authorized,
            state.username = payload.username,
            state.password = payload.password
        }
    }
};
