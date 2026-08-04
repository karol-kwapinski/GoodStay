import axios from 'axios';
import axiosClient from "../config/axiosClient.js";

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

const userGetRequest = async (url) => {
    try {
        const response = await axiosClient.get(url);

        return response.data;
    } catch (error) {
        handleAxiosError(error);
    }
}

const userPostRequest = async (url, form = null) => {
    try {
        let response;

        if (form) {
            response = await axiosClient.post(url, form);
        }
        else {
            response = await axiosClient.post(url);
        }

        return response.data;
    } catch (error) {
        handleAxiosError(error);
    }
}

export const registerUser = (form) => {
    return userPostRequest( "http://localhost:8082/api/users/register", form);
}

export const loginUser = (form) => {
    return userPostRequest( "http://localhost:8082/api/users/login", form);
}

export const logoutUser = () => {
    return userPostRequest("http://localhost:8082/api/users/logout")
}

export const getCurrentUser = () => {
    return userGetRequest("http://localhost:8082/api/users/me");
}