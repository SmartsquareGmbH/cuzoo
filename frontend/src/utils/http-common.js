import axios from 'axios'
import auth from '../store/auth.js'

const commonAxios = axios.create({
    baseURL: `http://${process.env.VUE_APP_API_HOSTNAME}:${process.env.VUE_APP_API_PORT}/api/`
});

commonAxios.interceptors.request.use((config) => {
    if(!config.auth) {
        config.auth = {
            username: auth.state.username,
            password: auth.state.password
        }
    }

    return config
});

export default commonAxios