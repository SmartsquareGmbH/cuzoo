import axios from 'axios'

export default axios.create({
    baseURL: `http://${process.env.VUE_APP_API_HOSTNAME}:${process.env.VUE_APP_API_PORT}/api/`
})