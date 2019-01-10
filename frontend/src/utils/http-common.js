import axios from 'axios'
import store from '@/store.js'

const commonAxios = axios.create({
    baseURL: `http://${process.env.VUE_APP_API_HOSTNAME}:${process.env.VUE_APP_API_PORT}/api/`
});

commonAxios.interceptors.request.use((config) => {
    if(!config.auth) {
        config.auth = {
            username: store.getters.getUsername,
            password: store.getters.getPassword
        }
    }

    return config
})

export default commonAxios