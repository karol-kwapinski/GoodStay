import axios from "axios";

const axiosClient = axios.create({
    baseURL: "http://localhost:8082",
    withCredentials: true
})

export default axiosClient