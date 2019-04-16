import api from '../utils/http-common'

export default {
    state: {
        authorized: false,
        username: '',
        password: '',
        usernames: [],
        user: {}
    },
    getters: {
        authorized: state => state.authorized,
        username: state => state.username,
        password: state => state.password,
        usernames: state => state.usernames,
        user: state => state.user
    },
    mutations: {
        storeLogData(state, payload) {
            state.authorized = payload.authorized,
                state.username = payload.username,
                state.password = payload.password
        },
        storeUsernames(state, payload) {
            state.usernames = payload.usernames
        },
        storeUser(state, payload) {
            state.user = payload.user
        }
    },
    actions: {
        getUsernames() {
            return api.get('user/get/usernames').then(res => {
                this.commit({
                    type: 'storeUsernames',
                    usernames: res.data
                })
            }).catch(error => {
                console.log(error);
            });
        },
        getUserInformation() {
            return api.get(`user/get/${this.getters.username}`).then(res => {
                this.commit({
                    type: 'storeUser',
                    user: res.data
                })
            }).catch(error => {
                console.log(error);
            });
        }
    }
};
