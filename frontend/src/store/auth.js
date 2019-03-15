import api from '../utils/http-common'

export default {
    state: {
        authorized: false,
        username: '',
        password: '',
        usernames: []
    },
    getters: {
        authorized: state => state.authorized,
        username: state => state.username,
        password: state => state.password,
        usernames: state => state.usernames
    },
    mutations: {
        storeLogData(state, payload) {
            state.authorized = payload.authorized,
            state.username = payload.username,
            state.password = payload.password
        },
        storeUsernames(state, payload) {
            state.usernames = payload.usernames
        }
    },
    actions: {
        getUsernames() {
            return api.get('user/get/usernames').then(response => {
                this.commit({
                    type: 'storeUsernames',
                    usernames: response.data
                })
            }).catch(error => {
                console.log(error);
            });
        }
    }
};
