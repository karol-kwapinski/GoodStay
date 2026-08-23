import {getApi, postApi} from "../config/apiClient.js";

export const addReservation = (data) => {
    return postApi("api/reservations/addReservation", data);
}

export const getTotalPrice = (hotelId, data) => {
    return postApi(`api/reservations/getTotalPrice/${hotelId}`, data)
}

export const getReservations = () => {
    return getApi('api/reservations/getReservations');
}

export const getReservationDetails = (reservationId) => {
    return getApi(`api/reservations/getReservationDetails/${reservationId}`);
}