import axios from "axios";
import axiosClient from "./axiosClient.js";

const handleAxiosError = (error) => {
    if (axios.isAxiosError(error)) {
        if (error.response) {
            throw {
                status: error.response.status,
                code: error.response.data?.code
            }
        }

        if (error.request) {
            throw {
                status: null,
                code: "SERVER_UNAVAILABLE"
            }
        }
    }
    throw {
        status: null,
        code: "UNKNOWN_ERROR"
    }
}

export const getApi = async (url, config = {}) => {
    try {
        const response = await axiosClient.get(url, config);
        return response.data;
    } catch (error) {
        handleAxiosError(error);
    }
}

export const postApi = async (url, data = undefined, config = {}) => {
    try {
        console.log(data);
        const response = await axiosClient.post(url, data, config);
        return response.data;
    } catch (error) {
        handleAxiosError(error);
    }
}