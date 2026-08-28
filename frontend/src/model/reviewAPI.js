import {getApi, postApi} from "../config/apiClient.js";

export const getReviews = (hotelId) => {
    return getApi(`api/reviews/getReviews/${hotelId}`);
}

export const addReview = (data) => {
    return postApi("/api/reviews/addReview", data);
}