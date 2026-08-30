import {postApi} from "../config/apiClient.js";
import {getApi} from "../config/apiClient.js";

export const registerUser = (form) => {
    return postApi( "/api/users/register", form);
}

export const loginUser = (form) => {
    return postApi( "/api/users/login", form);
}

export const logoutUser = () => {
    return postApi("/api/users/logout")
}

export const getCurrentUser = () => {
    return getApi("/api/users/me");
}

export const getAllHotelOwnersEmails = () => {
    return getApi("/api/users/getAllHotelOwnersEmails");
}