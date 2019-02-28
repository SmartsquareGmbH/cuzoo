import axios from 'axios';
import vuex from '../plugins/vuex.js';

const commonAxios = axios.create({
    baseURL: `${process.env.VUE_APP_API_SCHEME}://${process.env.VUE_APP_API_HOSTNAME}:${process.env.VUE_APP_API_PORT}/api/`
});

commonAxios.interceptors.request.use((config) => {
    if(!config.auth) {
        config.auth = {
            username: vuex.getters.username,
            password: vuex.getters.password
        }
    }

    return config
});

export default commonAxios